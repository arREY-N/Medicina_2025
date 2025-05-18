package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicina.model.Medicine
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SearchViewModel: ViewModel() {

    private val _searchItem = MutableStateFlow("")
    val searchItem: StateFlow<String> = _searchItem

    private val _searchResults = MutableStateFlow<List<Medicine>>(emptyList())
    val searchResults: StateFlow<List<Medicine>> = _searchResults

    val resultsMap: StateFlow<Map<Int, Medicine>> = _searchResults
        .map { list -> list.associateBy { it.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )
    
    fun updateSearchItem(newItem: String){
        _searchItem.value = newItem
    }

    fun performSearch(){
        // searchResults = results
    }
}