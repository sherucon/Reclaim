package dev.reclaim.app.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.ReclaimTheme
import dev.reclaim.app.design.Space
import dev.reclaim.app.design.components.CircularCheck
import dev.reclaim.app.design.components.IconBubble
import dev.reclaim.app.design.components.PermissionCard
import dev.reclaim.app.design.components.PillToggle
import dev.reclaim.app.design.components.PlatformGlyph
import dev.reclaim.app.design.components.ScreenScaffold
import dev.reclaim.app.design.components.SoftCard
import dev.reclaim.app.design.icons.ReclaimIcons
import dev.reclaim.app.domain.model.Platform
import dev.reclaim.app.ui.nav.ReclaimBackdrop
import dev.reclaim.app.ui.nav.ReclaimDestination
import dev.reclaim.app.ui.nav.ReclaimDock
import java.time.DayOfWeek

// Demo selection until the app picker lands with real package names.
private val DemoApps = listOf(
    Platform.INSTAGRAM,
    Platform.YOUTUBE,
    Platform.TIKTOK,
    Platform.REDDIT,
)

@Composable
fun AppsScreen(
    onNavigate: (ReclaimDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    var downtimeOn by rememberSaveable { mutableStateOf(true) }
    var notificationsOn by rememberSaveable { mutableStateOf(false) }
    var paused by rememberSaveable { mutableStateOf(false) }
    var selectedDays by rememberSaveable { mutableStateOf(DayOfWeek.entries.toSet()) }
    var selectedApps by rememberSaveable { mutableStateOf(DemoApps.map { it.key }.toSet()) }

    ScreenScaffold(
        modifier = modifier,
        title = "Apps",
        subtitle = "Choose what stays out of reach, and when.",
        dock = { ReclaimDock(ReclaimDestination.APPS, onNavigate) },
    ) {
        DowntimeCard(
            enabled = downtimeOn,
            onEnabledChange = { downtimeOn = it },
            days = selectedDays,
            onDayToggle = { day ->
                selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
            },
        )

        Spacer(Modifier.height(Space.cardGap))

        BlockedAppsCard(
            apps = DemoApps,
            selectedKeys = selectedApps,
            onToggle = { platform ->
                selectedApps = if (platform.key in selectedApps) {
                    selectedApps - platform.key
                } else {
                    selectedApps + platform.key
                }
            },
        )

        Spacer(Modifier.height(Space.cardGap))

        PermissionCard(
            title = "Quiet notifications",
            description = "Blocked apps stop buzzing while a rule is on.",
            icon = ReclaimIcons.Bell,
            granted = notificationsOn,
            actionLabel = if (notificationsOn) "Turn off" else "Allow access",
            onAction = { notificationsOn = !notificationsOn },
        )

        Spacer(Modifier.height(Space.cardGap))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(CircleShape)
                .clickable { paused = !paused },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (paused) {
                    "Paused for five minutes. Tap to resume."
                } else {
                    "Pause everything for five minutes"
                },
                style = MaterialTheme.typography.labelLarge,
                color = ReclaimColors.InkSecondary,
            )
        }
    }
}

@Composable
private fun DowntimeCard(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    days: Set<DayOfWeek>,
    onDayToggle: (DayOfWeek) -> Unit,
) {
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble(
                icon = ReclaimIcons.Moon,
                tint = ReclaimColors.Butter.Deep,
                background = ReclaimColors.Butter.Soft,
            )
            Spacer(Modifier.width(Space.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Evenings",
                    style = MaterialTheme.typography.titleMedium,
                    color = ReclaimColors.Ink,
                )
                Text(
                    text = "22:00 to 07:00",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReclaimColors.InkSecondary,
                )
            }
            PillToggle(
                checked = enabled,
                onCheckedChange = onEnabledChange,
                contentDescription = "Evenings",
            )
        }

        Spacer(Modifier.height(Space.lg))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            DayOfWeek.entries.forEach { day ->
                val selected = day in days
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(CircleShape)
                        .background(if (selected) ReclaimColors.Ink else ReclaimColors.Surface.copy(alpha = 0.7f))
                        .clickable { onDayToggle(day) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = day.name.take(1) + day.name
                            .drop(1)
                            .lowercase()
                            .take(1),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) ReclaimColors.Surface else ReclaimColors.InkSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun BlockedAppsCard(
    apps: List<Platform>,
    selectedKeys: Set<String>,
    onToggle: (Platform) -> Unit,
) {
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Blocked apps",
                style = MaterialTheme.typography.titleMedium,
                color = ReclaimColors.Ink,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${selectedKeys.size} of ${apps.size}",
                style = MaterialTheme.typography.labelMedium,
                color = ReclaimColors.InkTertiary,
            )
        }

        apps.forEach { platform ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
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
                CircularCheck(
                    checked = platform.key in selectedKeys,
                    onClick = { onToggle(platform) },
                    contentDescription = platform.displayName,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F4EF)
@Composable
private fun AppsPreview() {
    ReclaimTheme {
        Box(Modifier.fillMaxSize()) {
            ReclaimBackdrop(ReclaimDestination.APPS)
            AppsScreen(onNavigate = {})
        }
    }
}
