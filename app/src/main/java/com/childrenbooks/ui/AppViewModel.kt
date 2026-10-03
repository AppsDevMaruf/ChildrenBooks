package com.childrenbooks.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ExternalAction {
    MoreApps,
    PrivacyPolicy,
    RateApp,
    ShareApp,
}

data class AppUiState(
    val pendingExternalAction: ExternalAction? = null,
)

class AppViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AppUiState(
            pendingExternalAction = savedStateHandle.get<String>(PendingExternalActionKey)
                ?.let(::externalActionOrNull),
        ),
    )
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun requestExternalAction(action: ExternalAction) {
        savedStateHandle[PendingExternalActionKey] = action.name
        _uiState.value = AppUiState(action)
    }

    fun dismissParentGate() {
        clearPendingAction()
    }

    fun approveParentGate(): ExternalAction? {
        val action = _uiState.value.pendingExternalAction
        clearPendingAction()
        return action
    }

    private fun clearPendingAction() {
        savedStateHandle.remove<String>(PendingExternalActionKey)
        _uiState.value = AppUiState()
    }

    private companion object {
        const val PendingExternalActionKey = "pendingExternalAction"

        fun externalActionOrNull(value: String): ExternalAction? =
            ExternalAction.entries.firstOrNull { it.name == value }
    }
}
