package dev.reclaim.app.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors

data class DockItem(
    val icon: Int,
    val label: String,
)

private val ItemWidth = 64.dp
private val ItemGap = 4.dp
private val DockPadding = 6.dp

/**
 * Floating white pill navigation. One capsule slides between the tabs instead of
 * each tab growing its own. Tap an item to travel, or drag across the dock and
 * the pages follow your finger.
 */
@Composable
fun FloatingDock(
    items: List<DockItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onScrub: ((Int) -> Unit)? = null,
) {
    val density = LocalDensity.current
    val stepPx = with(density) { (ItemWidth + ItemGap).toPx() }
    val paddingPx = with(density) { DockPadding.toPx() }

    val interaction = remember { MutableInteractionSource() }

    val indicatorOffset by animateDpAsState(
        targetValue = (ItemWidth + ItemGap) * selectedIndex,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "dockIndicator",
    )

    Box(
        modifier = modifier
            .pointerInput(items.size, onScrub) {
                if (onScrub == null) return@pointerInput

                fun indexAt(x: Float): Int = ((x - paddingPx) / stepPx).toInt().coerceIn(0, items.lastIndex)

                detectHorizontalDragGestures(
                    onDragStart = { offset -> onScrub(indexAt(offset.x)) },
                    onHorizontalDrag = { change, _ -> onScrub(indexAt(change.position.x)) },
                )
            }.clip(CircleShape)
            .background(ReclaimColors.Surface)
            .padding(DockPadding),
    ) {
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .size(width = ItemWidth, height = 48.dp)
                .clip(CircleShape)
                .background(ReclaimColors.CanvasRaised),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(ItemGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                val selected = index == selectedIndex
                val tint by animateColorAsState(
                    targetValue = if (selected) ReclaimColors.Ink else ReclaimColors.InkTertiary,
                    animationSpec = tween(durationMillis = 160),
                    label = "dockIcon",
                )
                Box(
                    modifier = Modifier
                        .size(width = ItemWidth, height = 48.dp)
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
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(22.dp),
                        tint = tint,
                    )
                }
            }
        }
    }
}
