package com.yamanf.taskman.ui.updatetask

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Timestamp
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.databinding.FragmentUpdateTaskBinding
import com.yamanf.taskman.firebase.FirebaseRepositoryImpl
import com.yamanf.taskman.ui.taskdetail.TaskDetailFragmentDirections
import com.yamanf.taskman.utils.dateFormatter
import com.yamanf.taskman.utils.gone
import java.text.SimpleDateFormat
import java.util.*

class UpdateTaskFragment : Fragment(R.layout.fragment_update_task), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private var _binding: FragmentUpdateTaskBinding? = null
    private val binding get() = _binding!!
    private val args: UpdateTaskFragmentArgs by navArgs()
    private lateinit var taskId: String
    private lateinit var workspaceId: String
    private val updateTaskViewModel: UpdateTaskViewModel by viewModels() {
        UpdateTaskViewModelFactory(
            FirebaseRepositoryImpl()
        )
    }

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0
    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0
    private var dateString: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateTaskBinding.inflate(inflater, container, false)
        taskId = args.taskId
        workspaceId = args.workspaceId

        binding.cbImportant.gone()// isImportant feature is gonna add later.

        updateTaskViewModel.getTaskDetails(taskId) { taskModel ->
            configureView(taskModel)
        }

        configureAdMob()


        return binding.root
    }

    private fun configureView(taskModel: TaskModel?) {
        if (taskModel != null) {
            binding.ivBackToTaskDetailButton.setOnClickListener {
                it.findNavController().navigate(UpdateTaskFragmentDirections.actionUpdateTaskFragmentToTaskDetailFragment(taskModel.taskId,taskModel.workspaceId))
            }
            binding.ivDeleteButton.setOnClickListener { view->
                deleteTaskDialog()
            }
            binding.etTaskTitle.setText(taskModel.title)
            if (taskModel.description.isNotBlank()) {
                binding.etTaskDescription.setText(taskModel.description)
            } else binding.etTaskDescription.hint = getString(R.string.add_description)
            binding.cbImportant.isChecked = taskModel.isDone
            if(taskModel.taskTime?.dateFormatter().isNullOrBlank()){
                binding.tvTaskTime.setText(R.string.select_date)
            } else binding.tvTaskTime.text = taskModel.taskTime?.dateFormatter()

        }
        binding.btnCalendar.setOnClickListener {
            updateDate()
        }
        binding.tvTaskTime.setOnClickListener {
            updateDate()
        }

        binding.btnUpdateTask.setOnClickListener(){view->
            prepareUpdatedTaskModel(taskModel){
                updateTaskViewModel.updateTask(it){
                    if (it){
                        view?.findNavController()
                            ?.navigate(UpdateTaskFragmentDirections.actionUpdateTaskFragmentToTaskDetailFragment(taskId,workspaceId))
                    }else{
                        Toast.makeText(
                            requireContext(), getString(R.string.failed_to_delete_task), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }

    }

    private fun configureAdMob() {
        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
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
                updateTaskViewModel.deleteTask(taskId) {
                    if (it) {
                        Toast.makeText(
                            requireContext(), getString(R.string.task_deleted_successfully), Toast.LENGTH_SHORT
                        ).show()
                        view?.findNavController()
                            ?.navigate(UpdateTaskFragmentDirections.actionUpdateTaskFragmentToWorkspaceFragment(workspaceId))
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

    private fun prepareUpdatedTaskModel(oldTaskModel:TaskModel?, newTaskModel: (TaskModel) -> Unit) {
        val taskTitle = binding.etTaskTitle.text.toString()
        val taskDescription = binding.etTaskDescription.text.toString()
        val isImportant = binding.cbImportant.isChecked
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
        var newTaskModel = TaskModel()

        if (taskTitle.isNotBlank()) {
            if (dateString != null) {
                val date = dateFormat.parse(dateString)
                val timestamp = Timestamp(date)
                newTaskModel = TaskModel(
                    taskId = taskId,
                    title = taskTitle,
                    description = taskDescription,
                    taskTime = timestamp,
                    createdAt = oldTaskModel!!.createdAt,
                    workspaceId = workspaceId,
                    isImportant = isImportant,
                    uids = oldTaskModel.uids
                )
            } else {
                newTaskModel = TaskModel(
                    taskId = taskId,
                    title = taskTitle,
                    description = taskDescription,
                    createdAt = oldTaskModel!!.createdAt,
                    workspaceId = workspaceId,
                    isImportant = isImportant,
                    uids = oldTaskModel.uids
                )
            }
            return newTaskModel(newTaskModel)
        } else Toast.makeText(requireContext(), getString(R.string.task_title_cannot_be_empty), Toast.LENGTH_SHORT)
            .show()

    }

    private fun updateDate() {
        getDateTimeCalendar()
        DatePickerDialog(requireContext(), this, year, month, day).show()
    }

    private fun getDateTimeCalendar() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateTimeCalendar()
        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        dateString = "$savedDay-$savedMonth-$savedYear  $savedHour:$savedMinute"
        binding.tvTaskTime.text = dateString
    }

}