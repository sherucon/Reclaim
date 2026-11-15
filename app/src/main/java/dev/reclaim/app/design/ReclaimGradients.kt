package dev.reclaim.app.design

import androidx.compose.ui.graphics.Brush

/**
 * Soft tone shifts, one per section identity. Never a hue-to-hue rainbow and
 * never behind body text without a white surface on top.
 */
object ReclaimGradients {
    val SageMist: Brush = Brush.verticalGradient(
        listOf(ReclaimColors.Sage.Soft, ReclaimColors.Canvas),
    )

    val Dawn: Brush = Brush.verticalGradient(
        listOf(ReclaimColors.Butter.Soft, ReclaimColors.Peach.Soft),
    )

    val Sunrise: Brush = Brush.verticalGradient(
        listOf(ReclaimColors.Peach.Soft, ReclaimColors.Blush.Soft, ReclaimColors.Lilac.Soft),
    )

    val LilacMist: Brush = Brush.verticalGradient(
        listOf(ReclaimColors.Lilac.Soft, ReclaimColors.Canvas),
    )

    val SkyMist: Brush = Brush.verticalGradient(
        listOf(ReclaimColors.Sky.Soft, ReclaimColors.Canvas),
    )

    val WarmNeutral: Brush = Brush.verticalGradient(
        listOf(ReclaimColors.CanvasRaised, ReclaimColors.Canvas),
    )
}
