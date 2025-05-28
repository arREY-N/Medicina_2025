package com.example.medicina.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val notificationBanner: String = "",
    val notificationMessage: String = "",
    val notificationOverview: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
    val source: String = "",
    val action: String = ""
)