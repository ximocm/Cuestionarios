package com.example.cuestionarios

import android.app.Application
import androidx.room.Room
import com.example.cuestionarios.data.db.AppDatabase
import com.example.cuestionarios.data.db.seedIfNeeded
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizApplication : Application() {

    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "quiz_database"
        )
            .fallbackToDestructiveMigration()
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            seedIfNeeded(applicationContext, database)
        }
    }
}
