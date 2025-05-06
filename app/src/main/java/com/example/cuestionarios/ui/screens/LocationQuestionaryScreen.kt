package com.example.cuestionarios.ui.screens

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
            locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                scope.launch {
                    if (location != null) {
                        val db = QuizApplication.database
                        val cities = db.cityDao().getAll()
                        val nearby = cities.map { city ->
                            val cityLocation = Location("").apply {
                                latitude = city.latitude
                                longitude = city.longitude
                            }
                            val dist = location.distanceTo(cityLocation)
                            city to dist
                        }.filter { it.second <= 50000 } // 50 km

                        val closest = nearby.minByOrNull { it.second }

                        if (closest != null) {
                            val cityId = closest.first.id
                            questions = db.questionDao().getQuestionsByCity(cityId).shuffled().take(3)
                        } else {
                            message = "No nearby quizzes found."
                        }
                    } else {
                        message = "Could not get location."
                    }
                }
            }
        } else {
            message = "Location permission not granted."
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
