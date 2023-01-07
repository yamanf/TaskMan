package com.yamanf.taskman.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.data.WorkspaceModel
import com.yamanf.taskman.databinding.FragmentHomeBinding
import com.yamanf.taskman.firebase.FirebaseRepositoryImpl
import com.yamanf.taskman.ui.adapters.MainRVAdapter
import com.yamanf.taskman.ui.adapters.SearchRVAdapter
import com.yamanf.taskman.ui.adapters.TaskRVAdapter
import com.yamanf.taskman.utils.*
import java.util.*
import kotlin.collections.ArrayList


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
        binding.searchEditText.addTextChangedListener(textWatcher)


        homeViewModel.isSearchActiveLiveData.observe(viewLifecycleOwner){
            if (it){
                binding.rvSearch.visible()
                binding.fabCreateWorkSpace.gone()
                binding.rvMain.gone()
            }else{
                binding.rvSearch.gone()
                binding.fabCreateWorkSpace.visible()
                binding.rvMain.visible()
            }
        }

        homeViewModel.getUserWorkspaces()
        observeWorkspaceListAndFillWorkspaceRV()

        binding.fabCreateWorkSpace.setOnClickListener {
            createNewWorkspace()
        }
        return binding.root
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // get the content of both the edit text
            val searchInput = binding.searchEditText.text.toString()
            // check whether both the fields are empty or not
            if (searchInput.isEmpty()){
                homeViewModel.changeIsSearchInActive()
            }else if (searchInput.isNotEmpty()){
                homeViewModel.changeIsSearchActive()
                observeSearchTaskAndFillSearchRV(searchInput)
            }
        }
        override fun afterTextChanged(s: Editable) {}
    }

    private fun observeSearchTaskAndFillSearchRV(query:String) {
        val filteredList = arrayListOf<TaskModel>()
        homeViewModel.getSearchTasks()
        homeViewModel.searchTaskLiveData.observe(viewLifecycleOwner) { searchList ->
            val queryLower = query.lowercase()
            filteredList.clear()
            if (searchList != null) {
                for (currentItem in searchList) {
                    if (currentItem.title?.lowercase(Locale.getDefault())!!.contains(queryLower)) {
                        filteredList.add(currentItem)
                    }
                }
                configureSearchRecyclerView(filteredList)
            }
        }
    }

    private fun observeWorkspaceListAndFillWorkspaceRV() {
        homeViewModel.getUserWorkspaces()
        homeViewModel.workspaceListLiveData.observe(viewLifecycleOwner) { workspaceList ->
            if (workspaceList != null) {
                configureWorkspaceRecyclerView(workspaceList as ArrayList<WorkspaceModel>)
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
        ) {Title ->
            if(Title.isNotBlank()){
                val timestamp = Timestamp.now()
                val newWorkspace = WorkspaceModel(
                    title = Title, createdAt = timestamp
                )
                homeViewModel.createNewWorkspace(newWorkspace) { result ->
                    if (result) {
                        Toast.makeText(
                            requireContext(), "Workspace created successfully.", Toast.LENGTH_SHORT
                        ).show()
                        observeWorkspaceListAndFillWorkspaceRV()
                        binding.rvMain.adapter?.notifyDataSetChanged()
                    } else Toast.makeText(
                        requireContext(), "Workspace cannot created.", Toast.LENGTH_SHORT
                    ).show()
                }
            }else Toast.makeText(requireContext(), "Workspace title cannot be empty!", Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun configureWorkspaceRecyclerView(workspaceList: ArrayList<WorkspaceModel>) {
        binding.rvMain.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = MainRVAdapter(workspaceList)
        }
    }

    private fun configureSearchRecyclerView(searchList: ArrayList<TaskModel>) {
        binding.rvSearch.apply {
            println(searchList)
            adapter = SearchRVAdapter(searchList)
            layoutManager = LinearLayoutManager(context)
        }
    }

}