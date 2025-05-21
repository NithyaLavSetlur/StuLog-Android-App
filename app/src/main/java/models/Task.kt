package com.yourdomain.stulogandroidapp.models

import java.util.Date

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val subjectId: String = "",
    val dueDate: Date? = null,
    val isCompleted: Boolean = false,
    val completionImageUrl: String? = null,
    val weight: Float = 1f // For progress calculation
)