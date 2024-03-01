package com.yamanf.taskman

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yamanf.taskman.firebase.FirebaseRepositoryImpl
import com.yamanf.taskman.ui.auth.AuthActivity
import com.yamanf.taskman.ui.onboarding.OnboardingActivity
import com.yamanf.taskman.ui.splash.SplashScreenViewModel
import com.yamanf.taskman.ui.splash.SplashScreenViewModelFactory

class MainActivity : AppCompatActivity() {
    private val splashScreenViewModel: SplashScreenViewModel by viewModels() {
        SplashScreenViewModelFactory(
            FirebaseRepositoryImpl()
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            checkUser()
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)
    }

    private fun checkUser(){
        val onBoarding: SharedPreferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE)
        val isFirstTime = onBoarding.getBoolean("firstTime",true)
        if (splashScreenViewModel.isUserLoggedIn() && !isFirstTime) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else if (!splashScreenViewModel.isUserLoggedIn() && !isFirstTime) {
            startActivity(Intent(this, AuthActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        } else if (isFirstTime) {
            val editor = onBoarding.edit()
            editor.putBoolean("firstTime", false)
            editor.apply()
            startActivity(Intent(this, OnboardingActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}