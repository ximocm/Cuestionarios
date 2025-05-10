package com.example.cuestionarios.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "has_answers",
    primaryKeys = ["id_question", "id_user"],
    foreignKeys = [
        ForeignKey(entity = Question::class, parentColumns = ["id"], childColumns = ["id_question"]),
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["id_user"])
    ]
)
data class HasAnswer(
    val id_question: Int,
    val id_user: Int,
    val answer: String,
    val correct: Boolean
)
