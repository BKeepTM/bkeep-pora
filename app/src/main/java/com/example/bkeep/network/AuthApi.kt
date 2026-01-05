package com.example.bkeep.network

import com.example.lib.login.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
