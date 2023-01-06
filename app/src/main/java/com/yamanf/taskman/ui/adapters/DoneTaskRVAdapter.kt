package com.yamanf.taskman.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.utils.FirestoreManager
import com.yamanf.taskman.utils.dateFormatter

class DoneTaskRVAdapter(private var doneTaskList: List<TaskModel>) :
    RecyclerView.Adapter<DoneTaskRVAdapter.DoneTaskListViewHolder>() {

    inner class DoneTaskListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cbIsUnDone = view.findViewById<CheckBox>(R.id.cbTaskUnDone)
        private val tvTaskTitle = view.findViewById<TextView>(R.id.tvDoneTaskTitle)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bindItems(doneTask: TaskModel) {
            tvTaskTitle.text = doneTask.title
            cbIsUnDone.setOnCheckedChangeListener { buttonView, isUnChecked ->
                if (!isUnChecked) {
                    FirestoreManager.changeTaskDoneStatus(doneTask,false) {
                        if (it) {

                        }
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoneTaskListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rv_done_task_item, parent, false)
        return DoneTaskListViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DoneTaskListViewHolder, position: Int) {
        holder.bindItems(doneTaskList[position])
    }

    override fun getItemCount(): Int {
        return doneTaskList.size
    }

}