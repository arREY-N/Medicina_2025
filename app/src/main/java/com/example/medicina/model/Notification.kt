package com.example.medicina.model

import androidx.compose.ui.graphics.Color
import com.example.medicina.ui.theme.CustomGreen
import java.time.LocalDate

data class Notification(
    val id: Int = 0,
    val notificationBanner: String = "",
    val notificationMessage: String = "",
    val notificationOverview: String = "",
    val date: LocalDate = LocalDate.now(),
    val color: Color = CustomGreen
)