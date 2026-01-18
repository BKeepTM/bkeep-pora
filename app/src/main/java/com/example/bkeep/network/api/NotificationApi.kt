package com.example.bkeep.network.api

import com.example.lib.data.notification.CreateNotificationRequest
import com.example.lib.data.notification.NotificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationApi {
    @POST("/notification")
    suspend fun createNotification(@Body request: CreateNotificationRequest): Response<NotificationResponse>
}