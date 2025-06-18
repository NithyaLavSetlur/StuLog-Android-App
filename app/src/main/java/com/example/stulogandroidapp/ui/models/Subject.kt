package com.example.stulogandroidapp.ui.models

import android.graphics.Color
import com.google.firebase.firestore.Exclude

/**
 * Represents a study subject with visual properties
 * @property id Firestore document ID (excluded from database fields)
 * @property name Name of the subject (e.g. "Mathematics")
 * @property color ARGB color value for the subject's visual representation
 * @property userId ID of the user who owns this subject
 */

// For sorting/ordering
val lastUpdated: Long = System.currentTimeMillis()

// For progress tracking
val completedTasks: Int = 0
val totalTasks: Int = 0

data class Subject(
    @Exclude var id: String = "",  // Not stored in Firestore, used as document ID
    val name: String = "",
    val color: Int = Color.RED,    // Default color if not specified
    val userId: String = "",       // Links subject to user account
    val createdAt: Long = System.currentTimeMillis()  // For sorting
) {
    // Required empty constructor for Firestore
    constructor() : this("", "", Color.RED, "", 0L)

    // Helper function to convert to Firestore-friendly map
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "color" to color,
            "userId" to userId,
            "createdAt" to createdAt
        )
    }
}