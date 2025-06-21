package com.example.stulogandroidapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.stulogandroidapp.models.Subject

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): LiveData<List<Subject>>

    @Insert
    fun insert(subject: Subject)

    @Update
    fun update(subject: Subject)

    @Delete
    fun delete(subject: Subject)
}
