package com.yamanf.taskman.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Timestamp
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.data.WorkspaceModel
import com.yamanf.taskman.databinding.FragmentHomeBinding
import com.yamanf.taskman.firebase.FirebaseRepositoryImpl
import com.yamanf.taskman.ui.adapters.MainRVAdapter
import com.yamanf.taskman.ui.adapters.SearchRVAdapter
import com.yamanf.taskman.utils.*
import java.util.*


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

        configureView()

        configureAdMob()

        return binding.root
    }

    private fun configureView() {
        binding.llNothing.gone()
        binding.searchEditText.addTextChangedListener(textWatcher)

        binding.ivProfileButton.setOnClickListener {
            it.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
        }

        homeViewModel.isSearchActiveLiveData.observe(viewLifecycleOwner) {
            if (it) {
                binding.rvSearch.visible()
                binding.fabCreateWorkSpace.gone()
                binding.rvMain.gone()
                binding.searchCancel.visible()
            } else {
                binding.rvSearch.gone()
                binding.fabCreateWorkSpace.visible()
                binding.rvMain.visible()
                binding.searchCancel.gone()
                observeWorkspaceListAndFillWorkspaceRV()
            }
        }

        binding.searchCancel.setOnClickListener {
            homeViewModel.changeIsSearchInActive()
            binding.searchEditText.text = null
        }

        homeViewModel.getUserWorkspaces()

        binding.fabCreateWorkSpace.setOnClickListener {
            createNewWorkspace()
        }
        binding.llNothing.setOnClickListener() {
            createNewWorkspace()
        }
    }

    private fun configureAdMob() {
        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }




    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // get the content of both the edit text
            val searchInput = binding.searchEditText.text.toString()
            // check whether both the fields are empty or not
            if (searchInput.isEmpty()) {
                homeViewModel.changeIsSearchInActive()
            } else if (searchInput.isNotEmpty()) {
                homeViewModel.changeIsSearchActive()
                observeSearchTaskAndFillSearchRV(searchInput)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun observeSearchTaskAndFillSearchRV(query: String) {
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
                if (filteredList.isNotEmpty()) {
                    configureSearchRecyclerView(filteredList)
                    binding.llNothing.gone()
                } else {
                    configureSearchRecyclerView(filteredList)
                    binding.llNothing.visible()
                    binding.tvCreateNewWorkspace.gone()
                }
            }
        }
    }

    private fun observeWorkspaceListAndFillWorkspaceRV() {
        homeViewModel.getUserWorkspaces()
        homeViewModel.workspaceListLiveData.observe(viewLifecycleOwner) { workspaceList ->
            if (workspaceList != null) {
                if (workspaceList.isNotEmpty()) {
                    binding.llNothing.gone()
                    configureWorkspaceRecyclerView(workspaceList as ArrayList<WorkspaceModel>)
                } else {
                    binding.llNothing.visible()
                    binding.tvCreateNewWorkspace.visible()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun createNewWorkspace() {
        Utils.showEditTextDialog(
            getString(R.string.create_a_new_workspace),
            getString(R.string.enter_workspace_title),
            getString(R.string.create),
            layoutInflater,
            requireContext()
        ) { Title ->
            if (Title.isNotBlank() && Title.length <= Constants.MAX_WORKSPACE_TITLE_LENGTH) {
                val timestamp = Timestamp.now()
                val newWorkspace = WorkspaceModel(
                    title = Title, createdAt = timestamp
                )
                homeViewModel.createNewWorkspace(newWorkspace) { result ->
                    if (result) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.workspace_created_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                        observeWorkspaceListAndFillWorkspaceRV()
                        binding.rvMain.adapter?.notifyDataSetChanged()
                    } else Toast.makeText(
                        requireContext(),
                        getString(R.string.workspace_could_not_created),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (Title.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.workspace_title_cannot_be_empty),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (Title.length > Constants.MAX_WORKSPACE_TITLE_LENGTH) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.workspace_title_cannot_be_longer),
                    Toast.LENGTH_SHORT
                ).show()
            }

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

}