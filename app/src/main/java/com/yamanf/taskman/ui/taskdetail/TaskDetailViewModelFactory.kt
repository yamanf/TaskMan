package com.yamanf.taskman.ui.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yamanf.taskman.firebase.FirebaseRepository

class TaskDetailViewModelFactory (private val firebaseRepository: FirebaseRepository):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskDetailViewModel(firebaseRepository) as T
    }
}