package dev.reclaim.app.domain.onboarding

import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.domain.model.DowntimeSchedule
import dev.reclaim.app.domain.model.Sensitivity
import java.time.DayOfWeek

/**
 * Translates the flow's answers into the settings the services read. Kept pure
 * so a test can assert exactly what the summary promised.
 */
object OnboardingSettings {
    fun apply(
        answers: OnboardingAnswers,
        current: AppSettings,
        accessibilityGranted: Boolean,
    ): AppSettings {
        val downtime = answers.downtime
        val schedule = if (downtime.startMinutes == null || downtime.endMinutes == null) {
            current.downtime.copy(enabled = false)
        } else {
            DowntimeSchedule(
                enabled = true,
                days = DayOfWeek.entries.toSet(),
                startMinutes = downtime.startMinutes,
                endMinutes = downtime.endMinutes,
            )
        }

        return current.copy(
            onboardingComplete = true,
            onboardingStep = "",
            displayName = answers.name.trim(),
            primaryGoals = answers.struggles.map { it.code }.toSet(),
            analyticsEnabled = answers.analyticsEnabled,
            appBlockingEnabled = accessibilityGranted,
            blockedPackages = answers.selectedPlatforms.flatMap { it.packages }.toSet(),
            downtime = schedule,
            contentBlockingEnabled = accessibilityGranted && answers.detectablePlatforms.isNotEmpty(),
            contentPlatforms = answers.detectablePlatforms.map { it.key }.toSet(),
            sensitivity = answers.sensitivity,
            webCategories = answers.webCategories,
        )
    }

    fun sensitivityLabel(sensitivity: Sensitivity): String =
        when (sensitivity) {
            Sensitivity.RELAXED -> "Gentle"
            Sensitivity.BALANCED -> "Balanced"
            Sensitivity.STRICT -> "Strict"
        }
}
