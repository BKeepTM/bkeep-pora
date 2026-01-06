package com.example.bkeep.network.api

import com.example.lib.data.login.LoginRequest
import com.example.lib.data.login.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}