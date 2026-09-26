package com.kampplus.hava.core.ui.state

import com.kampplus.hava.core.ui.text.UiText

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>

    data class Success<T>(
        val data: T
    ) : UiState<T>

    data object Empty : UiState<Nothing>

    data class Error(
        val message: UiText
    ) : UiState<Nothing>
}
