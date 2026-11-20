package dev.reclaim.app.ui.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.ReclaimTheme
import dev.reclaim.app.design.SageSection
import dev.reclaim.app.design.Space
import dev.reclaim.app.design.components.CircleIconButton
import dev.reclaim.app.design.components.IconBubble
import dev.reclaim.app.design.components.MiniBarChart
import dev.reclaim.app.design.components.PillButton
import dev.reclaim.app.design.components.PillStyle
import dev.reclaim.app.design.components.PillToggle
import dev.reclaim.app.design.components.ScreenScaffold
import dev.reclaim.app.design.components.SoftCard
import dev.reclaim.app.design.components.StatPill
import dev.reclaim.app.design.components.StatusRing
import dev.reclaim.app.design.icons.ReclaimIcons
import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.domain.model.PlatformRegistry
import dev.reclaim.app.ui.common.SectionPreview
import dev.reclaim.app.ui.common.rememberGreeting
import dev.reclaim.app.ui.nav.ReclaimDestination

// Demo values until the services feed the counters in Phase 4 and 6.
private val DemoWeek = listOf(2, 5, 3, 8, 4, 6, 3)

private fun countWord(count: Int): String =
    when (count) {
        0 -> "None"
        1 -> "One"
        2 -> "Two"
        else -> "All three"
    }

@Composable
fun HomeScreen(
    settings: AppSettings,
    onNavigate: (ReclaimDestination) -> Unit,
    onOpenSettings: () -> Unit,
    onUpdate: ((AppSettings) -> AppSettings) -> Unit,
    onFeatureToggled: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val appsOn = settings.appBlockingEnabled
    val reelsOn = settings.contentBlockingEnabled
    val webOn = settings.webBlockingEnabled
    val activeProtections = listOf(appsOn, reelsOn, webOn).count { it }
    val blockedApps = PlatformRegistry.all.count { platform ->
        platform.packages.any { it in settings.blockedPackages }
    }

    ScreenScaffold(
        modifier = modifier,
        title = rememberGreeting(),
        titleStyle = MaterialTheme.typography.displayLarge,
        subtitle = "${countWord(activeProtections)} of three protections are on.",
        headerActions = {
            CircleIconButton(
                icon = ReclaimIcons.Settings,
                contentDescription = "Settings",
                onClick = onOpenSettings,
            )
        },
    ) {
        ProtectionCard(
            active = activeProtections,
            onAdjust = { onNavigate(ReclaimDestination.APPS) },
        )

        Spacer(Modifier.height(Space.cardGap))

        InterventionsCard()

        Spacer(Modifier.height(Space.cardGap))

        FeatureCard(
            title = "Apps",
            detail = if (blockedApps == 0) "Nothing blocked yet" else "$blockedApps apps and quiet evenings",
            icon = ReclaimIcons.Apps,
            tint = ReclaimColors.Peach.Deep,
            background = ReclaimColors.Peach.Soft,
            checked = appsOn,
            onCheckedChange = { enabled ->
                onFeatureToggled("app_blocking", enabled)
                onUpdate { it.copy(appBlockingEnabled = enabled) }
            },
        )
        Spacer(Modifier.height(Space.cardGap))
        FeatureCard(
            title = "Reels",
            detail = "Instagram, YouTube, TikTok",
            icon = ReclaimIcons.Reels,
            tint = ReclaimColors.Lilac.Deep,
            background = ReclaimColors.Lilac.Soft,
            checked = reelsOn,
            onCheckedChange = { enabled ->
                onFeatureToggled("reels_blocking", enabled)
                onUpdate { it.copy(contentBlockingEnabled = enabled) }
            },
        )
        Spacer(Modifier.height(Space.cardGap))
        FeatureCard(
            title = "Web",
            detail = "Social sites filtered in every browser",
            icon = ReclaimIcons.Web,
            tint = ReclaimColors.Sky.Deep,
            background = ReclaimColors.Sky.Soft,
            checked = webOn,
            onCheckedChange = { enabled ->
                onFeatureToggled("web_blocking", enabled)
                onUpdate { it.copy(webBlockingEnabled = enabled) }
            },
        )
    }
}

@Composable
private fun ProtectionCard(
    active: Int,
    onAdjust: () -> Unit,
) {
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
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
                trackColor = ReclaimColors.CanvasRaised,
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
    icon: Int,
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
        SectionPreview(SageSection) {
            HomeScreen(
                settings = AppSettings(),
                onNavigate = {},
                onOpenSettings = {},
                onUpdate = {},
                onFeatureToggled = { _, _ -> },
            )
        }
    }
}
