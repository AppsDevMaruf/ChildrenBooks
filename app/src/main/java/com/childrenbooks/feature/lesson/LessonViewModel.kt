package com.childrenbooks.feature.lesson

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import com.childrenbooks.appViewModelFactory
import com.childrenbooks.core.audio.Narrator
import com.childrenbooks.data.content.Card
import com.childrenbooks.data.content.ContentRepository
import com.childrenbooks.data.content.WorldId
import com.childrenbooks.data.progress.ProgressRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LessonUiState(
    val worldId: WorldId,
    @param:StringRes val worldTitle: Int,
    val setIndex: Int,
    val setTitle: String,
    val cards: List<Card>,
    val index: Int = 0,
    val isSpeaking: Boolean = false,
    val stars: Int = 0,
) {
    val isLast: Boolean get() = index == cards.lastIndex
}

sealed interface LessonEvent {
    data object CardTapped : LessonEvent
    data object NextTapped : LessonEvent
    data object BackTapped : LessonEvent
    data class CardSettled(val index: Int) : LessonEvent
}

sealed interface LessonEffect {
    data object PageTick : LessonEffect
    data class OpenPractice(val worldId: WorldId, val setIndex: Int) : LessonEffect
}

class LessonViewModel(
    private val savedStateHandle: SavedStateHandle,
    content: ContentRepository,
    private val progressRepository: ProgressRepository,
    private val narrator: Narrator,
) : ViewModel() {
    private val worldId = WorldId.fromKey(savedStateHandle.get<String>(WorldIdArg).orEmpty()) ?: WorldId.Letters
    private val world = content.world(worldId)
    private val set = content.set(worldId, savedStateHandle.get<Int>(SetIndexArg) ?: 0) ?: world.sets.first()
    private val startCard = (savedStateHandle.get<Int>(CardArg) ?: 0).coerceIn(0, set.cards.lastIndex)
    private val index = savedStateHandle.getStateFlow(KeyIndex, startCard)

    private val initial = LessonUiState(worldId, world.title, set.index, set.title, set.cards, startCard)

    val uiState: StateFlow<LessonUiState> = combine(
        index,
        narrator.isSpeaking,
        progressRepository.progress,
    ) { current, speaking, progress ->
        initial.copy(index = current, isSpeaking = speaking, stars = progress.stars)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)

    private val _effects = Channel<LessonEffect>(Channel.BUFFERED)
    val effects: Flow<LessonEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            progressRepository.savePosition(set.key, index.value)
            delay(AutoPlayDelayMillis)
            speakCurrent()
        }
    }

    fun onEvent(event: LessonEvent) {
        when (event) {
            LessonEvent.CardTapped -> speakCurrent()
            LessonEvent.NextTapped -> next()
            LessonEvent.BackTapped -> moveTo(index.value - 1)
            is LessonEvent.CardSettled -> moveTo(event.index)
        }
    }

    private fun next() {
        if (index.value < set.cards.lastIndex) {
            moveTo(index.value + 1)
            return
        }
        narrator.stop()
        viewModelScope.launch {
            _effects.send(LessonEffect.OpenPractice(worldId, set.index))
        }
    }

    private fun moveTo(target: Int) {
        val bounded = target.coerceIn(0, set.cards.lastIndex)
        if (bounded == index.value) return
        savedStateHandle[KeyIndex] = bounded
        viewModelScope.launch { progressRepository.savePosition(set.key, bounded) }
        _effects.trySend(LessonEffect.PageTick)
        speakCurrent()
    }

    private fun speakCurrent() = narrator.speak(set.cards[index.value].audio)

    override fun onCleared() = narrator.stop()

    companion object {
        const val WorldIdArg = "worldId"
        const val SetIndexArg = "setIndex"
        const val CardArg = "card"
        private const val KeyIndex = "lessonIndex"
        private const val AutoPlayDelayMillis = 350L

        val Factory = appViewModelFactory { container ->
            LessonViewModel(createSavedStateHandle(), container.contentRepository, container.progressRepository, container.narrator)
        }
    }
}
