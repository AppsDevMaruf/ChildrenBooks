package com.childrenbooks.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val isSearchVisible: Boolean = false,
    val searchQuery: String = "",
)

class HomeViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        HomeUiState(
            isSearchVisible = savedStateHandle[SearchVisibleKey] ?: false,
            searchQuery = savedStateHandle[SearchQueryKey] ?: "",
        ),
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun openSearch() = update(isSearchVisible = true)

    fun closeSearch() = update(isSearchVisible = false, searchQuery = "")

    fun updateSearchQuery(query: String) = update(searchQuery = query.take(MaxQueryLength))

    private fun update(
        isSearchVisible: Boolean = _uiState.value.isSearchVisible,
        searchQuery: String = _uiState.value.searchQuery,
    ) {
        val state = HomeUiState(isSearchVisible, searchQuery)
        savedStateHandle[SearchVisibleKey] = state.isSearchVisible
        savedStateHandle[SearchQueryKey] = state.searchQuery
        _uiState.value = state
    }

    private companion object {
        const val SearchVisibleKey = "searchVisible"
        const val SearchQueryKey = "searchQuery"
        const val MaxQueryLength = 80
    }
}
