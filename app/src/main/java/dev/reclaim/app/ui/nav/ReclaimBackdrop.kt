package dev.reclaim.app.ui.nav

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.SectionAccent

/**
 * The section color lives behind the screen, not inside a card. Each tab has its
 * own soft wash, it falls in from the top when the tab changes, and it fades to
 * the canvas before the bottom of the screen so content always sits on calm space.
 */
@Composable
fun ReclaimBackdrop(
    accent: SectionAccent,
    modifier: Modifier = Modifier,
) {
    val stops = remember(accent) { accent.wash }

    AnimatedContent(
        targetState = stops,
        modifier = modifier.fillMaxSize(),
        transitionSpec = {
            (
                slideInVertically(
                    animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
                ) { height -> -height / 3 } + fadeIn(animationSpec = tween(durationMillis = 420))
            ) togetherWith fadeOut(animationSpec = tween(durationMillis = 320))
        },
        label = "backdrop",
    ) { layerStops ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ReclaimColors.Canvas)
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colorStops = layerStops,
                            startY = 0f,
                            endY = size.height * 0.85f,
                        ),
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height),
                    )
                },
        )
    }
}
