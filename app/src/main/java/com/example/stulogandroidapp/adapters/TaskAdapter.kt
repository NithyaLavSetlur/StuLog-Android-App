package com.example.stulogandroidapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.database.AppDatabase
import com.example.stulogandroidapp.models.Task
import kotlinx.coroutines.*

class TaskAdapter(
    private var tasks: List<Task>,
    private val context: Context
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val db = AppDatabase.getDatabase(context)

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks.sortedWith(compareBy({ it.completed }, { it.dueDate ?: "" }))
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskName: TextView = view.findViewById(R.id.taskName)
        val checkBox: CheckBox = view.findViewById(R.id.checkBoxTask)

        init {
            view.setOnClickListener {
                val task = tasks[adapterPosition]
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_task_detail, null)

                val nameText = dialogView.findViewById<TextView>(R.id.taskName)
                val descriptionText = dialogView.findViewById<TextView>(R.id.taskDescription)
                val weightText = dialogView.findViewById<TextView>(R.id.taskWeight)
                val dueText = dialogView.findViewById<TextView>(R.id.taskDue)

                nameText.text = task.name
                descriptionText.text = "Description: ${task.description ?: "No description"}"
                weightText.text = "Weight: ${task.weighting}"
                dueText.text = "Due: ${task.dueDate ?: "No due date"}"

                AlertDialog.Builder(context)
                    .setTitle("Task Details")
                    .setView(dialogView)
                    .setPositiveButton("Close", null)
                    .show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.taskName.text = task.name
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = task.completed

        // Grey out completed tasks
        if (task.completed) {
            holder.taskName.setTextColor(Color.GRAY)
            holder.taskName.paint.isStrikeThruText = true
        } else {
            holder.taskName.setTextColor(Color.BLACK)
            holder.taskName.paint.isStrikeThruText = false
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !task.completed) {
                showImageUploadDialog(task)
            }
        }
    }

    override fun getItemCount(): Int = tasks.size

    private fun showImageUploadDialog(task: Task) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_upload_photo, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.confirmationImage)
        var imageSelected = false

        imageView.setOnClickListener {
            Toast.makeText(context, "Replace with image picker", Toast.LENGTH_SHORT).show()
            imageSelected = true // simulate image selection
        }

        AlertDialog.Builder(context)
            .setTitle("Upload Photo")
            .setView(dialogView)
            .setPositiveButton("Confirm") { _, _ ->
                if (imageSelected) {
                    val updatedTask = task.copy(completed = true)
                    CoroutineScope(Dispatchers.IO).launch {
                        db.taskDao().update(updatedTask)
                    }
                } else {
                    Toast.makeText(context, "You must upload at least one image", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
