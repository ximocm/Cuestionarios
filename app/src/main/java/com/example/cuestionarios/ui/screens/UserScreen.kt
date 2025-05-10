package com.example.cuestionarios.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cuestionarios.QuizApplication
import com.example.cuestionarios.data.db.entities.HasAnswer

@Composable
fun UserScreen() {
    val context = LocalContext.current
    val username = "prueba"
    var answers by remember { mutableStateOf<List<HasAnswer>>(emptyList()) }

    var correctCount by remember { mutableStateOf(0) }
    var incorrectCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val db = QuizApplication.database
        val user = db.userDao().getByUsername(username)
        if (user != null) {
            answers = db.hasAnswerDao().getAnswersByUser(user.id)
            val questionDao = db.questionDao()
            var correct = 0
            var incorrect = 0

            for (ans in answers) {
                val q = questionDao.getById(ans.id_question)
                if (q?.correct == ans.answer) correct++ else incorrect++
            }

            correctCount = correct
            incorrectCount = incorrect
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Usuario: $username", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (answers.isEmpty()) {
                Text("Sin respuestas registradas.")
            } else {
                Text("Total: ${answers.size}")
                Text("Aciertos: $correctCount")
                Text("Fallos: $incorrectCount")
                Spacer(modifier = Modifier.height(24.dp))

                PieChart(correctCount, incorrectCount)
            }
        }
    }
}

@Composable
fun PieChart(correct: Int, incorrect: Int) {
    val total = correct + incorrect
    val correctAngle = if (total > 0) (correct.toFloat() / total) * 360f else 0f
    val incorrectAngle = 360f - correctAngle

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        ) {
            val size = Size(size.minDimension, size.minDimension)
            val topLeft = Offset(
                (this.size.width - size.width) / 2f,
                (this.size.height - size.height) / 2f
            )
            drawArc(
                color = Color(0xFF4CAF50), // verde para aciertos
                startAngle = -90f,
                sweepAngle = correctAngle,
                useCenter = true,
                topLeft = topLeft,
                size = size
            )
            drawArc(
                color = Color(0xFFF44336), // rojo para fallos
                startAngle = -90f + correctAngle,
                sweepAngle = incorrectAngle,
                useCenter = true,
                topLeft = topLeft,
                size = size
            )
        }

        // Leyenda
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            LegendItem(color = Color(0xFF4CAF50), label = "Aciertos")
            Spacer(modifier = Modifier.width(16.dp))
            LegendItem(color = Color(0xFFF44336), label = "Fallos")
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(16.dp)) {
            drawRect(color = color)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(label)
    }
}