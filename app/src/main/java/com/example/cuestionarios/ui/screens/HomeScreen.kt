package com.example.cuestionarios.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cuestionarios.navigation.Screen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person

// Enable experimental APIs if needed
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz App") },
                actions = {
                    // Button that navigates to the user profile screen
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
            Text(
                text = "Welcome!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Next daily quiz available in: 03:15:42", // TODO: Replace with real countdown
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Button to start the daily quiz
            Button(
                onClick = {
                    navController.navigate(Screen.Questionary.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Take Daily Quiz")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to start the location-based quiz
            Button(
                onClick = {
                    navController.navigate(Screen.Questionary.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Take Location Quiz")
            }
        }
    }
}
