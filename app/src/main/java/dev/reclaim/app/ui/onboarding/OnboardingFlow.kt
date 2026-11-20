package dev.reclaim.app.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.reclaim.app.AppContainer
import dev.reclaim.app.analytics.AnalyticsEvents
import dev.reclaim.app.core.permissions.PermissionChecker
import dev.reclaim.app.data.onboarding.SettingsOnboardingStore
import dev.reclaim.app.design.OnboardingSpace
import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.domain.onboarding.OnboardingPlan
import dev.reclaim.app.domain.onboarding.OnboardingStep

private data class Grants(
    val app: Boolean,
    val content: Boolean,
    val notifications: Boolean,
)

@Composable
fun OnboardingFlow(
    container: AppContainer,
    settings: AppSettings,
) {
    val context = LocalContext.current
    val permissions = remember { PermissionChecker(context) }
    val store = remember { SettingsOnboardingStore(container.settingsRepository) }

    val initialAnswers = remember { SettingsOnboardingStore.answersFrom(settings) }
    val initialStep = remember { OnboardingPlan.resumeAt(settings.onboardingStep, initialAnswers) }

    val viewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModel.factory(
            store = store,
            analytics = container.analytics,
            initialAnswers = initialAnswers,
            initialStep = initialStep,
        ),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    var grants by remember { mutableStateOf(readGrants(permissions)) }
    var pendingPermission by remember { mutableStateOf<String?>(null) }
    var direction by remember { mutableIntStateOf(1) }

    fun logPrompt(permission: String) {
        pendingPermission = permission
        container.analytics.logEvent(
            AnalyticsEvents.PERMISSION_PROMPTED,
            mapOf(AnalyticsEvents.Params.PERMISSION to permission),
        )
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        grants = readGrants(permissions)
        pendingPermission?.let { permission ->
            container.analytics.logEvent(
                AnalyticsEvents.PERMISSION_RESULT,
                mapOf(
                    AnalyticsEvents.Params.PERMISSION to permission,
                    AnalyticsEvents.Params.GRANTED to grants.granted(permission),
                ),
            )
            pendingPermission = null
        }
    }

    OnboardingScaffold(
        showNav = state.step != OnboardingStep.WELCOME,
        canGoBack = state.canGoBack,
        onBack = {
            direction = -1
            viewModel.back()
        },
        primaryLabel = primaryLabel(state.step),
        onPrimary = {
            if (state.isSummary) {
                viewModel.finish(accessibilityGranted = grants.app)
            } else {
                direction = 1
                viewModel.next(optionCode = optionCode(state))
            }
        },
    ) {
        AnimatedContent(
            targetState = state.step,
            transitionSpec = {
                (
                    slideInHorizontally(animationSpec = tween(durationMillis = 320)) { width ->
                        direction * width / 5
                    } + fadeIn(animationSpec = tween(durationMillis = 220))
                ) togetherWith (
                    slideOutHorizontally(animationSpec = tween(durationMillis = 320)) { width ->
                        -direction * width / 5
                    } + fadeOut(animationSpec = tween(durationMillis = 180))
                )
            },
            label = "onboardingStep",
            modifier = Modifier.fillMaxSize(),
        ) { step ->
            val topPadding = if (step == OnboardingStep.WELCOME) 178.dp else 117.33.dp
            when (step) {
                OnboardingStep.WELCOME -> StepScroll(topPadding) { WelcomeStep() }

                OnboardingStep.NAME -> StepScroll(topPadding) {
                    NameStep(state.answers.name) { name ->
                        viewModel.updateAnswers { it.copy(name = name) }
                    }
                }

                OnboardingStep.APPS -> AppsStep(state.answers.platforms) { platform ->
                    viewModel.updateAnswers { answers ->
                        answers.copy(platforms = answers.platforms.toggle(platform.key))
                    }
                }

                OnboardingStep.STRUGGLE -> StepScroll(topPadding) {
                    StruggleStep(state.answers.struggles) { struggle ->
                        viewModel.updateAnswers { answers ->
                            answers.copy(struggles = answers.struggles.toggle(struggle))
                        }
                    }
                }

                OnboardingStep.DOWNTIME -> StepScroll(topPadding) {
                    DowntimeStep(state.answers.downtime) { choice ->
                        viewModel.updateAnswers { it.copy(downtime = choice) }
                    }
                }

                OnboardingStep.SENSITIVITY -> StepScroll(topPadding) {
                    SensitivityStep(state.answers.sensitivity) { sensitivity ->
                        viewModel.updateAnswers { it.copy(sensitivity = sensitivity) }
                    }
                }

                OnboardingStep.APP_PERMISSION -> StepScroll(topPadding) {
                    PermissionStep(
                        title = "Let Reclaim step in\nwhen a blocked app\nopens.",
                        helper = "Android needs accessibility access for this.\n" +
                            "Reclaim only checks which app is on screen.",
                        granted = grants.app,
                        actionLabel = if (grants.app) "Open settings" else "Allow access",
                        onAction = {
                            logPrompt("accessibility_app")
                            context.startActivity(permissions.accessibilitySettingsIntent())
                        },
                        footnote = "Reclaim never reads what you\ntype or see.",
                    )
                }

                OnboardingStep.CONTENT_PERMISSION -> StepScroll(topPadding) {
                    PermissionStep(
                        title = "Let Reclaim notice\nwhen you are in Reels\nor Shorts.",
                        helper = "The same access, used only to spot short-video\nsurfaces inside the apps you chose.",
                        granted = grants.content,
                        actionLabel = if (grants.content) "Open settings" else "Allow access",
                        onAction = {
                            logPrompt("accessibility_content")
                            context.startActivity(permissions.accessibilitySettingsIntent())
                        },
                        footnote = "Chats, comments, and\nstories are left alone.",
                    )
                }

                OnboardingStep.WEB_SETUP -> StepScroll(topPadding) {
                    WebSetupStep(state.answers.webCategories) { category ->
                        viewModel.updateAnswers { answers ->
                            answers.copy(webCategories = answers.webCategories.toggle(category))
                        }
                    }
                }

                OnboardingStep.NOTIFICATIONS -> StepScroll(topPadding) {
                    PermissionStep(
                        title = "Should blocked apps\nstay quiet?",
                        helper = "With notification access, a blocked app cannot\nbuzz you while a rule is on.",
                        granted = grants.notifications,
                        actionLabel = if (grants.notifications) "Open settings" else "Allow access",
                        onAction = {
                            logPrompt("notifications")
                            context.startActivity(permissions.notificationSettingsIntent())
                        },
                        footnote = "Reclaim never reads the\ncontent of a notification.",
                    )
                }

                OnboardingStep.ANALYTICS -> StepScroll(topPadding) {
                    AnalyticsStep(state.answers.analyticsEnabled) { enabled ->
                        viewModel.updateAnswers { it.copy(analyticsEnabled = enabled) }
                    }
                }

                OnboardingStep.SUMMARY -> StepScroll(topPadding) {
                    SummaryStep(
                        answers = state.answers,
                        accessibilityGranted = grants.app,
                    )
                }
            }
        }
    }
}

@Composable
private fun StepScroll(
    topPadding: Dp,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = topPadding)
            .padding(bottom = 24.dp),
    ) {
        content()
    }
}

@Composable
private fun OnboardingScaffold(
    showNav: Boolean,
    canGoBack: Boolean,
    onBack: () -> Unit,
    primaryLabel: String,
    onPrimary: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingBackdrop()
        OnboardingLightSystemBars()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            if (showNav) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = OnboardingSpace.Text, end = OnboardingSpace.Text, top = 9.33.dp)
                        .heightIn(min = 50.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (canGoBack) {
                        OnboardingBackButton(onClick = onBack)
                    }
                    Spacer(Modifier.weight(1f))
                    OnboardingWordmark()
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                content()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = OnboardingSpace.Card),
            ) {
                OnboardingPrimaryButton(text = primaryLabel, onClick = onPrimary)
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

private fun readGrants(permissions: PermissionChecker): Grants =
    Grants(
        app = permissions.isAppBlockerEnabled(),
        content = permissions.isContentBlockerEnabled(),
        notifications = permissions.isNotificationListenerEnabled(),
    )

private fun Grants.granted(permission: String): Boolean =
    when (permission) {
        "accessibility_app" -> app
        "accessibility_content" -> content
        "notifications" -> notifications
        else -> false
    }

private fun primaryLabel(step: OnboardingStep): String =
    when (step) {
        OnboardingStep.WELCOME -> "Let’s begin"
        OnboardingStep.SUMMARY -> "Start using Reclaim"
        else -> "Continue"
    }

private fun optionCode(state: OnboardingViewModel.UiState): String? {
    val answers = state.answers
    return when (state.step) {
        OnboardingStep.NAME -> if (answers.name.isBlank()) "skipped" else "named"
        OnboardingStep.APPS -> countBucket(answers.platforms.size)
        OnboardingStep.STRUGGLE -> countBucket(answers.struggles.size)
        OnboardingStep.DOWNTIME -> answers.downtime.code
        OnboardingStep.SENSITIVITY -> answers.sensitivity.name.lowercase()
        OnboardingStep.WEB_SETUP -> countBucket(answers.webCategories.size)
        OnboardingStep.ANALYTICS -> if (answers.analyticsEnabled) "on" else "off"
        else -> null
    }
}

private fun countBucket(count: Int): String =
    when {
        count == 0 -> "none"
        count == 1 -> "one"
        count <= 4 -> "two_to_four"
        else -> "five_plus"
    }

private fun <T> Set<T>.toggle(value: T): Set<T> = if (value in this) this - value else this + value
