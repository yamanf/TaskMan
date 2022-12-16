package com.yamanf.taskman.utils

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreManager {
    companion object{
       fun createNewWorkspace(title:String, result: (Boolean) -> Unit){
           lateinit var uid :String
           FirebaseAuthManager.getUserUid {
               uid = it
           }
           val uids: ArrayList<String> = arrayListOf(uid)
            Firebase.firestore.collection(Constants.Firestore.WORKSPACE.workspace).document().set(
                mapOf(
                    "title" to title,
                    "uids" to uids
                )
            ).addOnSuccessListener {
                result(true)
            }.addOnFailureListener {
                result(false)
            }
       }
    }
}