package com.example.cuestionarios.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cuestionarios.QuizApplication
import com.example.cuestionarios.R
import com.example.cuestionarios.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var canTakeQuiz by remember { mutableStateOf(false) }
    var countdownText by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        val db = QuizApplication.database
        val user = db.userDao().getByUsername("prueba") ?: return@LaunchedEffect
        val userId = user.id

        while (true) {
            val lastQuizTime = db.userDao().getLastDailyQuiz(userId)
            val now = System.currentTimeMillis()

            if (lastQuizTime == null || now >= lastQuizTime + 24 * 60 * 60 * 1000) {
                canTakeQuiz = true
                countdownText = "Daily quiz available!"
            } else {
                canTakeQuiz = false
                val millisLeft = lastQuizTime + 24 * 60 * 60 * 1000 - now
                val hours = (millisLeft / 3600000).toInt()
                val minutes = (millisLeft % 3600000 / 60000).toInt()
                val seconds = (millisLeft % 60000 / 1000).toInt()
                countdownText = String.format("Next daily quiz in: %02d:%02d:%02d", hours, minutes, seconds)
            }

            delay(1000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GeoQuizz") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.User.route) }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Profile"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "City Quiz Logo",
                modifier = Modifier
                    .size(128.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Welcome!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = countdownText,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.Questionary.route)
                    scope.launch {
                        val db = QuizApplication.database
                        val user = db.userDao().getByUsername("prueba")
                        user?.let {
                            db.userDao().updateLastDailyQuiz(it.id, System.currentTimeMillis())
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canTakeQuiz
            ) {
                Text("Take Daily Quiz")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.LocationQuestionary.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Take Location Quiz")
            }
        }
    }
}
