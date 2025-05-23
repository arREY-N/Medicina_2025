package com.example.medicina.model

import java.time.LocalDate

data class Order (
    val id: Int = -1,
    val medicineId: Int = -1,
    val supplierId: Int = -1,
    val quantity: Int = -1,
    val price: Float = 0f,
    val expirationDate: LocalDate = LocalDate.of(2000, 1, 1),
    val orderDate: LocalDate = LocalDate.of(2000, 1, 1),
    val remainingQuantity: Int = -1
)