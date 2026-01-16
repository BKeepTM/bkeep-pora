package com.example.lib.data.hive

data class CreateHiveRequest(
    val name: String,
    val location: String,
    val type: String,
    val status: String,
    val latitude: Double,
    val longitude: Double
)
