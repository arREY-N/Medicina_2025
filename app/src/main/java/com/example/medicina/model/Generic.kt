package com.example.medicina.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generics")
data class Generic (
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val genericName: String = ""
)