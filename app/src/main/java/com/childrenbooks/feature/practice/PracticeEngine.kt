package com.childrenbooks.feature.practice

import com.childrenbooks.data.content.Card
import com.childrenbooks.data.content.CountCard
import com.childrenbooks.data.content.LetterCard
import com.childrenbooks.data.content.PictureCard
import kotlin.random.Random

enum class QuestionKind { FindPicture, StartsWith, HowMany }

data class Question(
    val target: Card,
    val options: List<Card>,
) {
    val kind: QuestionKind
        get() = when (target) {
            is LetterCard -> QuestionKind.StartsWith
            is CountCard -> QuestionKind.HowMany
            is PictureCard -> QuestionKind.FindPicture
        }
}

/** Builds practice questions from the cards a child has just seen. */
object QuestionGenerator {
    const val QuestionsPerSet = 3

    fun generate(
        cards: List<Card>,
        optionCount: Int,
        random: Random,
        questionCount: Int = QuestionsPerSet,
    ): List<Question> {
        require(cards.isNotEmpty()) { "A set needs at least one card" }
        val targets = cards.shuffled(random).take(questionCount.coerceAtMost(cards.size))
        var previousCorrectIndex = -1
        return targets.map { target ->
            val distractors = cards.filter { it.id != target.id }.shuffled(random)
                .take((optionCount - 1).coerceAtMost(cards.size - 1))
            val options = (distractors + target).shuffled(random).toMutableList()
            val correctIndex = options.indexOf(target)
            if (correctIndex == previousCorrectIndex && options.size > 1) {
                val swapWith = (correctIndex + 1) % options.size
                options[correctIndex] = options[swapWith]
                options[swapWith] = target
            }
            previousCorrectIndex = options.indexOf(target)
            Question(target, options)
        }
    }
}

/** Per-question result, recorded to progress for the grown-ups report. */
data class QuestionResult(val cardId: String, val firstTry: Boolean, val misses: Int, val earnedStar: Boolean)

/**
 * Practice state machine. A question earns a star when answered within two tries;
 * a third miss reveals the answer with no star, and stars are never taken away.
 */
data class PracticeState(
    val questionIndex: Int = 0,
    val misses: Int = 0,
    val lastWrong: String? = null,
    val attempt: Int = 0,
    val solved: Boolean = false,
    val revealed: Boolean = false,
    val results: List<QuestionResult> = emptyList(),
    val finished: Boolean = false,
) {
    val answered: Boolean get() = solved || revealed
    val stars: Int get() = results.count { it.earnedStar }
}

sealed interface PracticeAction {
    data class Pick(val cardId: String) : PracticeAction
    data object Advance : PracticeAction
}

object PracticeReducer {
    const val MissesBeforeReveal = 3
    const val MissesForStar = 1

    fun reduce(state: PracticeState, questions: List<Question>, action: PracticeAction): PracticeState {
        if (state.finished) return state
        val question = questions.getOrNull(state.questionIndex) ?: return state.copy(finished = true)
        return when (action) {
            is PracticeAction.Pick -> pick(state, question, action.cardId)
            PracticeAction.Advance -> advance(state, questions)
        }
    }

    private fun pick(state: PracticeState, question: Question, cardId: String): PracticeState {
        if (state.answered) return state
        return if (cardId == question.target.id) {
            state.copy(
                solved = true,
                results = state.results + QuestionResult(
                    cardId = question.target.id,
                    firstTry = state.misses == 0,
                    misses = state.misses,
                    earnedStar = state.misses <= MissesForStar,
                ),
            )
        } else {
            val misses = state.misses + 1
            val reveal = misses >= MissesBeforeReveal
            state.copy(
                misses = misses,
                lastWrong = cardId,
                attempt = state.attempt + 1,
                revealed = reveal,
                results = if (reveal) {
                    state.results + QuestionResult(question.target.id, firstTry = false, misses = misses, earnedStar = false)
                } else {
                    state.results
                },
            )
        }
    }

    private fun advance(state: PracticeState, questions: List<Question>): PracticeState {
        if (!state.answered) return state
        val next = state.questionIndex + 1
        return if (next >= questions.size) {
            state.copy(finished = true)
        } else {
            PracticeState(questionIndex = next, results = state.results, attempt = state.attempt)
        }
    }
}
