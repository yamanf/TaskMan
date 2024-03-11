package com.yamanf.taskman.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import com.yamanf.taskman.R


class Utils {
    companion object{
        fun showEditTextDialog(
            title:String,
            hint:String,
            positiveText:String,
            layoutInflater:LayoutInflater,
            context:Context,
            etContent : (String) -> Unit) {
            val builder = AlertDialog.Builder(context)
            val dialogLayout = layoutInflater.inflate(R.layout.edit_text_layout, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.etAlertDialog)
            with(builder){
                setTitle(title)
                editText.hint = hint
                setPositiveButton(positiveText) { _, _ ->
                    val etContent = editText.text.toString()
                    return@setPositiveButton etContent("$etContent")
                }
                setNegativeButton(R.string.cancel,){ _, _ ->
                    Toast.makeText(context,R.string.you_cancelled,Toast.LENGTH_SHORT).show()
                }
                setView(dialogLayout)
                show()
            }
        }
    }
}