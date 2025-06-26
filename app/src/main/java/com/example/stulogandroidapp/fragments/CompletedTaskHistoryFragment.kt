package com.example.stulogandroidapp.fragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.adapters.CompletedTaskAdapter
import com.example.stulogandroidapp.database.AppDatabase

class CompletedTaskHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CompletedTaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_completed_task_history, container, false)
        recyclerView = view.findViewById(R.id.completedTasksRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CompletedTaskAdapter()
        recyclerView.adapter = adapter

        AppDatabase.getDatabase(requireContext())
            .completedTaskDao()
            .getAllCompletedTasks()
            .observe(viewLifecycleOwner, Observer { completedTasks ->
                adapter.updateTasks(completedTasks)
            })

        return view
    }
}
