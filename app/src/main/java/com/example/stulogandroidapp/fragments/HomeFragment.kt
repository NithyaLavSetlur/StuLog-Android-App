package com.example.stulogandroidapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.adapters.CupPagerAdapter
import com.example.stulogandroidapp.database.AppDatabase
import com.example.stulogandroidapp.models.Subject
import com.example.stulogandroidapp.viewmodels.SubjectViewModel
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: CupPagerAdapter
    private lateinit var subjectViewModel: SubjectViewModel
    private lateinit var subjectNameView: TextView
    private var selectedColor = "#FFA726".toColorInt()
    private var loggedInUsername: String? = null
    private var currentSubjects: List<Subject> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        subjectViewModel = ViewModelProvider(this)[SubjectViewModel::class.java]

        val sharedPref = requireActivity().getSharedPreferences("StuLogPrefs", Context.MODE_PRIVATE)
        loggedInUsername = sharedPref.getString("loggedInUsername", null)

        viewPager = view.findViewById(R.id.subjectViewPager)
        subjectNameView = view.findViewById(R.id.subjectNameDisplay)

        adapter = CupPagerAdapter(requireContext(), mutableListOf(), subjectViewModel)
        viewPager.adapter = adapter

        // Update name when swiping between cups
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (currentSubjects.isNotEmpty()) {
                    subjectNameView.text = currentSubjects[position].name
                }
            }
        })

        // Add subject button
        view.findViewById<ImageView>(R.id.btnAddSubject).setOnClickListener {
            showAddSubjectDialog()
        }

        // Observe subject list for this user
        if (loggedInUsername != null) {
            subjectViewModel.getSubjectsForUser(loggedInUsername!!).observe(viewLifecycleOwner) { subjects ->
                currentSubjects = subjects
                adapter.updateSubjects(subjects)

                if (subjects.isEmpty()) {
                    subjectNameView.text = "Press + to add a new Subject!"
                } else {
                    val currentIndex = viewPager.currentItem.coerceIn(0, subjects.lastIndex)
                    subjectNameView.text = subjects[currentIndex].name
                }
            }
        } else {
            Toast.makeText(requireContext(), "No logged-in user found", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun showAddSubjectDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_subject, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.inputSubjectName)
        val colorPreview = dialogView.findViewById<View>(R.id.colorPreview)
        colorPreview.setBackgroundColor(selectedColor)

        colorPreview.setOnClickListener {
            ColorPickerDialogBuilder
                .with(requireContext())
                .setTitle("Pick Subject Color")
                .initialColor(selectedColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("OK") { _, selectedColorInt, _ ->
                    selectedColor = selectedColorInt
                    colorPreview.setBackgroundColor(selectedColorInt)
                }
                .setNegativeButton("Cancel", null)
                .build()
                .show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add Subject")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val subjectName = nameInput.text.toString()
                if (subjectName.isNotEmpty() && loggedInUsername != null) {
                    val hexColor = String.format("#%06X", 0xFFFFFF and selectedColor)
                    val newSubject = Subject(name = subjectName, color = hexColor, username = loggedInUsername!!)
                    subjectViewModel.insert(newSubject)
                } else {
                    Toast.makeText(requireContext(), "Please enter a subject name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
