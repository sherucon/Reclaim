package dev.reclaim.app.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.domain.model.Platform

data class Accent(
    val soft: Color,
    val deep: Color,
)

fun accentFor(platform: Platform): Accent =
    when (platform) {
        Platform.INSTAGRAM -> Accent(ReclaimColors.Peach.Soft, ReclaimColors.Peach.Deep)
        Platform.YOUTUBE -> Accent(ReclaimColors.Blush.Soft, ReclaimColors.Blush.Deep)
        Platform.TIKTOK -> Accent(ReclaimColors.Lilac.Soft, ReclaimColors.Lilac.Deep)
        Platform.FACEBOOK -> Accent(ReclaimColors.Sky.Soft, ReclaimColors.Sky.Deep)
        Platform.SNAPCHAT -> Accent(ReclaimColors.Butter.Soft, ReclaimColors.Butter.Deep)
        Platform.LINKEDIN -> Accent(ReclaimColors.Sky.Soft, ReclaimColors.Sky.Deep)
        Platform.REDDIT -> Accent(ReclaimColors.Peach.Soft, ReclaimColors.Peach.Deep)
        Platform.X -> Accent(ReclaimColors.CanvasRaised, ReclaimColors.Ink)
    }

/**
 * Our own mark for a platform: its initial in a pastel circle. No official logo
 * is bundled or drawn.
 */
@Composable
fun PlatformGlyph(
    platform: Platform,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
) {
    val accent = accentFor(platform)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(accent.soft),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = platform.displayName.take(1),
            style = if (size >= 40.dp) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.labelLarge
            },
            color = accent.deep,
        )
    }
}
