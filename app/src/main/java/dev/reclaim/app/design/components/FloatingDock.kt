package dev.reclaim.app.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.LocalSectionAccent
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.icons.ReclaimIcon

data class DockItem(
    val icon: Int,
    val label: String,
)

private val DockWidth = 276.dp

/**
 * The tab bar is the same segmented control as the rest of the app: a recessed
 * track with one raised pill that slides between tabs. Tap an item, or drag
 * across the bar and the pages follow your finger.
 */
@Composable
fun FloatingDock(
    items: List<DockItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onScrub: ((Int) -> Unit)? = null,
    position: (() -> Float)? = null,
) {
    SegmentedTrack(
        count = items.size,
        selectedIndex = selectedIndex,
        onSelect = onSelect,
        itemHeight = 48.dp,
        modifier = modifier
            .width(DockWidth)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = ReclaimColors.Shadow,
                spotColor = ReclaimColors.Shadow,
            ),
        // Glass only on the dock. The segmented control stays opaque.
        trackColor = ReclaimColors.Surface.copy(alpha = 0.74f),
        onScrub = onScrub,
        position = position,
        itemContent = { index, selected ->
            val accent = LocalSectionAccent.current
            val tint by animateColorAsState(
                targetValue = if (selected) accent.deep else ReclaimColors.InkTertiary,
                animationSpec = tween(durationMillis = 160),
                label = "dockIcon",
            )
            ReclaimIcon(
                icon = items[index].icon,
                contentDescription = items[index].label,
                size = 22.dp,
                tint = tint,
            )
        },
    )
}
