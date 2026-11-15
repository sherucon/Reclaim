package dev.reclaim.app.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors

/**
 * The only switch in the app. The track sits in a shallow recess and the thumb
 * rises out of it, so the control reads as physical without being a toy.
 */
@Composable
fun PillToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) ReclaimColors.Sage.Deep else ReclaimColors.CanvasRaised,
        animationSpec = tween(durationMillis = 180),
        label = "toggleTrack",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 23.dp else 3.dp,
        animationSpec = tween(durationMillis = 180),
        label = "toggleThumb",
    )

    Box(
        modifier = modifier
            .size(width = 52.dp, height = 32.dp)
            .clip(CircleShape)
            .background(trackColor)
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(ReclaimColors.RecessEdge, Color.Transparent),
                        startY = 0f,
                        endY = size.height * 0.34f,
                    ),
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height * 0.34f),
                )
            }.toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ).semantics { if (contentDescription != null) this.contentDescription = contentDescription },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(26.dp)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFFF1EDE6)),
                    ),
                    shape = CircleShape,
                ),
        )
    }
}
