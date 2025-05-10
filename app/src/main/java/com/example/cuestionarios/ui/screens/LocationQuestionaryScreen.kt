package com.example.cuestionarios.ui.screens

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.cuestionarios.QuizApplication
import com.example.cuestionarios.data.db.entities.Question
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationQuestionaryScreen(navController: NavController) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val scope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var message by remember { mutableStateOf("Searching location...") }

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
        if (permissionState.status.isGranted) {
            val locationClient = LocationServices.getFusedLocationProviderClient(context)
            val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
                numUpdates = 1
            }

            locationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location: Location? ->
                scope.launch {
                    if (location != null) {
                        Log.d("GPS", "Ubicación detectada: ${location.latitude}, ${location.longitude}")

                        val db = QuizApplication.database
                        val cities = db.cityDao().getAll()

                        cities.forEach { city ->
                            Log.d("GPS", "Ciudad: ${city.name} (${city.latitude}, ${city.longitude})")
                        }

                        val nearby = cities.map { city ->
                            val cityLocation = Location("").apply {
                                latitude = city.latitude
                                longitude = city.longitude
                            }
                            val dist = location.distanceTo(cityLocation)
                            Log.d("GPS", "Distancia a ${city.name}: ${dist} metros")
                            city to dist
                        }.filter { it.second <= 50000 } // 50 km

                        val closest = nearby.minByOrNull { it.second }

                        if (closest != null) {
                            Log.d("GPS", "Ciudad seleccionada: ${closest.first.name}")
                            val cityId = closest.first.id
                            questions = db.questionDao().getQuestionsByCity(cityId).shuffled().take(3)
                        } else {
                            message = "No nearby quizzes found."
                            Log.d("GPS", "No se encontró ninguna ciudad dentro del radio.")
                        }
                    } else {
                        message = "Could not get location."
                        Log.d("GPS", "La ubicación fue null.")
                    }
                }
            }
        } else {
            message = "Location permission not granted."
            Log.d("GPS", "Permiso de localización no concedido.")
        }
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = message)
        }
    } else {
        QuestionaryScreenWithQuestions(questions = questions, navController = navController)
    }
}

@Composable
fun QuestionaryScreenWithQuestions(questions: List<Question>, navController: NavController) {
    QuestionaryScreenStatic(questions = questions, navController = navController)
}
