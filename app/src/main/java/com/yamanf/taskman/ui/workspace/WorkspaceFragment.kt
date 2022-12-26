package com.yamanf.taskman.ui.workspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.data.repository.workspace.WorkspaceRepository
import com.yamanf.taskman.databinding.FragmentWorkspaceBinding
import com.yamanf.taskman.ui.adapters.UndoneTaskRVAdapter
import com.yamanf.taskman.ui.home.HomeFragmentDirections
import com.yamanf.taskman.ui.newtask.NewTaskFragment
import com.yamanf.taskman.utils.Constants
import com.yamanf.taskman.utils.FirestoreManager
import com.yamanf.taskman.utils.Utils

class WorkspaceFragment() : Fragment(R.layout.fragment_workspace) {
    private var _binding : FragmentWorkspaceBinding? = null
    private val binding get() = _binding!!
    private val args: WorkspaceFragmentArgs by navArgs()
    private lateinit var workspaceId : String
  //  val viewModel: WorkspaceViewModel by viewModels()

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
            configureUnDoneRecyclerView()
        }
        configureFragmentTitle()
        configureUnDoneRecyclerView()
        return binding.root
    }

    private fun configureUnDoneRecyclerView(){
        binding.rvUndoneTask.apply {
            layoutManager = LinearLayoutManager(context)
            val divider = DividerItemDecoration(
                context,
                (layoutManager as LinearLayoutManager).orientation
            )
            divider.setDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.divider)!!)
            addItemDecoration(divider)
        }

        FirestoreManager.getUnDoneTasksFromWorkspace(workspaceId){
            binding.rvUndoneTask.adapter = UndoneTaskRVAdapter(it)
        }
    }
    private fun configureFragmentTitle(){
        FirestoreManager.getWorkspaceNameFromId(workspaceId){
            binding.tvWorkspaceName.text = it
        }
    }


}