package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicina.model.Repository
import com.example.medicina.model.Supplier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SupplierViewModel : ViewModel() {
    private val repository = Repository()

    private val _suppliers: MutableStateFlow<List<Supplier>> = MutableStateFlow(emptyList())
    val suppliers: StateFlow<List<Supplier>> = _suppliers

    val supplierMap: StateFlow<Map<Int, Supplier>> = _suppliers
        .map { list -> list.associateBy { it.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    init {
        _suppliers.value = repository.getAllSuppliers()
    }

    private val _supplierData = MutableStateFlow(Supplier())
    val supplierData: StateFlow<Supplier> = _supplierData

    private val _upsertSupplier = MutableStateFlow(Supplier())
    val upsertSupplier: StateFlow<Supplier> = _upsertSupplier

    fun save() {
        repository.updateSupplier(upsertSupplier.value)
        _supplierData.value = upsertSupplier.value
        reset()
    }

    fun reset(){
        _supplierData.value = Supplier()
        _upsertSupplier.value = Supplier()
    }

    fun delete(){
        repository.deleteSupplier(upsertSupplier.value.id)
    }

    fun getSupplierById(supplierId: Int) {
        val supplier = repository.getSupplierById(supplierId)
        supplier?.let {
            _supplierData.value = it
            _upsertSupplier.value = it.copy()
        }
    }

    fun updateData(transform: (Supplier) -> Supplier) {
        _upsertSupplier.value = transform(_upsertSupplier.value)
    }
}


