package dev.reclaim.app.domain.onboarding

import dev.reclaim.app.domain.model.DetectionStrength
import dev.reclaim.app.domain.model.DomainCategory
import dev.reclaim.app.domain.model.Platform
import dev.reclaim.app.domain.model.PlatformRegistry
import dev.reclaim.app.domain.model.Sensitivity

enum class OnboardingStep(
    val id: String,
) {
    WELCOME("welcome"),
    NAME("name"),
    APPS("apps"),
    STRUGGLE("struggle"),
    DOWNTIME("downtime"),
    SENSITIVITY("sensitivity"),
    APP_PERMISSION("app_permission"),
    CONTENT_PERMISSION("content_permission"),
    WEB_SETUP("web_setup"),
    NOTIFICATIONS("notifications"),
    ANALYTICS("analytics"),
    SUMMARY("summary"),
    ;

    companion object {
        fun fromId(id: String?): OnboardingStep? = entries.firstOrNull { it.id == id }
    }
}

enum class Struggle(
    val code: String,
    val title: String,
    val detail: String,
) {
    SCROLLING("scrolling", "Endless scrolling", "Reels, Shorts, and the for you page"),
    NOTIFICATIONS("notifications", "Constant buzzing", "Notifications while you are trying to focus"),
    OPENING_APPS("opening_apps", "Opening apps on autopilot", "You unlock the phone and end up somewhere else"),
    WEBSITES("websites", "The mobile web", "The same feeds, in a browser"),
}

enum class DowntimeChoice(
    val code: String,
    val startMinutes: Int?,
    val endMinutes: Int?,
) {
    EVENINGS("evenings", 22 * 60, 7 * 60),
    LATE_NIGHT("late_night", 23 * 60, 6 * 60),
    OFF("off", null, null),
    ;

    companion object {
        fun fromCode(code: String?): DowntimeChoice = entries.firstOrNull { it.code == code } ?: EVENINGS
    }
}

/**
 * Everything the flow asks. Stored locally, never sent to analytics.
 */
data class OnboardingAnswers(
    val name: String = "",
    val platforms: Set<String> = emptySet(),
    val struggles: Set<Struggle> = emptySet(),
    val downtime: DowntimeChoice = DowntimeChoice.EVENINGS,
    val sensitivity: Sensitivity = Sensitivity.BALANCED,
    val webCategories: Set<DomainCategory> = setOf(DomainCategory.SOCIAL),
    val analyticsEnabled: Boolean = false,
) {
    val selectedPlatforms: List<Platform>
        get() = platforms.mapNotNull(PlatformRegistry::byKey)

    /** Platforms where the reels blocker can actually see something. */
    val detectablePlatforms: List<Platform>
        get() = selectedPlatforms.filter { it.detection != DetectionStrength.None }

    val wantsWebFilter: Boolean
        get() = Struggle.WEBSITES in struggles
}

/**
 * The flow's shape, kept pure so branching and resume are testable without Android.
 */
object OnboardingPlan {
    fun visibleSteps(answers: OnboardingAnswers): List<OnboardingStep> =
        OnboardingStep.entries.filter { step ->
            when (step) {
                OnboardingStep.APP_PERMISSION, OnboardingStep.CONTENT_PERMISSION ->
                    answers.detectablePlatforms.isNotEmpty()
                OnboardingStep.WEB_SETUP -> answers.wantsWebFilter
                else -> true
            }
        }

    fun next(
        current: OnboardingStep,
        answers: OnboardingAnswers,
    ): OnboardingStep? {
        val steps = visibleSteps(answers)
        val index = steps.indexOf(current)
        return if (index >= 0) steps.getOrNull(index + 1) else steps.firstOrNull()
    }

    fun previous(
        current: OnboardingStep,
        answers: OnboardingAnswers,
    ): OnboardingStep? {
        val steps = visibleSteps(answers)
        val index = steps.indexOf(current)
        return if (index > 0) steps[index - 1] else null
    }

    /** Resume point after a restart, clamped to the steps the answers allow. */
    fun resumeAt(
        storedId: String?,
        answers: OnboardingAnswers,
    ): OnboardingStep {
        val steps = visibleSteps(answers)
        val stored = OnboardingStep.fromId(storedId)
        if (stored != null && stored in steps) return stored
        if (stored != null) {
            val index = OnboardingStep.entries.indexOf(stored)
            return steps.firstOrNull { OnboardingStep.entries.indexOf(it) >= index } ?: OnboardingStep.SUMMARY
        }
        return OnboardingStep.WELCOME
    }

    fun summaryLines(answers: OnboardingAnswers): List<String> =
        buildList {
            val apps = answers.selectedPlatforms
            if (apps.isNotEmpty()) {
                add(apps.joinToString(", ") { it.displayName } + " blocked")
            }
            answers.downtime.let { choice ->
                if (choice != DowntimeChoice.OFF && choice.startMinutes != null && choice.endMinutes != null) {
                    add("Quiet from ${formatTime(choice.startMinutes)} to ${formatTime(choice.endMinutes)}")
                }
            }
            add(
                when (answers.sensitivity) {
                    Sensitivity.RELAXED -> "Gentle scroll budget"
                    Sensitivity.BALANCED -> "Balanced scroll budget"
                    Sensitivity.STRICT -> "Strict scroll budget"
                },
            )
            if (answers.wantsWebFilter) {
                val names = answers.webCategories.map { it.displayName }.sorted()
                if (names.isNotEmpty()) add(names.joinToString(", ") + " sites filtered")
            }
        }

    fun formatTime(minutes: Int): String {
        val hour = minutes / 60
        val minute = minutes % 60
        return "%02d:%02d".format(hour, minute)
    }
}
