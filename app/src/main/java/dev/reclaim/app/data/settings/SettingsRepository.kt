package dev.reclaim.app.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.domain.model.DomainCategory
import dev.reclaim.app.domain.model.DowntimeSchedule
import dev.reclaim.app.domain.model.Sensitivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.DayOfWeek
import java.time.Instant

/**
 * One settings flow feeds every service. Writes replace the whole snapshot,
 * which keeps the read and write paths symmetrical and impossible to half-apply.
 */
class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
) {
    val settings: Flow<AppSettings> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }.map(::read)

    suspend fun current(): AppSettings = settings.first()

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        dataStore.edit { preferences ->
            write(preferences, transform(read(preferences)))
        }
    }

    private fun read(preferences: Preferences): AppSettings =
        AppSettings(
            onboardingComplete = preferences[SettingsKeys.OnboardingComplete] ?: false,
            onboardingStep = preferences[SettingsKeys.OnboardingStep] ?: "",
            displayName = preferences[SettingsKeys.DisplayName] ?: "",
            primaryGoals = preferences[SettingsKeys.PrimaryGoals] ?: emptySet(),
            analyticsEnabled = preferences[SettingsKeys.AnalyticsEnabled] ?: false,
            appBlockingEnabled = preferences[SettingsKeys.AppBlockingEnabled] ?: false,
            blockedPackages = preferences[SettingsKeys.BlockedPackages] ?: emptySet(),
            downtime = DowntimeSchedule(
                enabled = preferences[SettingsKeys.DowntimeEnabled] ?: false,
                days = (preferences[SettingsKeys.DowntimeDays] ?: DefaultDaysMask).toDays(),
                startMinutes = preferences[SettingsKeys.DowntimeStartMinutes]
                    ?: DowntimeSchedule.DEFAULT_START_MINUTES,
                endMinutes = preferences[SettingsKeys.DowntimeEndMinutes]
                    ?: DowntimeSchedule.DEFAULT_END_MINUTES,
            ),
            pauseUntil = preferences[SettingsKeys.PauseUntilEpochMs]
                ?.takeIf { it > 0L }
                ?.let(Instant::ofEpochMilli),
            notificationsSuppressed = preferences[SettingsKeys.NotificationsSuppressed] ?: false,
            contentBlockingEnabled = preferences[SettingsKeys.ContentBlockingEnabled] ?: false,
            contentPlatforms = preferences[SettingsKeys.ContentPlatforms]
                ?: setOf("instagram", "youtube", "tiktok"),
            sensitivity = Sensitivity.fromKey(preferences[SettingsKeys.ContentSensitivity]),
            webBlockingEnabled = preferences[SettingsKeys.WebBlockingEnabled] ?: false,
            webCategories = (preferences[SettingsKeys.WebCategories] ?: setOf(DomainCategory.SOCIAL.key))
                .mapNotNull(DomainCategory::fromKey)
                .toSet(),
            customDomains = preferences[SettingsKeys.WebCustomDomains] ?: emptySet(),
        )

    private fun write(
        preferences: MutablePreferences,
        value: AppSettings,
    ) {
        preferences[SettingsKeys.OnboardingComplete] = value.onboardingComplete
        preferences[SettingsKeys.OnboardingStep] = value.onboardingStep
        preferences[SettingsKeys.DisplayName] = value.displayName
        preferences[SettingsKeys.PrimaryGoals] = value.primaryGoals
        preferences[SettingsKeys.AnalyticsEnabled] = value.analyticsEnabled
        preferences[SettingsKeys.AppBlockingEnabled] = value.appBlockingEnabled
        preferences[SettingsKeys.BlockedPackages] = value.blockedPackages
        preferences[SettingsKeys.DowntimeEnabled] = value.downtime.enabled
        preferences[SettingsKeys.DowntimeDays] = value.downtime.days.toBitmask()
        preferences[SettingsKeys.DowntimeStartMinutes] = value.downtime.startMinutes
        preferences[SettingsKeys.DowntimeEndMinutes] = value.downtime.endMinutes
        preferences[SettingsKeys.PauseUntilEpochMs] = value.pauseUntil?.toEpochMilli() ?: 0L
        preferences[SettingsKeys.NotificationsSuppressed] = value.notificationsSuppressed
        preferences[SettingsKeys.ContentBlockingEnabled] = value.contentBlockingEnabled
        preferences[SettingsKeys.ContentPlatforms] = value.contentPlatforms
        preferences[SettingsKeys.ContentSensitivity] = value.sensitivity.name.lowercase()
        preferences[SettingsKeys.WebBlockingEnabled] = value.webBlockingEnabled
        preferences[SettingsKeys.WebCategories] = value.webCategories.map { it.key }.toSet()
        preferences[SettingsKeys.WebCustomDomains] = value.customDomains
    }

    companion object {
        /** Monday is bit 0, matching [DayOfWeek.ordinal]. */
        private val DefaultDaysMask = DayOfWeek.entries.toSet().toBitmask()
    }
}

internal fun Set<DayOfWeek>.toBitmask(): Int = fold(0) { mask, day -> mask or (1 shl day.ordinal) }

internal fun Int.toDays(): Set<DayOfWeek> = DayOfWeek.entries.filter { this and (1 shl it.ordinal) != 0 }.toSet()
