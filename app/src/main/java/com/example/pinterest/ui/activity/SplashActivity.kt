package com.example.pinterest.ui.activity

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.pinterest.R


class SplashActivity : AppCompatActivity() {

    private lateinit var linearInternet: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        linearInternet = findViewById(R.id.linearInternet)

        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottie_layer_name)
        lottieAnimationView.playAnimation()

        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                while (true) {
                    if (!isInternetAvailable()) {
                        Log.d("TAG", "onAnimationEnd: no")
                        linearInternet.visibility = View.VISIBLE
                    } else {
                        Log.d("TAG", "onAnimationEnd: yes")
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                        break
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
    }

    private fun isInternetAvailable(): Boolean {
        val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val infoMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val infoWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return infoMobile!!.isConnected || infoWifi!!.isConnected
    }
}