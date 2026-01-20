package com.example.bkeep.network.api

import com.example.lib.data.hive.Weight
import com.example.lib.data.location.HiveLocation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WeightApi {

    @GET("hiveWeight/{id}")
    suspend fun getHiveWeight(@Path("id")hiveId: Int): Response<List<Weight>>

}