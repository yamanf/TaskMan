package com.yamanf.taskman.ui.updatetask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yamanf.taskman.firebase.FirebaseRepository
import com.yamanf.taskman.ui.newtask.NewTaskViewModel

class UpdateTaskViewModelFactory (private val firebaseRepository: FirebaseRepository):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UpdateTaskViewModel(firebaseRepository) as T
    }
}