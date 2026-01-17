package com.example.bkeep.auth

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    fun getUserId(token: String): Int {
        val parts = token.split(".")
        val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
        val json = JSONObject(payload)
        return json.getJSONObject("data").getInt("id")
    }
}
