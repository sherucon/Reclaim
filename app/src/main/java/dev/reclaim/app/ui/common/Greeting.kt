package dev.reclaim.app.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.time.LocalTime

@Composable
fun rememberGreeting(): String =
    remember {
        when (LocalTime.now().hour) {
            in 0..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }
