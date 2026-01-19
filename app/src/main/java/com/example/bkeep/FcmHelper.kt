package com.example.bkeep

import android.util.Log
import com.example.bkeep.auth.JwtUtils.getUserId
import com.example.bkeep.auth.TokenManager.getToken
import com.example.bkeep.network.RetrofitInstance
import com.example.lib.data.user.FcmTokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FcmHelper {
    fun sendTokenToServer(fcmToken: String) {
        val request = FcmTokenRequest(
            userId = getUserId(getToken().toString()),
            token = fcmToken
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.fcmApi.saveToken(request)
                if (response.isSuccessful) {
                    Log.d("FCM", "Token uspešno poslan na server")
                } else {
                    Log.e("FCM", "Napaka pri pošiljanju tokena: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}
