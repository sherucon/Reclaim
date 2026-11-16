package dev.reclaim.app.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.icons.ReclaimIcon

/**
 * A glyph in a pastel circle. Every screen-level icon sits in one of these.
 */
@Composable
fun IconBubble(
    icon: Int,
    tint: Color,
    background: Color,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconSize: Dp = 20.dp,
    contentDescription: String? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        ReclaimIcon(
            icon = icon,
            contentDescription = contentDescription,
            size = iconSize,
            tint = tint,
        )
    }
}
