package com.example.bkeep.network.api

import com.example.lib.data.hive.CreateHiveRequest
import com.example.lib.data.hive.Hive
import com.example.lib.data.location.HiveLocation
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface HiveApi {

    @GET("location/list")
    suspend fun getHiveLocations(): Response<List<HiveLocation>>

    @GET("/hive/list")
    suspend fun getHiveByUserId(): Response<List<Hive>>

    @POST("hive/remove")
    suspend fun deleteHive(@Query("id") id: Int): Response<Unit>

    @POST("hive")
    suspend fun createHive(@Body request: CreateHiveRequest): Response<Unit>

}