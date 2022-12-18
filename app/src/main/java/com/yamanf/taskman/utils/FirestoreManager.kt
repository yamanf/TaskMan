package com.yamanf.taskman.utils

import android.provider.ContactsContract.RawContacts.Data
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.yamanf.taskman.data.WorkspaceModel
import java.util.EventListener

class FirestoreManager {
    companion object{

        fun saveUserEmail(uid:String, eMail:String){
            Firebase.firestore.collection(Constants.Firestore.USERDATA.collectionNames).document(uid).set(
                mapOf(
                    "eMail" to eMail
                )
            )
        }

       fun createNewWorkspace(title:String, result: (Boolean) -> Unit){
           var uid = FirebaseAuthManager.getUserUid()

           val uids: ArrayList<String> = arrayListOf(uid)
            Firebase.firestore.collection(Constants.Firestore.WORKSPACE.collectionNames).document().set(
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

        fun getUserWorkspaces(workspaceList:(ArrayList<WorkspaceModel>) -> Unit){
            var uid = FirebaseAuthManager.getUserUid()

            val workspaceList = ArrayList<WorkspaceModel>()
            Firebase.firestore.collection(Constants.Firestore.WORKSPACE.collectionNames)
                .whereArrayContains("uids",uid)
                .get()
                .addOnSuccessListener {
                    for (document in it){
                       val workspace = document.toObject(WorkspaceModel::class.java)
                        workspaceList.add(workspace)
                    }
                    return@addOnSuccessListener workspaceList(workspaceList)
                }.addOnFailureListener{
                println("fail")
                }
        }
    }
}