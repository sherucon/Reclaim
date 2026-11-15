package dev.reclaim.app.design.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import dev.reclaim.app.design.Space

/**
 * A sensitive permission, its plain-language reason, its current state, and one
 * action. Never claim a service is on when it is off.
 */
@Composable
fun PermissionCard(
    title: String,
    description: String,
    @DrawableRes icon: Int,
    granted: Boolean,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = ReclaimColors.Sage.Deep,
    background: Color = ReclaimColors.Sage.Soft,
) {
    SoftCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble(icon = icon, tint = tint, background = background)
            Spacer(Modifier.width(Space.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = ReclaimColors.Ink,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReclaimColors.InkSecondary,
                )
            }
            Spacer(Modifier.width(Space.sm))
            StatusPill(active = granted)
        }
        Spacer(Modifier.height(Space.lg))
        PillButton(
            text = actionLabel,
            onClick = onAction,
            modifier = Modifier.fillMaxWidth(),
            style = PillStyle.Secondary,
        )
    }
}

@Composable
fun StatusPill(
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val container = if (active) ReclaimColors.Sage.Soft else ReclaimColors.CanvasRaised
    val content = if (active) ReclaimColors.Sage.Deep else ReclaimColors.InkSecondary
    val dot = if (active) ReclaimColors.Sage.Deep else ReclaimColors.InkTertiary

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(container)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dot),
        )
        Text(
            text = if (active) "On" else "Off",
            style = MaterialTheme.typography.labelSmall,
            color = content,
        )
    }
}
