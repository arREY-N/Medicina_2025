package com.example.medicina.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "brandedGenerics",
    primaryKeys = ["genericId", "medicineId"],
    foreignKeys = [
        ForeignKey(
            entity = Generic::class,
            parentColumns = ["id"],
            childColumns = ["genericId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Medicine::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["medicineId"]), Index(value = ["genericId"])]
)
data class BrandedGeneric(
    val genericId: Int = -1,
    val medicineId: Int = -1,
)
