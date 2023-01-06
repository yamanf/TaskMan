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

       fun createNewWorkspace(newWorkSpace:WorkspaceModel, result: (Boolean) -> Unit){
           val title = newWorkSpace.title
           val createdAt = newWorkSpace.createdAt
           var uid = FirebaseAuthManager.getUserUid()
           val uids: ArrayList<String> = arrayListOf(uid)
            Firebase.firestore.collection(Constants.WORKSPACE).add(
                mapOf(
                    Constants.TITLE to title,
                )
            ).addOnSuccessListener {
                Firebase.firestore.collection(Constants.WORKSPACE).document(it.id).set(
                    hashMapOf(
                        Constants.WORKSPACE_ID to it.id,
                        Constants.CREATED_AT to createdAt,
                        Constants.TITLE to title,
                        Constants.UIDS to uids,
                    )
                )
                result(true)
            }.addOnFailureListener {
                result(false)
            }
       }

        fun getUserWorkspaces(workspaceList:(ArrayList<WorkspaceModel>) -> Unit){
            var uid = FirebaseAuthManager.getUserUid()
            val workspaceList = ArrayList<WorkspaceModel>()
            Firebase.firestore.collection(Constants.WORKSPACE)
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

        fun createNewTask(task:TaskModel, result: (Boolean) -> Unit){
            Firebase.firestore.collection(Constants.TASKS).add(
                mapOf(
                    Constants.TITLE to task.title,
                )
            ).addOnSuccessListener {
                Firebase.firestore.collection(Constants.TASKS).document(it.id).set(
                    mapOf(
                        Constants.TASK_ID to it.id,
                        Constants.TITLE to task.title,
                        Constants.DESCRIPTION to task.description,
                        Constants.TASK_TIME to task.taskTime,
                        Constants.CREATED_AT to task.createdAt,
                        Constants.IS_DONE to task.isDone,
                        Constants.IS_IMPORTANT to task.isImportant,
                        Constants.WORKSPACE_ID to task.workspaceId
                    )
                )
                result(true)
            }.addOnFailureListener {
                result(false)
            }
        }

        fun getWorkspaceNameFromId(workspaceId: String, workspaceName:(String)->Unit) {
            Firebase.firestore.collection(Constants.WORKSPACE).document(workspaceId)
                .get()
                .addOnSuccessListener {
                    val workspaceModel = it.toObject<WorkspaceModel>()
                     val workspaceName = workspaceModel!!.title.toString()
                    return@addOnSuccessListener workspaceName(workspaceName)
                }.addOnFailureListener{
                    println("fail")
                }
        }

        fun getUnDoneTasksFromWorkspace(workspaceId:String, undoneTaskList:(ArrayList<TaskModel>) -> Unit){
            val undoneTaskList = ArrayList<TaskModel>()
            Firebase.firestore.collection("tasks")
                .whereEqualTo(Constants.WORKSPACE_ID,workspaceId)
                .whereEqualTo(Constants.IS_DONE,false)
                .get()
                .addOnSuccessListener {
                    for (document in it){
                        val undoneTask =  document.toObject(TaskModel::class.java)
                        undoneTaskList.add(undoneTask)
                    }
                    return@addOnSuccessListener undoneTaskList(undoneTaskList)
                }.addOnFailureListener{
                    println("fail")
                }
        }

        fun changeTaskDoneStatus(task:TaskModel, result: (Boolean) -> Unit){
            Firebase.firestore.collection(Constants.TASKS).document(task.taskId).set(
                mapOf(
                    Constants.TASK_ID to task.taskId,
                    Constants.TITLE to task.title,
                    Constants.DESCRIPTION to task.description,
                    Constants.TASK_TIME to task.taskTime,
                    Constants.CREATED_AT to task.createdAt,
                    Constants.IS_DONE to true,
                    Constants.IS_IMPORTANT to task.isImportant,
                    Constants.WORKSPACE_ID to task.workspaceId
                )
            ).addOnSuccessListener {
                result(true)
            }.addOnFailureListener {
                result(false)
            }
        }

    }
}