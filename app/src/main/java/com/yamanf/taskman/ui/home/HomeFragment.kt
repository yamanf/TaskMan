package com.yamanf.taskman.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.yamanf.taskman.R
import com.yamanf.taskman.databinding.FragmentHomeBinding
import com.yamanf.taskman.ui.adapters.MainRVAdapter
import com.yamanf.taskman.utils.FirestoreManager
import com.yamanf.taskman.utils.Utils


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        configureRecyclerView()

        binding.fabCreateWorkSpace.setOnClickListener{
            createNewWorkspace()
        }
        return binding.root
    }

    private fun createNewWorkspace(){
        Utils.showEditTextDialog("Create a new workspace", "Enter a workspace name", "Create", layoutInflater,requireContext()){
            FirestoreManager.createNewWorkspace(it){ result ->
                if (result){
                    Toast.makeText(requireContext(),"Workspace created successfully.",Toast.LENGTH_SHORT).show()
                    FirestoreManager.getUserWorkspaces {
                        binding.rvMain.adapter = MainRVAdapter(it)
                    }
                    binding.rvMain.adapter?.notifyDataSetChanged()
                }else Toast.makeText(requireContext(),"Workspace cannot created.",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun configureRecyclerView(){
        binding.rvMain.layoutManager = GridLayoutManager(context,2)
        FirestoreManager.getUserWorkspaces {
            binding.rvMain.adapter = MainRVAdapter(it)
        }
    }

}