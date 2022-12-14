package com.yamanf.taskman.utils

class Constants {
    companion object{
        const val WORKSPACE = "workspace"
        const val TASKS = "tasks"
        const val USER_DATA = "userdata"
        const val UIDS = "uids"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val TASK_TIME = "taskTime"
        const val CREATED_AT = "createdAt"
        const val IS_DONE = "isDone"
        const val IS_IMPORTANT = "isImportant"
        const val WORKSPACE_ID = "workspaceId"
        const val TASK_ID = "taskId"
    }

    /*
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

     */
}