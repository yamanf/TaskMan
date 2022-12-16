package com.yamanf.taskman.data

data class RegisterModel(val eMail:String = "",val password:String = "",val passwordRepeat:String = "", val cbTerms:Boolean= false)
data class LoginModel(val eMail:String= "",val password:String= "",val cbRemember: Boolean = false)