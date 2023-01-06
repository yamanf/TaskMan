package com.yamanf.taskman.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yamanf.taskman.data.WorkspaceModel
import com.yamanf.taskman.firebase.FirebaseRepository
import com.yamanf.taskman.utils.Constants

class HomeViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    private val TAG = "[DEBUG] HomeViewModel"

    private lateinit var userUid: String

    var workspaceList = mutableListOf<WorkspaceModel>()
    private var _workspaceListLiveData = MutableLiveData<List<WorkspaceModel>?>()
    val workspaceListLiveData: LiveData<List<WorkspaceModel>?>
        get() = _workspaceListLiveData

    init {
        getUserUid()
        getUserWorkspaces()
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

    fun createNewWorkspace(newWorkspace: WorkspaceModel, result:(Boolean)->Unit) {
        firebaseRepository.createWorkspace(newWorkspace){
            if (it){
                Log.d(TAG, "createNewWorkspace: success")
                return@createWorkspace result(true)
            }
            else{
                Log.d(TAG, "createNewWorkspace: failed")
                return@createWorkspace result(false)
            }
        }
    }




}