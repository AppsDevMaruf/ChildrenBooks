package com.childrenbooks.feature.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.childrenbooks.appViewModelFactory
import com.childrenbooks.core.audio.Narrator
import com.childrenbooks.data.content.AudioClip
import com.childrenbooks.data.content.ContentRepository
import com.childrenbooks.data.content.LessonSet
import com.childrenbooks.data.content.World
import com.childrenbooks.data.content.WorldId
import com.childrenbooks.data.progress.Progress
import com.childrenbooks.data.progress.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ContinueItem(
    val worldId: WorldId,
    @param:StringRes val worldTitle: Int,
    val setIndex: Int,
    val setTitle: String,
    val cardIndex: Int,
    val cardCount: Int,
    @param:DrawableRes val cover: Int,
    val resume: Boolean,
)

data class WorldTile(
    val id: WorldId,
    @param:StringRes val title: Int,
    @param:DrawableRes val art: Int?,
    val glyph: String?,
    val setsDone: Int,
    val setCount: Int,
)

data class HomeUiState(
    val loading: Boolean = true,
    val childName: String = "",
    val stars: Int = 0,
    val newStickers: Int = 0,
    val continueItem: ContinueItem? = null,
    val worlds: List<WorldTile> = emptyList(),
)

/** Picks what "Up next" should open: resume an unfinished set, else the next set to do. */
object ContinuePlanner {
    data class Target(val set: LessonSet, val cardIndex: Int, val resume: Boolean)

    fun next(progress: Progress, worlds: List<World>, setByKey: (String) -> LessonSet?): Target? {
        val firstWorld = worlds.firstOrNull() ?: return null
        val last = progress.lastSetKey?.let(setByKey)
        if (last != null && !progress.isSetDone(last.key)) {
            val index = progress.lastCardIndex.coerceIn(0, last.cards.lastIndex)
            return Target(last, index, resume = index > 0)
        }
        val preferredWorld = worlds.firstOrNull { it.id == last?.worldId } ?: firstWorld
        val ordered = listOf(preferredWorld) + worlds.filter { it.id != preferredWorld.id }
        val nextSet = ordered.firstNotNullOfOrNull { world -> world.sets.firstOrNull { !progress.isSetDone(it.key) } }
        return Target(nextSet ?: firstWorld.sets.first(), 0, resume = false)
    }
}

class HomeViewModel(
    private val progressRepository: ProgressRepository,
    private val content: ContentRepository,
    private val narrator: Narrator,
    private val strings: (Int) -> String,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        progressRepository.progress,
        progressRepository.settings,
    ) { progress, settings ->
        val target = ContinuePlanner.next(progress, content.worlds, content::setByKey)
        HomeUiState(
            loading = false,
            childName = settings.childName,
            stars = progress.stars,
            newStickers = (progress.stickers.size - settings.stickersSeen).coerceAtLeast(0),
            continueItem = target?.let {
                ContinueItem(
                    worldId = it.set.worldId,
                    worldTitle = content.world(it.set.worldId).title,
                    setIndex = it.set.index,
                    setTitle = it.set.title,
                    cardIndex = it.cardIndex,
                    cardCount = it.set.cards.size,
                    cover = it.set.cards[it.cardIndex].image,
                    resume = it.resume,
                )
            },
            worlds = content.worlds.map { world ->
                WorldTile(
                    id = world.id,
                    title = world.title,
                    art = world.art,
                    glyph = world.glyph,
                    setsDone = world.sets.count { progress.isSetDone(it.key) },
                    setCount = world.sets.size,
                )
            },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    /** Spoken label so pre-readers hear where they are going. */
    fun speak(@StringRes title: Int) = narrator.speak(AudioClip.Spoken(strings(title)))

    companion object {
        val Factory = appViewModelFactory { container ->
            HomeViewModel(container.progressRepository, container.contentRepository, container.narrator, container.resources::getString)
        }
    }
}
