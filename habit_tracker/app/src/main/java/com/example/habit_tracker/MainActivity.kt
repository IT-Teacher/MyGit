package com.example.habit_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.habit_tracker.ui.HabitListScreen
import com.example.habit_tracker.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val habitId = intent.getIntExtra("HABIT_ID", -1)

        setContent {
            AppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    HabitTrackerApp(habitId = habitId)
                }
            }
        }
    }
}

@Composable
fun HabitTrackerApp(habitId: Int) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            HabitListScreen(
                onHabitClick = { id ->
                    navController.navigate("details/$id")
                }
            )
        }

    }

    if (habitId != -1) {
        navController.navigate("details/$habitId")
    }
}
