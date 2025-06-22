package com.example.stulogandroidapp.fragments

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.adapters.CompletedTaskAdapter
import com.example.stulogandroidapp.database.AppDatabase
import kotlinx.coroutines.launch

class CompletedTaskHistoryFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CompletedTaskAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_completed_task_history, container, false)
        db = AppDatabase.getDatabase(requireContext())

        recyclerView = view.findViewById(R.id.completedTasksRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = CompletedTaskAdapter(emptyList())
        recyclerView.adapter = adapter

        loadCompletedTasks()
        return view
    }

    private fun loadCompletedTasks() {
        lifecycleScope.launch {
            db.completedTaskDao().getAllCompletedTasks().observe(viewLifecycleOwner) {
                adapter.updateList(it)
            }
        }
    }
}
