package dev.reclaim.app.ui.reels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.ReclaimGradients
import dev.reclaim.app.design.ReclaimTheme
import dev.reclaim.app.design.Space
import dev.reclaim.app.design.components.IconBubble
import dev.reclaim.app.design.components.MiniBarChart
import dev.reclaim.app.design.components.PillToggle
import dev.reclaim.app.design.components.PlatformGlyph
import dev.reclaim.app.design.components.ScreenScaffold
import dev.reclaim.app.design.components.SegmentedPills
import dev.reclaim.app.design.components.SoftCard
import dev.reclaim.app.design.components.StatPill
import dev.reclaim.app.design.components.StatusRing
import dev.reclaim.app.design.icons.ReclaimIcons
import dev.reclaim.app.domain.model.Platform
import dev.reclaim.app.domain.model.Sensitivity
import dev.reclaim.app.ui.nav.ReclaimDestination
import dev.reclaim.app.ui.nav.ReclaimDock

private val DemoBreaks = listOf(1, 4, 2, 6, 3, 5, 2)

@Composable
fun ReelsScreen(
    onNavigate: (ReclaimDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sensitivity by rememberSaveable { mutableStateOf(Sensitivity.BALANCED) }
    var instagram by rememberSaveable { mutableStateOf(true) }
    var youtube by rememberSaveable { mutableStateOf(true) }
    var tiktok by rememberSaveable { mutableStateOf(false) }

    ScreenScaffold(
        modifier = modifier,
        title = "Reels",
        subtitle = "A budget for short video, applied where you actually scroll.",
        dock = { ReclaimDock(ReclaimDestination.REELS, onNavigate) },
    ) {
        BudgetCard(sensitivity = sensitivity)

        Spacer(Modifier.height(Space.cardGap))

        SegmentedPills(
            options = Sensitivity.entries.toList(),
            selected = sensitivity,
            onSelect = { sensitivity = it },
            label = ::sensitivityLabel,
        )

        Spacer(Modifier.height(Space.cardGap))

        SoftCard {
            PlatformRow(
                platform = Platform.INSTAGRAM,
                checked = instagram,
                onCheckedChange = { instagram = it },
            )
            PlatformRow(
                platform = Platform.YOUTUBE,
                checked = youtube,
                onCheckedChange = { youtube = it },
            )
            PlatformRow(
                platform = Platform.TIKTOK,
                checked = tiktok,
                onCheckedChange = { tiktok = it },
            )
        }

        Spacer(Modifier.height(Space.cardGap))

        BreaksCard()

        Spacer(Modifier.height(Space.cardGap))

        SoftCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBubble(
                    icon = ReclaimIcons.Shield,
                    tint = ReclaimColors.Sage.Deep,
                    background = ReclaimColors.Sage.Soft,
                )
                Spacer(Modifier.width(Space.md))
                Text(
                    text = "Chats, comments, and stories are left alone. Reclaim only steps in on the endless part.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReclaimColors.InkSecondary,
                )
            }
        }
    }
}

@Composable
private fun BudgetCard(sensitivity: Sensitivity) {
    SoftCard(brush = ReclaimGradients.LilacMist) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Scroll budget",
                    style = MaterialTheme.typography.labelMedium,
                    color = ReclaimColors.InkSecondary,
                )
                Spacer(Modifier.height(Space.sm))
                Text(
                    text = sensitivityLabel(sensitivity),
                    style = MaterialTheme.typography.titleLarge,
                    color = ReclaimColors.Ink,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = budgetDescription(sensitivity),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReclaimColors.InkSecondary,
                )
            }
            Spacer(Modifier.width(Space.lg))
            StatusRing(
                progress = sensitivity.budgetScreens / Sensitivity.RELAXED.budgetScreens,
                ringSize = 100.dp,
                trackColor = ReclaimColors.Surface.copy(alpha = 0.6f),
                progressColor = ReclaimColors.Lilac.Deep,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatScreens(sensitivity.budgetScreens),
                        style = MaterialTheme.typography.titleLarge,
                        color = ReclaimColors.Ink,
                    )
                    Text(
                        text = "screens",
                        style = MaterialTheme.typography.labelSmall,
                        color = ReclaimColors.InkTertiary,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlatformRow(
    platform: Platform,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlatformGlyph(platform = platform, size = 36.dp)
        Spacer(Modifier.width(Space.md))
        Text(
            text = platform.displayName,
            style = MaterialTheme.typography.bodyLarge,
            color = ReclaimColors.Ink,
            modifier = Modifier.weight(1f),
        )
        PillToggle(
            checked = checked,
            onCheckedChange = onCheckedChange,
            contentDescription = platform.displayName,
        )
    }
}

@Composable
private fun BreaksCard() {
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Breaks this week",
                    style = MaterialTheme.typography.labelMedium,
                    color = ReclaimColors.InkTertiary,
                )
                Spacer(Modifier.height(Space.xs))
                Text(
                    text = "${DemoBreaks.sum()}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = ReclaimColors.Ink,
                )
            }
            MiniBarChart(
                values = DemoBreaks,
                highlightIndex = DemoBreaks.lastIndex,
                barColor = ReclaimColors.Lilac.Deep.copy(alpha = 0.25f),
                highlightColor = ReclaimColors.Lilac.Deep,
                height = 48.dp,
                barWidth = 8.dp,
                contentDescription = "Reels breaks on each of the last seven days",
            )
        }
        Spacer(Modifier.height(Space.md))
        Row(horizontalArrangement = Arrangement.spacedBy(Space.sm)) {
            StatPill(
                icon = ReclaimIcons.Reels,
                value = "${DemoBreaks.sum()}",
                label = "breaks",
                tint = ReclaimColors.Lilac.Deep,
            )
            StatPill(
                icon = ReclaimIcons.Clock,
                value = "30",
                label = "second cooldown",
                tint = ReclaimColors.InkTertiary,
            )
        }
    }
}

private fun sensitivityLabel(sensitivity: Sensitivity): String =
    when (sensitivity) {
        Sensitivity.RELAXED -> "Gentle"
        Sensitivity.BALANCED -> "Balanced"
        Sensitivity.STRICT -> "Strict"
    }

private fun budgetDescription(sensitivity: Sensitivity): String =
    when (sensitivity) {
        Sensitivity.RELAXED -> "Three screens before a pause"
        Sensitivity.BALANCED -> "One and a half screens, then a break"
        Sensitivity.STRICT -> "Less than one screen, stepped in early"
    }

private fun formatScreens(screens: Float): String =
    if (screens % 1f == 0f) screens.toInt().toString() else screens.toString()

@Preview(showBackground = true, backgroundColor = 0xFFF6F4EF)
@Composable
private fun ReelsPreview() {
    ReclaimTheme {
        ReelsScreen(onNavigate = {})
    }
}
