package com.example.bkeep.network

import com.example.bkeep.auth.TokenManager
import com.example.bkeep.network.api.AuthApi
import com.example.bkeep.network.api.HiveApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:3000"

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = TokenManager.getToken()
                val request = chain.request().newBuilder()

                if (!token.isNullOrEmpty()) {
                    request.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(request.build())
            }
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //AUTH
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    //HIVES
    val hiveApi: HiveApi by lazy {
        retrofit.create(HiveApi::class.java)
    }
}

