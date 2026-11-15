package dev.reclaim.app.domain.model

import java.time.Instant

/**
 * Everything the services need to decide, and nothing else.
 *
 * Package names, domain lists, and the display name stay on the device and are
 * never attached to an analytics event.
 */
data class AppSettings(
    val onboardingComplete: Boolean = false,
    val displayName: String = "",
    val primaryGoal: String = "",
    val analyticsEnabled: Boolean = false,
    val appBlockingEnabled: Boolean = false,
    val blockedPackages: Set<String> = emptySet(),
    val downtime: DowntimeSchedule = DowntimeSchedule(),
    val pauseUntil: Instant? = null,
    val notificationsSuppressed: Boolean = false,
    val contentBlockingEnabled: Boolean = false,
    val contentPlatforms: Set<String> = emptySet(),
    val sensitivity: Sensitivity = Sensitivity.BALANCED,
    val webBlockingEnabled: Boolean = false,
    val webCategories: Set<DomainCategory> = setOf(DomainCategory.SOCIAL),
    val customDomains: Set<String> = emptySet(),
) {
    fun isPaused(now: Instant): Boolean = pauseUntil?.let { now.isBefore(it) } == true

    /** Platforms the reels blocker watches, resolved from the stored keys. */
    val contentPlatformSet: Set<Platform>
        get() = contentPlatforms.mapNotNull(PlatformRegistry::byKey).toSet()
}
