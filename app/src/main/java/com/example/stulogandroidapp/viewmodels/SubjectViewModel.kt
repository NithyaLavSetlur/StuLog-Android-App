package com.example.stulogandroidapp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.stulogandroidapp.database.AppDatabase
import com.example.stulogandroidapp.models.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubjectViewModel(application: Application) : AndroidViewModel(application) {
    private val subjectDao = AppDatabase.getDatabase(application).subjectDao()
    val subjects: LiveData<List<Subject>> = subjectDao.getAllSubjects()

    fun insert(subject: Subject) = viewModelScope.launch(Dispatchers.IO) {
        subjectDao.insert(subject)
    }

    fun update(subject: Subject) = viewModelScope.launch(Dispatchers.IO) {
        subjectDao.update(subject)
    }

    fun delete(subject: Subject) = viewModelScope.launch(Dispatchers.IO) {
        subjectDao.delete(subject)
    }
}
