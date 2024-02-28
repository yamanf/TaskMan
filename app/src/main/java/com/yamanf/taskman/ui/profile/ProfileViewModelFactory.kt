package com.yamanf.taskman.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yamanf.taskman.firebase.FirebaseRepository

class ProfileModelFactory(private val firebaseRepository: FirebaseRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(firebaseRepository) as T
    }
}