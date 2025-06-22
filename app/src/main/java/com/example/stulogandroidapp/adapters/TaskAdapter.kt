package com.example.stulogandroidapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.database.AppDatabase
import com.example.stulogandroidapp.models.Subject
import com.example.stulogandroidapp.models.Task
import android.view.*
import android.widget.*

private lateinit var db: AppDatabase
private lateinit var taskListView: RecyclerView
private lateinit var adapter: TaskAdapter
private var tasks: List<Task> = listOf()
private var subjects: List<Subject> = listOf()

abstract class TaskAdapter(
    private var tasks: List<Task>,
    private val onItemClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskName: TextView = view.findViewById(R.id.taskName) // find where you referenced this or created this variable (not created yet I think)

        init {
            view.setOnClickListener {
                onItemClick(tasks[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.taskName.text = tasks[position].name
    }

    override fun getItemCount(): Int = tasks.size
}
