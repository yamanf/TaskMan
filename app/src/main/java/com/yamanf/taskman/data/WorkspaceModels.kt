package com.yamanf.taskman.data

import com.google.firebase.Timestamp


data class WorkspaceModel(
    val workspaceId:String= "",
    val title:String= "",
    val createdAt: Timestamp? = null,
    val uids:ArrayList<String> = arrayListOf()
    )
