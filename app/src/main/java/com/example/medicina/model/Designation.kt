package com.example.medicina.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "designations")
data class Designation(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val designation: String = ""
)
