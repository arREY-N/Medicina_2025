package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicina.model.Category
import com.example.medicina.model.Repository
import androidx.lifecycle.viewModelScope
import com.example.medicina.model.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoryViewModel: ViewModel() {
    private val repository = Repository

    val categories = repository.getAllCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val categoryMap: StateFlow<Map<Int?, Category>> = categories
        .map { list -> list.associateBy { it.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    private val _categoryData = MutableStateFlow(Category())
    val categoryData: StateFlow<Category> = _categoryData

    private val _upsertCategory = MutableStateFlow(Category())
    val upsertCategory: StateFlow<Category> = _upsertCategory

    private val _categoryMedicines = MutableStateFlow<List<Medicine>>(emptyList())
    val categoryMedicines: StateFlow<List<Medicine>> = _categoryMedicines

    fun reset(){
        _categoryData.value = Category()
        _upsertCategory.value = Category()
    }

    fun updateData(transform: (Category) -> Category){
        _upsertCategory.value = transform(_upsertCategory.value)
    }

    suspend fun save(): Int {
        val id = repository.upsertCategory(upsertCategory.value)
        _categoryData.value = _upsertCategory.value
        reset()
        return id.toInt()
    }

    suspend fun delete() {
        val id = categoryData.value.id
        id?.let{
            repository.deleteCategory(id)
        }
    }

    fun getCategoryById(categoryId: Int){
        viewModelScope.launch {
            Repository.getAllCategories()
                .map { list -> list.find { it.id == categoryId } }
                .filterNotNull()
                .first() // suspends until non-null found
                .let {
                    _categoryData.value = it
                    _upsertCategory.value = it.copy()
                }
        }
    }
}