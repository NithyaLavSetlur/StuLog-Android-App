package com.example.stulogandroidapp.ui.home

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView //not use yet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.example.stulogandroidapp.databinding.FragmentHomeBinding
import com.example.stulogandroidapp.databinding.DialogAddSubjectBinding
import com.example.stulogandroidapp.ui.adapters.TaskAdapter
import com.example.stulogandroidapp.ui.models.Subject
import com.example.stulogandroidapp.ui.models.Task
import com.example.stulogandroidapp.ui.adapters.SubjectPagerAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.example.stulogandroidapp.FirestoreHelper
import android.widget.Toast
import android.util.Log

data class Subject(
    var id: String = "",
    var name: String = "",
    var color: Int = 0
)

data class Task(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var dueDate: String = "",
    var subjectId: String = ""
)

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var subjectPagerAdapter: SubjectPagerAdapter
    private lateinit var taskAdapter: TaskAdapter
    private val subjects = mutableListOf<Subject>()
    private val tasks = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()

        setupUI()
        setupListeners()
        loadSubjects()

        return binding.root
    }

    private fun setupUI() {
        val tasks = tasks.map { it.title } // Creates List<String>
        val adapter = TaskAdapter(tasks)

        // Set greeting and quote
        binding.textGreeting.text = "Good Morning, User!"
        binding.textInspirational.text = "\"Inspirational!\""

        // Setup ViewPager for subjects
        subjectPagerAdapter = SubjectPagerAdapter(subjects, requireContext()) { subject ->
            loadTasksForSubject(subject.id)
        }
        binding.viewPagerSubjects.adapter = subjectPagerAdapter
        binding.viewPagerSubjects.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // Setup dots indicator
        TabLayoutMediator(binding.tabLayoutDots, binding.viewPagerSubjects) { tab, position ->
            // Empty to just show dots
        }.attach()

        // Setup tasks RecyclerView
        taskAdapter = TaskAdapter(tasks)
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTasks.adapter = taskAdapter
    }

    private fun setupListeners() {
        binding.btnAddSubject.setOnClickListener { showAddSubjectDialog() }
        binding.btnDetails.setOnClickListener { navigateToSubjectDetails() }
    }

    private fun showAddSubjectDialog() {
        val dialogBinding = DialogAddSubjectBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add New Subject")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.etSubjectName.text.toString()
                val color = Color.RED // Default color (replace with color picker selection)
                addSubjectToDatabase(Subject(name = name, color = color))
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun addSubjectToDatabase(subject: Subject) {
        db.collection("subjects")
            .add(subject)
            .addOnSuccessListener { documentReference ->
                subject.id = documentReference.id
                subjects.add(subject)
                subjectPagerAdapter.notifyDataSetChanged()
            }
    }

    private fun loadSubjects() {
        db.collection("subjects")
            .get()
            .addOnSuccessListener { result ->
                subjects.clear()
                for (document in result) {
                    val subject = document.toObject(Subject::class.java)
                    subject.id = document.id
                    subjects.add(subject)
                }
                subjectPagerAdapter.notifyDataSetChanged()

                if (subjects.isNotEmpty()) {
                    loadTasksForSubject(subjects[0].id)
                }
            }
    }

    private fun loadTasksForSubject(subjectId: String) {
        db.collection("tasks")
            .whereEqualTo("subjectId", subjectId)
            .get()
            .addOnSuccessListener { result ->
                tasks.clear()
                for (document in result) {
                    val task = document.toObject(Task::class.java)
                    task.id = document.id
                    tasks.add(task)
                }
                taskAdapter.notifyDataSetChanged()
            }
    }

    private fun navigateToSubjectDetails() {
        val currentPosition = binding.viewPagerSubjects.currentItem
        if (currentPosition < subjects.size) {
            val subject = subjects[currentPosition]
            // TODO: Navigate to subject details fragment with subject.id
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add a subject
        binding.btnAddSubject.setOnClickListener {
            FirestoreHelper.addSubject(
                name = "Mathematics",
                color = Color.RED,
                onSuccess = { Toast.makeText(context, "Added!", Toast.LENGTH_SHORT).show() },
                onError = { e -> Log.e("Firestore", "Error: $e") }
            )
        }

        // Load subjects
        FirestoreHelper.getSubjects { subjects ->
            subjects.forEach { subject ->
                Log.d("Firestore", "Subject: ${subject["name"]}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}