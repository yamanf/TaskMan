package com.yamanf.taskman.firebase

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yamanf.taskman.data.LoginModel
import com.yamanf.taskman.data.RegisterModel
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.data.WorkspaceModel
import com.yamanf.taskman.utils.Constants
import com.yamanf.taskman.utils.FirestoreManager

class FirebaseRepositoryImpl : FirebaseRepository {

    private val TAG = "[DEBUG] FirebaseInteractorImpl"
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun getCurrentUserId() = firebaseAuth.currentUser?.uid.toString()
    override fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
    override fun getFirebaseAuthInstance(): FirebaseAuth = FirebaseAuth.getInstance()
    override fun getFirestoreInstance(): FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun registerWithEmail(
        registerModel: RegisterModel,
        success: (AuthResult) -> Unit,
        failure: (String) -> Unit
    ) {
        if (registerModel.eMail == "" || registerModel.password == "" || registerModel.passwordRepeat == "") {
            failure("Please fill the areas.")
        } else {
            if (registerModel.password == registerModel.passwordRepeat) {
                if (registerModel.cbTerms) {
                    firebaseAuth.createUserWithEmailAndPassword(
                        registerModel.eMail,
                        registerModel.password
                    ).addOnSuccessListener {authResult->
                        saveUserEmail(authResult.user!!.uid, registerModel.eMail)
                        val user = getCurrentUser()
                        user!!.sendEmailVerification()
                            .addOnCompleteListener {
                            createFirstWorkspace() {
                                if (it){
                                    success(authResult)
                                }else failure("First workspace couldn't created.")
                            }
                        }
                    }.addOnFailureListener {
                        failure(it.localizedMessage!!.toString())
                    }
                } else failure("You should accept the terms.")
            } else failure("Passwords should be same.")
        }
    }

    override fun logInWithEmail(
        loginModel: LoginModel,
        success: (AuthResult) -> Unit,
        failure: (String) -> Unit
    ) {
        if(loginModel.eMail== ""||loginModel.password== ""){
            failure("Please fill the areas.")
        }else{
            firebaseAuth.signInWithEmailAndPassword(loginModel.eMail, loginModel.password).addOnSuccessListener {
                success(it)
            } .addOnFailureListener {
                failure(it.localizedMessage!!.toString())
            }
        }
    }

    override fun saveUserEmail(uid:String, eMail:String){
        firestore.collection(Constants.USER_DATA).document(uid).set(
            mapOf(
                "eMail" to eMail
            )
        )
    }


    override fun updateDisplayName(username: String, result: (Boolean) -> Unit) {
        val user = getCurrentUser()
        val profileUpdates = userProfileChangeRequest {
            displayName = username
        }
        user!!.updateProfile(profileUpdates).addOnSuccessListener{
            return@addOnSuccessListener result(true)
        }.addOnFailureListener {
            return@addOnFailureListener result(false)
        }
    }

    override fun isUserLoggedIn():Boolean{
        return Firebase.auth.currentUser != null
    }

    override fun logOut() {
        Firebase.auth.signOut()
    }


    override fun getAllWorkspaces(): CollectionReference {
        return firestore.collection(Constants.WORKSPACE)
    }

    override fun createWorkspace(workspace: WorkspaceModel, result: (Boolean) -> Unit) {
        firestore.collection(Constants.WORKSPACE).add(
            mapOf(Constants.TITLE to workspace.title)
        ).addOnSuccessListener {
            var uid = getCurrentUserId()
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
            Log.d(TAG, "createWorkspace: failed " + it.message)
            return@addOnFailureListener result(false)
        }
    }

    override fun createFirstWorkspace(result: (Boolean) -> Unit) {
        firestore.collection(Constants.WORKSPACE).add(
            mapOf(Constants.TITLE to Constants.DAILY_TASKS)
        ).addOnSuccessListener {
            val uid = getCurrentUserId()
            val uids: ArrayList<String> = arrayListOf(uid)
            Firebase.firestore.collection(Constants.WORKSPACE).document(it.id).set(
                hashMapOf(
                    Constants.WORKSPACE_ID to it.id,
                    Constants.CREATED_AT to Timestamp.now(),
                    Constants.TITLE to Constants.DAILY_TASKS,
                    Constants.UIDS to uids,
                )
            )
            Log.d(TAG, "createFirstWorkspace: successfully")
            return@addOnSuccessListener result(true)
        }.addOnFailureListener {
            Log.d(TAG, "createFirstWorkspace: failed " + it.message)
            return@addOnFailureListener result(false)
        }
    }

    override fun updateWorkspace(workspace: WorkspaceModel, result: (Boolean) -> Unit) {
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
                Log.d(TAG, "createWorkspace: failed " + it.message)
                return@addOnFailureListener result(false)
            }
    }

    override fun deleteWorkspace(workspaceId: String, result: (Boolean) -> Unit) {
        firestore.collection(Constants.TASKS)
            .whereEqualTo(Constants.WORKSPACE_ID, workspaceId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
                firestore.collection(Constants.WORKSPACE).document(workspaceId).delete()
                return@addOnSuccessListener result(true)
            }.addOnFailureListener {
                return@addOnFailureListener result(false)
            }
    }

    override fun getAllTasks(): CollectionReference {
        return firestore.collection(Constants.TASKS)
    }

    override fun addTaskToWorkspace(task: TaskModel, result: (Boolean) -> Unit) {
        firestore.collection(Constants.TASKS).add(
            mapOf(Constants.TITLE to task.title)
        ).addOnSuccessListener {
            var uid = getCurrentUserId()
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

    override fun changeIsCompleted(
        documentSnapshot: DocumentSnapshot,
        isCompleted: Boolean
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun changeIsImportant(
        documentSnapshot: DocumentSnapshot,
        isImportant: Boolean
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateTask(task: TaskModel, result: (Boolean) -> Unit) {
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
                )
            ).addOnSuccessListener {
                return@addOnSuccessListener result(true)
            }.addOnFailureListener {
                return@addOnFailureListener result(false)
            }
    }

    override fun deleteTask(taskId: String, result: (Boolean) -> Unit) {
        firestore.collection(Constants.TASKS).document(taskId).delete().addOnSuccessListener {
            return@addOnSuccessListener result(true)
        }.addOnFailureListener {
            return@addOnFailureListener result(false)
        }
    }

    override fun undoDelete(documentSnapshot: DocumentSnapshot): Boolean {
        TODO("Not yet implemented")
    }
}