package com.example.cuestionarios.data.db

import android.content.Context
import android.util.Log
import com.example.cuestionarios.data.db.entities.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

suspend fun seedIfNeeded(context: Context, db: AppDatabase) = withContext(Dispatchers.IO) {
    val userDao = db.userDao()
    val cityDao = db.cityDao()
    val questionDao = db.questionDao()

    // Usuario
    val existingUser = userDao.getByUsername("prueba")
    val userId = existingUser?.id ?: userDao.insert(User(username = "prueba", password = "prueba")).toInt()

    // Ciudades y sus archivos
    val cities = listOf(
        Triple("Madrid", 40.4168, -3.7038),
        Triple("Barcelona", 41.3851, 2.1734),
        Triple("Valencia", 39.4699, -0.3763),
        Triple("Sevilla", 37.3886, -5.9823),
        Triple("Bilbao", 43.2630, -2.9350),
        Triple("Malaga", 36.7213, -4.4214),
        Triple("Salamanca", 40.9701, -5.6635),
        Triple("Zaragoza", 41.6488, -0.8891),
        Triple("Granada", 37.1773, -3.5986),
        Triple("Cordoba", 37.8882, -4.7794)
    )

    for ((name, lat, lon) in cities) {
        val existingCity = cityDao.getByName(name)
        val cityId = existingCity?.id ?: cityDao.insert(City(name = name, latitude = lat, longitude = lon)).toInt()

        val fileName = "preguntas_${name.lowercase()}.json"
        try {
            val inputStream = context.assets.open(fileName)
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<Map<String, List<Map<String, Any>>>>() {}.type
            val raw = Gson().fromJson<Map<String, List<Map<String, Any>>>>(reader, type)

            val rawQuestions = raw[name] ?: emptyList()

            val questions = rawQuestions.map { q ->
                Question(
                    id = 0,
                    image = "", // No hay imagen por ahora
                    question = q["pregunta"] as String,
                    answers = (q["opciones"] as List<*>).map { it.toString() },
                    correct = q["respuesta_correcta"] as String,
                    difficulty = 1,
                    cityId = cityId
                )
            }

            questionDao.insertAll(questions)
        } catch (e: Exception) {
            Log.e("Seeder", "Error al cargar preguntas para $name", e)
        }
    }
}
