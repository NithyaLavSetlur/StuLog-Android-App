package com.yourdomain.stulogandroidapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.yourdomain.stulogandroidapp.R
import com.yourdomain.stulogandroidapp.databinding.FragmentTaskBinding
import com.yourdomain.stulogandroidapp.models.Task
import com.yourdomain.stulogandroidapp.adapters.TaskAdapter

class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TaskAdapter { task ->
            // Handle task click (edit/mark complete)
        }

        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTasks.adapter = adapter

        binding.fabAddTask.setOnClickListener {
            // Show add task dialog
        }

        loadTasks()
    }

    private fun loadTasks() {
        val subjectId = arguments?.getString("subjectId") ?: return
        // Load tasks from Firestore based on subjectId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}