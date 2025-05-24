package com.example.medicina.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val categoryName: String = "",
    val description: String = "",
    val hexColor: String = "#9E9E9E"
)