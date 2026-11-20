package dev.reclaim.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.OnboardingColors
import dev.reclaim.app.design.OnboardingSpace
import dev.reclaim.app.design.OnboardingType
import dev.reclaim.app.design.icons.ReclaimIcons
import dev.reclaim.app.domain.model.DomainCategory
import dev.reclaim.app.domain.model.Platform
import dev.reclaim.app.domain.model.Sensitivity
import dev.reclaim.app.domain.onboarding.DowntimeChoice
import dev.reclaim.app.domain.onboarding.OnboardingAnswers
import dev.reclaim.app.domain.onboarding.OnboardingPlan
import dev.reclaim.app.domain.onboarding.Struggle

/**
 * One question per screen, one input pattern each. The flow file decides which
 * one is visible; nothing here knows about step order.
 */
@Composable
internal fun WelcomeStep() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = OnboardingSpace.Text)) {
        OnboardingWordmark()
        Spacer(Modifier.height(94.dp))
        Text(
            text = "Let’s make your\nphone feel lighter",
            style = OnboardingType.Title,
            color = OnboardingColors.Paper,
        )
        Spacer(Modifier.height(6.67.dp))
        Text(
            text = "A few quick questions, then Reclaim handles\nthe rest",
            style = OnboardingType.Body,
            color = OnboardingColors.Paper,
        )
    }
}

@Composable
internal fun NameStep(
    name: String,
    onName: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OnboardingStepHeader(title = "What should we\ncall you?")
        Column(modifier = Modifier.padding(horizontal = OnboardingSpace.Card)) {
            OnboardingTextField(value = name, onValueChange = onName)
        }
    }
}

@Composable
internal fun AppsStep(
    selected: Set<String>,
    onToggle: (Platform) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(top = 117.33.dp)) {
        OnboardingStepHeader(
            title = "Which apps pull\nyou in?",
            helper = "Pick as many as you like, you can change\nthese later",
            minHeight = 125.67.dp,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds()
                .verticalEdgeFade(top = 20.dp, bottom = 20.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = OnboardingSpace.Card)
                    .padding(top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(OnboardingSpace.CardGap),
            ) {
                Platform.entries.forEach { platform ->
                    OnboardingChoice(
                        title = platform.displayName,
                        selected = platform.key in selected,
                        onClick = { onToggle(platform) },
                        leading = { OnboardingPlatformMark(platform = platform) },
                    )
                }
            }
        }
    }
}

@Composable
internal fun StruggleStep(
    selected: Set<Struggle>,
    onToggle: (Struggle) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OnboardingStepHeader(
            title = "Where does it hit\nthe hardest?",
            helper = "This shapes what Reclaim sets up first",
        )
        Choices {
            Struggle.entries.forEach { struggle ->
                OnboardingChoice(
                    title = struggle.title,
                    selected = struggle in selected,
                    onClick = { onToggle(struggle) },
                    leading = { OnboardingGlyph(icon = struggleIcon(struggle)) },
                )
            }
        }
    }
}

@Composable
internal fun DowntimeStep(
    selected: DowntimeChoice,
    onSelect: (DowntimeChoice) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OnboardingStepHeader(
            title = "When should we\nprotect your evenings?",
            helper = "Reclaim can step in when you would rather be\noffline.",
        )
        Choices {
            DowntimeChoice.entries.forEach { choice ->
                OnboardingChoice(
                    title = downtimeTitle(choice),
                    detail = downtimeDetail(choice),
                    selected = choice == selected,
                    onClick = { onSelect(choice) },
                    leading = {
                        OnboardingGlyph(
                            icon = if (choice == DowntimeChoice.OFF) {
                                ReclaimIcons.Settings
                            } else {
                                ReclaimIcons.Moon
                            },
                        )
                    },
                )
            }
        }
    }
}

@Composable
internal fun SensitivityStep(
    selected: Sensitivity,
    onSelect: (Sensitivity) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OnboardingStepHeader(
            title = "How much scrolling\nfeels okay?",
            helper = "Gentle gives you more room. Strict steps in sooner.",
        )
        Choices {
            Sensitivity.entries.forEach { sensitivity ->
                OnboardingChoice(
                    title = sensitivityTitle(sensitivity),
                    detail = sensitivityDetail(sensitivity),
                    selected = sensitivity == selected,
                    onClick = { onSelect(sensitivity) },
                    leading = { OnboardingGlyph(icon = ReclaimIcons.Reels) },
                )
            }
        }
    }
}

@Composable
internal fun PermissionStep(
    title: String,
    helper: String,
    granted: Boolean,
    actionLabel: String,
    onAction: () -> Unit,
    footnote: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OnboardingStepHeader(
            title = title,
            helper = helper,
            minHeight = 188.34.dp,
        )
        Column(modifier = Modifier.padding(horizontal = OnboardingSpace.Card)) {
            OnboardingPermissionCard(
                description = footnote,
                granted = granted,
                actionLabel = actionLabel,
                onAction = onAction,
            )
        }
    }
}

@Composable
internal fun WebSetupStep(
    selected: Set<DomainCategory>,
    onToggle: (DomainCategory) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OnboardingStepHeader(
            title = "Want to quiet the\nmobile web too?",
            helper = "Reclaim can block these sites in every browser\non the phone.",
        )
        Choices {
            DomainCategory.entries.forEach { category ->
                OnboardingChoice(
                    title = category.displayName,
                    detail = categoryDetail(category),
                    selected = category in selected,
                    onClick = { onToggle(category) },
                    leading = { OnboardingGlyph(icon = categoryIcon(category)) },
                )
            }
        }
    }
}

@Composable
internal fun AnalyticsStep(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OnboardingStepHeader(
            title = "Help us make Reclaim\nbetter?",
            helper = "Share anonymous usage events and crash reports.\nNever your apps, sites, name, or content.",
        )
        Choices {
            OnboardingChoice(
                title = "Anonymous analytics",
                selected = enabled,
                onClick = { onEnabledChange(!enabled) },
                leading = { OnboardingGlyph(icon = ReclaimIcons.Info) },
            )
        }
    }
}

@Composable
internal fun SummaryStep(
    answers: OnboardingAnswers,
    accessibilityGranted: Boolean,
) {
    val lines = OnboardingPlan.summaryLines(answers)

    Column(modifier = Modifier.fillMaxWidth()) {
        OnboardingStepHeader(
            title = "Your calm plan is\nready.",
            helper = "You can change everything later in settings.",
        )
        Column(modifier = Modifier.padding(horizontal = OnboardingSpace.Card)) {
            OnboardingSummaryCard(lines = lines)
        }
        if (!accessibilityGranted) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Accessibility is still off, so blocking stays paused\nuntil you turn it on.",
                style = OnboardingType.Body,
                color = OnboardingColors.Paper,
                modifier = Modifier.padding(horizontal = OnboardingSpace.Text),
            )
        }
    }
}

@Composable
private fun Choices(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = OnboardingSpace.Card),
        verticalArrangement = Arrangement.spacedBy(OnboardingSpace.CardGap),
    ) {
        content()
    }
}

private fun struggleIcon(struggle: Struggle): Int =
    when (struggle) {
        Struggle.SCROLLING -> ReclaimIcons.Reels
        Struggle.NOTIFICATIONS -> ReclaimIcons.Bell
        Struggle.OPENING_APPS -> ReclaimIcons.Apps
        Struggle.WEBSITES -> ReclaimIcons.Web
    }

private fun categoryIcon(category: DomainCategory): Int =
    when (category) {
        DomainCategory.SOCIAL -> ReclaimIcons.Users
        DomainCategory.ADULT -> ReclaimIcons.ShieldAlert
        DomainCategory.GAMBLING -> ReclaimIcons.Banknotes
    }

private fun categoryDetail(category: DomainCategory): String =
    when (category) {
        DomainCategory.SOCIAL -> "Instagram, YouTube, X, and more"
        DomainCategory.ADULT -> "Known adult sites"
        DomainCategory.GAMBLING -> "Betting and casino sites"
    }

private fun downtimeTitle(choice: DowntimeChoice): String =
    when (choice) {
        DowntimeChoice.EVENINGS -> "Evenings"
        DowntimeChoice.LATE_NIGHT -> "Late night"
        DowntimeChoice.OFF -> "Not now"
    }

private fun downtimeDetail(choice: DowntimeChoice): String =
    when (choice) {
        DowntimeChoice.EVENINGS -> "22:00 to 07:00, every day"
        DowntimeChoice.LATE_NIGHT -> "23:00 to 06:00, every day"
        DowntimeChoice.OFF -> "You can set a window later"
    }

private fun sensitivityTitle(sensitivity: Sensitivity): String =
    when (sensitivity) {
        Sensitivity.RELAXED -> "Gentle"
        Sensitivity.BALANCED -> "Balanced"
        Sensitivity.STRICT -> "Strict"
    }

private fun sensitivityDetail(sensitivity: Sensitivity): String =
    when (sensitivity) {
        Sensitivity.RELAXED -> "Three screens before a pause"
        Sensitivity.BALANCED -> "One and a half screens, then a\nbreak"
        Sensitivity.STRICT -> "Less than one screen, stepped\nin early"
    }
