package com.yourdomain.stulogandroidapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yourdomain.stulogandroidapp.R
import com.yourdomain.stulogandroidapp.databinding.FragmentHomeBinding
import com.yourdomain.stulogandroidapp.viewmodels.HomeViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Observe subjects and update UI
        viewModel.subjects.observe(viewLifecycleOwner) { subjects ->
            updateCups(subjects)
        }

        binding.fabAddSubject.setOnClickListener {
            // Show dialog to add new subject
        }
    }

    private fun updateCups(subjects: List<Subject>) {
        // Implement cup visualization based on subject progress
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}