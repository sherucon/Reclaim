package dev.reclaim.app.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import dev.reclaim.app.core.permissions.PermissionChecker
import dev.reclaim.app.design.LocalSectionAccent
import dev.reclaim.app.design.PeachSection
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.ReclaimTheme
import dev.reclaim.app.design.Space
import dev.reclaim.app.design.components.CircularCheck
import dev.reclaim.app.design.components.IconBubble
import dev.reclaim.app.design.components.PermissionCard
import dev.reclaim.app.design.components.PillButton
import dev.reclaim.app.design.components.PillStyle
import dev.reclaim.app.design.components.PillToggle
import dev.reclaim.app.design.components.PlatformGlyph
import dev.reclaim.app.design.components.ScreenScaffold
import dev.reclaim.app.design.components.SoftCard
import dev.reclaim.app.design.components.SoftDialog
import dev.reclaim.app.design.icons.ReclaimIcons
import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.domain.model.Platform
import dev.reclaim.app.ui.common.SectionPreview
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AppsScreen(
    settings: AppSettings,
    onUpdate: ((AppSettings) -> AppSettings) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val permissions = remember { PermissionChecker(context) }
    var notificationsGranted by remember { mutableStateOf(permissions.isNotificationListenerEnabled()) }
    var accessibilityGranted by remember { mutableStateOf(permissions.isAppBlockerEnabled()) }
    var showPauseDialog by remember { mutableStateOf(false) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        notificationsGranted = permissions.isNotificationListenerEnabled()
        accessibilityGranted = permissions.isAppBlockerEnabled()
    }

    // Suppression only means anything while the listener is connected.
    LaunchedEffect(notificationsGranted) {
        if (settings.notificationsSuppressed != notificationsGranted) {
            onUpdate { it.copy(notificationsSuppressed = notificationsGranted) }
        }
    }

    val isPaused = settings.isPaused(Instant.now())
    val selectedKeys = Platform.entries
        .filter { platform -> platform.packages.any { it in settings.blockedPackages } }
        .map { it.key }
        .toSet()

    ScreenScaffold(
        modifier = modifier,
        title = "Apps",
        subtitle = "Choose what stays out of reach, and when.",
    ) {
        if (settings.appBlockingEnabled && !accessibilityGranted) {
            SoftCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBubble(
                        icon = ReclaimIcons.ShieldAlert,
                        tint = ReclaimColors.Blush.Deep,
                        background = ReclaimColors.Blush.Soft,
                    )
                    Spacer(Modifier.width(Space.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Blocking is paused",
                            style = MaterialTheme.typography.titleMedium,
                            color = ReclaimColors.Ink,
                        )
                        Text(
                            text = "Accessibility is off, so Reclaim cannot see which app is open.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ReclaimColors.InkSecondary,
                        )
                    }
                }
                Spacer(Modifier.height(Space.lg))
                PillButton(
                    text = "Turn accessibility on",
                    onClick = { context.startActivity(permissions.accessibilitySettingsIntent()) },
                    modifier = Modifier.fillMaxWidth(),
                    style = PillStyle.Secondary,
                )
            }

            Spacer(Modifier.height(Space.cardGap))
        }

        DowntimeCard(
            enabled = settings.downtime.enabled,
            onEnabledChange = { enabled ->
                onUpdate { it.copy(downtime = it.downtime.copy(enabled = enabled)) }
            },
            days = settings.downtime.days,
            onDayToggle = { day ->
                onUpdate { current ->
                    val days = current.downtime.days
                    current.copy(
                        downtime = current.downtime.copy(
                            days = if (day in days) days - day else days + day,
                        ),
                    )
                }
            },
        )

        Spacer(Modifier.height(Space.cardGap))

        BlockedAppsCard(
            apps = Platform.entries,
            selectedKeys = selectedKeys,
            onToggle = { platform ->
                onUpdate { current ->
                    val blocked = current.blockedPackages
                    val packages = platform.packages.toSet()
                    current.copy(
                        blockedPackages = if (packages.all { it in blocked }) {
                            blocked - packages
                        } else {
                            blocked + packages
                        },
                    )
                }
            },
        )

        Spacer(Modifier.height(Space.cardGap))

        PermissionCard(
            title = "Quiet notifications",
            description = "Blocked apps stop buzzing while a rule is on.",
            icon = ReclaimIcons.Bell,
            granted = notificationsGranted,
            actionLabel = if (notificationsGranted) "Open settings" else "Allow access",
            onAction = { context.startActivity(permissions.notificationSettingsIntent()) },
        )

        Spacer(Modifier.height(Space.cardGap))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(CircleShape)
                .clickable {
                    if (isPaused) {
                        onUpdate { it.copy(pauseUntil = null) }
                    } else {
                        showPauseDialog = true
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isPaused) {
                    val until = settings.pauseUntil?.atZone(ZoneId.systemDefault())?.toLocalTime()
                    "Paused until ${until?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "later"}. Tap to resume."
                } else {
                    "Pause everything for five minutes"
                },
                style = MaterialTheme.typography.labelLarge,
                color = ReclaimColors.InkSecondary,
            )
        }
    }

    if (showPauseDialog) {
        SoftDialog(
            title = "Pause everything?",
            body = "Blocking stops for five minutes, then comes back on its own.",
            confirmLabel = "Pause for five minutes",
            onConfirm = {
                showPauseDialog = false
                onUpdate { it.copy(pauseUntil = Instant.now().plusSeconds(5 * 60)) }
            },
            dismissLabel = "Not now",
            onDismiss = { showPauseDialog = false },
        )
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
                        .background(
                            if (selected) LocalSectionAccent.current.deep else ReclaimColors.Surface.copy(alpha = 0.7f),
                        ).clickable { onDayToggle(day) },
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
        SectionPreview(PeachSection) {
            AppsScreen(settings = AppSettings(), onUpdate = {})
        }
    }
}
