package com.example.cuestionarios.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cuestionarios.QuizApplication
import com.example.cuestionarios.data.db.entities.HasAnswer
import com.example.cuestionarios.data.db.entities.Question
import kotlinx.coroutines.launch
import kotlin.random.Random
import android.media.MediaPlayer
import com.example.cuestionarios.R

@Composable
fun QuestionaryScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    val mediaPlayerCorrect = remember { MediaPlayer.create(context, R.raw.correct) }
    val mediaPlayerWrong = remember { MediaPlayer.create(context, R.raw.wrong) }

    // Cargar preguntas una sola vez
    LaunchedEffect(Unit) {
        scope.launch {
            val db = QuizApplication.database
            val cities = db.cityDao().getAll()
            if (cities.isNotEmpty()) {
                val randomCity = cities.random()
                val allQuestions = db.questionDao().getQuestionsByCity(randomCity.id)
                questions = allQuestions.shuffled().take(3)
            }
        }
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (finished) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Quiz Completed!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("You got $correctCount out of 3 correct.", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Back to Home")
            }
        }
        return
    }

    val question = questions[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Question ${currentIndex + 1} of 3", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(question.question, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))

        val shuffledAnswers by remember(currentIndex) { mutableStateOf(question.answers.shuffled()) }

        shuffledAnswers.forEach { answer ->
        val bgColor = when {
                !showResult -> Color.Transparent
                answer == selectedAnswer && answer == question.correct -> Color(0xFFB9F6CA) // Verde
                answer == selectedAnswer && answer != question.correct -> Color(0xFFFF8A80) // Rojo
                answer == question.correct -> Color(0xFFB9F6CA) // Verde para la correcta
                else -> Color.Transparent
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable(enabled = !showResult && selectedAnswer == null) {
                        selectedAnswer = answer
                        showResult = true
                        val isCorrect = answer == question.correct
                        if (isCorrect) {
                            correctCount++
                            mediaPlayerCorrect.start()
                        }
                        else {
                            mediaPlayerWrong.start()
                        }

                        scope.launch {
                            val db = QuizApplication.database
                            val user = db.userDao().getByUsername("prueba")
                            if (user != null) {
                                db.hasAnswerDao().insert(
                                    HasAnswer(
                                        id_question = question.id,
                                        id_user = user.id,
                                        answer = answer,
                                        correct = isCorrect
                                    )
                                )
                            }
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = bgColor
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = answer,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (showResult) {
            Button(
                onClick = {
                    if (currentIndex < 2) {
                        currentIndex++
                        selectedAnswer = null
                        showResult = false
                    } else {
                        scope.launch {
                            val db = QuizApplication.database
                            val user = db.userDao().getByUsername("prueba")
                            val incorrectCount = 3 - correctCount
                            if (user != null) {
                                db.userDao().updateStats(user.id, correctCount, incorrectCount)
                                // ✅ Actualizar timestamp del quiz diario
                                db.userDao().updateLastDailyQuiz(user.id, System.currentTimeMillis())
                            }
                            finished = true
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (currentIndex < 2) "Next" else "Finish")
            }
        }
    }
}
