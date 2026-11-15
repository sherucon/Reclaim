package dev.reclaim.app.data.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

internal object SettingsKeys {
    val OnboardingComplete = booleanPreferencesKey("onboarding_complete")
    val OnboardingStep = stringPreferencesKey("onboarding_step")
    val DisplayName = stringPreferencesKey("display_name")
    val PrimaryGoals = stringSetPreferencesKey("primary_goals")
    val AnalyticsEnabled = booleanPreferencesKey("analytics_enabled")

    val AppBlockingEnabled = booleanPreferencesKey("app_blocking_enabled")
    val BlockedPackages = stringSetPreferencesKey("blocked_packages")
    val DowntimeEnabled = booleanPreferencesKey("downtime_enabled")
    val DowntimeDays = intPreferencesKey("downtime_days")
    val DowntimeStartMinutes = intPreferencesKey("downtime_start_minutes")
    val DowntimeEndMinutes = intPreferencesKey("downtime_end_minutes")
    val PauseUntilEpochMs = longPreferencesKey("pause_until_epoch_ms")
    val NotificationsSuppressed = booleanPreferencesKey("notifications_suppressed")

    val ContentBlockingEnabled = booleanPreferencesKey("content_blocking_enabled")
    val ContentPlatforms = stringSetPreferencesKey("content_platforms")
    val ContentSensitivity = stringPreferencesKey("content_sensitivity")

    val WebBlockingEnabled = booleanPreferencesKey("web_blocking_enabled")
    val WebCategories = stringSetPreferencesKey("web_categories")
    val WebCustomDomains = stringSetPreferencesKey("web_custom_domains")
}
