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
            Firebase.firestore.collection(Constants.Firestore.USERDATA.collectionNames).document(uid).set(
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
            Firebase.firestore.collection(Constants.Firestore.WORKSPACE.collectionNames).add(
                mapOf(
                    Constants.Firestore.TITLE.collectionNames to title,
                )
            ).addOnSuccessListener {
                Firebase.firestore.collection(Constants.Firestore.WORKSPACE.collectionNames).document(it.id).set(
                    hashMapOf(
                        Constants.Firestore.WORKSPACEID.collectionNames to it.id,
                        Constants.Firestore.CREATEDAT.collectionNames to createdAt,
                        Constants.Firestore.TITLE.collectionNames to title,
                        Constants.Firestore.UIDS.collectionNames to uids,
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

        fun createNewTask(task:TaskModel, result: (Boolean) -> Unit){
            Firebase.firestore.collection(Constants.Firestore.TASKS.collectionNames).add(
                mapOf(
                    Constants.Firestore.TITLE.collectionNames to task.title,
                )
            ).addOnSuccessListener {
                Firebase.firestore.collection(Constants.Firestore.TASKS.collectionNames).document(it.id).set(
                    mapOf(
                        Constants.Firestore.TASKID.collectionNames to it.id,
                        Constants.Firestore.TITLE.collectionNames to task.title,
                        Constants.Firestore.DESCRIPTION.collectionNames to task.description,
                        Constants.Firestore.TASKTIME.collectionNames to task.taskTime,
                        Constants.Firestore.CREATEDAT.collectionNames to task.createdAt,
                        Constants.Firestore.ISDONE.collectionNames to task.isDone,
                        Constants.Firestore.ISIMPORTANT.collectionNames to task.isImportant,
                        Constants.Firestore.WORKSPACEID.collectionNames to task.workspaceId
                    )
                )
                result(true)
            }.addOnFailureListener {
                result(false)
            }
        }

        fun getWorkspaceNameFromId(workspaceId: String, workspaceName:(String)->Unit) {
            Firebase.firestore.collection(Constants.Firestore.WORKSPACE.collectionNames).document(workspaceId)
                .get()
                .addOnSuccessListener {
                    val workspaceModel = it.toObject<WorkspaceModel>()
                     val workspaceName = workspaceModel!!.title.toString()
                    return@addOnSuccessListener workspaceName(workspaceName)
                }.addOnFailureListener{
                    println("fail")
                }
        }
/*
        fun getUnDoneTasksFromWorkspaceLive(workspaceId:String undoneTaskList:(ArrayList<TaskModel>)->Unit){
            val undoneTaskList = ArrayList<TaskModel>()
            Firebase.firestore.collection("tasks")
                .whereEqualTo(Constants.Firestore.WORKSPACEID.collectionNames,workspaceId)
                .whereEqualTo(Constants.Firestore.ISDONE.collectionNames,false)
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

 */



        fun getUnDoneTasksFromWorkspace(workspaceId:String, undoneTaskList:(ArrayList<TaskModel>) -> Unit){
            val undoneTaskList = ArrayList<TaskModel>()
            Firebase.firestore.collection("tasks")
                .whereEqualTo(Constants.Firestore.WORKSPACEID.collectionNames,workspaceId)
                .whereEqualTo(Constants.Firestore.ISDONE.collectionNames,false)
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
            Firebase.firestore.collection(Constants.Firestore.TASKS.collectionNames).document(task.taskId).set(
                mapOf(
                    Constants.Firestore.TASKID.collectionNames to task.taskId,
                    Constants.Firestore.TITLE.collectionNames to task.title,
                    Constants.Firestore.DESCRIPTION.collectionNames to task.description,
                    Constants.Firestore.TASKTIME.collectionNames to task.taskTime,
                    Constants.Firestore.CREATEDAT.collectionNames to task.createdAt,
                    Constants.Firestore.ISDONE.collectionNames to true,
                    Constants.Firestore.ISIMPORTANT.collectionNames to task.isImportant,
                    Constants.Firestore.WORKSPACEID.collectionNames to task.workspaceId
                )
            ).addOnSuccessListener {
                result(true)
            }.addOnFailureListener {
                result(false)
            }
        }

    }
}