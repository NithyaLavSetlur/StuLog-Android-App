package com.example.stulogandroidapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.database.AppDatabase
import com.example.stulogandroidapp.models.CompletedTask
import com.example.stulogandroidapp.models.Task
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private var tasks: List<Task>,
    private val context: Context,
    private val onImageUploadRequest: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val db = AppDatabase.getDatabase(context)
    private val selectedTasks = mutableSetOf<Task>()
    private var deleteMode = false

    fun enableDeleteMode(enabled: Boolean) {
        deleteMode = enabled
        selectedTasks.clear()
        notifyDataSetChanged()
    }

    fun getSelectedTasks(): List<Task> = selectedTasks.toList()

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks.sortedWith(compareBy({ it.completed }, { it.dueDate ?: "" }))
        notifyDataSetChanged()
    }

    fun getCurrentTasks(): List<Task> = tasks

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskName: TextView = view.findViewById(R.id.taskName)
        val checkBox: CheckBox = view.findViewById(R.id.checkBoxTask)

        init {
            // Handle clicks on the entire item view:
            view.setOnClickListener {
                val task = tasks[adapterPosition]
                if (deleteMode) {
                    // Toggle checkbox in delete mode
                    checkBox.isChecked = !checkBox.isChecked
                } else {
                    showTaskDetailDialog(task)
                }
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

        // Remove previous listener before changing checked state
        holder.checkBox.setOnCheckedChangeListener(null)

        if (deleteMode) {
            holder.checkBox.visibility = View.VISIBLE
            holder.checkBox.isChecked = selectedTasks.contains(task)
        } else {
            holder.checkBox.visibility = View.VISIBLE
            holder.checkBox.isChecked = task.completed
        }

        // Now attach listener AFTER setting isChecked!
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (deleteMode) {
                if (isChecked) selectedTasks.add(task) else selectedTasks.remove(task)
            } else if (isChecked && !task.completed) {
                // Trigger image upload callback
                onImageUploadRequest(task)
            }
        }

        // Visual styling for completed tasks
        if (task.completed) {
            holder.taskName.setTextColor(Color.GRAY)
            holder.taskName.paint.isStrikeThruText = true
        } else {
            holder.taskName.setTextColor(Color.BLACK)
            holder.taskName.paint.isStrikeThruText = false
        }
    }

    override fun getItemCount(): Int = tasks.size

    private fun showTaskDetailDialog(task: Task) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_task_detail, null)

        dialogView.findViewById<TextView>(R.id.taskName).text = task.name
        dialogView.findViewById<TextView>(R.id.taskDescription).text = "Description: ${task.description ?: "No description"}"
        dialogView.findViewById<TextView>(R.id.taskWeight).text = "Weight: ${task.weighting}"
        dialogView.findViewById<TextView>(R.id.taskDue).text = "Due: ${task.dueDate ?: "No due date"}"

        AlertDialog.Builder(context)
            .setTitle("Task Details")
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .show()
    }

    private suspend fun getSubjectName(subjectId: Int): String {
        return db.subjectDao().getSubjectById(subjectId).name
    }
}
