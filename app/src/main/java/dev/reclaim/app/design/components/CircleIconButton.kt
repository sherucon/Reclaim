package dev.reclaim.app.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.icons.ReclaimIcon

/**
 * A quiet circular icon button. 48 dp, so it stays a valid touch target.
 */
@Composable
fun CircleIconButton(
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = ReclaimColors.Surface,
    tint: Color = ReclaimColors.Ink,
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .shadow(
                elevation = 2.dp,
                shape = CircleShape,
                ambientColor = ReclaimColors.Shadow,
                spotColor = ReclaimColors.Shadow,
            ).clip(CircleShape)
            .background(container)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        ReclaimIcon(
            icon = icon,
            contentDescription = contentDescription,
            size = 20.dp,
            tint = tint,
        )
    }
}
