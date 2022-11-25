package com.yamanf.taskman.ui.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthResult
import com.yamanf.taskman.utils.FirebaseManager
import com.yamanf.taskman.utils.LoginModel
import com.yamanf.taskman.utils.RegisterModel

class AuthViewModel : ViewModel() {

    fun registerWithEmail(registerModel: RegisterModel, success: (Boolean) -> Unit, failure: (String) -> Unit) {
        FirebaseManager.registerWithEmail(registerModel, {
            success(true)
        },{
            failure(it)
        })
    }
    fun loginWithEmail(loginModel: LoginModel, success: (AuthResult) -> Unit, failure: (String) -> Unit) {
        FirebaseManager.logInWithEmail(loginModel,{
            success(it)
        },{
            failure(it)
        })
    }

}