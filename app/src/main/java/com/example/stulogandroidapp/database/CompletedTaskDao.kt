package com.example.stulogandroidapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.stulogandroidapp.models.CompletedTask

@Dao
interface CompletedTaskDao {
    @Insert
    suspend fun insertCompletedTask(task: CompletedTask)

    @Query("SELECT * FROM completed_tasks ORDER BY dateCompleted DESC")
    fun getAllCompletedTasks(): LiveData<List<CompletedTask>>
}
