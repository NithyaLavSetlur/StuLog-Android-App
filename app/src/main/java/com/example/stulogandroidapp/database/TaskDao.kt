package com.example.stulogandroidapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.stulogandroidapp.models.Task

@Dao
interface TaskDao {

    // For SubjectTaskListFragment
    @Query("SELECT * FROM tasks WHERE subjectId = :subjectId ORDER BY dueDate ASC")
    fun getTasksBySubject(subjectId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE subjectId = :subjectId")
    suspend fun getTasksBySubjectOnce(subjectId: Int): List<Task>

    // For TaskDetailFragment
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): Task

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    // Mark task as complete by ID
    @Query("UPDATE tasks SET completed = 1 WHERE id = :taskId")
    suspend fun markComplete(taskId: Int)
}
