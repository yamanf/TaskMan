package com.yamanf.taskman.utils

class Constants {
    enum class Firestore(val collectionNames:String) {
        WORKSPACE("workspace"),
        TASKS("tasks"),
        USERDATA("userdata")
    }
}