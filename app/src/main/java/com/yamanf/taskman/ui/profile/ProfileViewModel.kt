package com.yamanf.taskman.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.yamanf.taskman.firebase.FirebaseRepository

class ProfileViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel(){

    fun getCurrentUser(): FirebaseUser? {
        return firebaseRepository.getCurrentUser()
    }

    fun updateUsername(username: String, result: (Boolean) -> Unit) {
        firebaseRepository.updateDisplayName(username) {
            result(it)
        }
    }

    fun logOut() {
        firebaseRepository.logOut()
    }

    fun deleteUser(result: (Boolean) -> Unit) {
        firebaseRepository.deleteUser {
            return@deleteUser result(it)
        }
    }
}