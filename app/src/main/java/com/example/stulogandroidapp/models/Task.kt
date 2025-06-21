package com.example.stulogandroidapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String? = null,
    val subjectId: Int,
    val weighting: Int = 1,
    val completed: Boolean = false,
    val imagePath: String? = null,
    val dueDate: String? = null // Store as ISO string or millis
)
