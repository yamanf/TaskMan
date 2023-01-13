package com.yamanf.taskman.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.ui.home.HomeFragmentDirections
import com.yamanf.taskman.ui.workspace.WorkspaceFragmentDirections
import com.yamanf.taskman.utils.*
import java.util.*

class TaskRVAdapter(private var undoneTaskList: List<TaskModel>) :
    RecyclerView.Adapter<TaskRVAdapter.UndoneTaskListViewHolder>() {

    inner class UndoneTaskListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cbIsDone = view.findViewById<CheckBox>(R.id.cbTaskDone)
        private val tvTaskTitle = view.findViewById<TextView>(R.id.tvUndoneTaskTitle)
        private val btnDrawer = view.findViewById<ImageView>(R.id.ivTaskDrawer)
        private val tvTaskTime = view.findViewById<TextView>(R.id.tvUndoneTaskTime)
        private val tvTaskDescription = view.findViewById<TextView>(R.id.tvUndoneTaskDescription)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bindItems(undoneTask: TaskModel) {
            if (undoneTask.taskTime != null) {
                tvTaskTime.visibility = View.VISIBLE
                tvTaskTime.isClickable = false
                tvTaskTime.text = undoneTask.taskTime?.dateFormatter()
            } else tvTaskTime.visibility = View.GONE

            tvTaskTitle.text = undoneTask.title
            if (undoneTask.isExpanded && undoneTask.description.isNotBlank()) {
                tvTaskDescription.visible()
                tvTaskTitle.maxLines = Constants.MAX_TASK_TITLE_LINE
                tvTaskDescription.text = undoneTask.description
            } else {
                tvTaskDescription.gone()
                tvTaskTitle.maxLines = Constants.MIN_TASK_TITLE_LINE
            }

            tvTaskTitle.setOnClickListener(){
                it.findNavController().navigate(WorkspaceFragmentDirections.actionWorkspaceFragmentToTaskDetailFragment(undoneTask.taskId,undoneTask.workspaceId))
            }

            cbIsDone.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    FirestoreManager.changeTaskDoneStatus(undoneTask,true) {
                        if (it) {
                        }
                    }
                }
            }

            if (undoneTask.description.isNotEmpty()){
                btnDrawer.visible()
                btnDrawer.setOnClickListener(){
                    onItemClicked(undoneTask)
                }
            }else if (undoneTask.description.isEmpty()){
                btnDrawer.gone()
            }

            if (undoneTask.isExpanded){
                btnDrawer.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            }else if (!undoneTask.isExpanded){
                btnDrawer.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
            }

        }
         private fun onItemClicked(item:TaskModel){
            item.isExpanded = !item.isExpanded
            notifyItemChanged(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UndoneTaskListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rv_undone_task_item, parent, false)
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