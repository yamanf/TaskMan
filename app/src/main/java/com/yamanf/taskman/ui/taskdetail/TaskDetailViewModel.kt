package com.yamanf.taskman.ui.taskdetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.toObject
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.data.WorkspaceModel
import com.yamanf.taskman.firebase.FirebaseRepository

class TaskDetailViewModel (private val firebaseRepository: FirebaseRepository) : ViewModel(){

    fun getTaskDetails(taskId: String, taskModel:(TaskModel?)-> Unit){
        firebaseRepository.getAllTasks()
            .document(taskId)
            .addSnapshotListener{ snapshot, e ->
                if (e != null){
                    return@addSnapshotListener taskModel(null)
                }
                if (snapshot != null){
                    val taskModel = snapshot.toObject<TaskModel>()
                    if (taskModel!=null){
                        return@addSnapshotListener taskModel(taskModel)
                    }
                }
            }
    }

    fun getWorkspaceDetails(workspaceId: String,workspaceModel:(WorkspaceModel)-> Unit){
        firebaseRepository.getAllWorkspaces()
            .document(workspaceId)
            .addSnapshotListener{ snapshot, e ->
                if (e != null){
                    return@addSnapshotListener
                }
                if (snapshot != null){
                    val workspaceModel = snapshot.toObject<WorkspaceModel>()
                    return@addSnapshotListener workspaceModel(workspaceModel!!)
                }
            }
    }

    fun deleteTask(taskId:String,result:(Boolean)->Unit){
        firebaseRepository.deleteTask(taskId){
            return@deleteTask result(it)
        }
    }


}