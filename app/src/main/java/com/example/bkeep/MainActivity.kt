package com.example.bkeep

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bkeep.auth.TokenManager
import com.example.bkeep.databinding.ActivityMainBinding
import com.example.bkeep.ui.login.LoginActivity
import com.example.bkeep.ui.map.MapFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        //setCurrentFragment(MapFragment())

        // BottomNav
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mapNav -> setCurrentFragment(MapFragment())
                //R.id.addNav -> setCurrentFragment(AddFragment())
                //R.id.listNav -> setCurrentFragment(DisplayFragment())
            }
            true
        }

        // DrawerNav
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                //R.id.drawNavAcc -> setCurrentFragment(ProfileFragment())
                //R.id.drawNavSettings -> setCurrentFragment(SettingsFragment())
                R.id.drawNavLogout -> TokenManager.clearToken()
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentView.id, fragment)
            .commit()
    }
}