package com.yamanf.taskman.data

import com.google.firebase.Timestamp

data class TaskModel(
    val title:String? = null,
    val taskId:String= "",
    val description:String= "",
    val taskTime: Timestamp? = null,
    val createdAt: Timestamp? = null,
    val isDone: Boolean = false,
    val isImportant: Boolean = false,
    val workspaceId: String = ""
)