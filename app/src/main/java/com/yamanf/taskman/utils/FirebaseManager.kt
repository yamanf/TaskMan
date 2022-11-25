package com.yamanf.taskman.utils

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.Serializable


data class UserModel(val uuid:String = "",val email:String = "",val username:String = "")
data class RegisterModel(val eMail:String = "",val password:String = "",val passwordRepeat:String = "", val cbTerms:Boolean= false)
data class LoginModel(val eMail:String= "",val password:String= "",val cbRemember: Boolean = false)
class FirebaseManager {

    companion object{

        fun registerWithEmail(registerModel: RegisterModel, success: (AuthResult) -> Unit, failure: (String) -> Unit){
            if(registerModel.cbTerms){
                if(registerModel.eMail== ""||registerModel.password== ""||registerModel.passwordRepeat== ""){
                    failure("Please fill the areas.")
                }else{
                    if (registerModel.password == registerModel.passwordRepeat){
                        Firebase.auth.createUserWithEmailAndPassword(registerModel.eMail, registerModel.password).addOnSuccessListener {
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

    }


}