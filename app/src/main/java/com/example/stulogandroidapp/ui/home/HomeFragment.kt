package com.example.stulogandroidapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stulogandroidapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set greeting dynamically
        binding.textGreeting.text = "Good Morning, User!"
        binding.textInspirational.text = "\"Inspirational!\""

        // Setup click listeners
        binding.btnAddSubject.setOnClickListener {
            // Show dialog to add new subject (you can use AlertDialog)
        }

        binding.btnDown.setOnClickListener {
            // Scroll to subject list
        }

        binding.btnUp.setOnClickListener {
            // Scroll to top
        }

        binding.btnDetails.setOnClickListener {
            // Navigate to detailed view (maybe a new fragment/activity)
        }
        

        return root

        val subjects = listOf(
            Subject("Math", listOf("Task 1", "Task 2")),
            Subject("Science", listOf("Read chapter 5", "Lab report")),
            Subject("English", listOf("Essay draft", "Poem analysis"))
        )
        binding.viewPagerSubjects.adapter = SubjectPagerAdapter(subjects)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}