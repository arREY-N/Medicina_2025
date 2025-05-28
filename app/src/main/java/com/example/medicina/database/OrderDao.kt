package com.example.medicina.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medicina.model.Order
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface OrderDao {

    @Query("Select * FROM orders")
    fun getAlOrders(): Flow<List<Order>>

    @Query("Select * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Int): Order?

    @Query("Select * FROM orders WHERE medicineId = :id")
    fun getOrdersByMedicine(id: Int): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE supplierId = :id")
    suspend fun getOrdersBySupplier(id: Int): List<Order>

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrder(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Update
    suspend fun updateOrder(order: Order)
}