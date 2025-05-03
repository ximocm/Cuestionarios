package com.example.cuestionarios.data.db.entities

import androidx.room.*

@Entity(
    tableName = "questions",
    foreignKeys = [ForeignKey(
        entity = City::class,
        parentColumns = ["id"],
        childColumns = ["city_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("city_id")]
)
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val image: String?,
    val question: String,
    val answers: List<String>,
    val correct: String,
    val difficulty: Int,
    @ColumnInfo(name = "city_id") val cityId: Int
)

