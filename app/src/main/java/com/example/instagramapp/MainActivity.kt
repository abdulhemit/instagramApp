package com.example.instagramapp

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.instagramapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                binding.textview.setText("Home")
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                binding.textview.setText("Search")
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                binding.textview.setText("Add")
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                binding.textview.setText("Notifications")
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                binding.textview.setText("Profile")
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }
}