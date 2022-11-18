package com.yamanf.taskman.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yamanf.taskman.MainActivity
import com.yamanf.taskman.R
import com.yamanf.taskman.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.splashLogo.animate().setDuration(2500).alpha(1f).withEndAction(){

            startActivity(Intent(this@SplashScreen,MainActivity::class.java))

        }
    }
}