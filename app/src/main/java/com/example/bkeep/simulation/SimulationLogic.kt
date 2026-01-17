package com.example.bkeep.simulation

import androidx.compose.remote.creation.random

const val MAX_DAILY_HONEY = 0.22f

fun simulate(
    days: Int,
    tempRange: ClosedFloatingPointRange<Float>,
    humRange: ClosedFloatingPointRange<Float>,
    luxRange: ClosedFloatingPointRange<Float>,
    weather: String,
    population: String
): List<Float> {

    return (1..days).map {
        val temp = tempRange.random()
        val hum = humRange.random()
        val lux = luxRange.random()

        dailyHoneyChange(
            temp = temp,
            humidity = hum,
            lux = lux,
            weather = weather,
            population = population
        )
    }
}

fun dailyHoneyChange(
    temp: Float,
    humidity: Float,
    lux: Float,
    weather: String,
    population: String
): Float {

    val factor =
        temperatureFactor(temp) *
                humidityFactor(humidity) *
                lightFactor(lux) *
                weatherFactor(weather) *
                populationFactor(population)

    return MAX_DAILY_HONEY * factor
}

fun temperatureFactor(temp: Float): Float =
    when {
        temp < 9f -> -0.4f
        temp < 16f -> 0.2f
        temp <= 28.5f -> 1.0f
        temp <= 35f -> 0.6f
        else -> -0.2f
    }

fun humidityFactor(humidity: Float): Float =
    when {
        humidity < 50.4f -> -0.3f
        humidity <= 71.6f -> 1.0f
        else -> 0.4f
    }

fun lightFactor(lux: Float): Float =
    when {
        lux < 1_000f -> 0.0f
        lux < 10_000f -> 0.3f
        lux < 50_000f -> 0.8f
        else -> 1.0f
    }

fun weatherFactor(weather: String): Float =
    when (weather) {
        "Sunny" -> 1.0f
        "Cloudy" -> 0.7f
        "Light rain" -> 1.0f
        "Moderate rain" -> 0.4f
        "Heavy rain" -> -0.5f
        else -> 1.0f
    }

fun populationFactor(population: String): Float =
    when (population) {
        "Weak" -> 0.6f
        "Medium" -> 1.0f
        "Strong" -> 1.3f
        else -> 1.0f
    }
fun ClosedFloatingPointRange<Float>.random(): Float {
    return (start + Math.random() * (endInclusive - start)).toFloat()
}
