package dev.reclaim.app.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors

data class DockItem(
    val icon: Int,
    val label: String,
)

/**
 * Floating white pill navigation. The active item gets a quiet surface step,
 * not a color fill.
 */
@Composable
fun FloatingDock(
    items: List<DockItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(ReclaimColors.Surface)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 48.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) ReclaimColors.CanvasRaised else Color.Transparent,
                    ).selectable(
                        selected = selected,
                        role = Role.Tab,
                        onClick = { onSelect(index) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(item.icon),
                    contentDescription = item.label,
                    modifier = Modifier.size(22.dp),
                    tint = if (selected) ReclaimColors.Ink else ReclaimColors.InkTertiary,
                )
            }
        }
    }
}
