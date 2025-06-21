package com.example.stulogandroidapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val color: String // Stored as hex color string like "#FF5722"
)
