package com.example.medicina.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = Medicine::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Supplier::class,
            parentColumns = ["id"],
            childColumns = ["supplierId"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index(value = ["medicineId"]), Index(value = ["supplierId"])])
data class Order (
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val medicineId: Int = -1,
    val supplierId: Int = -1,
    val quantity: Int = -1,
    val price: Float = 0f,
    val expirationDate: LocalDate = LocalDate.of(2000, 1, 1),
    val orderDate: LocalDate = LocalDate.of(2000, 1, 1),
    val remainingQuantity: Int = -1
)