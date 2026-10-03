package com.childrenbooks.ui.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SelectionViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _selectedIndex = MutableStateFlow(savedStateHandle[SelectedIndexKey] ?: 0)
    val selectedIndex: StateFlow<Int> = _selectedIndex.asStateFlow()

    fun select(index: Int, lastIndex: Int) {
        require(lastIndex >= 0) { "A selection requires at least one item" }
        val boundedIndex = index.coerceIn(0, lastIndex)
        savedStateHandle[SelectedIndexKey] = boundedIndex
        _selectedIndex.value = boundedIndex
    }

    fun previous(lastIndex: Int) = select(_selectedIndex.value - 1, lastIndex)

    fun next(lastIndex: Int) = select(_selectedIndex.value + 1, lastIndex)

    private companion object {
        const val SelectedIndexKey = "selectedIndex"
    }
}
