package com.yamanf.taskman.utils

class Constants {
    enum class Firestore(val collectionNames:String) {
        WORKSPACE("workspace"),
        TASKS("tasks"),
        USERDATA("userdata"),
        UIDS("uids"),
        TITLE("title"),
        DESCRIPTION("description"),
        TASKTIME("taskTime"),
        CREATEDAT("createdAt"),
        ISDONE("isDone"),
        ISIMPORTANT("isImportant"),
        WORKSPACEID("workspaceId"),
        TASKID("taskId")



    }
}