package com.example.habit_tracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp

val Typography = Typography(
    // Slightly larger titles for presentation
    titleLarge = Typography().titleLarge.copy(letterSpacing = 0.2.sp),
    titleMedium = Typography().titleMedium.copy(letterSpacing = 0.1.sp),
    bodyLarge = Typography().bodyLarge,
    bodyMedium = Typography().bodyMedium
)