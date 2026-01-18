package com.example.bkeep.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bkeep.MainActivity
import com.example.bkeep.R

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Prikaži LoginFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, RegisterFragment())
            .commit()
    }

    // klic iz LoginFragment, ko je login uspešen
    fun onLoginSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}