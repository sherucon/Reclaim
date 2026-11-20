package dev.reclaim.app.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.reclaim.app.R

/**
 * The onboarding surface has its own language: light type on the orange wash,
 * cream pills with a hard ink edge, and Inter instead of the app's PT Serif.
 */
object OnboardingColors {
    val Ink = Color(0xFF1F1F1F)
    val Paper = Color(0xFFF7F7F9)
    val Muted = Color(0xFF7A7A7A)
    val Placeholder = Color(0xFF919191)
}

object OnboardingSpace {
    val Text = 9.33.dp
    val Card = 16.67.dp
    val CardGap = 6.67.dp
}

object OnboardingShapes {
    val Card = RoundedCornerShape(33.dp)
    val Checkbox = RoundedCornerShape(6.67.dp)
    val ActionBorder = 1.2.dp
}

private val InterFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.W400),
    Font(R.font.inter_medium, FontWeight.W500),
    Font(R.font.inter_semibold, FontWeight.W600),
)

object OnboardingType {
    val Wordmark = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W600,
        fontSize = 42.7.sp,
        lineHeight = 50.sp,
    )
    val Title = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W500,
        fontSize = 32.sp,
        lineHeight = 39.sp,
    )
    val Body = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 19.5.sp,
    )
    val Caption = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.W400,
        fontSize = 13.3.sp,
        lineHeight = 16.sp,
    )
}
