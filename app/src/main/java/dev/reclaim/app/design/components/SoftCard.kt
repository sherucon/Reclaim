package dev.reclaim.app.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import dev.reclaim.app.design.Elevation
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.ReclaimShapes
import dev.reclaim.app.design.Space

/**
 * The one floating surface in the app: white or a soft tone shift, 28 dp
 * corners, diffuse warm shadow. Never a hard border.
 */
@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Space.cardPadding),
    brush: Brush? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = Elevation.card,
                shape = ReclaimShapes.Card,
                ambientColor = ReclaimColors.Shadow,
                spotColor = ReclaimColors.Shadow,
            ).clip(ReclaimShapes.Card)
            .background(brush ?: Brush.verticalGradient(listOf(ReclaimColors.Surface, ReclaimColors.Surface)))
            .padding(contentPadding),
        content = content,
    )
}
