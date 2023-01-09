package com.yamanf.taskman.ui.newtask

import androidx.lifecycle.ViewModel
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.firebase.FirebaseRepository

class NewTaskViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    fun addTaskToWorkspace(task: TaskModel, result:(Boolean) -> Unit) {
        firebaseRepository.addTaskToWorkspace(task){
            return@addTaskToWorkspace result(it)
        }
    }

}