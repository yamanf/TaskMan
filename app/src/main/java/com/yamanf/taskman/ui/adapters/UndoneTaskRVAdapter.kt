package com.yamanf.taskman.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.utils.Extensions.dateFormatter
import com.yamanf.taskman.utils.FirestoreManager
import java.util.*

class UndoneTaskRVAdapter(private var undoneTaskList: List<TaskModel>) :
    RecyclerView.Adapter<UndoneTaskRVAdapter.UndoneTaskListViewHolder>() {

    class UndoneTaskListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cbIsDone = view.findViewById<CheckBox>(R.id.cbTaskDone)
        private val tvTaskTitle = view.findViewById<TextView>(R.id.tvUndoneTaskTitle)
        private val tvTaskTime = view.findViewById<Button>(R.id.tvUndoneTaskTime)
        private val tvTaskDescription = view.findViewById<TextView>(R.id.tvUndoneTaskDescription)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bindItems(undoneTask: TaskModel) {
            if (undoneTask.taskTime != null) {
                tvTaskTime.visibility = View.VISIBLE
                tvTaskTime.isClickable = false
                tvTaskTime.text = undoneTask.taskTime?.dateFormatter()
            } else tvTaskTime.visibility = View.GONE

            if (undoneTask.description.isNotBlank()){
                tvTaskDescription.visibility =View.VISIBLE
                tvTaskDescription.text =  undoneTask.description
            }else tvTaskDescription.visibility = View.GONE

            tvTaskTitle.text = undoneTask.title
            cbIsDone.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                   FirestoreManager.changeTaskDoneStatus(undoneTask){
                       if (it){
                       }
                   }
                }
            }

        }
    }

    fun MyAdapter(data: List<TaskModel>) {
        undoneTaskList = data
    }

    fun refreshData(newData: List<TaskModel>) {
        this.undoneTaskList = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UndoneTaskListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_undone_task_item, parent, false)
        return UndoneTaskListViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: UndoneTaskListViewHolder, position: Int) {
        holder.bindItems(undoneTaskList[position])
    }

    override fun getItemCount(): Int {
        return undoneTaskList.size
    }
}