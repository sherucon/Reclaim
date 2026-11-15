package dev.reclaim.app.design

import androidx.compose.ui.graphics.Brush

/**
 * Soft tone shifts, one per section identity. Each one ends on its own tint, not
 * on the canvas, so the card edge stays visible against the page.
 */
object ReclaimGradients {
    val SageMist: Brush = Brush.verticalGradient(
        listOf(ReclaimColors.Sage.Soft, ReclaimColors.Sage.Tint),
    )

    val Dawn: Brush = Brush.verticalGradient(
        listOf(ReclaimColors.Butter.Soft, ReclaimColors.Peach.Soft),
    )

    val Sunrise: Brush = Brush.verticalGradient(
        listOf(ReclaimColors.Peach.Soft, ReclaimColors.Blush.Soft, ReclaimColors.Lilac.Soft),
    )

    val LilacMist: Brush = Brush.verticalGradient(
        listOf(ReclaimColors.Lilac.Soft, ReclaimColors.Lilac.Tint),
    )

    val SkyMist: Brush = Brush.verticalGradient(
        listOf(ReclaimColors.Sky.Soft, ReclaimColors.Sky.Tint),
    )
}
