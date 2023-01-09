package com.yamanf.taskman.utils

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yamanf.taskman.data.LoginModel
import com.yamanf.taskman.data.RegisterModel


class FirebaseAuthManager {

    companion object{

        fun registerWithEmail(registerModel: RegisterModel, success: (AuthResult) -> Unit, failure: (String) -> Unit){
            if(registerModel.cbTerms){
                if(registerModel.eMail== ""||registerModel.password== ""||registerModel.passwordRepeat== ""){
                    failure("Please fill the areas.")
                }else{
                    if (registerModel.password == registerModel.passwordRepeat){
                        Firebase.auth.createUserWithEmailAndPassword(registerModel.eMail, registerModel.password).addOnSuccessListener {
                            FirestoreManager.saveUserEmail(it.user!!.uid,registerModel.eMail)
                            success(it)
                        } .addOnFailureListener {
                            failure(it.localizedMessage!!.toString())
                        }
                    }else failure ("Passwords should be same.")
                }
            } else failure("You should accept the terms.")
        }

        fun logInWithEmail(loginModel: LoginModel, success: (AuthResult) -> Unit, failure: (String) -> Unit) {
            if(loginModel.eMail== ""||loginModel.password== ""){
                failure("Please fill the areas.")
            }else{
                Firebase.auth.signInWithEmailAndPassword(loginModel.eMail, loginModel.password).addOnSuccessListener {
                    success(it)
                } .addOnFailureListener {
                    failure(it.localizedMessage!!.toString())
                }
            }
        }

        fun isUserLoggedIn():Boolean{
            return Firebase.auth.currentUser != null
        }

        fun getUserUid(): String {
            return Firebase.auth.currentUser?.uid.toString()
        }

    }
}