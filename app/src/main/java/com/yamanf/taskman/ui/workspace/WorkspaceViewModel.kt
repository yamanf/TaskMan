package com.yamanf.taskman.ui.workspace

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.data.WorkspaceModel
import com.yamanf.taskman.firebase.FirebaseRepository
import com.yamanf.taskman.utils.Constants


class WorkspaceViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    private val TAG = "[DEBUG] WorkspaceViewModel"

    private var _unDoneTaskLiveData = MutableLiveData<ArrayList<TaskModel>>()
    val unDoneTaskLiveData: MutableLiveData<ArrayList<TaskModel>>
        get() = _unDoneTaskLiveData

    private var _workspaceDetailsLiveData = MutableLiveData<WorkspaceModel>()
    val workspaceDetailsLiveData: MutableLiveData<WorkspaceModel>
        get() = _workspaceDetailsLiveData



    fun getUnDoneTasksFromWorkspace(workspaceId:String) {
        firebaseRepository.getAllTasks()
            .whereEqualTo(Constants.WORKSPACE_ID,workspaceId)
            .whereEqualTo(Constants.IS_DONE,false)
            .addSnapshotListener {snapshot, e ->
                if (e != null){
                    Log.w(TAG,"Listen failed",e)
                    return@addSnapshotListener
                }
                if (snapshot != null){
                    val unDoneTaskList = ArrayList<TaskModel>()
                    val documents = snapshot.documents
                    documents.forEach{
                        val undoneTask =  it.toObject(TaskModel::class.java)
                        unDoneTaskList.add(undoneTask!!)
                    }
                    _unDoneTaskLiveData.value = unDoneTaskList
                }
            }
    }

    fun addTaskToWorkspace(task: TaskModel, result:(Boolean) -> Unit) {
        firebaseRepository.addTaskToWorkspace(task){
            return@addTaskToWorkspace result(it)
        }
    }

    fun getWorkspaceDetails(workspaceId: String){
        firebaseRepository.getAllWorkspaces()
            .document(workspaceId)
            .addSnapshotListener{ snapshot, e ->
                if (e != null){
                    Log.w(TAG,"Listen failed",e)
                    return@addSnapshotListener
                }
                if (snapshot != null){
                    val workspaceModel = snapshot.toObject<WorkspaceModel>()
                    _workspaceDetailsLiveData.value = workspaceModel!!
                }
            }
    }

    fun updateWorkspace(workspaceModel: WorkspaceModel, result:(Boolean)->Unit){
        firebaseRepository.updateWorkspace(workspaceModel){
            Log.d(TAG, "updateWorkspace")
            return@updateWorkspace result(it)
        }
    }



}