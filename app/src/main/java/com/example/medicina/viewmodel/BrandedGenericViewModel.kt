package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicina.model.BrandedGeneric
import com.example.medicina.model.Generic
import com.example.medicina.model.Medicine
import com.example.medicina.model.Repository
import com.example.medicina.model.Repository.medicines
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BrandedGenericViewModel: ViewModel() {
    private val repository = Repository

    private val _brandedGenerics = repository.getAllBrandedGenerics()
    val brandedGenerics: StateFlow<List<BrandedGeneric>> = _brandedGenerics

    private val _generics = repository.getAllGenerics()
    val generics: StateFlow<List<Generic>> = _generics

    private val _currentGenerics = MutableStateFlow<List<Generic>>(emptyList())
    val currentGenerics: StateFlow<List<Generic>> = _currentGenerics

    fun getGenericsId(medicineId: Int) {
        val matchGenericIds = brandedGenerics.value
            .filter { it.medicineId == medicineId }
            .map{ it.genericId }

        val matchGenerics = generics.value.filter { it.id in matchGenericIds }

        _currentGenerics.value = matchGenerics
    }
}