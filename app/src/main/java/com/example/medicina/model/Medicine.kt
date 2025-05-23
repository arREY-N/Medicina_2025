package com.example.medicina.model

data class Medicine (
    val id: Int = -1,
    val brandName: String = "",
    val categoryId: Int = -1,
    val regulationId: Int = -1,
    val price: Float = 0f,
    val description: String = ""
)