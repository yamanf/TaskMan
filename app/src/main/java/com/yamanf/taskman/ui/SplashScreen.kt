package com.yamanf.taskman.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yamanf.taskman.MainActivity
import com.yamanf.taskman.databinding.ActivitySplashScreenBinding
import com.yamanf.taskman.ui.auth.AuthActivity
import com.yamanf.taskman.utils.FirebaseAuthManager
import com.yamanf.taskman.ui.onboarding.OnboardingActivity

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val onBoarding: SharedPreferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE)
        val isFirstTime = onBoarding.getBoolean("firstTime",true)

        binding.splashLogo.animate().setDuration(1000).alpha(1f).withEndAction(){
            if (FirebaseAuthManager.isUserLoggedIn() && !isFirstTime){
                startActivity(Intent(this@SplashScreen,MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }else if(!FirebaseAuthManager.isUserLoggedIn() && !isFirstTime){
                startActivity(Intent(this@SplashScreen,AuthActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }else if (isFirstTime){
                val editor = onBoarding.edit()
                editor.putBoolean("firstTime",false)
                editor.apply()
                startActivity(Intent(this@SplashScreen, OnboardingActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }

        }
    }
}