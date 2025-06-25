package com.example.stulogandroidapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.stulogandroidapp.models.Subject

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): LiveData<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    suspend fun getSubjectById(subjectId: Int): Subject

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subject: Subject)

    @Update
    suspend fun update(subject: Subject)

    @Delete
    suspend fun delete(subject: Subject)
}
