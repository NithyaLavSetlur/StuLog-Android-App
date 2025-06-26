package com.example.stulogandroidapp.fragments

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.adapters.TaskAdapter
import com.example.stulogandroidapp.database.AppDatabase
import com.example.stulogandroidapp.models.CompletedTask
import com.example.stulogandroidapp.models.Subject
import com.example.stulogandroidapp.models.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import androidx.appcompat.app.AlertDialog

class SubjectTaskListFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var taskListView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var editButton: ImageButton
    private lateinit var deleteButton: ImageButton

    private var subjects: List<Subject> = listOf()
    private var selectedSubjectId: Int = -1
    private var taskPendingImageUpload: Task? = null
    private var deleteMode = false

    // Image picker launcher
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            taskPendingImageUpload?.let { task ->
                markTaskCompleteWithImage(task, it)
                taskPendingImageUpload = null
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_subject_task_list, container, false)
        db = AppDatabase.getDatabase(requireContext())

        taskListView = view.findViewById(R.id.taskListRecycler)
        taskListView.layoutManager = LinearLayoutManager(requireContext())

        editButton = view.findViewById(R.id.editButton)
        deleteButton = view.findViewById(R.id.deleteButton)

        adapter = TaskAdapter(emptyList(), requireContext()) { task ->
            taskPendingImageUpload = task
            pickImageLauncher.launch("image/*")
        }
        taskListView.adapter = adapter

        editButton.setOnClickListener { promptSelectTaskToEdit() }
        deleteButton.setOnClickListener { handleDeleteModeToggle() }

        selectedSubjectId = arguments?.getInt("subjectId") ?: -1
        if (selectedSubjectId != -1) {
            loadTasks(selectedSubjectId)
        } else {
            showSubjectPopup()
        }

        return view
    }

    private fun showSubjectPopup() {
        lifecycleScope.launch {
            subjects = db.subjectDao().getAllSubjectsOnce()
            if (subjects.isEmpty()) return@launch

            val subjectNames = subjects.map { it.name }.toTypedArray()

            AlertDialog.Builder(requireContext())
                .setTitle("Choose Subject")
                .setItems(subjectNames) { _, which ->
                    selectedSubjectId = subjects[which].id
                    loadTasks(selectedSubjectId)
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun loadTasks(subjectId: Int) {
        db.taskDao().getTasksBySubject(subjectId).observe(viewLifecycleOwner) { taskList ->
            adapter.updateTasks(taskList)
        }
    }

    private fun handleDeleteModeToggle() {
        if (!deleteMode) {
            // Enter delete mode: show checkboxes for selection
            deleteMode = true
            adapter.enableDeleteMode(true)
            Toast.makeText(requireContext(), "Select tasks to delete", Toast.LENGTH_SHORT).show()
        } else {
            // Exit delete mode: confirm deletion or cancel
            val selectedTasks = adapter.getSelectedTasks()
            if (selectedTasks.isEmpty()) {
                Toast.makeText(requireContext(), "No tasks selected", Toast.LENGTH_SHORT).show()
                return
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Delete Selected Tasks")
                .setMessage("Are you sure you want to delete ${selectedTasks.size} task(s)?")
                .setPositiveButton("Delete") { _, _ ->
                    lifecycleScope.launch {
                        selectedTasks.forEach { db.taskDao().delete(it) }
                        withContext(Dispatchers.Main) {
                            adapter.enableDeleteMode(false)
                            deleteMode = false
                            Toast.makeText(requireContext(), "Tasks deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancel") { _, _ ->
                    adapter.enableDeleteMode(false)
                    deleteMode = false
                }
                .show()
        }
    }

    private fun promptSelectTaskToEdit() {
        val allTasks = adapter.getCurrentTasks()
        if (allTasks.isEmpty()) {
            Toast.makeText(requireContext(), "No tasks to edit", Toast.LENGTH_SHORT).show()
            return
        }

        val taskNames = allTasks.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Select a Task to Edit")
            .setItems(taskNames) { _, which ->
                val selectedTask = allTasks[which]
                openEditTaskDialog(selectedTask)
            }
            .show()
    }

    private fun openEditTaskDialog(task: Task) {
        val dialogView = layoutInflater.inflate(R.layout.fragment_add_task, null)

        val nameField = dialogView.findViewById<EditText>(R.id.inputTaskName)
        val descField = dialogView.findViewById<EditText>(R.id.inputTaskDescription)
        val weightField = dialogView.findViewById<SeekBar>(R.id.taskWeightingSeek)
        val subjectSpinner = dialogView.findViewById<Spinner>(R.id.spinnerSubjects)
        val dueDateBtn = dialogView.findViewById<Button>(R.id.btnDueDate)

        nameField.setText(task.name)
        descField.setText(task.description ?: "")
        weightField.progress = task.weighting

        val subjectNames = subjects.map { it.name }
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjectNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        subjectSpinner.adapter = spinnerAdapter
        val subjectIndex = subjects.indexOfFirst { it.id == task.subjectId }
        subjectSpinner.setSelection(subjectIndex)

        dueDateBtn.text = task.dueDate ?: "Select Date"

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val updatedTask = task.copy(
                    name = nameField.text.toString(),
                    description = descField.text.toString(),
                    weighting = weightField.progress,
                    subjectId = subjects[subjectSpinner.selectedItemPosition].id,
                    dueDate = if (dueDateBtn.text != "Select Date") dueDateBtn.text.toString() else null
                )

                lifecycleScope.launch {
                    db.taskDao().update(updatedTask)
                    Toast.makeText(requireContext(), "Task updated!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun markTaskCompleteWithImage(task: Task, uri: Uri) {
        val completionDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        lifecycleScope.launch(Dispatchers.IO) {
            val updatedTask = task.copy(completed = true)
            db.taskDao().update(updatedTask)

            val subjectName = db.subjectDao().getSubjectById(task.subjectId).name

            db.completedTaskDao().insertCompletedTask(
                CompletedTask(
                    taskName = task.name,
                    subjectName = subjectName,
                    imageUri = uri.toString(),
                    dateCompleted = completionDate
                )
            )

            withContext(Dispatchers.Main) {
                val currentTasks = adapter.getCurrentTasks().map {
                    if (it.id == task.id) updatedTask else it
                }
                adapter.updateTasks(currentTasks)
                Toast.makeText(requireContext(), "Task marked as complete", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
