package com.yourdomain.stulogandroidapp.models

data class Subject(
    val id: String = "",
    val name: String = "",
    val color: Int = 0,
    val progress: Float = 0f, // 0 to 1
    val tasks: List<Task> = emptyList()
)