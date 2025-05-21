package com.yourdomain.stulogandroidapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yourdomain.stulogandroidapp.models.Subject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _subjects = MutableLiveData<List<Subject>>()
    val subjects: LiveData<List<Subject>> = _subjects

    init {
        loadSubjects()
    }

    private fun loadSubjects() {
        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).collection("subjects")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener

                    val subjectsList = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(Subject::class.java)?.copy(id = doc.id)
                    } ?: emptyList()

                    _subjects.value = subjectsList
                }
        }
    }

    fun addSubject(name: String, color: Int) {
        auth.currentUser?.uid?.let { userId ->
            val subject = Subject(name = name, color = color)
            db.collection("users").document(userId).collection("subjects")
                .add(subject)
        }
    }
}