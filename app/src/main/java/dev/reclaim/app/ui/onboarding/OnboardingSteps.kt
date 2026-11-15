package dev.reclaim.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.SageSection
import dev.reclaim.app.design.Space
import dev.reclaim.app.design.components.ChoiceCard
import dev.reclaim.app.design.components.IconBubble
import dev.reclaim.app.design.components.PermissionCard
import dev.reclaim.app.design.components.PillToggle
import dev.reclaim.app.design.components.PlatformGlyph
import dev.reclaim.app.design.components.SoftCard
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
internal fun StepHeader(
    title: String,
    helper: String? = null,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.displaySmall,
        color = ReclaimColors.Ink,
    )
    if (helper != null) {
        Spacer(Modifier.height(Space.sm))
        Text(
            text = helper,
            style = MaterialTheme.typography.bodyLarge,
            color = ReclaimColors.InkSecondary,
        )
    }
}

@Composable
internal fun WelcomeStep() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(ReclaimColors.Sage.Soft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(ReclaimIcons.Shield),
                    contentDescription = null,
                    modifier = Modifier.size(76.dp),
                    tint = ReclaimColors.Sage.Deep,
                )
            }
        }
        Spacer(Modifier.height(Space.huge))
        StepHeader(
            title = "Let's make your phone feel lighter.",
            helper = "A few quick questions, then Reclaim handles the rest.",
        )
    }
}

@Composable
internal fun NameStep(
    name: String,
    onName: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        StepHeader(
            title = "What should we call you?",
            helper = "Only used for the greeting. It never leaves the phone.",
        )
        Spacer(Modifier.height(Space.xxl))
        dev.reclaim.app.design.components.SoftTextField(
            value = name,
            onValueChange = onName,
            placeholder = "Your name",
        )
    }
}

@Composable
internal fun AppsStep(
    selected: Set<String>,
    onToggle: (Platform) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        StepHeader(
            title = "Which apps pull you in?",
            helper = "Pick as many as you like. You can change this later.",
        )
        Spacer(Modifier.height(Space.xxl))
        Platform.entries.forEach { platform ->
            ChoiceCard(
                title = platform.displayName,
                selected = platform.key in selected,
                onClick = { onToggle(platform) },
                leading = { PlatformGlyph(platform = platform, size = 36.dp) },
            )
            Spacer(Modifier.height(Space.cardGap))
        }
    }
}

@Composable
internal fun StruggleStep(
    selected: Set<Struggle>,
    onToggle: (Struggle) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        StepHeader(
            title = "Where does it hit hardest?",
            helper = "This shapes what Reclaim sets up first.",
        )
        Spacer(Modifier.height(Space.xxl))
        Struggle.entries.forEach { struggle ->
            ChoiceCard(
                title = struggle.title,
                detail = struggle.detail,
                selected = struggle in selected,
                onClick = { onToggle(struggle) },
                leading = {
                    IconBubble(
                        icon = struggleIcon(struggle),
                        tint = SageSection.deep,
                        background = SageSection.soft,
                    )
                },
            )
            Spacer(Modifier.height(Space.cardGap))
        }
    }
}

@Composable
internal fun DowntimeStep(
    selected: DowntimeChoice,
    onSelect: (DowntimeChoice) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        StepHeader(
            title = "When should we protect your evenings?",
            helper = "Reclaim can step in when you would rather be offline.",
        )
        Spacer(Modifier.height(Space.xxl))
        DowntimeChoice.entries.forEach { choice ->
            ChoiceCard(
                title = downtimeTitle(choice),
                detail = downtimeDetail(choice),
                selected = choice == selected,
                onClick = { onSelect(choice) },
                leading = {
                    IconBubble(
                        icon = if (choice == DowntimeChoice.OFF) ReclaimIcons.Sun else ReclaimIcons.Moon,
                        tint = SageSection.deep,
                        background = SageSection.soft,
                    )
                },
            )
            Spacer(Modifier.height(Space.cardGap))
        }
    }
}

@Composable
internal fun SensitivityStep(
    selected: Sensitivity,
    onSelect: (Sensitivity) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        StepHeader(
            title = "How much scrolling feels okay?",
            helper = "Gentle gives you more room. Strict steps in sooner.",
        )
        Spacer(Modifier.height(Space.xxl))
        Sensitivity.entries.forEach { sensitivity ->
            ChoiceCard(
                title = sensitivityTitle(sensitivity),
                detail = sensitivityDetail(sensitivity),
                selected = sensitivity == selected,
                onClick = { onSelect(sensitivity) },
                leading = {
                    IconBubble(
                        icon = ReclaimIcons.Reels,
                        tint = SageSection.deep,
                        background = SageSection.soft,
                    )
                },
            )
            Spacer(Modifier.height(Space.cardGap))
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
        StepHeader(title = title, helper = helper)
        Spacer(Modifier.height(Space.xxl))
        PermissionCard(
            title = "Access",
            description = footnote,
            icon = ReclaimIcons.Shield,
            granted = granted,
            actionLabel = actionLabel,
            onAction = onAction,
        )
    }
}

@Composable
internal fun WebSetupStep(
    selected: Set<DomainCategory>,
    onToggle: (DomainCategory) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        StepHeader(
            title = "Want to quiet the mobile web too?",
            helper = "Reclaim can block these sites in every browser on the phone.",
        )
        Spacer(Modifier.height(Space.xxl))
        DomainCategory.entries.forEach { category ->
            ChoiceCard(
                title = category.displayName,
                detail = categoryDetail(category),
                selected = category in selected,
                onClick = { onToggle(category) },
                leading = {
                    IconBubble(
                        icon = categoryIcon(category),
                        tint = SageSection.deep,
                        background = SageSection.soft,
                    )
                },
            )
            Spacer(Modifier.height(Space.cardGap))
        }
    }
}

@Composable
internal fun AnalyticsStep(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        StepHeader(
            title = "Help us make Reclaim better?",
            helper = "Share anonymous usage events and crash reports. Never your apps, sites, name, or content.",
        )
        Spacer(Modifier.height(Space.xxl))
        SoftCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBubble(
                    icon = ReclaimIcons.Info,
                    tint = SageSection.deep,
                    background = SageSection.soft,
                )
                Spacer(Modifier.width(Space.md))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Anonymous analytics",
                        style = MaterialTheme.typography.titleMedium,
                        color = ReclaimColors.Ink,
                    )
                    Text(
                        text = "Off by default. You can turn it off any time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ReclaimColors.InkSecondary,
                    )
                }
                PillToggle(
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                    contentDescription = "Anonymous analytics",
                )
            }
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
        StepHeader(
            title = "Your calm plan is ready.",
            helper = "You can change everything later in Settings.",
        )
        Spacer(Modifier.height(Space.xxl))
        SoftCard {
            Column(verticalArrangement = Arrangement.spacedBy(Space.md)) {
                lines.forEach { line ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(ReclaimIcons.Check),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = SageSection.deep,
                        )
                        Spacer(Modifier.width(Space.sm))
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodyLarge,
                            color = ReclaimColors.Ink,
                        )
                    }
                }
            }
        }
        if (!accessibilityGranted) {
            Spacer(Modifier.height(Space.cardGap))
            Text(
                text = "Accessibility is still off, so blocking stays paused until you turn it on.",
                style = MaterialTheme.typography.bodyMedium,
                color = ReclaimColors.InkSecondary,
            )
        }
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
        Sensitivity.BALANCED -> "One and a half screens, then a break"
        Sensitivity.STRICT -> "Less than one screen, stepped in early"
    }
