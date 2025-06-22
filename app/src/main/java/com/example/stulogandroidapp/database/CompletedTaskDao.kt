package com.example.stulogandroidapp.database

@Dao
interface CompletedTaskDao {
    @Insert
    suspend fun insertCompletedTask(task: CompletedTask)

    @Query("SELECT * FROM completed_tasks ORDER BY dateCompleted DESC")
    fun getAllCompletedTasks(): LiveData<List<CompletedTask>>
}
