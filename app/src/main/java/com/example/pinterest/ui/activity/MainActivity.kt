package com.example.pinterest.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.pinterest.R
import com.example.pinterest.databinding.ActivityMainBinding
import com.example.pinterest.model.userprofile.User
import com.example.pinterest.networking.ApiClient
import com.example.pinterest.networking.services.ApiService
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.bottomNavigation.clearAnimation()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val bottomMenuView = binding.bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        val view = bottomMenuView.getChildAt(3)
        val itemView = view as BottomNavigationItemView

        val profileCustom =
            LayoutInflater.from(this).inflate(R.layout.item_profile, bottomMenuView, false)
        itemView.addView(profileCustom)

        binding.bottomNavigation.setupWithNavController(
            navHostFragment.navController
        )
    }
}