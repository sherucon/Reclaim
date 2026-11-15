package dev.reclaim.app.design.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors

/**
 * Seven rounded bars, no axes and no gridlines. One accent maximum: the bar
 * that matters, everything else in a muted step.
 */
@Composable
fun MiniBarChart(
    values: List<Int>,
    modifier: Modifier = Modifier,
    barColor: Color = ReclaimColors.Sage.Deep.copy(alpha = 0.28f),
    highlightIndex: Int? = null,
    highlightColor: Color = ReclaimColors.Sage.Deep,
    height: Dp = 56.dp,
    barWidth: Dp = 10.dp,
    contentDescription: String? = null,
) {
    val maxValue = (values.maxOrNull() ?: 0).coerceAtLeast(1)
    val grow by animateFloatAsState(
        targetValue = if (values.isEmpty()) 0f else 1f,
        animationSpec = tween(durationMillis = 600),
        label = "miniBars",
    )

    Row(
        modifier = modifier
            .height(height)
            .semantics { if (contentDescription != null) this.contentDescription = contentDescription },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        values.forEachIndexed { index, value ->
            val fraction = value.toFloat() / maxValue
            val barHeight = (height * fraction * grow).coerceAtLeast(MIN_BAR_HEIGHT)
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(barHeight)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(if (index == highlightIndex) highlightColor else barColor),
            )
        }
    }
}

private val MIN_BAR_HEIGHT = 4.dp
