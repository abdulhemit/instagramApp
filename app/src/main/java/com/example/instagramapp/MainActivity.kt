package com.example.instagramapp

import android.os.Bundle

import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.instagramapp.databinding.ActivityMainBinding
import com.example.instagramapp.fragments.HomeFragment
import com.example.instagramapp.fragments.NotificationFragment
import com.example.instagramapp.fragments.ProfileFragment
import com.example.instagramapp.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    internal var selectedFragment : Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,HomeFragment()).commit()
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {

                selectedFragment = HomeFragment()
            }
            R.id.nav_search -> {

                selectedFragment = SearchFragment()
            }
            R.id.nav_add_post -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {

                selectedFragment = NotificationFragment()
            }
            R.id.nav_profile -> {

                selectedFragment = ProfileFragment()
            }
        }
        if(selectedFragment != null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,selectedFragment!!).commit()

        }

        false
    }
}