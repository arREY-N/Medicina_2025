package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicina.model.Regulation
import com.example.medicina.model.Repository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RegulationViewModel: ViewModel() {
    private val repository = Repository

    private val _regulations = MutableStateFlow<List<Regulation>>(emptyList())
    val regulations: StateFlow<List<Regulation>> = _regulations

    val regulationMap: StateFlow<Map<Int, Regulation>> = _regulations
        .map { list -> list.associateBy { it.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    init {
        _regulations.value = repository.getAllRegulations()
    }
}