package com.example.cuestionarios.data.db.dao

import androidx.room.*
import com.example.cuestionarios.data.db.entities.Question

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Query("SELECT * FROM questions WHERE city_id = :cityId")
    suspend fun getQuestionsByCity(cityId: Int): List<Question>

    @Query("SELECT EXISTS(SELECT 1 FROM questions WHERE question = :text AND city_id = :cityId)")
    suspend fun exists(text: String, cityId: Int): Boolean

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Question?
}
