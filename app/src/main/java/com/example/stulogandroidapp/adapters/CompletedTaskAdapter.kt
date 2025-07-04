package com.example.stulogandroidapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.models.CompletedTask

class CompletedTaskAdapter(private var tasks: List<CompletedTask> = emptyList()) :
    RecyclerView.Adapter<CompletedTaskAdapter.ViewHolder>() {

    fun updateTasks(newTasks: List<CompletedTask>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskName: TextView = view.findViewById(R.id.completedTaskName)
        val subjectName: TextView = view.findViewById(R.id.completedSubjectName)
        val completedDate: TextView = view.findViewById(R.id.completedDate)
        val image: ImageView = view.findViewById(R.id.completedImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_completed_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskName.text = task.taskName
        holder.subjectName.text = "Subject: ${task.subjectName}"
        holder.completedDate.text = "Completed: ${task.dateCompleted}"

        // Set image safely, fallback if URI invalid or null
        val uri = Uri.parse(task.imageUri)
        if (uri != null) {
            holder.image.setImageURI(uri)
        } else {
            holder.image.setImageResource(android.R.drawable.ic_menu_report_image) // fallback icon
        }
    }

    override fun getItemCount(): Int = tasks.size
}
