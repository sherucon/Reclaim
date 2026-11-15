package dev.reclaim.app.design.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors

enum class PillStyle { Primary, Secondary }

@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: PillStyle = PillStyle.Primary,
    enabled: Boolean = true,
) {
    val container =
        when (style) {
            PillStyle.Primary -> ReclaimColors.Ink
            PillStyle.Secondary -> ReclaimColors.SurfaceMuted
        }
    val content =
        when (style) {
            PillStyle.Primary -> ReclaimColors.Surface
            PillStyle.Secondary -> ReclaimColors.Ink
        }

    Surface(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        enabled = enabled,
        shape = CircleShape,
        color = container,
        contentColor = content,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
