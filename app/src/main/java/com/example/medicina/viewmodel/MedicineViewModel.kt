package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicina.model.Medicine
import com.example.medicina.model.Order
import com.example.medicina.model.Repository
import com.example.medicina.model.Category
import com.example.medicina.model.Regulation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MedicineViewModel : ViewModel() {
    private val repository = Repository

    private val _medicineData = MutableStateFlow(Medicine())
    val medicineData: StateFlow<Medicine> = _medicineData

    private val _upsertMedicine = MutableStateFlow(Medicine())
    val upsertMedicine: StateFlow<Medicine> = _upsertMedicine

    private val _medicineCategory = MutableStateFlow(Category())
    val medicineCategory: StateFlow<Category> = _medicineCategory

    private val _medicineRegulation = MutableStateFlow(Regulation())
    val medicineRegulation: StateFlow<Regulation> = _medicineRegulation

    fun save(): Int {
        val id = repository.upsertMedicine(upsertMedicine.value)
        _medicineData.value = upsertMedicine.value
        reset()
        return id
    }

    fun delete() {
        repository.deleteMedicine(upsertMedicine.value.id)
    }

    fun reset(){
        _medicineData.value = Medicine()
        _upsertMedicine.value = Medicine()
        _medicineCategory.value = Category()
        _medicineRegulation.value = Regulation()
    }

    fun getMedicineById(medicineId: Int) {
        val medicine = repository.getMedicineById(medicineId)
        medicine?.let {
            _medicineData.value = it
            _upsertMedicine.value = it.copy()
        }
    }

    fun getMedicineCategory(categoryId: Int){
        val category = repository.getCategoryById(categoryId)
        category?.let {
            _medicineCategory.value = it
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


