package com.example.lib.data.device

data class DeviceDataResponse(
    val id: Int,
    val time: String,
    val humidity: Float?,
    val brightness: Float?,
    val temperature: Float?
)