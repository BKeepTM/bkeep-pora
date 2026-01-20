package com.example.bkeep.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bkeep.R
import com.example.bkeep.network.RetrofitInstance.deviceDataApi
import com.example.bkeep.network.RetrofitInstance.notificationApi
import com.example.bkeep.sensors.humidity.HumiditySensor
import com.example.bkeep.sensors.light.LightSensor
import com.example.bkeep.sensors.temperature.DeviceTemperatureSensor
import com.example.lib.data.device.CreateDeviceDataRequest
import com.example.lib.data.notification.CreateNotificationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

class SensorUploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    //Singleton senzorji, da Worker lahko prebere vrednosti
    private val lightSensor: LightSensor by lazy { LightSensor(applicationContext) { } }
    private val humiditySensor: HumiditySensor by lazy { HumiditySensor(applicationContext) { } }
    private val deviceTemperatureSensor: DeviceTemperatureSensor by lazy { DeviceTemperatureSensor(applicationContext) { } }

    override suspend fun doWork(): Result {
        Log.d("SensorWorker", "Worker started at ${System.currentTimeMillis()}")

        lightSensor.start()
        humiditySensor.start()
        deviceTemperatureSensor.start()

        return try {
            val temperature = deviceTemperatureSensor.getCurrentValue() ?: 25f
            val humidity = humiditySensor.getCurrentValue() ?: 50f
            val light = lightSensor.getCurrentValue() ?: 100f

            val location = getLastKnownLocation()
            val lat = location?.latitude ?: 0.0
            val lng = location?.longitude ?: 0.0

            val time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())

            val dataRequest = CreateDeviceDataRequest(
                time = time,
                temperature = temperature,
                humidity = humidity,
                brightness = light,
                latitude = lat,
                longitude = lng
            )

            Log.d("SensorWorker", "Uploading data: $dataRequest")

            try {
                val response = deviceDataApi.createDeviceData(dataRequest)
                if (response.isSuccessful) {
                    Log.d("SensorWorker", "Data uploaded successfully: ${response.body()}")
                } else {
                    Log.e("SensorWorker", "Upload failed ${response.code()} ${response.errorBody()?.string()}")
                    return Result.retry()
                }
            } catch (e: Exception) {
                Log.e("SensorWorker", "Exception during upload", e)
                return Result.retry()
            }

            checkAndNotifyExtremes(temperature)

            Result.success()
        } catch (e: Exception) {
            Log.e("SensorWorker", "Worker error", e)
            Result.retry()
        }
    }

    private suspend fun checkAndNotifyExtremes(temp: Float) {
        if (temp <= 5 || temp >= 35) {
            val summary = if (temp >= 35) "Extreme Heat Alert" else "Freeze Alert"
            val description = "Temperature detected: $temp °C"

            val request = CreateNotificationRequest(
                summary = summary,
                description = description,
                severity = 2,
                id_user = 0
            )

            try {
                val response = notificationApi.createNotification(request)
                if (response.isSuccessful) {
                    Log.d("SensorWorker", "Extreme notification sent")
                } else {
                    Log.e("SensorWorker", "Notification failed ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SensorWorker", "Failed to send API notification", e)
            }

            // Lokalno Android obvestilo
            showLocalNotification(summary, description)
        }
    }

    private fun showLocalNotification(title: String, body: String) {
        val channelId = "sensor_alerts"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Sensor Alerts", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    // Lokacija
    private suspend fun getLastKnownLocation(): Location? =
        suspendCancellableCoroutine { cont ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                cont.resume(null)
                return@suspendCancellableCoroutine
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (cont.isActive) cont.resume(location)
            }.addOnFailureListener {
                if (cont.isActive) cont.resume(null)
            }
        }
}
