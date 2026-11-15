package dev.reclaim.app.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun rememberTodayLine(): String {
    val locale = Locale.getDefault()
    return remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))
    }
}

@Composable
fun rememberGreeting(): String =
    remember {
        when (LocalTime.now().hour) {
            in 0..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }
