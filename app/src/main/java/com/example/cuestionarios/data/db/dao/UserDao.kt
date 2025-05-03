package com.example.cuestionarios.data.db.dao

import androidx.room.*
import com.example.cuestionarios.data.db.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): User?
}
