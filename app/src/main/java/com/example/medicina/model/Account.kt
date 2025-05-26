package com.example.medicina.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "accounts",
    foreignKeys = [
        ForeignKey(
            entity = Designation::class,
            parentColumns = ["id"],
            childColumns = ["designationID"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["designationID"])])
data class Account (
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val firstname: String = "",
    val lastname: String = "",
    val middlename: String = "",
    val designationID: Int? = null,
    val username: String = "",
    val password: String = ""
)
