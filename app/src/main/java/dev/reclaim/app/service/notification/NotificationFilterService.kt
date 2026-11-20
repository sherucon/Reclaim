package dev.reclaim.app.service.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dev.reclaim.app.ReclaimApplication
import dev.reclaim.app.domain.blocking.BlockEvaluator
import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.service.blocking.BlockTargets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

/**
 * Best effort only: Android may not deliver every notification. When a blocked
 * app posts while its rule is active, the notification is cancelled. Nothing is
 * inspected, rewritten, or forwarded.
 */
class NotificationFilterService : NotificationListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @Volatile
    private var settings = AppSettings()

    private var ignoredPackages: Set<String> = emptySet()

    override fun onListenerConnected() {
        super.onListenerConnected()
        ignoredPackages = BlockTargets.ignoredPackages(this)

        val container = (application as ReclaimApplication).container
        scope.launch {
            container.settingsRepository.settings.collect { settings = it }
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val posted = sbn ?: return
        if (!settings.notificationsSuppressed) return

        val decision = BlockEvaluator.evaluate(
            packageName = posted.packageName ?: return,
            now = ZonedDateTime.now(),
            settings = settings,
            ownPackage = packageName,
            ignoredPackages = ignoredPackages,
        )
        if (!decision.blocked) return

        runCatching { cancelNotification(posted.key) }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
