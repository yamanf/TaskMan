package com.yamanf.taskman.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yamanf.taskman.firebase.FirebaseRepository

class SplashScreenViewModelFactory (private val firebaseRepository: FirebaseRepository):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SplashScreenViewModel(firebaseRepository) as T
    }
}