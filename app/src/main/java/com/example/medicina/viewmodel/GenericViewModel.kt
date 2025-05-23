package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicina.model.BrandedGeneric
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

    private val _medicineID = MutableStateFlow(-1)
    val medicineId: StateFlow<Int> = _medicineID

    fun setId(id: Int){
        _medicineID.value = id
    }

    val generics: StateFlow<List<Generic>> = repository.getAllGenerics()

    val genericMap: StateFlow<Map<Int, Generic>> = generics
        .map { list -> list.associateBy { gen -> gen.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )
}
