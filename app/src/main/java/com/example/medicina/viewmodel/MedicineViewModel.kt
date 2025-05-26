package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicina.model.Medicine
import com.example.medicina.functions.MedicinaException
import com.example.medicina.model.Repository
import com.example.medicina.model.Regulation
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
        updateData { it.copy(price = upsertPrice.value.toFloat()) }
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
        _price.value = ""
        _upsertPrice.value = ""
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
                    _price.value = it.price.toString()
                    _upsertPrice.value = it.copy().price.toString()
                }
        }
    }

    fun getMedicineColor(categoryId: Int): String{
        // val category = repository.getCategoryById(categoryId)
        return "#9e9e9e" // category?.hexColor ?: "#1A998E"
    }

    fun getMedicineRegulation(regulationId: Int){
        viewModelScope.launch {
            val regulation = repository.getRegulationById(regulationId)
            regulation?.let {
                _medicineRegulation.value = it
            }
        }
    }

    fun updateData(transform: (Medicine) -> Medicine) {
        _upsertMedicine.value = transform(_upsertMedicine.value)
    }

    private val _price = MutableStateFlow("")
    val price: StateFlow<String> = _price

    private val _upsertPrice = MutableStateFlow("")
    val upsertPrice: StateFlow<String> = _upsertPrice

    fun updatePrice(price: String){
        _upsertPrice.value = price
    }

    fun validateScreen(){
        if(_upsertMedicine.value.brandName.trim().isEmpty()){
            throw MedicinaException("Brand name cannot be empty")
        }

        if(upsertPrice.value.trim() == "" || upsertPrice.value.toFloatOrNull() == null || upsertPrice.value.toFloatOrNull() == 0f){
            throw MedicinaException("Invalid price")
        }

        if(_upsertMedicine.value.regulationId == -1){
            throw MedicinaException("Invalid regulation")
        }
    }
}


