package com.yamanf.taskman.ui.updatetask

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.firebase.FirebaseRepository

class UpdateTaskViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

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

    fun updateTask(taskModel: TaskModel,result: (Boolean) -> Unit ){
        firebaseRepository.updateTask(taskModel){
            return@updateTask result(it)
        }
    }

    fun deleteTask(taskId:String,result:(Boolean)->Unit){
        firebaseRepository.deleteTask(taskId){
            return@deleteTask result(it)
        }
    }
}