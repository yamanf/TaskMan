package com.yamanf.taskman.ui.workspace

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.data.repository.workspace.WorkspaceRepository
import com.yamanf.taskman.utils.Constants


class WorkspaceViewModel(private val workspaceRepository: WorkspaceRepository) : ViewModel() {

    val unDoneTaskList = mutableListOf<TaskModel>()

    private var _unDoneTaskLiveData = MutableLiveData<ArrayList<TaskModel>>()
    val unDoneTaskLiveData: MutableLiveData<ArrayList<TaskModel>>
        get() = _unDoneTaskLiveData

    fun getUnDoneTasksFromWorkspace(workspaceId:String, unDoneTask:(ArrayList<TaskModel>) -> Unit) {
        workspaceRepository.getAllTasks()
            .whereEqualTo(Constants.Firestore.WORKSPACEID.collectionNames,workspaceId)
            .whereEqualTo(Constants.Firestore.ISDONE.collectionNames,false)
            .addSnapshotListener {snapshot, e ->
                if (e != null){
                    Log.w(TAG,"Listen failed",e)
                    return@addSnapshotListener
                }
                if (snapshot != null){
                    val undoneTaskList = ArrayList<TaskModel>()
                    val documents = snapshot.documents
                    documents.forEach{
                        val undoneTask =  it.toObject(TaskModel::class.java)
                        undoneTaskList.add(undoneTask!!)
                        return@addSnapshotListener unDoneTask(undoneTaskList)
                    }
                }
            }
    }


}