package com.example.bkeep.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.bkeep.MainActivity
import com.example.bkeep.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Brez layouta, samo preverjanje
        val sharedPrefs = EncryptedSharedPreferences.create(
            "auth_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val token = sharedPrefs.getString("jwt_token", null)

        if (token.isNullOrEmpty()) {
            // User ni prijavljen → LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            // User že prijavljen → MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        }

        finish() // da ne ostane v back stacku
    }
}
