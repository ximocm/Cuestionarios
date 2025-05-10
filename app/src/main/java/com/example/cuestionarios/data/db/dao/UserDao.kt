package com.example.cuestionarios.data.db.dao

import androidx.room.*
import com.example.cuestionarios.data.db.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): User?

    @Query("UPDATE users SET correct = correct + :correctAdd, incorrect = incorrect + :incorrectAdd WHERE id = :userId")
    suspend fun updateStats(userId: Int, correctAdd: Int, incorrectAdd: Int)

    @Query("UPDATE users SET lastDailyQuiz = :timestamp WHERE id = :userId")
    suspend fun updateLastDailyQuiz(userId: Int, timestamp: Long)

    @Query("SELECT lastDailyQuiz FROM users WHERE id = :userId")
    suspend fun getLastDailyQuiz(userId: Int): Long?

}
