package com.childrenbooks

import androidx.lifecycle.SavedStateHandle
import com.childrenbooks.ui.AppViewModel
import com.childrenbooks.ui.ExternalAction
import com.childrenbooks.ui.Routes
import com.childrenbooks.ui.about.SettingsViewModel
import com.childrenbooks.ui.games.MatchGameRoundCount
import com.childrenbooks.ui.games.MatchGameViewModel
import com.childrenbooks.ui.home.HomeViewModel
import com.childrenbooks.ui.listen.ListenCategory
import com.childrenbooks.ui.state.SelectionViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArchitectureStateTest {
    @Test
    fun parentGateAction_isConsumedExactlyOnce() {
        val viewModel = AppViewModel(SavedStateHandle())

        viewModel.requestExternalAction(ExternalAction.ShareApp)

        assertEquals(ExternalAction.ShareApp, viewModel.approveParentGate())
        assertNull(viewModel.approveParentGate())
        assertNull(viewModel.uiState.value.pendingExternalAction)
    }

    @Test
    fun homeSearchState_isBoundedAndClearedOnClose() {
        val viewModel = HomeViewModel(SavedStateHandle())

        viewModel.openSearch()
        viewModel.updateSearchQuery("a".repeat(100))

        assertTrue(viewModel.uiState.value.isSearchVisible)
        assertEquals(80, viewModel.uiState.value.searchQuery.length)

        viewModel.closeSearch()
        assertFalse(viewModel.uiState.value.isSearchVisible)
        assertEquals("", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun selectionState_neverMovesOutsideCatalog() {
        val viewModel = SelectionViewModel(SavedStateHandle())

        viewModel.previous(lastIndex = 6)
        assertEquals(0, viewModel.selectedIndex.value)

        viewModel.select(index = 99, lastIndex = 6)
        assertEquals(6, viewModel.selectedIndex.value)

        viewModel.next(lastIndex = 6)
        assertEquals(6, viewModel.selectedIndex.value)
    }

    @Test
    fun settingsPrivacyState_togglesPredictably() {
        val viewModel = SettingsViewModel(SavedStateHandle())

        viewModel.togglePrivacySummary()
        assertTrue(viewModel.uiState.value.isPrivacySummaryVisible)

        viewModel.togglePrivacySummary()
        assertFalse(viewModel.uiState.value.isPrivacySummaryVisible)
    }

    @Test
    fun matchGame_scoresOnceAndRequiresAnswerBeforeAdvancing() {
        val viewModel = MatchGameViewModel(SavedStateHandle())

        viewModel.nextRound()
        assertEquals(0, viewModel.uiState.value.round)

        viewModel.selectAnswer(choice = "A", correctAnswer = "A")
        viewModel.selectAnswer(choice = "A", correctAnswer = "A")
        assertEquals(1, viewModel.uiState.value.score)

        repeat(MatchGameRoundCount) {
            viewModel.nextRound()
            if (!viewModel.uiState.value.isComplete) {
                viewModel.selectAnswer(choice = "wrong", correctAnswer = "right")
            }
        }

        assertTrue(viewModel.uiState.value.isComplete)
        assertEquals(MatchGameRoundCount, viewModel.uiState.value.round)
    }

    @Test
    fun navigationArguments_areRejectedWhenUnknown() {
        assertTrue(Routes.isSupportedGame("word-puzzle"))
        assertFalse(Routes.isSupportedGame("not-a-game"))
        assertEquals(ListenCategory.Rhymes, ListenCategory.fromRouteId("rhymes"))
        assertNull(ListenCategory.fromRouteId("not-a-category"))
    }
}
