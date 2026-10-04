package com.childrenbooks.feature.practice

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import com.childrenbooks.R
import com.childrenbooks.appViewModelFactory
import com.childrenbooks.core.audio.Narrator
import com.childrenbooks.core.designsystem.components.AnswerTileState
import com.childrenbooks.data.content.AudioClip
import com.childrenbooks.data.content.ContentRepository
import com.childrenbooks.data.content.LetterCard
import com.childrenbooks.data.content.WorldId
import com.childrenbooks.data.progress.AgeBand
import com.childrenbooks.data.progress.AppSettings
import com.childrenbooks.data.progress.ProgressRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class PracticeTile(
    val cardId: String,
    val image: Int,
    val label: String?,
    val glyph: String?,
    val state: AnswerTileState,
)

data class PracticeUiState(
    val worldId: WorldId,
    val questionIndex: Int = 0,
    val questionCount: Int = 0,
    val kind: QuestionKind = QuestionKind.FindPicture,
    val prompt: String = "",
    val promptGlyph: String? = null,
    val promptImage: Int? = null,
    val tiles: List<PracticeTile> = emptyList(),
    val attempt: Int = 0,
    val feedback: PracticeFeedback = PracticeFeedback.Asking,
    val feedbackWord: String = "",
    val stars: Int = 0,
    val loading: Boolean = true,
)

enum class PracticeFeedback { Asking, TryAgain, Hint, Correct, Revealed }

sealed interface PracticeEffect {
    data object Correct : PracticeEffect
    data object Miss : PracticeEffect
    data class Finished(val worldId: WorldId, val setIndex: Int, val stars: Int, val stickerId: String?) : PracticeEffect
}

class PracticeViewModel(
    savedStateHandle: SavedStateHandle,
    private val content: ContentRepository,
    private val progressRepository: ProgressRepository,
    private val narrator: Narrator,
    private val strings: (Int, Array<out Any>) -> String,
) : ViewModel() {
    private val worldId = WorldId.fromKey(savedStateHandle.get<String>(WorldIdArg).orEmpty()) ?: WorldId.Letters
    private val set = content.set(worldId, savedStateHandle.get<Int>(SetIndexArg) ?: 0) ?: content.world(worldId).sets.first()

    private val questions = MutableStateFlow<List<Question>>(emptyList())
    private val state = MutableStateFlow(PracticeState())
    private val settings = MutableStateFlow(AppSettings())
    private var advanceJob: Job? = null

    private val _effects = Channel<PracticeEffect>(Channel.BUFFERED)
    val effects: Flow<PracticeEffect> = _effects.receiveAsFlow()

    val uiState: StateFlow<PracticeUiState> = combine(
        questions,
        state,
        settings,
        progressRepository.progress,
    ) { questionList, practice, appSettings, progress ->
        buildUi(questionList, practice, appSettings, progress.stars)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PracticeUiState(worldId))

    init {
        viewModelScope.launch {
            val loaded = progressRepository.settings.first()
            settings.value = loaded
            val optionCount = if (loaded.ageBand == AgeBand.Older) 4 else 3
            questions.value = QuestionGenerator.generate(set.cards, optionCount, Random(System.nanoTime()))
            speakPrompt()
        }
    }

    fun onPick(cardId: String) {
        val list = questions.value
        val before = state.value
        val after = PracticeReducer.reduce(before, list, PracticeAction.Pick(cardId))
        if (after == before) return
        state.value = after
        val question = list[after.questionIndex]
        when {
            after.solved -> {
                _effects.trySend(PracticeEffect.Correct)
                narrator.speak(AudioClip.Spoken(string(R.string.feedback_great)))
                recordLastResult(after)
                scheduleAdvance()
            }
            after.revealed -> {
                _effects.trySend(PracticeEffect.Miss)
                narrator.speak(AudioClip.Spoken(string(R.string.feedback_reveal, question.target.word)))
                recordLastResult(after)
                scheduleAdvance(RevealDelayMillis)
            }
            after.misses >= 2 -> {
                _effects.trySend(PracticeEffect.Miss)
                narrator.speak(AudioClip.Spoken(string(R.string.feedback_hint, question.target.word)))
            }
            else -> {
                _effects.trySend(PracticeEffect.Miss)
                narrator.speak(AudioClip.Spoken(string(R.string.feedback_try_again)))
            }
        }
    }

    fun onReplayPrompt() = speakPrompt()

    private fun recordLastResult(practice: PracticeState) {
        val result = practice.results.lastOrNull() ?: return
        viewModelScope.launch {
            progressRepository.recordAnswer(result.cardId, result.firstTry, result.misses)
        }
    }

    private fun scheduleAdvance(delayMillis: Long = AdvanceDelayMillis) {
        advanceJob?.cancel()
        advanceJob = viewModelScope.launch {
            delay(delayMillis)
            val next = PracticeReducer.reduce(state.value, questions.value, PracticeAction.Advance)
            state.value = next
            if (next.finished) {
                finish(next)
            } else {
                speakPrompt()
            }
        }
    }

    private suspend fun finish(practice: PracticeState) {
        val result = progressRepository.completeSet(set.key, practice.stars, content.stickers.map { it.id })
        _effects.send(PracticeEffect.Finished(worldId, set.index, result.starsEarned, result.stickerId))
    }

    private fun speakPrompt() {
        val question = questions.value.getOrNull(state.value.questionIndex) ?: return
        narrator.speak(AudioClip.Spoken(promptText(question)))
    }

    private fun promptText(question: Question): String = when (question.kind) {
        QuestionKind.StartsWith -> string(R.string.prompt_starts_with, (question.target as LetterCard).upper)
        QuestionKind.HowMany -> string(R.string.prompt_how_many)
        QuestionKind.FindPicture -> string(R.string.prompt_find, question.target.word)
    }

    private fun buildUi(list: List<Question>, practice: PracticeState, appSettings: AppSettings, stars: Int): PracticeUiState {
        val question = list.getOrNull(practice.questionIndex) ?: return PracticeUiState(worldId, stars = stars)
        val targetId = question.target.id
        val tiles = question.options.map { option ->
            val tileState = when {
                practice.solved -> if (option.id == targetId) AnswerTileState.Correct else AnswerTileState.Dimmed
                practice.revealed -> if (option.id == targetId) AnswerTileState.Correct else AnswerTileState.Dimmed
                option.id == practice.lastWrong -> AnswerTileState.TryAgain
                practice.misses >= 2 && option.id == targetId -> AnswerTileState.Hinted
                else -> AnswerTileState.Idle
            }
            PracticeTile(
                cardId = option.id,
                image = option.image,
                label = when {
                    question.kind == QuestionKind.HowMany -> null
                    appSettings.showWords || appSettings.ageBand == AgeBand.Older -> option.word
                    else -> null
                },
                glyph = if (question.kind == QuestionKind.HowMany) (option as? com.childrenbooks.data.content.CountCard)?.numeral else null,
                state = tileState,
            )
        }
        val feedback = when {
            practice.solved -> PracticeFeedback.Correct
            practice.revealed -> PracticeFeedback.Revealed
            practice.misses >= 2 -> PracticeFeedback.Hint
            practice.misses == 1 -> PracticeFeedback.TryAgain
            else -> PracticeFeedback.Asking
        }
        return PracticeUiState(
            worldId = worldId,
            questionIndex = practice.questionIndex,
            questionCount = list.size,
            kind = question.kind,
            prompt = promptText(question),
            promptGlyph = (question.target as? LetterCard)?.upper,
            promptImage = if (question.kind == QuestionKind.HowMany) question.target.image else null,
            tiles = tiles,
            attempt = practice.attempt,
            feedback = feedback,
            feedbackWord = question.target.word,
            stars = stars,
            loading = false,
        )
    }

    private fun string(id: Int, vararg args: Any): String = strings(id, args)

    override fun onCleared() = narrator.stop()

    companion object {
        const val WorldIdArg = "worldId"
        const val SetIndexArg = "setIndex"
        private const val AdvanceDelayMillis = 1_200L
        private const val RevealDelayMillis = 2_000L

        val Factory = appViewModelFactory { container ->
            PracticeViewModel(
                savedStateHandle = createSavedStateHandle(),
                content = container.contentRepository,
                progressRepository = container.progressRepository,
                narrator = container.narrator,
                strings = { id, args -> container.resources.getString(id, *args) },
            )
        }
    }
}
