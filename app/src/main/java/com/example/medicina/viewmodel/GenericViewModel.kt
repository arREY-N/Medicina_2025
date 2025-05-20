package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicina.model.Generic
import com.example.medicina.model.Medicine
import com.example.medicina.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class GenericViewModel: ViewModel() {
    private val repository = Repository

    val generics: StateFlow<List<Generic>> = repository.getAllGenerics()

    val genericMap: StateFlow<Map<Int, Generic>> = generics
        .map { list -> list.associateBy { gen -> gen.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    private val _genericData = MutableStateFlow<List<Generic>>(emptyList())
    val genericData: StateFlow<List<Generic>> = _genericData

    fun updateGeneric(addGeneric: Generic){
        _genericData.update { current ->
            if (addGeneric in current) current else current + addGeneric
        }
    }

    fun removeGeneric(removeGeneric: Generic){
        _genericData.update { current -> current - removeGeneric }
    }
}