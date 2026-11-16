package dev.reclaim.app.design.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors

private val TrackGap = 4.dp
private val TrackPadding = 4.dp

/**
 * The one segmented control in the app: a recessed track with a single raised
 * pill that slides between equal cells. Used by the dock and by any option picker
 * so both behave and feel the same.
 */
@Composable
fun SegmentedTrack(
    count: Int,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    itemHeight: Dp,
    modifier: Modifier = Modifier,
    trackColor: Color = ReclaimColors.Recess,
    onScrub: ((Int) -> Unit)? = null,
    position: (() -> Float)? = null,
    itemContent: @Composable (index: Int, selected: Boolean) -> Unit,
) {
    val density = LocalDensity.current
    val interaction = remember { MutableInteractionSource() }

    BoxWithConstraints(
        modifier = modifier
            .clip(CircleShape)
            .background(trackColor)
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(ReclaimColors.RecessEdge, Color.Transparent),
                        startY = 0f,
                        endY = size.height * 0.32f,
                    ),
                    size = Size(size.width, size.height * 0.32f),
                )
            }.padding(TrackPadding),
    ) {
        val cellWidth = (maxWidth - TrackGap * (count - 1)) / count
        val stepPx = with(density) { (cellWidth + TrackGap).toPx() }

        val animatedOffset = animateDpAsState(
            targetValue = (cellWidth + TrackGap) * selectedIndex,
            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
            label = "trackIndicator",
        )
        // A pager-linked track follows the finger; a standalone one animates on
        // tap. Reading both inside the placement lambda keeps scrolling and
        // animation out of composition and layout.
        val indicatorOffset: () -> Dp = position?.let { follow ->
            { (cellWidth + TrackGap) * follow().coerceIn(0f, (count - 1).toFloat()) }
        } ?: { animatedOffset.value }

        Box(
            modifier = Modifier
                .offset { IntOffset(indicatorOffset().roundToPx(), 0) }
                .size(width = cellWidth, height = itemHeight)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(ReclaimColors.Surface),
        )

        Row(
            modifier = Modifier.pointerInput(count, stepPx, onScrub) {
                fun indexAt(x: Float): Int = (x / stepPx).toInt().coerceIn(0, count - 1)
                val scrub: (Int) -> Unit = onScrub ?: onSelect

                detectHorizontalDragGestures(
                    onDragStart = { offset -> scrub(indexAt(offset.x)) },
                    onHorizontalDrag = { change, _ -> scrub(indexAt(change.position.x)) },
                )
            },
            horizontalArrangement = Arrangement.spacedBy(TrackGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(count) { index ->
                val selected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .width(cellWidth)
                        .height(itemHeight)
                        .clip(CircleShape)
                        .selectable(
                            selected = selected,
                            interactionSource = interaction,
                            indication = null,
                            role = Role.Tab,
                            onClick = { onSelect(index) },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    itemContent(index, selected)
                }
            }
        }
    }
}
