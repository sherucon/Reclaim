package dev.reclaim.app.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.LocalSectionAccent
import dev.reclaim.app.design.ReclaimColors

/**
 * Two or three options in the shared segmented track. The raised pill slides
 * between choices and can be dragged, exactly like the tab bar.
 */
@Composable
fun <T> SegmentedPills(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = options.indexOf(selected).coerceAtLeast(0)

    SegmentedTrack(
        count = options.size,
        selectedIndex = selectedIndex,
        onSelect = { index -> onSelect(options[index]) },
        itemHeight = 38.dp,
        modifier = modifier.fillMaxWidth(),
        trackColor = ReclaimColors.Recess,
        itemContent = { index, isSelected ->
            val accent = LocalSectionAccent.current
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) accent.deep else ReclaimColors.InkSecondary,
                animationSpec = tween(durationMillis = 160),
                label = "segmentText",
            )
            Text(
                text = label(options[index]),
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}
