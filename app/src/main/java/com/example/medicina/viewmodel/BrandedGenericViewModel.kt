package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicina.model.BrandedGeneric
import com.example.medicina.model.Generic
import com.example.medicina.model.Medicine
import com.example.medicina.model.Repository
// import com.example.medicina.model.Repository.medicines
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class BrandedGenericViewModel: ViewModel() {
    private val repository = Repository

    val brandedGenerics = repository.getAllBrandedGenerics().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val medicines = repository.getAllMedicines().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val generics: StateFlow<List<Generic>> = repository.getAllGenerics().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val genericMap: StateFlow<Map<Int, Generic>> = generics
        .map { list -> list.associateBy { gen -> gen.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    fun getGenericsById(medicineId: Int){
        val matchingGenericIds = brandedGenerics.value
            .filter { it.medicineId == medicineId }
            .map { it.genericId }
            .toSet()

        _genericNames.value = generics.value.filter { it.id in matchingGenericIds }
        _upsertGenericNames.value = _genericNames.value
    }

    private val _genericNames = MutableStateFlow<List<Generic>>(emptyList())
    val genericNames: StateFlow<List<Generic>> = _genericNames

    // editable copy of the list generic names
    private val _upsertGenericNames = MutableStateFlow<List<Generic>>(emptyList())
    val upsertGenericNames: StateFlow<List<Generic>> = _upsertGenericNames

    // updates the list of generic names
    fun updateGenericNames(addGeneric: Generic){
        _upsertGenericNames.update { current ->
            if (addGeneric in current) current else current + addGeneric
        }
    }

    // remove generic names from the editable list
    fun removeGeneric(removeGeneric: Generic){
        _upsertGenericNames.update { current -> current - removeGeneric }
    }

    fun getGenericNamesText(medicineId: Int? = null): String{
        reset()
        var genNames = ""

        medicineId?.let{
            getGenericsById(medicineId)
        }

        _genericNames.value.forEach {
            genNames += it.genericName + ", "
        }

        if (genNames.isNotEmpty()) {
            genNames = genNames.dropLast(2)
        }

        return genNames
    }

    fun save(medicineId: Int){
        val newEntries = _upsertGenericNames.value.map { generic ->
            BrandedGeneric(
                medicineId = medicineId,
                genericId = generic.id
            )
        }

        repository.updateBrandedGenericsForMedicine(
            medicineId,
            newEntries
        )
    }

    // reset the list for incoming new data
    fun reset(){
        _genericNames.value = emptyList()
        _upsertGenericNames.value = emptyList()
    }
}