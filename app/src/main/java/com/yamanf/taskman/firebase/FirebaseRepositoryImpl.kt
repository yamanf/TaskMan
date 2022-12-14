package com.yamanf.taskman.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.data.WorkspaceModel
import com.yamanf.taskman.utils.Constants
import com.yamanf.taskman.utils.FirebaseAuthManager

class FirebaseRepositoryImpl: FirebaseRepository {

    private val TAG = "[DEBUG] FirebaseInteractorImpl"
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun getCurrentUserId() = firebaseAuth.currentUser?.uid
    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
    override fun getFirebaseAuthInstance(): FirebaseAuth = FirebaseAuth.getInstance()
    override fun getFirestoreInstance(): FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun getAllWorkspaces(): CollectionReference {
        return firestore.collection(Constants.WORKSPACE)
    }

    override fun createWorkspace(workspace: WorkspaceModel,result:(Boolean) -> Unit) {
        firestore.collection(Constants.WORKSPACE).add(
            mapOf(Constants.TITLE to workspace.title)
        ).addOnSuccessListener {
            var uid = FirebaseAuthManager.getUserUid()
            val uids: ArrayList<String> = arrayListOf(uid)
            Firebase.firestore.collection(Constants.WORKSPACE).document(it.id).set(
                hashMapOf(
                    Constants.WORKSPACE_ID to it.id,
                    Constants.CREATED_AT to workspace.createdAt,
                    Constants.TITLE to workspace.title,
                    Constants.UIDS to uids,
                )
            )
            Log.d(TAG, "createWorkspace: successfully")
            return@addOnSuccessListener result(true)
        }.addOnFailureListener {
            Log.d(TAG, "createWorkspace: failed "+it.message)
            return@addOnFailureListener result(false)
        }
    }

    override fun updateWorkspace(workspace: WorkspaceModel,result:(Boolean) -> Unit) {
        firestore.collection(Constants.WORKSPACE)
            .document(workspace.workspaceId)
            .set(
            hashMapOf(
                Constants.WORKSPACE_ID to workspace.workspaceId,
                Constants.CREATED_AT to workspace.createdAt,
                Constants.TITLE to workspace.title,
                Constants.UIDS to workspace.uids,
            )
        ).addOnSuccessListener {
            Log.d(TAG, "createWorkspace: successfully")
            return@addOnSuccessListener result(true)
        }.addOnFailureListener {
            Log.d(TAG, "createWorkspace: failed "+it.message)
            return@addOnFailureListener result(false)
        }
    }

    override fun deleteWorkspace(workspaceId:String,result:(Boolean) -> Unit) {
        firestore.collection(Constants.TASKS)
            .whereEqualTo(Constants.WORKSPACE_ID,workspaceId)
            .get()
            .addOnSuccessListener { documents->
                for (document in documents){
                    document.reference.delete()
                }
                firestore.collection(Constants.WORKSPACE).document(workspaceId).delete()
                return@addOnSuccessListener result(true)
            }.addOnFailureListener{
                return@addOnFailureListener result(false)
            }
    }

    override fun getAllTasks(): CollectionReference {
        return firestore.collection(Constants.TASKS)
    }

    override fun addTaskToWorkspace(task: TaskModel,result:(Boolean) -> Unit) {
        firestore.collection(Constants.TASKS).add(
            mapOf(Constants.TITLE to task.title)
        ).addOnSuccessListener {
            var uid = FirebaseAuthManager.getUserUid()
            val uids: ArrayList<String> = arrayListOf(uid)
            firestore.collection(Constants.TASKS).document(it.id).set(
                mapOf(
                    Constants.TASK_ID to it.id,
                    Constants.TITLE to task.title,
                    Constants.DESCRIPTION to task.description,
                    Constants.TASK_TIME to task.taskTime,
                    Constants.CREATED_AT to task.createdAt,
                    Constants.IS_DONE to task.isDone,
                    Constants.IS_IMPORTANT to task.isImportant,
                    Constants.WORKSPACE_ID to task.workspaceId,
                    Constants.UIDS to uids
                )
            )
            Log.d(TAG, "addTaskToWorkspace: successfully")
            return@addOnSuccessListener result(true)
        }.addOnFailureListener {
            Log.d(TAG, "addTaskToWorkspace: failed")
            return@addOnFailureListener result(false)
        }
    }

    override fun changeIsCompleted(documentSnapshot: DocumentSnapshot, isCompleted: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun changeIsImportant(documentSnapshot: DocumentSnapshot, isImportant: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateTask(task: TaskModel,result:(Boolean) -> Unit) {
        firestore.collection(Constants.TASKS).document(task.taskId)
            .set(
                mapOf(
                Constants.TASK_ID to task.taskId,
                Constants.TITLE to task.title,
                Constants.DESCRIPTION to task.description,
                Constants.TASK_TIME to task.taskTime,
                Constants.CREATED_AT to task.createdAt,
                Constants.IS_DONE to task.isDone,
                Constants.IS_IMPORTANT to task.isImportant,
                Constants.WORKSPACE_ID to task.workspaceId,
                Constants.UIDS to task.uids
            )).addOnSuccessListener {
                return@addOnSuccessListener result(true)
            }.addOnFailureListener{
                return@addOnFailureListener result(false)
            }
    }

    override fun deleteTask(taskId: String,result:(Boolean) -> Unit) {
        firestore.collection(Constants.TASKS).document(taskId).delete().addOnSuccessListener {
            return@addOnSuccessListener result (true)
        }.addOnFailureListener {
            return@addOnFailureListener result (false)
        }
    }

    override fun undoDelete(documentSnapshot: DocumentSnapshot): Boolean {
        TODO("Not yet implemented")
    }
}