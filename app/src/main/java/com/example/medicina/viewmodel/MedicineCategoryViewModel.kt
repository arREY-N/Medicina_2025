package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicina.model.BrandedGeneric
import com.example.medicina.model.Category
import com.example.medicina.model.Generic
import com.example.medicina.model.Medicine
import com.example.medicina.model.MedicineCategory
import com.example.medicina.model.Repository
import com.example.medicina.model.Repository.medicineCategoryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MedicineCategoryViewModel: ViewModel() {
    val repository = Repository

    val medicineCategories = repository.getAllMedicineCategory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val categories = repository.getAllCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val medicines = repository.getAllMedicines().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _medicineNames = MutableStateFlow<List<Medicine>>(emptyList())
    val medicineNames: StateFlow<List<Medicine>> = _medicineNames

    private val _categoryNames = MutableStateFlow<List<Category>>(emptyList())
    val categoryNames: StateFlow<List<Category>> = _categoryNames
    // editable copy of the list category medicine names
    private val _upsertMedicineCategory = MutableStateFlow<List<Category>>(emptyList())
    val upsertMedicineCategory: StateFlow<List<Category>> = _upsertMedicineCategory

    fun getCategoriesById(medicineId: Int){
        val matchingGenericIds = medicineCategories.value
            .filter { it.medicineId == medicineId }
            .map { it.categoryId }
            .toSet()

        _categoryNames.value = categories.value.filter { it.id in matchingGenericIds }
        _upsertMedicineCategory.value = _categoryNames.value
    }

    // updates the list of categories
    fun updateCategories(addCategory: Category){
        println("Add category: ${addCategory.categoryName}")
        _upsertMedicineCategory.update { current ->
            if (addCategory in current) current else current + addCategory
        }
    }

    // remove generic names from the editable list
    fun removeCategory(removeCategory: Category){
        _upsertMedicineCategory.update { current -> current - removeCategory }
    }

    suspend fun save(medicineId: Int) {
        val newCategories = _upsertMedicineCategory.value.map { category ->
            MedicineCategory(
                medicineId = medicineId,
                categoryId = category.id ?: -1
            )
        }
        println("New categories: $newCategories")
        repository.upsertMedicineCategory(medicineId, newCategories)
    }

    fun getCategoryNamesText(medicineId: Int? = null): String{
        reset()
        var catNames = ""

        println("medicine ID: $medicineId")
        medicineId?.let{
            getCategoriesById(medicineId)
        }

        if(_categoryNames.value.isEmpty()){
            println("No category loaded")
        }

        _categoryNames.value.forEach {
            catNames += it.categoryName + ", "
        }

        if (catNames.isNotEmpty()) {
            catNames = catNames.dropLast(2)
        }

        println("Category name: $catNames")
        return catNames
    }


    fun getMedicinesByCategory(categoryId: Int) : List<Medicine> {
        val medicineIds = medicineCategories.value
            .filter { it.categoryId == categoryId }
            .map { it.medicineId }
            .toSet()

        _medicineNames.value = medicines.value.filter { it.id in medicineIds }
        _upsertMedicineCategory.value = _categoryNames.value

        return _medicineNames.value
    }

    fun getCategorySize(categoryId: Int): Int {
        return getMedicinesByCategory(categoryId).size
    }

    fun reset(){
        _categoryNames.value = emptyList()
    }
}