package com.example.medicina.viewmodel

sealed class UiState {
    object Idle : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}
