package com.yamanf.taskman.ui.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthResult
import com.yamanf.taskman.data.LoginModel
import com.yamanf.taskman.data.RegisterModel
import com.yamanf.taskman.firebase.FirebaseRepository

class AuthViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    fun registerWithEmail(
        registerModel: RegisterModel,
        success: (Boolean) -> Unit,
        failure: (String) -> Unit
    ) {
        firebaseRepository.registerWithEmail(registerModel, {
            success(true)
        }, {
            failure(it)
        })
    }

    fun loginWithEmail(
        loginModel: LoginModel,
        success: (AuthResult) -> Unit,
        failure: (String) -> Unit
    ) {
        firebaseRepository.logInWithEmail(loginModel, {
            success(it)
        }, {
            failure(it)
        })
    }

}