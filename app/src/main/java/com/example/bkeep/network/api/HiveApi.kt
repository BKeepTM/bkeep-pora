package com.example.bkeep.network.api

import com.example.lib.data.hive.Hive
import com.example.lib.data.location.HiveLocation
import retrofit2.Response
import retrofit2.http.GET

interface HiveApi {

    @GET("location/list")
    suspend fun getHiveLocations(): Response<List<HiveLocation>>

    @GET("/hive/list")
    suspend fun getHiveByUserId(): Response<List<Hive>>
}