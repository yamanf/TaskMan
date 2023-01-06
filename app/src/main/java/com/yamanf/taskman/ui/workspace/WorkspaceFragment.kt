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
import com.google.firebase.Timestamp
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.data.WorkspaceModel
import com.yamanf.taskman.databinding.FragmentWorkspaceBinding
import com.yamanf.taskman.firebase.FirebaseRepositoryImpl
import com.yamanf.taskman.ui.adapters.TaskRVAdapter
import com.yamanf.taskman.utils.FirestoreManager
import com.yamanf.taskman.utils.Utils

class WorkspaceFragment() : Fragment(R.layout.fragment_workspace) {
    private var _binding: FragmentWorkspaceBinding? = null
    private val binding get() = _binding!!
    private val args: WorkspaceFragmentArgs by navArgs()
    private lateinit var workspaceId: String
    private lateinit var workspaceTitle: String
    private lateinit var createdAt: Timestamp

    private val workspaceViewModel: WorkspaceViewModel by viewModels() {
        WorkspaceViewModelFactory(
            FirebaseRepositoryImpl()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkspaceBinding.inflate(inflater, container, false)
        workspaceId = args.workspaceId

        workspaceViewModel.getWorkspaceDetails(workspaceId)
        workspaceViewModel.workspaceDetailsLiveData.observe(viewLifecycleOwner) {
            workspaceTitle = it.title
            createdAt = it.createdAt!!
            configureFragmentTitle(workspaceTitle)
        }

        workspaceViewModel.getUnDoneTasksFromWorkspace(workspaceId)
        workspaceViewModel.unDoneTaskLiveData.observe(viewLifecycleOwner) {
            configureTaskRecyclerView(it)
        }

        binding.ivBackButton.setOnClickListener() {
            it.findNavController()
                .navigate(WorkspaceFragmentDirections.actionWorkspaceFragmentToHomeFragment())
        }
        binding.fabCreateTask.setOnClickListener {
            it.findNavController()
                .navigate(WorkspaceFragmentDirections.actionWorkspaceFragmentToNewTaskFragment(workspaceId))
        }

        binding.tvWorkspaceName.setOnLongClickListener {
            updateWorkspaceDialog()
            return@setOnLongClickListener true
        }

        return binding.root
    }


    private fun configureTaskRecyclerView(unDoneTaskList: ArrayList<TaskModel>) {
        binding.rvUndoneTask.apply {
            adapter = TaskRVAdapter(unDoneTaskList)
            layoutManager = LinearLayoutManager(context)
            val divider = DividerItemDecoration(
                context, (layoutManager as LinearLayoutManager).orientation
            )
            divider.setDrawable(
                AppCompatResources.getDrawable(
                    requireContext(), R.drawable.divider
                )!!
            )
            addItemDecoration(divider)
        }
    }

    private fun configureFragmentTitle(title: String) {
        binding.tvWorkspaceName.text = title
    }

    private fun updateWorkspaceDialog() {
        Utils.showEditTextDialog(
            "Update workspace title",
            "Enter a new workspace title",
            "Update",
            layoutInflater,
            requireContext()
        ) { Title ->
            val updatedWorkspace = WorkspaceModel(
                workspaceId = workspaceId, title = Title, createdAt = createdAt
            )
            workspaceViewModel.updateWorkspace(updatedWorkspace) { result ->
                if (result) {
                    Toast.makeText(
                        requireContext(), "Workspace updated successfully.", Toast.LENGTH_SHORT
                    ).show()
                } else Toast.makeText(
                    requireContext(), "Workspace cannot updated.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}