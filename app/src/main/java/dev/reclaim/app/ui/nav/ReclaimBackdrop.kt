package dev.reclaim.app.ui.nav

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.SectionAccent

/**
 * The section color lives behind the screen, not inside a card. Each tab has its
 * own soft wash and a tab change crossfades the stops in place, so the wash is
 * one full-screen layer instead of two stacked ones.
 */
@Composable
fun ReclaimBackdrop(
    accent: SectionAccent,
    modifier: Modifier = Modifier,
) {
    val colors = accent.wash.mapIndexed { index, (_, color) ->
        animateColorAsState(
            targetValue = color,
            animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
            label = "washColor$index",
        )
    }
    val offsets = accent.wash.mapIndexed { index, (offset, _) ->
        animateFloatAsState(
            targetValue = offset,
            animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
            label = "washStop$index",
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ReclaimColors.Canvas)
            .drawWithCache {
                val stops = offsets.indices
                    .map { index -> offsets[index].value to colors[index].value }
                    .toTypedArray()
                val brush = Brush.verticalGradient(
                    colorStops = stops,
                    startY = 0f,
                    endY = size.height * 0.85f,
                )
                onDrawBehind { drawRect(brush) }
            },
    )
}
