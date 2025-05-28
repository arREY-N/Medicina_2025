package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicina.functions.MedicinaException
import com.example.medicina.model.BrandedGeneric
import com.example.medicina.model.Generic
import com.example.medicina.model.Medicine
import com.example.medicina.model.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GenericViewModel: ViewModel() {
    private val repository = Repository

    private val _genericData = MutableStateFlow(Generic())
    val genericData: StateFlow<Generic> = _genericData

    private val _upsertGeneric = MutableStateFlow(Generic())
    val upsertGeneric: StateFlow<Generic> = _upsertGeneric

    val generics = repository.getAllGenerics()
        .map{ list -> list.sortedBy { it.genericName.lowercase() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun getGenericById(genericId: Int){
        viewModelScope.launch {
            Repository.getAllGenerics()
                .map { list -> list.find { it.id == genericId } }
                .filterNotNull()
                .first() // suspends until non-null found
                .let {
                    _genericData.value = it
                    _upsertGeneric.value = it.copy()
                }
        }
    }

    val genericMap: StateFlow<Map<Int?, Generic>> = generics
        .map { list -> list.associateBy { gen -> gen.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    fun reset(){
        _genericData.value = Generic()
        _upsertGeneric.value = Generic()
    }

    fun updateData(transform: (Generic) -> Generic){
        _upsertGeneric.value = transform(_upsertGeneric.value)
    }

    suspend fun save(): Int {
        val id = repository.upsertGeneric(upsertGeneric.value)
        _genericData.value = _upsertGeneric.value
        reset()
        return id.toInt()
    }

    suspend fun delete(){
        repository.deleteGeneric(_genericData.value)
    }

    fun validateScreen(){
        generics.value.forEach {
            if(it.genericName == upsertGeneric.value.genericName){
                throw MedicinaException("Generic already exists")
            }
        }

        if(_upsertGeneric.value.genericName.isEmpty()){
            throw MedicinaException("Generic name cannot be empty")
        }
    }
}
