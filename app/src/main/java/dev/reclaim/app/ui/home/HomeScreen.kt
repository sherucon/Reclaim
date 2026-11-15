package dev.reclaim.app.ui.home

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.ReclaimGradients
import dev.reclaim.app.design.ReclaimTheme
import dev.reclaim.app.design.Space
import dev.reclaim.app.design.components.CircleIconButton
import dev.reclaim.app.design.components.IconBubble
import dev.reclaim.app.design.components.MascotBubble
import dev.reclaim.app.design.components.MiniBarChart
import dev.reclaim.app.design.components.PillButton
import dev.reclaim.app.design.components.PillStyle
import dev.reclaim.app.design.components.PillToggle
import dev.reclaim.app.design.components.ScreenScaffold
import dev.reclaim.app.design.components.SoftCard
import dev.reclaim.app.design.components.StatPill
import dev.reclaim.app.design.components.StatusRing
import dev.reclaim.app.design.icons.ReclaimIcons
import dev.reclaim.app.ui.common.rememberGreeting
import dev.reclaim.app.ui.common.rememberTodayLine
import dev.reclaim.app.ui.nav.ReclaimDestination
import dev.reclaim.app.ui.nav.ReclaimDock

// Demo values until the services feed the counters in Phase 4 and 6.
private val DemoWeek = listOf(2, 5, 3, 8, 4, 6, 3)

@Composable
fun HomeScreen(
    onNavigate: (ReclaimDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    var appsOn by rememberSaveable { mutableStateOf(true) }
    var reelsOn by rememberSaveable { mutableStateOf(true) }
    var webOn by rememberSaveable { mutableStateOf(false) }
    val activeProtections = listOf(appsOn, reelsOn, webOn).count { it }

    ScreenScaffold(
        modifier = modifier,
        overline = rememberTodayLine(),
        title = rememberGreeting(),
        titleStyle = MaterialTheme.typography.displayLarge,
        subtitle = "$activeProtections of three protections are on.",
        headerActions = {
            CircleIconButton(
                icon = ReclaimIcons.Settings,
                contentDescription = "Settings",
                onClick = { },
            )
        },
        dock = { ReclaimDock(ReclaimDestination.HOME, onNavigate) },
    ) {
        ProtectionCard(
            active = activeProtections,
            onAdjust = { onNavigate(ReclaimDestination.APPS) },
        )

        Spacer(Modifier.height(Space.cardGap))

        InterventionsCard()

        Spacer(Modifier.height(Space.section))

        FeatureCard(
            title = "Apps",
            detail = "Four apps and quiet evenings",
            icon = ReclaimIcons.Apps,
            tint = ReclaimColors.Peach.Deep,
            background = ReclaimColors.Peach.Soft,
            checked = appsOn,
            onCheckedChange = { appsOn = it },
        )
        Spacer(Modifier.height(Space.cardGap))
        FeatureCard(
            title = "Reels",
            detail = "Instagram, YouTube, TikTok",
            icon = ReclaimIcons.Reels,
            tint = ReclaimColors.Lilac.Deep,
            background = ReclaimColors.Lilac.Soft,
            checked = reelsOn,
            onCheckedChange = { reelsOn = it },
        )
        Spacer(Modifier.height(Space.cardGap))
        FeatureCard(
            title = "Web",
            detail = "Social sites filtered in every browser",
            icon = ReclaimIcons.Web,
            tint = ReclaimColors.Sky.Deep,
            background = ReclaimColors.Sky.Soft,
            checked = webOn,
            onCheckedChange = { webOn = it },
        )

        Spacer(Modifier.height(Space.section))

        MascotBubble(
            message = "Your evenings are quiet from 22:00. Want to start them an hour earlier?",
        )
    }
}

@Composable
private fun ProtectionCard(
    active: Int,
    onAdjust: () -> Unit,
) {
    SoftCard(brush = ReclaimGradients.SageMist) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Protection",
                    style = MaterialTheme.typography.labelMedium,
                    color = ReclaimColors.InkSecondary,
                )
                Spacer(Modifier.height(Space.sm))
                Text(
                    text = if (active == 3) "Everything is on" else "Almost there",
                    style = MaterialTheme.typography.titleLarge,
                    color = ReclaimColors.Ink,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (active == 3) {
                        "Reclaim is watching the scroll, the apps, and the web."
                    } else {
                        "One more switch and the whole day is covered."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReclaimColors.InkSecondary,
                )
                Spacer(Modifier.height(Space.lg))
                PillButton(
                    text = "Adjust",
                    onClick = onAdjust,
                    style = PillStyle.Secondary,
                    compact = true,
                )
            }
            Spacer(Modifier.width(Space.lg))
            StatusRing(
                progress = active / 3f,
                ringSize = 104.dp,
                strokeWidth = 10.dp,
                trackColor = ReclaimColors.Surface.copy(alpha = 0.6f),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$active of 3",
                        style = MaterialTheme.typography.titleMedium,
                        color = ReclaimColors.Ink,
                    )
                    Text(
                        text = "on",
                        style = MaterialTheme.typography.labelSmall,
                        color = ReclaimColors.InkTertiary,
                    )
                }
            }
        }
    }
}

@Composable
private fun InterventionsCard() {
    val total = DemoWeek.sum()
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "This week",
                    style = MaterialTheme.typography.labelMedium,
                    color = ReclaimColors.InkTertiary,
                )
                Spacer(Modifier.height(Space.xs))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$total",
                        style = MaterialTheme.typography.headlineLarge,
                        color = ReclaimColors.Ink,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "interventions",
                        style = MaterialTheme.typography.labelMedium,
                        color = ReclaimColors.InkTertiary,
                    )
                }
            }
            MiniBarChart(
                values = DemoWeek,
                highlightIndex = DemoWeek.lastIndex,
                height = 48.dp,
                barWidth = 8.dp,
                contentDescription = "Interventions on each of the last seven days",
            )
        }
        Spacer(Modifier.height(Space.md))
        Row(horizontalArrangement = Arrangement.spacedBy(Space.sm)) {
            StatPill(
                icon = ReclaimIcons.Apps,
                value = "12",
                label = "app blocks",
                tint = ReclaimColors.Peach.Deep,
            )
            StatPill(
                icon = ReclaimIcons.Reels,
                value = "19",
                label = "reels breaks",
                tint = ReclaimColors.Lilac.Deep,
            )
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    detail: String,
    @DrawableRes icon: Int,
    tint: Color,
    background: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    SoftCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconBubble(icon = icon, tint = tint, background = background)
            Spacer(Modifier.width(Space.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = ReclaimColors.Ink,
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReclaimColors.InkSecondary,
                )
            }
            PillToggle(
                checked = checked,
                onCheckedChange = onCheckedChange,
                contentDescription = title,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F4EF)
@Composable
private fun HomePreview() {
    ReclaimTheme {
        HomeScreen(onNavigate = {})
    }
}
