package com.example.cuestionarios.data.db.dao

import androidx.room.*
import com.example.cuestionarios.data.db.entities.City

@Dao
interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: City): Long

    @Query("SELECT * FROM cities")
    suspend fun getAll(): List<City>

    @Query("SELECT * FROM cities WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): City?
}
