package com.yamanf.taskman.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel


class SearchRVAdapter(private var searchList: List<TaskModel>) :
    RecyclerView.Adapter<SearchRVAdapter.SearchListViewHolder>() {

    inner class SearchListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTaskTitle = view.findViewById<TextView>(R.id.tvSearchTaskTitle)
        fun bindItems(task: TaskModel) {
            tvTaskTitle.text = task.title
            /*
            TODO("Changing between done and undone is not working")
            if(!task.isDone){
                tvTaskTitle.apply {
                    background = null
                    setTextColor(Color.parseColor("#000000"))
                }
            }else if(task.isDone){
                tvTaskTitle.apply {
                    setBackgroundResource(R.drawable.strikethrough)
                    setTextColor(Color.parseColor("#9E9E9E"))
                }
            }

             */

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