package com.example.lib.data.device

data class CreateDeviceDataRequest(
    val time: String, // ISO 8601 format: "2024-05-05T12:00:00"
    val humidity: Float?,
    val brightness: Float?,
    val temperature: Float?,
    val longitude: Double,
    val latitude: Double
)
