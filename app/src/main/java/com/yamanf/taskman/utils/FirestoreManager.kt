package com.yamanf.taskman.utils

import android.provider.ContactsContract.RawContacts.Data
import android.util.Log
import androidx.lifecycle.LiveData
import com.firebase.ui.auth.data.model.State
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.data.WorkspaceModel
import kotlinx.coroutines.flow.flow
import java.util.EventListener

class FirestoreManager {
    companion object{
        fun saveUserEmail(uid:String, eMail:String){
            Firebase.firestore.collection(Constants.USER_DATA).document(uid).set(
                mapOf(
                    "eMail" to eMail
                )
            )
        }

        fun changeTaskDoneStatus(task:TaskModel,isDone:Boolean, result: (Boolean) -> Unit){
            Firebase.firestore.collection(Constants.TASKS).document(task.taskId).set(
                mapOf(
                    Constants.TASK_ID to task.taskId,
                    Constants.TITLE to task.title,
                    Constants.DESCRIPTION to task.description,
                    Constants.TASK_TIME to task.taskTime,
                    Constants.CREATED_AT to task.createdAt,
                    Constants.IS_DONE to isDone,
                    Constants.IS_IMPORTANT to task.isImportant,
                    Constants.WORKSPACE_ID to task.workspaceId,
                    Constants.UIDS to task.uids
                )
            ).addOnSuccessListener {
                result(true)
            }.addOnFailureListener {
                result(false)
            }
        }

    }
}