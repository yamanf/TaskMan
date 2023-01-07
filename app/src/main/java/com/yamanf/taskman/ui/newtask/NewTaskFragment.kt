package com.yamanf.taskman.ui.newtask


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.Timestamp
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.databinding.FragmentNewTaskBinding
import com.yamanf.taskman.firebase.FirebaseRepositoryImpl
import com.yamanf.taskman.ui.adapters.TaskRVAdapter
import com.yamanf.taskman.ui.workspace.WorkspaceViewModel
import com.yamanf.taskman.ui.workspace.WorkspaceViewModelFactory
import com.yamanf.taskman.utils.FirestoreManager
import java.text.SimpleDateFormat
import java.util.*


class NewTaskFragment : Fragment(R.layout.fragment_new_task), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private var _binding: FragmentNewTaskBinding? = null
    private val binding get() = _binding!!
    private val args: NewTaskFragmentArgs by navArgs()
    private lateinit var workspaceId: String
    private val workspaceViewModel: WorkspaceViewModel by viewModels() {
        WorkspaceViewModelFactory(
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
    ): View {
        _binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        workspaceId = args.workspaceId
        binding.btnCalendar.setOnClickListener() {
            pickDate()
        }
        binding.tvTaskTime.setOnClickListener() {
            pickDate()
        }

        binding.btnCreateTask.setOnClickListener() { view ->
            prepareNewTaskModel { newTaskModel ->
                addTaskToWorkspace(newTaskModel,view)
            }
        }
        return binding.root
    }

    private fun prepareNewTaskModel(newTaskModel: (TaskModel) -> Unit) {
        val taskTitle = binding.etTaskTitle.text.toString()
        val taskDescription = binding.etTaskDescription.text.toString()
        val isImportant = binding.cbImportant.isChecked
        val createdAt = Timestamp.now()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
        var newTaskModel = TaskModel()

        if (taskTitle.isNotBlank()) {
            if (dateString != null) {
                val date = dateFormat.parse(dateString)
                val timestamp = Timestamp(date)
                newTaskModel = TaskModel(
                    title = taskTitle,
                    description = taskDescription,
                    taskTime = timestamp,
                    createdAt = createdAt,
                    workspaceId = workspaceId,
                    isImportant = isImportant
                )
            } else {
                newTaskModel = TaskModel(
                    title = taskTitle,
                    description = taskDescription,
                    createdAt = createdAt,
                    workspaceId = workspaceId,
                    isImportant = isImportant
                )
            }
            return newTaskModel(newTaskModel)
        } else Toast.makeText(requireContext(), "Task title cannot be empty!", Toast.LENGTH_SHORT)
            .show()

    }

    private fun addTaskToWorkspace(newTaskModel:TaskModel,view: View){
        workspaceViewModel.addTaskToWorkspace(newTaskModel) { result ->
            if (result) {
                Toast.makeText(
                    requireContext(), "Task created successfully.", Toast.LENGTH_SHORT
                ).show()
                view.findNavController().navigate(
                    NewTaskFragmentDirections.actionNewTaskFragmentToWorkspaceFragment(
                        workspaceId
                    )
                )
            } else Toast.makeText(
                requireContext(), "Task cannot created.", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun pickDate() {
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