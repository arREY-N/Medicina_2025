package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicina.model.Medicine
import com.example.medicina.model.Order
import com.example.medicina.model.Repository
import com.example.medicina.model.Category
import com.example.medicina.model.Regulation
import com.example.medicina.model.TestData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MedicineViewModel : ViewModel() {
    private val repository = Repository

    private val _medicines: StateFlow<List<Medicine>> = repository.getAllMedicines().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val medicines: StateFlow<List<Medicine>> = _medicines

    private val _medicineData = MutableStateFlow(Medicine())
    val medicineData: StateFlow<Medicine> = _medicineData

    private val _upsertMedicine = MutableStateFlow(Medicine())
    val upsertMedicine: StateFlow<Medicine> = _upsertMedicine

    private val _medicineRegulation = MutableStateFlow(Regulation())
    val medicineRegulation: StateFlow<Regulation> = _medicineRegulation

    suspend fun save(): Int {
        val id = repository.upsertMedicine(upsertMedicine.value)
        _medicineData.value = upsertMedicine.value
        reset()
        return id.toInt()
    }

    suspend fun delete() {
        val id = _upsertMedicine.value.id
        id?.let{
            repository.deleteMedicine(id)
        }
    }

    fun reset(){
        _medicineData.value = Medicine()
        _upsertMedicine.value = Medicine()
        _medicineRegulation.value = Regulation()
    }

    fun getMedicineById(medicineId: Int) {
        viewModelScope.launch {
            Repository.getAllMedicines()
                .map { list -> list.find { it.id == medicineId } }
                .filterNotNull()
                .first() // suspends until non-null found
                .let {
                    _medicineData.value = it
                    _upsertMedicine.value = it.copy()
                }
        }
    }

    fun getMedicineColor(categoryId: Int): String{
        val category = repository.getCategoryById(categoryId)
        return category?.hexColor ?: "#1A998E"
    }

    fun getMedicineRegulation(regulationId: Int){
        val regulation = repository.getRegulationById(regulationId)
        regulation?.let {
            _medicineRegulation.value = it
        }
    }

    fun updateData(transform: (Medicine) -> Medicine) {
        _upsertMedicine.value = transform(_upsertMedicine.value)
    }
}


