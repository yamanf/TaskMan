package com.yamanf.taskman.ui.workspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.databinding.FragmentWorkspaceBinding
import com.yamanf.taskman.utils.FirestoreManager

class WorkspaceFragment() : Fragment(R.layout.fragment_workspace) {
    private var _binding : FragmentWorkspaceBinding? = null
    private val binding get() = _binding!!
    private val args: WorkspaceFragmentArgs by navArgs()
    private lateinit var workspaceId : String
    private val workspaceViewModel: WorkspaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkspaceBinding.inflate(inflater,container,false)
        workspaceId = args.workspaceId
        binding.ivBackButton.setOnClickListener(){
            it.findNavController().navigate(WorkspaceFragmentDirections.actionWorkspaceFragmentToHomeFragment())
        }
        binding.fabCreateTask.setOnClickListener{
            it.findNavController().navigate(
                WorkspaceFragmentDirections.actionWorkspaceFragmentToNewTaskFragment(workspaceId))

        }
        configureFragmentTitle()
        var unDoneTaskList = arrayListOf<TaskModel>()

        workspaceViewModel.getUnDoneTasksFromWorkspace(workspaceId){
            unDoneTaskList = it

        }
        configureUnDoneRecyclerView(unDoneTaskList)
        return binding.root
    }


    private fun configureUnDoneRecyclerView(unDoneTaskList: ArrayList<TaskModel>){
        binding.rvUndoneTask.apply {
            layoutManager = LinearLayoutManager(context)
            val divider = DividerItemDecoration(
                context,
                (layoutManager as LinearLayoutManager).orientation
            )
            divider.setDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.divider)!!)
            addItemDecoration(divider)
        }

    }
    private fun configureFragmentTitle(){
        FirestoreManager.getWorkspaceNameFromId(workspaceId){
            binding.tvWorkspaceName.text = it
        }
    }


}