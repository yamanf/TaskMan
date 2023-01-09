package com.yamanf.taskman.ui.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yamanf.taskman.firebase.FirebaseRepository


class WorkspaceViewModelFactory (private val firebaseRepository: FirebaseRepository):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WorkspaceViewModel(firebaseRepository) as T
    }
}