package com.yamanf.taskman.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.yamanf.taskman.R
import com.yamanf.taskman.databinding.FragmentHomeBinding
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

        binding.fabCreateWorkSpace.setOnClickListener{
            Utils.showEditTextDialog("Create a new workspace", "Enter a workspace name", "Create", layoutInflater,requireContext()){
                FirestoreManager.createNewWorkspace(it){ result ->
                    if (result){
                        Toast.makeText(requireContext(),"Workspace created successfully.",Toast.LENGTH_SHORT).show()
                    }else Toast.makeText(requireContext(),"Workspace cannot created.",Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

}