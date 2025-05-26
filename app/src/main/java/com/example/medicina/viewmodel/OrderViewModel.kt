package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicina.functions.MedicinaException
import com.example.medicina.model.Order
import com.example.medicina.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class OrderViewModel : ViewModel() {
    private val repository = Repository

    val orders = repository.getAllOrders().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val orderMap: StateFlow<Map<Int?, Order>> = orders
        .map { list -> list.associateBy { it.id } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    private val _orderData = MutableStateFlow(Order())
    val orderData: StateFlow<Order> = _orderData

    private val _upsertOrder = MutableStateFlow(Order())
    val upsertOrder: StateFlow<Order> = _upsertOrder

    private val _upsertPrice = MutableStateFlow("")
    val upsertPrice: StateFlow<String> = _upsertPrice

    fun getOrderById(orderId: Int) {
        viewModelScope.launch {
            Repository.getAllOrders()
                .map { list -> list.find { it.id == orderId } }
                .filterNotNull()
                .first() // suspends until non-null found
                .let {
                    _orderData.value = it
                    _upsertOrder.value = it.copy()
                    _upsertPrice.value = _orderData.value.price.toString()
                }
        }
    }

    suspend fun save(): Int {

        val floatPrice = _upsertPrice.value.toFloatOrNull() ?: 0f
        val date = LocalDate.now()
        updateData { it.copy(orderDate = date) }

        if(upsertOrder.value.id == null){
            updateData { it.copy(remainingQuantity = it.quantity ) }
        }

        updateData { it.copy(price = floatPrice) }

        val id = repository.upsertOrder(upsertOrder.value).toInt()

        updateData { it.copy(id = id) }

        _orderData.value = upsertOrder.value
        reset()

        return id
    }

    fun delete() {
        viewModelScope.launch {
            val id = _upsertOrder.value.id
            id?.let{
                repository.deleteOrder(id)
            }
        }
    }

    fun reset(){
        _orderData.value = Order()
        _upsertOrder.value = Order()
        _upsertPrice.value = String()
    }

    fun updateData(transform: (Order) -> Order) {
        _upsertOrder.value = transform(_upsertOrder.value)
    }

    fun updatePrice(price: String){
        _upsertPrice.value = price
    }

    private val _supplierOrder = MutableStateFlow<List<Order>>(emptyList())
    val supplierOrder: StateFlow<List<Order>> = _supplierOrder

    fun getOrdersBySupplierId(supplierId: Int): List<Order> {
        viewModelScope.launch {
            orders.collect { orderList ->
                val filteredOrders = orderList.filter { it.supplierId == supplierId }
                _supplierOrder.value = filteredOrders
            }
        }
        return _supplierOrder.value
    }

    suspend fun getExpiringQuantity(medicineId: Int): Int {
        val orderList = orders.first() // Get the current list snapshot
        val expiringOrder = orderList
            .filter { it.medicineId == medicineId }
            .filter { it.remainingQuantity > -1 }
            .minByOrNull { it.expirationDate }

        return expiringOrder?.remainingQuantity ?: 0
    }


    suspend fun getTotalQuantity(medicineId: Int): Int {
        return orders.first()
            .filter { it.medicineId == medicineId }
            .filter { it.remainingQuantity > -1 }
            .sumOf { it.remainingQuantity }
    }

    suspend fun getOrderHistory(medicineId: Int): List<Order> {
        return orders.first().filter { it.medicineId == medicineId }
    }

    fun validateOrder() {
        if(upsertOrder.value.medicineId == -1){
            throw MedicinaException("Medicine not selected")
        }

        if(upsertOrder.value.supplierId == -1){
            throw MedicinaException("Supplier not selected")
        }

        if(upsertOrder.value.quantity <= 0){
            throw MedicinaException("Invalid quantity")
        }

        if(upsertPrice.value.trim() == "" || upsertPrice.value.toFloat() <= 0f){
            throw MedicinaException("Invalid price value")
        }

        if(upsertOrder.value.expirationDate == LocalDate.of(2000, 1, 1)){
            throw MedicinaException("Expiration date not selected")
        }

        if(upsertOrder.value.expirationDate <= LocalDate.now()){
            throw MedicinaException("Invalid expiration date")
        }
    }
}


