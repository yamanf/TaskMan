package com.yamanf.taskman.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.data.WorkspaceModel

interface FirebaseRepository {

    fun getCurrentUserId(): String?
    fun getCurrentUser(): FirebaseUser?
    fun getFirebaseAuthInstance(): FirebaseAuth
    fun getFirestoreInstance(): FirebaseFirestore

    fun createWorkspace(workspace: WorkspaceModel, result:(Boolean) -> Unit)
    fun updateWorkspace(workspace: WorkspaceModel, result:(Boolean) -> Unit)
    fun deleteWorkspace(workspaceId:String,result:(Boolean) -> Unit)
    fun getAllWorkspaces(): CollectionReference

    fun getAllTasks(): CollectionReference
    fun addTaskToWorkspace(task: TaskModel, result:(Boolean) -> Unit)
    fun changeIsCompleted(documentSnapshot: DocumentSnapshot, isCompleted: Boolean): Boolean
    fun changeIsImportant(documentSnapshot: DocumentSnapshot, isImportant: Boolean): Boolean
    fun updateTask(task: TaskModel, result:(Boolean) -> Unit)
    fun deleteTask(taskId: String,result:(Boolean) -> Unit)
    fun undoDelete(documentSnapshot: DocumentSnapshot): Boolean

}