package com.yamanf.taskman.ui.splash

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yamanf.taskman.firebase.FirebaseRepository

class SplashScreenViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    fun isUserLoggedIn():Boolean{
        return firebaseRepository.isUserLoggedIn()
    }
}