package dev.reclaim.app.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import dev.reclaim.app.BuildConfig
import dev.reclaim.app.ReclaimApplication
import dev.reclaim.app.analytics.AnalyticsEvents
import dev.reclaim.app.domain.blocking.BlockEvaluator
import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.domain.model.BlockReason
import dev.reclaim.app.domain.model.DayCounts
import dev.reclaim.app.service.blocking.BlockTargets
import dev.reclaim.app.service.blocking.OverlayThrottle
import dev.reclaim.app.ui.overlay.BlockOverlayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime

/**
 * Watches which app comes to the foreground and interrupts the ones the user
 * chose. The decision itself is pure; this class only supplies Android state.
 */
class AppBlockerService : AccessibilityService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val throttle = OverlayThrottle()

    @Volatile
    private var settings = AppSettings()

    private var ignoredPackages: Set<String> = emptySet()
    private var foregroundPackage: String? = null

    private val container get() = (application as ReclaimApplication).container

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(
            "ReclaimBlocker",
            "connected, ignored=${
                BlockTargets.ignoredPackages(this)
            }",
        )
        ignoredPackages = BlockTargets.ignoredPackages(this)

        scope.launch {
            container.settingsRepository.settings.collect { settings = it }
        }

        // A downtime window can start while an app is already open, so the
        // current package is re-evaluated on a slow tick as well.
        scope.launch {
            while (isActive) {
                delay(SCHEDULE_TICK_MS)
                foregroundPackage?.let(::evaluate)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val event = event ?: return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        foregroundPackage = packageName
        if (BuildConfig.DEBUG) {
            Log.d(
                "ReclaimBlocker",
                "event pkg=$packageName enabled=${settings.appBlockingEnabled} paused=${settings.isPaused(
                    java.time.Instant.now(),
                )} blockedPkgs=${settings.blockedPackages.size}",
            )
        }
        evaluate(packageName)
    }

    private fun evaluate(packageName: String) {
        val decision = BlockEvaluator.evaluate(
            packageName = packageName,
            now = ZonedDateTime.now(),
            settings = settings,
            ownPackage = this.packageName,
            ignoredPackages = ignoredPackages,
        )
        if (!decision.blocked) return
        val allowed = throttle.shouldShow(packageName, SystemClock.elapsedRealtime())
        if (BuildConfig.DEBUG) Log.d("ReclaimBlocker", "decision=$decision allowed=$allowed")
        if (!allowed) return

        val reason = decision.reason ?: BlockReason.ALWAYS
        container.analytics.logEvent(
            AnalyticsEvents.BLOCK_OVERLAY_SHOWN,
            mapOf(
                AnalyticsEvents.Params.KIND to "app",
                AnalyticsEvents.Params.REASON to reason.code,
            ),
        )

        val today = LocalDate.now().toEpochDay()
        scope.launch {
            container.counterRepository.record(today, DayCounts(appBlocks = 1))
        }

        val overlay = Intent(this, BlockOverlayActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP,
            )
            putExtra(BlockOverlayActivity.EXTRA_PACKAGE, packageName)
            putExtra(BlockOverlayActivity.EXTRA_REASON, reason.name)
        }

        runCatching { startActivity(overlay) }
    }

    override fun onInterrupt() {
        Log.d("ReclaimBlocker", "interrupted")
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        Log.d("ReclaimBlocker", "unbound")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d("ReclaimBlocker", "destroyed")
        scope.cancel()
        super.onDestroy()
    }

    private companion object {
        const val SCHEDULE_TICK_MS = 15_000L
    }
}
