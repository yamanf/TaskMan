package com.yamanf.taskman.ui.updatetask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.yamanf.taskman.R
import com.yamanf.taskman.databinding.FragmentUpdateTaskBinding
import com.yamanf.taskman.firebase.FirebaseRepositoryImpl

class UpdateTaskFragment : Fragment() {
    private var _binding : FragmentUpdateTaskBinding?= null
    private val binding get() =  _binding!!
    private val updateTaskViewModel : UpdateTaskViewModel by viewModels() {
        UpdateTaskViewModelFactory(
            FirebaseRepositoryImpl()
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentUpdateTaskBinding.inflate(inflater, container,false)




        return binding.root
    }

}