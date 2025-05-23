package com.example.medicina.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.medicina.model.Medicine
import com.example.medicina.model.Repository
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import androidx.lifecycle.viewModelScope

class InventoryViewModel: ViewModel() {
    private val repository = Repository

    // private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> = repository.getAllMedicines()

    val medicineMap: StateFlow<Map<Int, Medicine>> = medicines
        .map { list -> list.associateBy { med -> med.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    private val _searchedMedicines = mutableStateOf<List<Medicine>>(emptyList())
    val searchedMedicines: State<List<Medicine>> = _searchedMedicines

    fun getMedicineByName(medicineName: String) {
        val searchedMedicines = repository.getMedicinesByName(medicineName)
        searchedMedicines?.let {
            _searchedMedicines.value = it
        }
    }
}