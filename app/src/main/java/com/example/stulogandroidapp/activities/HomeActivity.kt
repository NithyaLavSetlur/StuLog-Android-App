package com.example.stulogandroidapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.stulogandroidapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navView = findViewById<BottomNavigationView>(R.id.bottomNav)
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        NavigationUI.setupWithNavController(navView, navController)
    }
}
