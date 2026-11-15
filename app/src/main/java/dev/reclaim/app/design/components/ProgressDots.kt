package dev.reclaim.app.design.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors

/**
 * The active dot stretches into a short pill instead of changing colour alone,
 * so progress reads at a glance without a numbers label.
 */
@Composable
fun ProgressDots(
    count: Int,
    currentIndex: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count.coerceAtLeast(1)) { index ->
            val active = index == currentIndex
            val width by animateDpAsState(
                targetValue = if (active) 22.dp else 6.dp,
                animationSpec = tween(durationMillis = 180),
                label = "dotWidth",
            )
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(
                        when {
                            active -> ReclaimColors.Ink
                            index < currentIndex -> ReclaimColors.InkSecondary
                            else -> ReclaimColors.Hairline
                        },
                    ),
            )
        }
    }
}
