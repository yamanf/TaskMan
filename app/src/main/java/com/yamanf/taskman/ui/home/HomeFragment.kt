package com.yamanf.taskman.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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
import com.yamanf.taskman.ui.auth.AuthActivity
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

        binding.ivMenuButton.setOnClickListener {
            showMenu(it)
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

    private fun showMenu(view: View) {
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.homeDrawer,
            R.string.open,
            R.string.close
        )
        binding.homeDrawer.addDrawerListener(toggle)
        toggle.syncState()
        binding.homeDrawer.openDrawer(GravityCompat.START)
        val btnCreateWorkspace =
            binding.navView.rootView.findViewById<Button>(R.id.btnCreateWorkspace)
        val btnLogout = binding.navView.rootView.findViewById<Button>(R.id.btnLogOut)
        val btnBuyMeCoffee = binding.navView.rootView.findViewById<Button>(R.id.btnBuyMeCoffee)
        val btnSendEmail = binding.navView.rootView.findViewById<Button>(R.id.btnSendEmail)
        val tvDeleteAccount = binding.navView.rootView.findViewById<TextView>(R.id.tvDeleteAccount)
        btnCreateWorkspace.setOnClickListener {
            createNewWorkspace()
        }
        btnLogout.setOnClickListener {
            homeViewModel.logOut()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            activity?.finish()
        }
        btnBuyMeCoffee.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string._buy_me_coffee_link))
                )
            )
        }
        btnSendEmail.setOnClickListener {
            sendEmail()
        }
        updateUserdata()

        tvDeleteAccount.setOnClickListener {
            deleteAccount()
        }
    }

    private fun updateUserdata() {
        val tvUsername = binding.navView.rootView.findViewById<TextView>(R.id.tvUsername)
        val tvEmail = binding.navView.rootView.findViewById<TextView>(R.id.tvEmail)
        val btnChangeUsername =
            binding.navView.rootView.findViewById<Button>(R.id.btnChangeUsername)
        val ivProfile = binding.navView.rootView.findViewById<ImageView>(R.id.ivProfile)
        homeViewModel.getCurrentUser()?.let { user ->
            if (user.displayName.isNullOrBlank()) {
                tvUsername.text = user.email!!.split("@")[0]
            } else tvUsername.text = user.displayName
            tvEmail.text = user.email
            Glide.with(requireParentFragment())
                .load(user.photoUrl)
                .into(ivProfile)
            println(user.photoUrl.toString())
        }
        btnChangeUsername.setOnClickListener {
            changeUsernameDialog {
                if (it) {
                    homeViewModel.getCurrentUser()?.let { user ->
                        if (user.displayName.isNullOrBlank()) {
                            tvUsername.text = user.email!!.split("@")[0]
                        } else tvUsername.text = user.displayName
                        tvEmail.text = user.email
                    }
                }
            }
        }
        tvUsername.setOnLongClickListener {
            changeUsernameDialog {
                if (it) {
                    homeViewModel.getCurrentUser()?.let { user ->
                        if (user.displayName.isNullOrBlank()) {
                            tvUsername.text = user.email!!.split("@")[0]
                        } else tvUsername.text = user.displayName
                        tvEmail.text = user.email
                    }
                }
            }
            return@setOnLongClickListener true
        }
    }

    private fun deleteAccount() {
        val builder = AlertDialog.Builder(requireContext())
        with(builder) {
            setTitle(getString(R.string.delete_account))
            setMessage(getString(R.string.are_you_sure_you_want_to_delete_your_account))
            setNegativeButton(getString(R.string.cancel)) { _, _ ->
                Toast.makeText(context, getString(R.string.you_cancelled), Toast.LENGTH_SHORT)
                    .show()
            }
            setPositiveButton(getString(R.string.ok)) { dialog, which ->
                deleteUser()
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteUser() {
        homeViewModel.deleteUser {
            if (it) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.account_deleted_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(requireContext(), AuthActivity::class.java))
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.account_could_not_deleted),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun changeUsernameDialog(result: (Boolean) -> Unit) {
        Utils.showEditTextDialog(
            getString(R.string.change_username),
            getString(R.string.enter_new_username),
            getString(R.string.change),
            layoutInflater,
            requireContext()
        ) { username ->
            if (username.isNotBlank() && username.length <= Constants.MAX_USERNAME_LENGTH) {
                homeViewModel.updateUsername(username) {
                    return@updateUsername result(it)
                }
            } else if (username.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.username_cannot_be_empty),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (username.length > Constants.MAX_WORKSPACE_TITLE_LENGTH) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.username_cannot_be_longer),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("IntentReset")
    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("app.taskman@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_us))
        startActivity(Intent.createChooser(emailIntent, "Send email..."))

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