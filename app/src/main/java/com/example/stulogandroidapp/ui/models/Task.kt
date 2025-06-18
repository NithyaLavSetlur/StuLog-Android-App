package com.example.stulogandroidapp.ui.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Represents a study task with completion requirements
 * @property id Firestore document ID (excluded from direct storage)
 * @property title Short description of the task
 * @property description Detailed notes (optional)
 * @property subjectId Reference to parent Subject
 * @property dueDate When the task should be completed
 * @property isCompleted Whether task was finished
 * @property completionImageUrl URL of proof image (required for completion)
 * @property weight Importance for progress calculation (1.0 = normal)
 * @property createdAt When task was created (for sorting)
 */
@Parcelize
data class Task(
    @Exclude var id: String = "",
    val title: String = "",
    val description: String = "",
    val subjectId: String = "",  // Links to Subject document
    val dueDate: Timestamp? = null,
    val isCompleted: Boolean = false,
    val completionImageUrl: String? = null,  // Firebase Storage URL
    val weight: Float = 1.0f,  // Default normal weight
    val createdAt: Timestamp = Timestamp.now(),
    val lastUpdated: Timestamp = Timestamp.now()
) : Parcelable {

    // Required empty constructor for Firestore
    constructor() : this("", "", "", "", null, false, null, 1.0f, Timestamp.now(), Timestamp.now())

    // Convert to Firestore-compatible map (excludes id)
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "description" to description,
            "subjectId" to subjectId,
            "dueDate" to dueDate,
            "isCompleted" to isCompleted,
            "completionImageUrl" to completionImageUrl,
            "weight" to weight,
            "createdAt" to createdAt,
            "lastUpdated" to lastUpdated
        )
    }

    companion object {
        // For creating tasks with current timestamp
        fun create(
            title: String,
            subjectId: String,
            dueDate: Date? = null
        ): Task {
            return Task(
                title = title,
                subjectId = subjectId,
                dueDate = dueDate?.let { Timestamp(it) }
            )
        }
    }
}