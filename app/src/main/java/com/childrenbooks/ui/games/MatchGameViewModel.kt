package com.childrenbooks.ui.games

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MatchGameUiState(
    val round: Int = 0,
    val score: Int = 0,
    val selectedAnswer: String? = null,
) {
    val isComplete: Boolean get() = round >= MatchGameRoundCount
}

class MatchGameViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MatchGameUiState(
            round = (savedStateHandle[RoundKey] ?: 0).coerceIn(0, MatchGameRoundCount),
            score = (savedStateHandle[ScoreKey] ?: 0).coerceIn(0, MatchGameRoundCount),
            selectedAnswer = savedStateHandle[SelectedAnswerKey],
        ),
    )
    val uiState: StateFlow<MatchGameUiState> = _uiState.asStateFlow()

    fun selectAnswer(choice: String, correctAnswer: String) {
        val current = _uiState.value
        if (current.isComplete || current.selectedAnswer != null) return
        update(
            current.copy(
                selectedAnswer = choice,
                score = current.score + if (choice == correctAnswer) 1 else 0,
            ),
        )
    }

    fun nextRound() {
        val current = _uiState.value
        if (current.selectedAnswer == null || current.isComplete) return
        update(current.copy(round = current.round + 1, selectedAnswer = null))
    }

    fun playAgain() = update(MatchGameUiState())

    private fun update(state: MatchGameUiState) {
        savedStateHandle[RoundKey] = state.round
        savedStateHandle[ScoreKey] = state.score
        savedStateHandle[SelectedAnswerKey] = state.selectedAnswer
        _uiState.value = state
    }

    private companion object {
        const val RoundKey = "matchRound"
        const val ScoreKey = "matchScore"
        const val SelectedAnswerKey = "matchSelectedAnswer"
    }
}

const val MatchGameRoundCount = 10
