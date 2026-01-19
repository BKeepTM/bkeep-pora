package com.example.bkeep.network.api

import com.example.lib.data.user.FcmTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmApi {
    @POST("/users/fcm")
    suspend fun saveToken(@Body request: FcmTokenRequest): Response<okhttp3.ResponseBody>
}