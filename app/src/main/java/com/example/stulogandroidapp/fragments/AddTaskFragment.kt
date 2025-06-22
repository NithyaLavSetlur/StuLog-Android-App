package com.example.stulogandroidapp.fragments

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider // where do you plan to use this? (also, what does this library do)
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.database.AppDatabase
import com.example.stulogandroidapp.models.Task
import com.example.stulogandroidapp.models.Subject
import java.text.SimpleDateFormat
import java.util.*

class AddTaskFragment : Fragment() {

    private lateinit var subjectSpinner: Spinner
    private lateinit var dateButton: Button
    private lateinit var notifSwitch: Switch
    private lateinit var db: AppDatabase
    private var selectedSubjectId: Int? = null
    private var selectedDate: Long? = null
    private var subjects: List<Subject> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_task, container, false)
        db = AppDatabase.getDatabase(requireContext())

        val taskNameInput = view.findViewById<EditText>(R.id.inputTaskName)
        val taskDescInput = view.findViewById<EditText>(R.id.inputTaskDescription)
        val weightingSeek = view.findViewById<SeekBar>(R.id.taskWeightingSeek)
        val weightingValue = view.findViewById<TextView>(R.id.weightingValue)
        subjectSpinner = view.findViewById(R.id.spinnerSubjects)
        dateButton = view.findViewById(R.id.btnDueDate)
        notifSwitch = view.findViewById(R.id.switchNotification)
        val btnAddTask = view.findViewById<Button>(R.id.btnAddTask)

        weightingSeek.progress = 1
        weightingSeek.max = 9
        weightingValue.text = "1"
        weightingSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                weightingValue.text = (progress + 1).toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        dateButton.setOnClickListener {
            showDatePicker()
        }

        notifSwitch.isEnabled = false

        btnAddTask.setOnClickListener {
            val name = taskNameInput.text.toString().trim()
            val desc = taskDescInput.text.toString().trim()
            val weighting = weightingSeek.progress + 1

            if (name.isEmpty() || selectedSubjectId == null) {
                Toast.makeText(requireContext(), "Please fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val task = Task(
                name = name,
                description = if (desc.isEmpty()) null else desc,
                subjectId = selectedSubjectId!!,
                weighting = weighting,
                dueDate = selectedDate?.toString(),
                completed = false
            )

            db.taskDao().insert(task)
            Toast.makeText(requireContext(), "Task added!", Toast.LENGTH_SHORT).show()

            if (notifSwitch.isChecked && selectedDate != null) {
                scheduleNotification(task.name, selectedDate!!)
            }
        }

        loadSubjects()
        return view
    }

    private fun loadSubjects() {
        db.subjectDao().getAllSubjects().observe(viewLifecycleOwner) {
            subjects = it
            val names = it.map { s -> s.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, names)
            subjectSpinner.adapter = adapter

            subjectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedSubjectId = subjects[position].id
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(requireContext(), { _, year, month, day ->
            calendar.set(year, month, day, 9, 0) // default time
            selectedDate = calendar.timeInMillis
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            dateButton.text = formatter.format(Date(selectedDate!!))
            notifSwitch.isEnabled = true
        },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.show()
    }

    private fun scheduleNotification(taskName: String, millis: Long) {
        val intent = Intent(requireContext(), ReminderReceiver::class.java).apply {
            putExtra("taskName", taskName) // what's wrong here TT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), millis.toInt(), intent, PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact( // what is this? I don't think it's compiling
            AlarmManager.RTC_WAKEUP,
            millis - 3600000L, // 1 hour before
            pendingIntent
        )
    }
}
