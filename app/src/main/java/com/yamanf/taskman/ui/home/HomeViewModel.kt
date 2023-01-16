package com.yamanf.taskman.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.data.WorkspaceModel
import com.yamanf.taskman.firebase.FirebaseRepository
import com.yamanf.taskman.utils.Constants
import java.util.*
import kotlin.collections.ArrayList

class HomeViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    private val TAG = "[DEBUG] HomeViewModel"

    private lateinit var userUid: String

    private var _workspaceListLiveData = MutableLiveData<List<WorkspaceModel>?>()
    val workspaceListLiveData: LiveData<List<WorkspaceModel>?>
        get() = _workspaceListLiveData

    private var _isSearchActiveLiveData = MutableLiveData<Boolean>()
    val isSearchActiveLiveData: MutableLiveData<Boolean>
        get() = _isSearchActiveLiveData

    private var _searchTaskLiveData = MutableLiveData<ArrayList<TaskModel>>()
    val searchTaskLiveData: MutableLiveData<ArrayList<TaskModel>>
        get() = _searchTaskLiveData

    init {
        getUserUid()
        getUserWorkspaces()
        _isSearchActiveLiveData.value = false
    }

    fun changeIsSearchActive() {
        _isSearchActiveLiveData.value = true
    }

    fun changeIsSearchInActive() {
        _isSearchActiveLiveData.value = false
    }

    private fun getUserUid() {
        userUid = firebaseRepository.getCurrentUserId().toString()
    }

    fun getUserWorkspaces() {
        val workspaceList = ArrayList<WorkspaceModel>()
        firebaseRepository.getAllWorkspaces()
            .whereArrayContains("uids", userUid)
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val workspace = document.toObject(WorkspaceModel::class.java)
                    workspaceList.add(workspace)
                }
                _workspaceListLiveData.value = workspaceList
                Log.d(TAG, "getUserWorkspaces: success")
            }.addOnFailureListener {
                Log.d(TAG, "getUserWorkspaces: failed")
            }
    }

    fun createNewWorkspace(newWorkspace: WorkspaceModel, result: (Boolean) -> Unit) {
        firebaseRepository.createWorkspace(newWorkspace) {
            if (it) {
                Log.d(TAG, "createNewWorkspace: success")
                return@createWorkspace result(true)
            } else {
                Log.d(TAG, "createNewWorkspace: failed")
                return@createWorkspace result(false)
            }
        }
    }

    fun getSearchTasks() {
        val taskList = ArrayList<TaskModel>()
        firebaseRepository.getAllTasks()
            .whereArrayContains("uids", userUid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val documents = snapshot.documents
                    documents.forEach {
                        val searchTask = it.toObject(TaskModel::class.java)
                        taskList.add(searchTask!!)
                    }
                }
                println(taskList)
                _searchTaskLiveData.value = taskList
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseRepository.getCurrentUser()
    }

    fun updateUsername(username: String, result: (Boolean) -> Unit) {
        firebaseRepository.updateDisplayName(username) {
            result(it)
        }
    }

    fun logOut() {
        firebaseRepository.logOut()
    }

    fun deleteUser(result: (Boolean) -> Unit) {
        firebaseRepository.deleteUser {
            return@deleteUser result(it)
        }
    }
}