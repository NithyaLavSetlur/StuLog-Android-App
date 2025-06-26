package com.example.stulogandroidapp.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.stulogandroidapp.R
import com.example.stulogandroidapp.database.AppDatabase
import kotlinx.coroutines.*

class TaskDetailActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val taskId = intent.getIntExtra("taskId", -1)

        if (taskId != -1) {
            loadTaskDetails(taskId)
        } else {
            Toast.makeText(this, "Invalid Task ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.btnMarkComplete).setOnClickListener {
            Toast.makeText(this, "You’ll be able to mark tasks complete after uploading a photo!", Toast.LENGTH_LONG).show()
            // Later: launch camera/gallery intent and mark task complete.
        }
    }

    private fun loadTaskDetails(taskId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val task = db.taskDao().getTaskById(taskId)

            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.taskDetailTitle).text = task.name
                findViewById<TextView>(R.id.taskDetailDesc).text =
                    "Description: ${task.description ?: "No description"}"
                findViewById<TextView>(R.id.taskDetailDue).text =
                    "Due Date: ${task.dueDate ?: "No due date"}"
                findViewById<TextView>(R.id.taskDetailWeight).text =
                    "Weight: ${task.weighting}"
            }
        }
    }
}
