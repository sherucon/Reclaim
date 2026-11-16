package dev.reclaim.app.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.icons.ReclaimIcon

/**
 * A tiny figure with its label, for use inside cards. Quiet by design: muted
 * surface, no glow, no uppercase.
 */
@Composable
fun StatPill(
    icon: Int,
    value: String,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(ReclaimColors.SurfaceMuted)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReclaimIcon(
            icon = icon,
            contentDescription = null,
            size = 14.dp,
            tint = tint,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = ReclaimColors.Ink,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = ReclaimColors.InkTertiary,
        )
    }
}
