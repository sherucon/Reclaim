package dev.reclaim.app.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors

/**
 * Pip plus one line of guidance. The illustration is a placeholder until the
 * real art lands; the layout and the voice are final.
 */
@Composable
fun MascotBubble(
    message: String,
    modifier: Modifier = Modifier,
    icon: Int = dev.reclaim.app.design.icons.ReclaimIcons.Sprout,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBubble(
            icon = icon,
            tint = ReclaimColors.Sage.Deep,
            background = ReclaimColors.Sage.Soft,
            size = 44.dp,
            iconSize = 22.dp,
        )
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            color = ReclaimColors.Surface,
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = ReclaimColors.InkSecondary,
            )
        }
    }
}

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
