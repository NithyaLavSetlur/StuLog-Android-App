package com.example.stulogandroidapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_tasks")
data class CompletedTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val taskName: String,           // Name of the completed task
    val subjectName: String,        // Subject this task belongs to
    val imageUri: String,           // URI of the uploaded image (local or external)
    val dateCompleted: String       // Completion date (formatted as "dd/MM/yyyy")
)
