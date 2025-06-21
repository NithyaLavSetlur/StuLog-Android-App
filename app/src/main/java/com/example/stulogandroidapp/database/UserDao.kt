package com.example.stulogandroidapp.database

import androidx.room.*
import com.example.stulogandroidapp.models.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    fun authenticate(username: String, password: String): User?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(user: User)
}
