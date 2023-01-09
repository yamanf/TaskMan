package com.yamanf.taskman.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.yamanf.taskman.R
import com.yamanf.taskman.data.WorkspaceModel
import com.yamanf.taskman.ui.home.HomeFragment
import com.yamanf.taskman.ui.home.HomeFragmentDirections

class MainRVAdapter(private val workspaceList: List<WorkspaceModel>) : RecyclerView.Adapter<MainRVAdapter.MainListViewHolder>() {

    class MainListViewHolder(view:View): RecyclerView.ViewHolder(view){
        private val tvWorkspace: TextView = view.findViewById(R.id.tvWorkspace)
        private val container: CardView = view.findViewById(R.id.itemMainRVContainer)
        fun bindItems(workspace: WorkspaceModel){
            tvWorkspace.text = workspace.title
            container.setOnClickListener(){
                it.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWorkspaceFragment(workspace.workspaceId))

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_main_item,parent,false)
        return MainListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainListViewHolder, position: Int) {
        holder.bindItems(workspaceList[position])
    }

    override fun getItemCount(): Int {
        return workspaceList.size
    }

}



