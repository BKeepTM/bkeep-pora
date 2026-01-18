package com.example.lib.data.notification

data class CreateNotificationRequest(
    val summary: String,
    val description: String,
    val href: String = "", // Not used but required by schema
    val severity: Int,
    val id_user: Int // You need to store the logged-in user ID in SharedPreferences to use this
)