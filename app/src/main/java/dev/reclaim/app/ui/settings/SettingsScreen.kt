package dev.reclaim.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.reclaim.app.AppContainer
import dev.reclaim.app.BuildConfig
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.SageSection
import dev.reclaim.app.design.Space
import dev.reclaim.app.design.components.CircleIconButton
import dev.reclaim.app.design.components.IconBubble
import dev.reclaim.app.design.components.PillButton
import dev.reclaim.app.design.components.PillStyle
import dev.reclaim.app.design.components.PillToggle
import dev.reclaim.app.design.components.ScreenScaffold
import dev.reclaim.app.design.components.SoftCard
import dev.reclaim.app.design.icons.ReclaimIcons
import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.ui.nav.ReclaimBackdrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    container: AppContainer,
    onClose: () -> Unit,
) {
    val settings by produceState<AppSettings?>(initialValue = null) {
        container.settingsRepository.settings.collectLatest { value = it }
    }
    val scope = rememberCoroutineScope()
    val current = settings

    Box(modifier = Modifier.fillMaxSize()) {
        ReclaimBackdrop(accent = SageSection)

        ScreenScaffold(
            title = "Settings",
            headerActions = {
                CircleIconButton(
                    icon = ReclaimIcons.Close,
                    contentDescription = "Close settings",
                    onClick = onClose,
                )
            },
        ) {
            SoftCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBubble(
                        icon = ReclaimIcons.Shield,
                        tint = ReclaimColors.Sage.Deep,
                        background = ReclaimColors.Sage.Soft,
                    )
                    Spacer(Modifier.width(Space.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Anonymous analytics",
                            style = MaterialTheme.typography.titleMedium,
                            color = ReclaimColors.Ink,
                        )
                        Text(
                            text = "Usage events and crash reports. No apps, sites, or content.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ReclaimColors.InkSecondary,
                        )
                    }
                    PillToggle(
                        checked = current?.analyticsEnabled == true,
                        onCheckedChange = { enabled -> setAnalytics(scope, container, enabled) },
                        contentDescription = "Anonymous analytics",
                    )
                }
            }

            Spacer(Modifier.height(Space.cardGap))

            SoftCard {
                Text(
                    text = "Setup",
                    style = MaterialTheme.typography.titleMedium,
                    color = ReclaimColors.Ink,
                )
                Spacer(Modifier.height(Space.xs))
                Text(
                    text = "Run the questions again and rebuild your plan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ReclaimColors.InkSecondary,
                )
                Spacer(Modifier.height(Space.lg))
                PillButton(
                    text = "Run setup again",
                    onClick = {
                        scope.launch {
                            container.settingsRepository.update {
                                it.copy(onboardingComplete = false, onboardingStep = "welcome")
                            }
                            onClose()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    style = PillStyle.Secondary,
                )
            }

            Spacer(Modifier.height(Space.cardGap))

            SoftCard {
                Column(verticalArrangement = Arrangement.spacedBy(Space.xs)) {
                    Text(
                        text = "Reclaim ${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.titleMedium,
                        color = ReclaimColors.Ink,
                    )
                    Text(
                        text = "Free and open source under GPL-3.0. No account, no cloud, no ads.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ReclaimColors.InkSecondary,
                    )
                }
            }
        }
    }
}

private fun setAnalytics(
    scope: CoroutineScope,
    container: AppContainer,
    enabled: Boolean,
) {
    scope.launch {
        container.settingsRepository.update { it.copy(analyticsEnabled = enabled) }
        container.analytics.setEnabled(enabled)
        container.crashReporter.setEnabled(enabled)
        if (!enabled) container.analytics.reset()
    }
}
