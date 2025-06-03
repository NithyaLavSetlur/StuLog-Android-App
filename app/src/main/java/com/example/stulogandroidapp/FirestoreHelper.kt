package com.example.stulogandroidapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreHelper {
    // Initialize Firestore
    val db: FirebaseFirestore by lazy { Firebase.firestore }

    // Example: Add a subject
    fun addSubject(name: String, color: Int, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val subject = hashMapOf(
            "name" to name,
            "color" to color,
            "userId" to FirebaseAuth.getInstance().currentUser?.uid
        )

        db.collection("subjects")
            .add(subject)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    // Example: Fetch subjects
    fun getSubjects(onSuccess: (List<Map<String, Any>>) -> Unit) {
        db.collection("subjects")
            .whereEqualTo("userId", FirebaseAuth.getInstance().currentUser?.uid)
            .get()
            .addOnSuccessListener { documents ->
                val subjects = documents.map { it.data }
                onSuccess(subjects)
            }
    }
}