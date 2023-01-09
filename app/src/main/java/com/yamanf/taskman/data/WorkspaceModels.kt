package com.yamanf.taskman.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


data class WorkspaceModel(
    val workspaceId:String= "",
    val title:String= "",
    val createdAt: Timestamp? = null,
    val uids:ArrayList<String> = arrayListOf()
    )
