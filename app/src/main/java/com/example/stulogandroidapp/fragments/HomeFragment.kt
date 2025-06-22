package com.example.stulogandroidapp.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.adapters.CupPagerAdapter
import com.example.stulogandroidapp.database.AppDatabase
import com.example.stulogandroidapp.models.Subject
import com.example.stulogandroidapp.viewmodels.SubjectViewModel
import yuku.ambilwarna.AmbilWarnaDialog // external color picker lib

class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: CupPagerAdapter
    private lateinit var db: AppDatabase
    private lateinit var subjectViewModel: SubjectViewModel
    private var selectedColor = Color.parseColor("#FFA726")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        db = AppDatabase.getDatabase(requireContext())

        subjectViewModel = ViewModelProvider(this)[SubjectViewModel::class.java]

        viewPager = view.findViewById(R.id.subjectViewPager)
        adapter = CupPagerAdapter(requireContext(), mutableListOf(), subjectViewModel)
        viewPager.adapter = adapter

        view.findViewById<ImageView>(R.id.btnAddSubject).setOnClickListener {
            showAddSubjectDialog()
        }

        subjectViewModel.subjects.observe(viewLifecycleOwner) {
            adapter.updateSubjects(it)
            view.findViewById<TextView>(R.id.subjectNameDisplay).text =
                if (it.isEmpty()) "Press + to add a new Subject!" else it[viewPager.currentItem].name
        }

        return view
    }

    private fun showAddSubjectDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_subject, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.inputSubjectName)
        val colorPreview = dialogView.findViewById<View>(R.id.colorPreview)
        colorPreview.setBackgroundColor(selectedColor)

        colorPreview.setOnClickListener {
            AmbilWarnaDialog(requireContext(), selectedColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {}
                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    selectedColor = color
                    colorPreview.setBackgroundColor(color)
                }
            }).show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add Subject")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString()
                if (name.isNotEmpty()) {
                    subjectViewModel.insert(Subject(name = name, color = String.format("#%06X", 0xFFFFFF and selectedColor)))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
