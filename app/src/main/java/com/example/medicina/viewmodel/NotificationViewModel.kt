package com.example.medicina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicina.model.Notification
import com.example.medicina.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class NotificationViewModel: ViewModel() {
    private val repository = Repository

    val notifications= repository.getAllNotifications()
        .map{ list -> list.sortedByDescending { it.date } }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _notificationData = MutableStateFlow(Notification())
    val notificationData: StateFlow<Notification> = _notificationData

    val notificationsMap: StateFlow<Map<Int?, Notification>> = notifications
        .map { list -> list.associateBy {it.id} }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )


    suspend fun getNotificationById(id: Int){
        val notification = repository.getNotificationById(id)
        notification?.let {
            _notificationData.value = it
        }
    }

    fun addNotification(
        banner: String = "",
        message: String = "",
        overview: String = "",
        action: String = "",
        source: String = ""
    ){
        println("ADDING NOTIFICATION!")
        viewModelScope.launch {
            repository.upsertNotification(
                Notification(
                    notificationBanner = banner,
                    notificationMessage = message,
                    notificationOverview = overview,
                    action = action,
                    date = LocalDateTime.now(),
                    source = source
                )
            )
        }
    }
}