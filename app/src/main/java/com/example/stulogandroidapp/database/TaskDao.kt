package com.example.stulogandroidapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.stulogandroidapp.models.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE subjectId = :subjectId")
    fun getTasksForSubject(subjectId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): Task

    @Insert
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)
}
