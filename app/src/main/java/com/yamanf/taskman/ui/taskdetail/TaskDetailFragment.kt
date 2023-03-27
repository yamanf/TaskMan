package com.yamanf.taskman.ui.taskdetail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.databinding.FragmentTaskDetailBinding
import com.yamanf.taskman.firebase.FirebaseRepositoryImpl
import com.yamanf.taskman.ui.workspace.WorkspaceFragmentArgs
import com.yamanf.taskman.ui.workspace.WorkspaceFragmentDirections
import com.yamanf.taskman.ui.workspace.WorkspaceViewModel
import com.yamanf.taskman.ui.workspace.WorkspaceViewModelFactory
import com.yamanf.taskman.utils.dateFormatter
import com.yamanf.taskman.utils.gone

class TaskDetailFragment : Fragment() {
    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!
    private val args: TaskDetailFragmentArgs by navArgs()
    private lateinit var taskId: String
    private lateinit var workspaceId: String

    private val taskDetailViewModel: TaskDetailViewModel by viewModels() {
        TaskDetailViewModelFactory(
            FirebaseRepositoryImpl()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskDetailBinding.inflate(inflater,container,false)
        taskId = args.taskId
        workspaceId = args.workspaceId

        taskDetailViewModel.getWorkspaceDetails(workspaceId){ workspaceModel->
            binding.tvWorkspaceName.text = workspaceModel.title
        }

        taskDetailViewModel.getTaskDetails(taskId){taskModel->
            if (taskModel!=null){
                configureView(taskModel)
            }
        }

        configureAdMob()

        return binding.root
    }

    private fun configureAdMob() {
        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    @SuppressLint("SetTextI18n")
    private fun configureView(taskModel:(TaskModel)){
        binding.ivBackToWorkspaceButton.setOnClickListener(){
            it.findNavController().navigate(TaskDetailFragmentDirections.actionTaskDetailFragmentToWorkspaceFragment(taskModel.workspaceId))
        }
        binding.ivDeleteButton.setOnClickListener(){
            deleteTaskDialog()
        }

        binding.tvTaskTitle.text = taskModel.title
        if(taskModel.description.isNotBlank()){
            binding.tvTaskDescription.text = taskModel.description
        }else binding.tvTaskDescription.gone()
        if (taskModel.taskTime!=null){
            binding.tvTaskTime.text = taskModel.taskTime?.dateFormatter()
        }else binding.tvTaskTime.gone()

        binding.ivUpdateButton.setOnClickListener {
            it.findNavController().navigate(TaskDetailFragmentDirections.actionTaskDetailFragmentToUpdateTaskFragment(taskModel.taskId,taskModel.workspaceId))
        }

        binding.tvTaskCreatedTime.text =getString(R.string.created_at) + " " +taskModel.createdAt?.dateFormatter()
    }

    private fun deleteTaskDialog() {
        val builder = AlertDialog.Builder(requireContext())
        with(builder) {
            setTitle(getString(R.string.delete_task))
            setMessage(getString(R.string.are_you_sure_you_want_to_delete_the_task))
            setNegativeButton(getString(R.string.cancel)) { _, _ ->
                Toast.makeText(context, getString(R.string.you_cancelled), Toast.LENGTH_SHORT).show()
            }
            setPositiveButton(getString(R.string.ok)) { dialog, which ->
                taskDetailViewModel.deleteTask(taskId) {
                    if (it) {
                        Toast.makeText(
                            requireContext(), getString(R.string.task_deleted_successfully), Toast.LENGTH_SHORT
                        ).show()
                        view?.findNavController()
                            ?.navigate(TaskDetailFragmentDirections.actionTaskDetailFragmentToWorkspaceFragment(workspaceId))
                    } else {
                        Toast.makeText(
                            requireContext(), getString(R.string.failed_to_delete_task), Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

}