package com.example.pinterest.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.pinterest.R
import com.example.pinterest.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val bottomMenuView = binding.bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        val view = bottomMenuView.getChildAt(3)
        val itemView = view as BottomNavigationItemView

        val profileCustom =
            LayoutInflater.from(this).inflate(R.layout.item_profile, bottomMenuView, false)
        itemView.addView(profileCustom)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        setupWithNavController(binding.bottomNavigation, navHostFragment.navController)

    }
}