package com.yamanf.taskman.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yamanf.taskman.firebase.FirebaseRepository

class HomeViewModelFactory(private val firebaseRepository: FirebaseRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(firebaseRepository) as T
    }
}