package com.example.cuestionarios.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String,
    val correct: Int = 0,
    val incorrect: Int = 0,
    val lastDailyQuiz: Long? = null
)
