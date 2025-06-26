package com.example.stulogandroidapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.stulogandroidapp.models.CompletedTask

@Dao
interface CompletedTaskDao {

    // Inserts a completed task into the database
    @Insert
    suspend fun insertCompletedTask(task: CompletedTask)

    // Retrieves all completed tasks, most recent first
    @Query("SELECT * FROM completed_tasks ORDER BY dateCompleted DESC")
    fun getAllCompletedTasks(): LiveData<List<CompletedTask>>
}
