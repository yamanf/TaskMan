package com.yamanf.taskman.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.ui.home.HomeFragmentDirections
import com.yamanf.taskman.ui.workspace.WorkspaceFragmentDirections


class SearchRVAdapter(private var searchList: List<TaskModel>) :
    RecyclerView.Adapter<SearchRVAdapter.SearchListViewHolder>() {

    inner class SearchListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTaskTitle = view.findViewById<TextView>(R.id.tvSearchTaskTitle)
        private val container = view.findViewById<ConstraintLayout>(R.id.searchItemContainer)
        fun bindItems(task: TaskModel) {
            tvTaskTitle.text = task.title
            container.setOnClickListener(){
                it.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTaskDetailFragment(task.taskId,task.workspaceId))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_search_item, parent, false)
        return SearchListViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        holder.bindItems(searchList[position])
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

}