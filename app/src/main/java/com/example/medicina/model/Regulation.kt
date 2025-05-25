package com.example.medicina.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "regulations")
data class Regulation(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val regulation: String = ""
)
