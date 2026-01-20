package com.example.bkeep.network.api

import com.example.lib.data.device.CreateDeviceDataRequest
import com.example.lib.data.device.DeviceDataResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DeviceDataApi {
    @POST("/deviceData")
    suspend fun createDeviceData(@Body request: CreateDeviceDataRequest): Response<DeviceDataResponse>

    @GET("/deviceData/list")
    suspend fun listByUser(): Response<List<DeviceDataResponse>>
}