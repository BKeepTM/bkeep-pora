package com.example.bkeep.network.api

import com.example.lib.data.login.LoginRequest
import com.example.lib.data.login.LoginResponse
import com.example.lib.data.user.UpdateUserRequest
import com.example.lib.data.user.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    @POST("/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<UserResponse>

    @POST("users/update")
    suspend fun updateUser(@Body request: UpdateUserRequest): Response<Unit>
}