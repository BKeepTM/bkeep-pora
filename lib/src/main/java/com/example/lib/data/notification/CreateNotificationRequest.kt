package com.example.lib.data.notification

data class CreateNotificationRequest(
    val summary: String,
    val description: String,
    val href: String = "",
    val severity: Int,
    val id_user: Int
)