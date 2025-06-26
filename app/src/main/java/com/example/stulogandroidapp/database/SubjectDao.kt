package com.example.stulogandroidapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.stulogandroidapp.models.Subject

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subjects WHERE username = :username")
    fun getSubjectsByUsername(username: String): LiveData<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    suspend fun getSubjectById(subjectId: Int): Subject

    @Query("SELECT * FROM subjects") // New query to get all subjects
    suspend fun getAllSubjectsOnce(): List<Subject> // Use suspend for direct retrieval

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): LiveData<List<Subject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subject: Subject)

    @Update
    suspend fun update(subject: Subject)

    @Delete
    suspend fun delete(subject: Subject)
}
