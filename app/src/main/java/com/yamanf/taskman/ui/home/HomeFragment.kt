package com.yamanf.taskman.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.yamanf.taskman.R
import com.yamanf.taskman.data.WorkspaceModel
import com.yamanf.taskman.databinding.FragmentHomeBinding
import com.yamanf.taskman.firebase.FirebaseRepository
import com.yamanf.taskman.firebase.FirebaseRepositoryImpl
import com.yamanf.taskman.ui.adapters.MainRVAdapter
import com.yamanf.taskman.utils.Constants
import com.yamanf.taskman.utils.FirestoreManager
import com.yamanf.taskman.utils.Utils


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels() {
        HomeViewModelFactory(
            FirebaseRepositoryImpl()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel.getUserWorkspaces()
        observeWorkspaceListAndFillRecyclerview()

        binding.fabCreateWorkSpace.setOnClickListener {
            createNewWorkspace()
        }
        return binding.root
    }

    private fun observeWorkspaceListAndFillRecyclerview() {
        homeViewModel.getUserWorkspaces()
        homeViewModel.workspaceListLiveData.observe(viewLifecycleOwner) { workspaceList ->
            if (workspaceList != null) {
                configureRecyclerView(workspaceList as ArrayList<WorkspaceModel>)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun createNewWorkspace() {
        Utils.showEditTextDialog(
            "Create a new workspace",
            "Enter a workspace title",
            "Create",
            layoutInflater,
            requireContext()
        ) {
            val timestamp = Timestamp.now()
            val newWorkspace = WorkspaceModel(
                title = it, createdAt = timestamp
            )
            homeViewModel.createNewWorkspace(newWorkspace) { result ->
                if (result) {
                    Toast.makeText(
                        requireContext(), "Workspace created successfully.", Toast.LENGTH_SHORT
                    ).show()
                    observeWorkspaceListAndFillRecyclerview()
                    binding.rvMain.adapter?.notifyDataSetChanged()
                } else Toast.makeText(
                    requireContext(), "Workspace cannot created.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun configureRecyclerView(workspaceList: ArrayList<WorkspaceModel>) {
        binding.rvMain.layoutManager = GridLayoutManager(context, 2)
        binding.rvMain.adapter = MainRVAdapter(workspaceList)
    }

}