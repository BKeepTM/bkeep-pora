package com.example.bkeep

import android.app.Application
import com.example.bkeep.auth.TokenManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}
