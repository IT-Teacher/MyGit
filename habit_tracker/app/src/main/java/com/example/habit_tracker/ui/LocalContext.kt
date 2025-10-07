package com.example.habit_tracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext as AndroidLocalContext

@Composable
fun LocalContext() = AndroidLocalContext.current