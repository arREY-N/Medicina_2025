package com.example.medicina.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine (
    @PrimaryKey (autoGenerate = true) val id: Int? = null,
    val brandName: String = "",
    val categoryId: Int = -1,
    val regulationId: Int = -1,
    val price: Float = 0f,
    val description: String = ""
)