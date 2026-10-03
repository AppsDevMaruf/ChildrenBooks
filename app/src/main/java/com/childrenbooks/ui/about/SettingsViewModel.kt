package com.childrenbooks.ui.about

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val isPrivacySummaryVisible: Boolean = false,
)

class SettingsViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        SettingsUiState(savedStateHandle[PrivacyVisibleKey] ?: false),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun togglePrivacySummary() {
        val visible = !_uiState.value.isPrivacySummaryVisible
        savedStateHandle[PrivacyVisibleKey] = visible
        _uiState.value = SettingsUiState(visible)
    }

    private companion object {
        const val PrivacyVisibleKey = "privacySummaryVisible"
    }
}
