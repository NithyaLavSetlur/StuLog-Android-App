package com.example.stulogandroidapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.adapters.TaskAdapter
import com.example.stulogandroidapp.database.AppDatabase
import com.example.stulogandroidapp.models.Subject
import com.example.stulogandroidapp.models.Task
import kotlinx.coroutines.launch

class SubjectTaskListFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var taskListView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private var tasks: List<Task> = listOf()
    private var subjects: List<Subject> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_subject_task_list, container, false)
        db = AppDatabase.getDatabase(requireContext())
        taskListView = view.findViewById(R.id.taskListRecycler)
        taskListView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TaskAdapter(tasks) { task ->
            val action = SubjectTaskListFragmentDirections.actionSubjectTaskListFragmentToTaskDetailFragment(task.id)
            findNavController().navigate(action)
        }
        taskListView.adapter = adapter

        showSubjectPopup()
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
                    val subjectId = subjects[which].id
                    loadTasks(subjectId)
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun loadTasks(subjectId: Int) {
        lifecycleScope.launch {
            tasks = db.taskDao().getTasksBySubject(subjectId)
            adapter.updateTasks(tasks)
        }
    }
}
