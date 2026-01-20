package com.example.bkeep

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.bkeep.auth.TokenManager
import com.example.bkeep.databinding.ActivityMainBinding
import com.example.bkeep.sensors.SensorInfoFragment
import com.example.bkeep.ui.account.AccountFragment
import com.example.bkeep.ui.add.AddHiveFragment
import com.example.bkeep.ui.list.HiveFragment
import com.example.bkeep.ui.login.LoginActivity
import com.example.bkeep.ui.map.MapFragment
import com.example.bkeep.ui.settings.SettingsFragment
import com.example.bkeep.worker.SensorUploadWorker
import java.util.concurrent.TimeUnit

import com.google.firebase.messaging.FirebaseMessaging
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.d("FCM", "Ob zagonu token: $token")
                FcmHelper.sendTokenToServer(token)
            }

        setSupportActionBar(binding.toolbar)

        // Hamburger
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setCurrentFragment(HiveFragment())

        // BottomNav
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mapNav -> setCurrentFragment(MapFragment())
                R.id.addNav -> setCurrentFragment(AddHiveFragment())
                R.id.listNav -> setCurrentFragment(HiveFragment())
            }
            true
        }

        // DrawerNav
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.drawNavAcc -> setCurrentFragment(AccountFragment())
                R.id.drawNavSettings -> setCurrentFragment(SettingsFragment())
                R.id.drawSimulate -> setCurrentFragment(SimulationFragment())
                R.id.drawSensors -> setCurrentFragment(SensorInfoFragment())
                R.id.drawNavLogout -> logout()
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        val workRequest =
            PeriodicWorkRequestBuilder<SensorUploadWorker>(15, TimeUnit.MINUTES)
                .addTag("sensor_upload")
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sensor_upload",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )

    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentView.id, fragment)
            .commit()
    }

    private fun logout() {
        TokenManager.clearToken()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
    override fun attachBaseContext(newBase: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(newBase)
        val scale = prefs.getString("font_size", "1.0")?.toFloat() ?: 1.0f

        val config = newBase.resources.configuration
        config.fontScale = scale

        val newContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(newContext)
    }
}