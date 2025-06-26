package com.example.stulogandroidapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stulogandroidapp.models.CompletedTask
import com.example.stulogandroidapp.models.User
import com.example.stulogandroidapp.models.Subject
import com.example.stulogandroidapp.models.Task

@Database(
    entities = [User::class, Subject::class, Task::class, CompletedTask::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun completedTaskDao(): CompletedTaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stulog_database"
                )
                    .fallbackToDestructiveMigration() // Optional: clears DB if schema changes
                    .allowMainThreadQueries()         // TEMP: remove in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
