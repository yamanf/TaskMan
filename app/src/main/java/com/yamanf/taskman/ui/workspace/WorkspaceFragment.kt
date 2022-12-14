package com.yamanf.taskman.ui.workspace

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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
import com.yamanf.taskman.ui.adapters.DoneTaskRVAdapter
import com.yamanf.taskman.ui.adapters.TaskRVAdapter
import com.yamanf.taskman.utils.FirestoreManager
import com.yamanf.taskman.utils.Utils
import com.yamanf.taskman.utils.gone
import com.yamanf.taskman.utils.visible

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
            if (it != null) {
                workspaceTitle = it.title
            }
            if (it != null) {
                createdAt = it.createdAt!!
            }
            configureFragmentTitle(workspaceTitle)
        }

        workspaceViewModel.getUnDoneTasksFromWorkspace(workspaceId)
        workspaceViewModel.unDoneTaskLiveData.observe(viewLifecycleOwner) {
            configureTaskRecyclerView(it)
        }

        workspaceViewModel.getDoneTasksFromWorkspace(workspaceId)
        workspaceViewModel.doneTaskLiveData.observe(viewLifecycleOwner) {
            configureDoneTaskRecyclerView(it)
        }

        binding.ivDoneRVDrawer.setOnClickListener {
            workspaceViewModel.changeIsDoneRVExpand()
        }

        workspaceViewModel.isDoneRVExpandedLiveData.observe(viewLifecycleOwner){
            if (it) {
                binding.rvDoneTask.visible()
                binding.ivDoneRVDrawer.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            } else {
                binding.rvDoneTask.gone()
                binding.ivDoneRVDrawer.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
            }
        }



        binding.ivBackButton.setOnClickListener() {
            it.findNavController()
                .navigate(WorkspaceFragmentDirections.actionWorkspaceFragmentToHomeFragment())
        }

        binding.fabCreateTask.setOnClickListener {
            it.findNavController()
                .navigate(
                    WorkspaceFragmentDirections.actionWorkspaceFragmentToNewTaskFragment(
                        workspaceId
                    )
                )
        }

        binding.tvWorkspaceName.setOnLongClickListener {
            updateWorkspaceDialog()
            return@setOnLongClickListener true
        }

        binding.ivMenuButton.setOnClickListener {
            showMenu(it)
        }

        return binding.root
    }

    private fun showMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.workspace_menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.menuWorkspaceUpdate -> {
                    updateWorkspaceDialog()
                }
                R.id.menuWorkspaceDelete -> {
                    deleteWorkspaceDialog()
                }
            }
            true
        }
        popupMenu.show()
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

    @SuppressLint("SetTextI18n")
    private fun configureDoneTaskRecyclerView(doneTaskList: ArrayList<TaskModel>) {
        binding.rvDoneTask.apply {
            val doneListSize = doneTaskList.size
            binding.tvDone.text = "Done ($doneListSize)"
            adapter = DoneTaskRVAdapter(doneTaskList)
            layoutManager = LinearLayoutManager(context)
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
            if (Title.isNotBlank()) {
                val updatedWorkspace = WorkspaceModel(
                    workspaceId = workspaceId, title = Title, createdAt = createdAt
                )
                workspaceViewModel.updateWorkspace(updatedWorkspace) { result ->
                    if (result) {
                        Toast.makeText(
                            requireContext(), "Workspace updated successfully.", Toast.LENGTH_SHORT
                        ).show()
                    } else Toast.makeText(
                        requireContext(), "Failed to update workspace.", Toast.LENGTH_SHORT
                    ).show()
                }
            } else Toast.makeText(
                requireContext(),
                "Workspace title cannot be empty!",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun deleteWorkspaceDialog() {
        val builder = AlertDialog.Builder(requireContext())
        with(builder) {
            setTitle("Delete workspace")
            setMessage("Are you sure you want to delete the workspace?")
            setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(context, "You cancelled.", Toast.LENGTH_SHORT).show()
            }
            setPositiveButton("OK") { dialog, which ->
                workspaceViewModel.deleteWorkspace(workspaceId) {
                    if (it) {
                        Toast.makeText(
                            requireContext(), "Workspace deleted successfully.", Toast.LENGTH_SHORT
                        ).show()
                        view?.findNavController()
                            ?.navigate(WorkspaceFragmentDirections.actionWorkspaceFragmentToHomeFragment())
                    } else {
                        Toast.makeText(
                            requireContext(), "Failed to delete workspace.", Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }


}