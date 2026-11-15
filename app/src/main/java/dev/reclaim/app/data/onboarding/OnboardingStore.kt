package dev.reclaim.app.data.onboarding

import dev.reclaim.app.data.settings.SettingsRepository
import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.domain.model.DowntimeSchedule
import dev.reclaim.app.domain.onboarding.DowntimeChoice
import dev.reclaim.app.domain.onboarding.OnboardingAnswers
import dev.reclaim.app.domain.onboarding.OnboardingSettings
import dev.reclaim.app.domain.onboarding.OnboardingStep
import dev.reclaim.app.domain.onboarding.Struggle
import java.time.DayOfWeek

/**
 * Persists the flow as it goes, so a kill in the middle of onboarding resumes
 * where it stopped and the summary reflects the real choices.
 */
interface OnboardingStore {
    suspend fun load(): AppSettings

    suspend fun save(
        answers: OnboardingAnswers,
        step: OnboardingStep,
    )

    suspend fun complete(
        answers: OnboardingAnswers,
        accessibilityGranted: Boolean,
    )
}

class SettingsOnboardingStore(
    private val settings: SettingsRepository,
) : OnboardingStore {
    override suspend fun load(): AppSettings = settings.current()

    override suspend fun save(
        answers: OnboardingAnswers,
        step: OnboardingStep,
    ) {
        settings.update { current ->
            current.copy(
                onboardingStep = step.id,
                displayName = answers.name.trim(),
                primaryGoals = answers.struggles.map { it.code }.toSet(),
                analyticsEnabled = answers.analyticsEnabled,
                blockedPackages = answers.selectedPlatforms.flatMap { it.packages }.toSet(),
                contentPlatforms = answers.detectablePlatforms.map { it.key }.toSet(),
                downtime = answers.downtime.toSchedule(),
                sensitivity = answers.sensitivity,
                webCategories = answers.webCategories,
            )
        }
    }

    override suspend fun complete(
        answers: OnboardingAnswers,
        accessibilityGranted: Boolean,
    ) {
        settings.update { current ->
            OnboardingSettings.apply(answers, current, accessibilityGranted)
        }
    }

    companion object {
        fun answersFrom(settings: AppSettings): OnboardingAnswers =
            OnboardingAnswers(
                name = settings.displayName,
                platforms = settings.contentPlatforms,
                struggles = settings.primaryGoals
                    .mapNotNull { code ->
                        Struggle.entries.firstOrNull { it.code == code }
                    }.toSet(),
                downtime = settings.downtime.toChoice(),
                sensitivity = settings.sensitivity,
                webCategories = settings.webCategories,
                analyticsEnabled = settings.analyticsEnabled,
            )

        private fun DowntimeChoice.toSchedule(): DowntimeSchedule =
            if (startMinutes == null || endMinutes == null) {
                DowntimeSchedule(enabled = false)
            } else {
                DowntimeSchedule(
                    enabled = true,
                    days = DayOfWeek.entries.toSet(),
                    startMinutes = startMinutes,
                    endMinutes = endMinutes,
                )
            }

        private fun DowntimeSchedule.toChoice(): DowntimeChoice =
            when {
                !enabled -> DowntimeChoice.OFF
                startMinutes == DowntimeChoice.LATE_NIGHT.startMinutes -> DowntimeChoice.LATE_NIGHT
                else -> DowntimeChoice.EVENINGS
            }
    }
}
