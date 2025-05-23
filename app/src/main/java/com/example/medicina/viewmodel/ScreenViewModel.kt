package com.example.medicina.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ScreenViewModel: ViewModel() {
    private val _errorMessage = mutableStateOf("")
    val errorMessage = _errorMessage

    fun setErrorMessage(message: String){
        _errorMessage.value = message
    }
}