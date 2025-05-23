package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicina.model.Medicine
import com.example.medicina.model.Order
import com.example.medicina.model.Repository
import com.example.medicina.model.Supplier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class OrderViewModel : ViewModel() {
    private val repository = Repository

    private val _orders = repository.getAllOrders()
    val orders: StateFlow<List<Order>> = _orders

    val orderMap: StateFlow<Map<Int, Order>> = _orders
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
        val order = repository.getOrderById(orderId)
        order?.let {
            _orderData.value = it
            _upsertOrder.value = it.copy()
            _upsertPrice.value = _orderData.value.price.toString()
        }
    }

    fun save() {
        val floatPrice = _upsertPrice.value.toFloatOrNull() ?: 0f
        val date = LocalDate.now()
        updateData { it.copy(orderDate = date) }
        if(upsertOrder.value.id == -1){
            updateData { it.copy(remainingQuantity = it.quantity ) }
        }
        updateData { it.copy(price = floatPrice) }
        repository.upsertOrder(upsertOrder.value)
        _orderData.value = upsertOrder.value
        reset()
    }

    fun delete() {
        repository.deleteOrder(upsertOrder.value.id)
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

    fun getOrdersBySupplierId(supplierId: Int): Int {
        val orders = _orders.value.filter { it.supplierId == supplierId }
        _supplierOrder.value = orders
        return orders.size
    }

    fun getExpiringQuantity(medicineId: Int): Int{
        val expiringOrder = _orders.value
            .filter{ it.medicineId == medicineId }
            .filter{ it.remainingQuantity > -1 }
            .minByOrNull { it.expirationDate }
        val expiringQuantity = expiringOrder?.remainingQuantity ?: 0
        return expiringQuantity
    }

    fun getTotalQuantity(medicineId: Int): Int {
        println(_orders.value
            .filter { it.medicineId == medicineId }
        )

        println(
            _orders.value
                .filter { it.medicineId == medicineId }
                .filter { it.remainingQuantity > -1 }
        )

        println(
            _orders.value
                .filter { it.medicineId == medicineId }
                .filter { it.remainingQuantity > -1 }
                .sumOf { it.remainingQuantity }
        )

        return _orders.value
            .filter { it.medicineId == medicineId }
            .filter { it.remainingQuantity > -1 }
            .sumOf { it.remainingQuantity }
    }

    fun getOrderHistory(medicineId: Int): List<Order> {
        return _orders.value.filter { it.medicineId == medicineId }
    }
}


