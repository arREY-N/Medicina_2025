package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicina.model.Medicine
import androidx.lifecycle.viewModelScope
import com.example.medicina.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SearchViewModel: ViewModel() {
    private val repository = Repository

    private val medicines = repository.getAllMedicines()

    private val _searchItem = MutableStateFlow("")
    val searchItem: StateFlow<String> = _searchItem

    private val _searchResults = MutableStateFlow<List<Medicine>>(emptyList())
    val searchResults: StateFlow<List<Medicine>> = _searchResults

    val resultsMap: StateFlow<Map<Int?, Medicine>> = _searchResults
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
        _searchResults.value = emptyList()
        _searchResults.value = getMedicinesByName(searchItem.value)
    }

    fun getMedicinesByName(searchKey: String) : List<Medicine> {

//        val name = searchKey.trim()
//
//        val medicineList: MutableSet<List<Medicine>> = mutableSetOf()
//
//        val genericsList = generics.value.filter{
//            it.genericName.contains(name, ignoreCase = true)
//        }.map{ it.id }
//
//        val brandList = medicines.value.filter{
//            it.brandName.contains(name, ignoreCase = true)
//        }
//
//        val descriptionList = medicines.value.filter {
//            it.description.contains(name, ignoreCase = true)
//        }.map{ it.id }
//
//        val matchIds = descriptionList + genericsList
//
//        val filteredMedicines = medicines.value.filter {
//            it.id in matchIds
//        }
//
//        medicineList.add(brandList)
//        medicineList.add(filteredMedicines)
//
//        return medicineList.flatten()
        return emptyList()
    }
}