package com.yamanf.taskman.ui.newtask


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.yamanf.taskman.R
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.databinding.FragmentNewTaskBinding
import com.yamanf.taskman.ui.adapters.UndoneTaskRVAdapter
import com.yamanf.taskman.ui.workspace.WorkspaceFragmentArgs
import com.yamanf.taskman.utils.FirestoreManager
import com.yamanf.taskman.utils.Utils
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Instant.now
import java.time.Month
import java.util.*


class NewTaskFragment : BottomSheetDialogFragment(R.layout.fragment_new_task),
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private var _binding: FragmentNewTaskBinding? = null
    private val binding get() = _binding!!
    private val args: NewTaskFragmentArgs by navArgs()
    private lateinit var workspaceId: String
    private lateinit var rvAdapter: UndoneTaskRVAdapter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        workspaceId = args.workspaceId
        binding.btnCalendar.setOnClickListener() {
            pickDate()
        }
        binding.tvTaskTime.setOnClickListener() {
            pickDate()
        }

        binding.btnCreateTask.setOnClickListener() {
            prepareNewTaskModel { newTaskModel ->
                createNewTask(newTaskModel)
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

        if (taskTitle.isNotBlank()){
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
        }else Toast.makeText(requireContext(),"Task title cannot be empty!",Toast.LENGTH_SHORT).show()

    }

    private fun createNewTask(task: TaskModel) {

        FirestoreManager.createNewTask(task) { result ->
            if (result) {
                Toast.makeText(requireContext(), "Task created successfully.", Toast.LENGTH_SHORT)
                    .show()
                FirestoreManager.getUnDoneTasksFromWorkspace(workspaceId) {}
                dismiss()
            } else Toast.makeText(requireContext(), "Task cannot created.", Toast.LENGTH_SHORT)
                .show()
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