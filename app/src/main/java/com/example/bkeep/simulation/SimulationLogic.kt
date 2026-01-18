package com.example.bkeep.simulation

const val MAX_DAILY_YIELD = 0.32f
const val DAILY_CONSUMPTION = 0.10f

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

    val fTemp = temperatureFactor(temp)
    val fHum = humidityFactor(humidity)
    val fLight = lightFactor(lux)
    val fWeather = weatherFactor(weather)
    val fPop = populationFactor(population)

    val production =
        MAX_DAILY_YIELD *
                fTemp * fHum * fLight * fWeather * fPop

    val avg = (fTemp + fHum + fLight + fWeather) / 4f
    val fStress = 1.0f + (1.0f - avg)

    val consumption =
        DAILY_CONSUMPTION * fPop * fStress

    return production - consumption
}
fun temperatureFactor(temp: Float): Float =
    when {
        temp < 9.2f -> 0.0f
        temp < 11f -> 0.2f
        temp < 16.3f -> 0.5f
        temp <= 28.5f -> 1.0f
        temp <= 35f -> 0.5f
        temp <= 39.9f -> 0.2f
        else -> 0.0f
    }
fun humidityFactor(humidity: Float): Float =
    when {
        humidity < 45f -> 0.0f
        humidity <= 50.4f -> 0.3f
        humidity <= 71.6f -> 1.0f
        humidity <= 79.9f -> 0.3f
        else -> 0.0f
    }
fun lightFactor(lux: Float): Float =
    when {
        lux < 1_000f -> 0.1f
        lux < 10_000f -> 0.4f
        lux < 50_000f -> 0.8f
        else -> 1.0f
    }
fun weatherFactor(weather: String): Float =
    when (weather) {
        "Sunny" -> 1.0f
        "Cloudy" -> 0.7f
        "Light rain" -> 1.0f
        "Moderate rain" -> 0.4f
        "Heavy rain" -> 0.2f
        else -> 1.0f
    }
fun populationFactor(population: String): Float =
    when (population) {
        "Weak" -> 0.5f
        "Medium" -> 0.7f
        "Strong" -> 1.0f
        else -> 1.0f
    }
fun ClosedFloatingPointRange<Float>.random(): Float {
    return (start + Math.random() * (endInclusive - start)).toFloat()
}
