package com.example.medicina.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicina.functions.MedicinaException
import com.example.medicina.model.Repository
import com.example.medicina.model.Supplier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SupplierViewModel : ViewModel() {
    private val repository = Repository

    val suppliers = repository.getAllSuppliers().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val supplierMap: StateFlow<Map<Int?, Supplier>> = suppliers
        .map { list -> list.associateBy { it.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    private val _supplierData = MutableStateFlow(Supplier())
    val supplierData: StateFlow<Supplier> = _supplierData

    private val _upsertSupplier = MutableStateFlow(Supplier())
    val upsertSupplier: StateFlow<Supplier> = _upsertSupplier

    suspend fun save(): Int {
        val id = repository.upsertSupplier(upsertSupplier.value)
        _supplierData.value = upsertSupplier.value
        reset()
        return id.toInt()
    }

    fun reset(){
        _supplierData.value = Supplier()
        _upsertSupplier.value = Supplier()
    }

    suspend fun delete(){
        val supplierId = upsertSupplier.value.id
        supplierId?.let{
            repository.deleteSupplier(supplierId)
        }
    }

    suspend fun getSupplierById(supplierId: Int) {
        val supplier = repository.getSupplierById(supplierId)
        supplier?.let {
            _supplierData.value = it
            _upsertSupplier.value = it.copy()
        }
    }

    fun updateData(transform: (Supplier) -> Supplier) {
        _upsertSupplier.value = transform(_upsertSupplier.value)
    }

    fun validateScreen(){
        if(upsertSupplier.value.name.trim() == ""){
            throw MedicinaException("Supplier name cannot be empty")
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(upsertSupplier.value.email).matches()){
            throw MedicinaException("Invalid email")
        }
    }
}


