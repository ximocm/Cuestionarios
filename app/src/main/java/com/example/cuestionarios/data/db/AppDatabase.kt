package com.example.cuestionarios.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cuestionarios.data.db.dao.*
import com.example.cuestionarios.data.db.entities.*

@Database(
    entities = [City::class, Question::class, User::class, HasAnswer::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun questionDao(): QuestionDao
    abstract fun userDao(): UserDao
    abstract fun hasAnswerDao(): HasAnswerDao
}
