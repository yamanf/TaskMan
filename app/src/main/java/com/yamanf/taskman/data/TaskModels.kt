package com.yamanf.taskman.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class TaskModel(
    val title:String? = null,
    val taskId:String= "",
    val description:String= "",
    val taskTime: Timestamp? = null,
    val createdAt: Timestamp = Timestamp(Date()),
    val isDone: Boolean = false,
    val isImportant: Boolean = false,
    val workspaceId: String = ""
) : Parcelable {

    @IgnoredOnParcel
    @get:Exclude
    var isExpanded: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) {
            return false
        }

        other as TaskModel

        if (title != other.title || taskId != other.taskId ||description != other.description|| taskTime != other.taskTime|| createdAt != other.createdAt|| isDone != other.isDone|| isImportant != other.isImportant|| workspaceId != other.workspaceId) {
            return false
        }

        return true
    }
}