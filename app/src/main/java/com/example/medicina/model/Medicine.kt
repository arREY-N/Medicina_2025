package com.example.medicina.model

data class Medicine (
    val medicineId: Int = -1,
    val brandName: String = "",
    val genericName: String = "",
    val categoryId: Int = -1,
    val regulationId: Int = -1,
    val price: Float = 0f,
    val description: String = "",
    val quantity: Int = 0
)