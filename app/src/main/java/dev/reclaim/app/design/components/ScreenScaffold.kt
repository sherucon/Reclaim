package dev.reclaim.app.design.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.Space

/**
 * The frame every screen shares: canvas, safe insets, one scroll column, an
 * optional floating dock. Screens only provide content.
 */
@Composable
fun ScreenScaffold(
    title: String,
    modifier: Modifier = Modifier,
    overline: String? = null,
    subtitle: String? = null,
    titleStyle: androidx.compose.ui.text.TextStyle? = null,
    headerActions: @Composable RowScope.() -> Unit = {},
    dock: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Space.screenHorizontal)
                .padding(bottom = if (dock != null) DockReservedHeight else Space.huge),
        ) {
            Spacer(Modifier.height(Space.xxl))

            if (overline != null) {
                Text(
                    text = overline,
                    style = MaterialTheme.typography.labelSmall,
                    color = ReclaimColors.InkTertiary,
                )
                Spacer(Modifier.height(Space.sm))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = titleStyle ?: MaterialTheme.typography.headlineLarge,
                    color = ReclaimColors.Ink,
                    modifier = Modifier.weight(1f),
                )
                headerActions()
            }

            if (subtitle != null) {
                Spacer(Modifier.height(Space.xs))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ReclaimColors.InkSecondary,
                )
            }

            Spacer(Modifier.height(Space.section))
            content()
        }

        if (dock != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = Space.lg),
            ) {
                dock()
            }
        }
    }
}

private val DockReservedHeight = 112.dp
