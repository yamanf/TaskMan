package com.yamanf.taskman.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

object Extensions {

    fun Timestamp?.dateFormatter(): String {
        val date = Date(this!!.seconds * 1000)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputFormat = SimpleDateFormat("HH:mm dd/MM/yyyy")
        val inputDateString = inputFormat.format(date)
        val outputDate = inputFormat.parse(inputDateString)
        val formattedTimestamp = outputFormat.format(outputDate)
        return formattedTimestamp
    }

}