package dev.reclaim.app.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ReclaimColorScheme =
    lightColorScheme(
        primary = ReclaimColors.Ink,
        onPrimary = ReclaimColors.Surface,
        secondary = ReclaimColors.Sage.Deep,
        onSecondary = ReclaimColors.Surface,
        background = ReclaimColors.Canvas,
        onBackground = ReclaimColors.Ink,
        surface = ReclaimColors.Surface,
        onSurface = ReclaimColors.Ink,
        surfaceVariant = ReclaimColors.CanvasRaised,
        onSurfaceVariant = ReclaimColors.InkSecondary,
        outline = ReclaimColors.Hairline,
        error = ReclaimColors.Blush.Deep,
        onError = ReclaimColors.Surface,
    )

/**
 * v1 is light only. The warm charcoal theme lands in v1.1.
 */
@Composable
fun ReclaimTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ReclaimColorScheme,
        typography = ReclaimTypography,
        shapes = ReclaimMaterialShapes,
        content = content,
    )
}
