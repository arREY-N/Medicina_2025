package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicina.model.Category
import com.example.medicina.model.Repository
import androidx.lifecycle.viewModelScope
import com.example.medicina.model.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.*

class CategoryViewModel: ViewModel() {
    private val repository = Repository

    private val _categoryData = MutableStateFlow(Category())
    val categoryData: StateFlow<Category> = _categoryData

    private val _upsertCategory = MutableStateFlow(Category())
    val upsertCategory: StateFlow<Category> = _upsertCategory

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: MutableStateFlow<List<Category>> = _categories

    val categoryMap: StateFlow<Map<Int, Category>> = _categories
        .map { list -> list.associateBy { it.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    private val _categoryMedicines = MutableStateFlow<List<Medicine>>(emptyList())
    val categoryMedicines: StateFlow<List<Medicine>> = _categoryMedicines

    init {
        _categories.value = repository.getAllCategories()
    }

    fun reset(){
        _categoryData.value = Category()
        _upsertCategory.value = Category()
    }

    fun updateData(transform: (Category) -> Category){
        _upsertCategory.value = transform(_upsertCategory.value)
    }

    fun save() {
        repository.upsertCategory(upsertCategory.value)
        _categoryData.value = _upsertCategory.value
        reset()
    }

    fun delete() {
        repository.deleteCategory(categoryData.value.id)
    }

    fun getCategoryById(categoryId: Int){
        val category = repository.getCategoryById(categoryId)
        category?.let {
            _categoryData.value = it
            _upsertCategory.value = it.copy()
        }
    }

    fun getMedicineInCategory(categoryId: Int){
        val categoryMedicine = repository.getMedicinesByCategory(categoryId)
        categoryMedicine.let {
            _categoryMedicines.value = it
        }
    }

    fun getMedicineNumber(categoryId: Int): Int{
        val categoryMedicine = repository.getMedicinesByCategory(categoryId)
        return categoryMedicine.size
    }

}