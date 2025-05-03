package com.example.cuestionarios.data.db.dao

import androidx.room.*
import com.example.cuestionarios.data.db.entities.HasAnswer

@Dao
interface HasAnswerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(answer: HasAnswer)

    @Query("SELECT * FROM has_answers WHERE id_user = :userId")
    suspend fun getAnswersByUser(userId: Int): List<HasAnswer>
}
