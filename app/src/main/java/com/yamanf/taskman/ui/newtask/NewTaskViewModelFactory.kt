package com.yamanf.taskman.ui.newtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yamanf.taskman.firebase.FirebaseRepository

class NewTaskViewModelFactory (private val firebaseRepository: FirebaseRepository):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewTaskViewModel(firebaseRepository) as T
    }
}