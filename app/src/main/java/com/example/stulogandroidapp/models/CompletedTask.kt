package com.example.stulogandroidapp.models

@Entity(tableName = "completed_tasks")
data class CompletedTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskName: String,
    val subjectName: String,
    val imageUri: String,
    val dateCompleted: String
)
