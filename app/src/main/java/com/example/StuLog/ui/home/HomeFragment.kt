// HomeFragment.kt
package com.example.StuLog.ui.home

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.StuLog.R
import com.example.StuLog.databinding.FragmentHomeBinding
import com.example.StuLog.databinding.DialogAddSubjectBinding
import com.example.StuLog.databinding.DialogEditSubjectBinding
import com.example.StuLog.ui.adapters.SubjectPagerAdapter
import com.example.StuLog.ui.adapters.TaskAdapter
import com.example.StuLog.ui.models.Subject
import com.example.StuLog.ui.models.Task
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var subjectPagerAdapter: SubjectPagerAdapter
    private lateinit var taskAdapter: TaskAdapter
    private val subjects = mutableListOf<Subject>()
    private val tasks = mutableListOf<Task>()
    private var selectedColor = Color.RED // Default color

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
        loadSubjects()
    }

    private fun setupUI() {
        // Setup ViewPager for subjects
        subjectPagerAdapter = SubjectPagerAdapter(subjects, requireContext(),
            onDeleteClick = { subject -> showDeleteConfirmation(subject) },
            onEditClick = { subject -> showEditSubjectDialog(subject) }
        )

        binding.viewPagerSubjects.adapter = subjectPagerAdapter
        binding.viewPagerSubjects.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPagerSubjects.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (subjects.isNotEmpty()) {
                    loadTasksForSubject(subjects[position].id)
                    binding.textSubjectName.text = subjects[position].name
                }
            }
        })

        // Setup dots indicator
        TabLayoutMediator(binding.tabLayoutDots, binding.viewPagerSubjects) { tab, position ->
            // Empty to just show dots
        }.attach()

        // Setup tasks RecyclerView
        taskAdapter = TaskAdapter(tasks)
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTasks.adapter = taskAdapter

        // Set greeting and quote
        binding.textGreeting.text = "Good Morning, User!"
        binding.textInspirational.text = "\"Inspirational!\""

        updateEmptyState()
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
                if (name.isNotEmpty()) {
                    addSubjectToDatabase(Subject(name = name, color = selectedColor))
                } else {
                    Toast.makeText(requireContext(), "Please enter a subject name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialogBinding.colorPickerButton.setOnClickListener {
            ColorPickerDialog.Builder(requireContext())
                .setTitle("Choose Subject Color")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton("Select", ColorEnvelopeListener { envelope, _ ->
                    selectedColor = envelope.color
                    dialogBinding.colorPickerButton.setBackgroundColor(envelope.color)
                })
                .setNegativeButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .show()
        }

        dialog.show()
    }

    private fun showEditSubjectDialog(subject: Subject) {
        val dialogBinding = DialogEditSubjectBinding.inflate(layoutInflater)
        dialogBinding.etSubjectName.setText(subject.name)
        dialogBinding.colorPickerButton.setBackgroundColor(subject.color)
        selectedColor = subject.color

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Subject")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val newName = dialogBinding.etSubjectName.text.toString()
                if (newName.isNotEmpty()) {
                    updateSubjectInDatabase(subject.copy(name = newName, color = selectedColor))
                } else {
                    Toast.makeText(requireContext(), "Please enter a subject name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialogBinding.colorPickerButton.setOnClickListener {
            ColorPickerDialog.Builder(requireContext())
                .setTitle("Choose Subject Color")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton("Select", ColorEnvelopeListener { envelope, _ ->
                    selectedColor = envelope.color
                    dialogBinding.colorPickerButton.setBackgroundColor(envelope.color)
                })
                .setNegativeButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .show()
        }

        dialog.show()
    }

    private fun showDeleteConfirmation(subject: Subject) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Subject")
            .setMessage("Are you sure you want to delete ${subject.name}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteSubjectFromDatabase(subject)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addSubjectToDatabase(subject: Subject) {
        db.collection("subjects")
            .add(subject)
            .addOnSuccessListener { documentReference ->
                subject.id = documentReference.id
                subjects.add(subject)
                subjectPagerAdapter.notifyDataSetChanged()
                binding.viewPagerSubjects.setCurrentItem(subjects.size - 1, true)
                updateEmptyState()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to add subject", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateSubjectInDatabase(subject: Subject) {
        db.collection("subjects").document(subject.id)
            .set(subject)
            .addOnSuccessListener {
                val index = subjects.indexOfFirst { it.id == subject.id }
                if (index != -1) {
                    subjects[index] = subject
                    subjectPagerAdapter.notifyDataSetChanged()
                    binding.textSubjectName.text = subject.name
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update subject", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteSubjectFromDatabase(subject: Subject) {
        db.collection("subjects").document(subject.id)
            .delete()
            .addOnSuccessListener {
                subjects.remove(subject)
                subjectPagerAdapter.notifyDataSetChanged()
                updateEmptyState()

                if (subjects.isNotEmpty()) {
                    loadTasksForSubject(subjects[0].id)
                    binding.textSubjectName.text = subjects[0].name
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete subject", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadSubjects() {
        db.collection("subjects")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error loading subjects", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                subjects.clear()
                snapshot?.documents?.forEach { document ->
                    val subject = document.toObject(Subject::class.java)?.apply {
                        id = document.id
                    }
                    subject?.let { subjects.add(it) }
                }

                subjectPagerAdapter.notifyDataSetChanged()
                updateEmptyState()

                if (subjects.isNotEmpty()) {
                    binding.viewPagerSubjects.setCurrentItem(0, false)
                    loadTasksForSubject(subjects[0].id)
                    binding.textSubjectName.text = subjects[0].name
                }
            }
    }

    private fun loadTasksForSubject(subjectId: String) {
        db.collection("tasks")
            .whereEqualTo("subjectId", subjectId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error loading tasks", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                tasks.clear()
                snapshot?.documents?.forEach { document ->
                    val task = document.toObject(Task::class.java)?.apply {
                        id = document.id
                    }
                    task?.let { tasks.add(it) }
                }
                taskAdapter.notifyDataSetChanged()
            }
    }

    private fun navigateToSubjectDetails() {
        if (subjects.isNotEmpty()) {
            val currentPosition = binding.viewPagerSubjects.currentItem
            if (currentPosition < subjects.size) {
                val subject = subjects[currentPosition]
                // TODO: Implement navigation to subject details fragment
                Toast.makeText(requireContext(), "Navigating to ${subject.name} details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateEmptyState() {
        if (subjects.isEmpty()) {
            binding.emptyStateText.visibility = View.VISIBLE
            binding.viewPagerSubjects.visibility = View.GONE
            binding.tabLayoutDots.visibility = View.GONE
            binding.textSubjectName.visibility = View.GONE
            binding.recyclerViewTasks.visibility = View.GONE
            binding.btnDetails.visibility = View.GONE
        } else {
            binding.emptyStateText.visibility = View.GONE
            binding.viewPagerSubjects.visibility = View.VISIBLE
            binding.tabLayoutDots.visibility = View.VISIBLE
            binding.textSubjectName.visibility = View.VISIBLE
            binding.recyclerViewTasks.visibility = View.VISIBLE
            binding.btnDetails.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}