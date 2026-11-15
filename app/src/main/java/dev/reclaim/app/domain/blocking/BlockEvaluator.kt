package dev.reclaim.app.domain.blocking

import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.domain.model.BlockReason
import java.time.ZonedDateTime

data class BlockDecision(
    val blocked: Boolean,
    val reason: BlockReason? = null,
) {
    companion object {
        val Allowed = BlockDecision(blocked = false)
    }
}

/**
 * Pure foreground decision. No Android types, so every rule is unit tested.
 */
object BlockEvaluator {
    /**
     * Never blocked, whatever the user selected. Reclaim must not be able to
     * lock itself out, and System UI and Settings stay reachable so a blocked
     * app can always be fixed.
     */
    val AlwaysIgnoredPackages: Set<String> = setOf(
        "com.android.systemui",
        "com.android.settings",
    )

    fun evaluate(
        packageName: String,
        now: ZonedDateTime,
        settings: AppSettings,
        ownPackage: String,
        ignoredPackages: Set<String> = AlwaysIgnoredPackages,
    ): BlockDecision {
        if (packageName.isBlank()) return BlockDecision.Allowed
        if (packageName == ownPackage) return BlockDecision.Allowed
        if (packageName in AlwaysIgnoredPackages) return BlockDecision.Allowed
        if (packageName in ignoredPackages) return BlockDecision.Allowed
        if (settings.isPaused(now.toInstant())) return BlockDecision.Allowed
        if (packageName !in settings.blockedPackages) return BlockDecision.Allowed

        // Downtime stands on its own, so quiet evenings work with the master
        // switch off.
        if (settings.downtime.isActive(now)) {
            return BlockDecision(blocked = true, reason = BlockReason.DOWNTIME)
        }
        if (settings.appBlockingEnabled) {
            return BlockDecision(blocked = true, reason = BlockReason.ALWAYS)
        }
        return BlockDecision.Allowed
    }
}
