package com.example.cuestionarios.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cuestionarios.data.db.dao.*
import com.example.cuestionarios.data.db.entities.*

@Database(
    entities = [City::class, Question::class, User::class, HasAnswer::class],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao
    abstract fun questionDao(): QuestionDao
    abstract fun userDao(): UserDao
    abstract fun hasAnswerDao(): HasAnswerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quiz_database"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
