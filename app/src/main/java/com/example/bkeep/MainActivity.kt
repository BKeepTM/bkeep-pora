package com.example.bkeep

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bkeep.auth.TokenManager
import com.example.bkeep.databinding.ActivityMainBinding
import com.example.bkeep.ui.list.HiveFragment
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

        setCurrentFragment(HiveFragment())

        // BottomNav
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mapNav -> setCurrentFragment(MapFragment())
                //R.id.addNav -> setCurrentFragment(AddFragment())
                R.id.listNav -> setCurrentFragment(HiveFragment())
            }
            true
        }

        // DrawerNav
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                //R.id.drawNavAcc -> setCurrentFragment(ProfileFragment())
                //R.id.drawNavSettings -> setCurrentFragment(SettingsFragment())
                R.id.drawNavLogout -> logout()
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

    private fun logout() {
        TokenManager.clearToken()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }

}