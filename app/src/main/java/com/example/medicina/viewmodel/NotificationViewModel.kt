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

class NotificationViewModel: ViewModel() {
    private val repository = Repository()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _notificationData = MutableStateFlow<Notification>(Notification())
    val notificationData: StateFlow<Notification> = _notificationData

    val notificationsMap: StateFlow<Map<Int, Notification>> = _notifications
        .map { list -> list.associateBy {it.id} }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    init{
        _notifications.value = repository.getAllNotifications()
    }

    fun getNotificationById(id: Int){
        val notification = repository.getNotificationById(id)
        notification?.let {
            _notificationData.value = it
        }
    }
}