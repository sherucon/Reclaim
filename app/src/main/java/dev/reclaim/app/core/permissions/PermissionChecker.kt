package dev.reclaim.app.core.permissions

import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

/**
 * The service names the system stores in its accessibility and notification
 * listener lists. Keep them in one place so detection and the manifest agree.
 */
object ReclaimServices {
    const val APP_BLOCKER = "dev.reclaim.app.service.accessibility.AppBlockerService"
    const val CONTENT_BLOCKER = "dev.reclaim.app.service.accessibility.ContentBlockerService"
}

/**
 * Reads the real system state. Never cache these answers across a screen; the
 * user can flip any of them in system settings while the app is in the background.
 */
class PermissionChecker(
    private val context: Context,
) {
    fun isAccessibilityEnabled(serviceClass: String): Boolean {
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        ) ?: return false

        return enabled.split(':').any { entry -> entry.matchesService(serviceClass) }
    }

    fun isAppBlockerEnabled(): Boolean = isAccessibilityEnabled(ReclaimServices.APP_BLOCKER)

    fun isContentBlockerEnabled(): Boolean = isAccessibilityEnabled(ReclaimServices.CONTENT_BLOCKER)

    fun isNotificationListenerEnabled(): Boolean =
        NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)

    fun isVpnPrepared(): Boolean = VpnService.prepare(context) == null

    fun accessibilitySettingsIntent(): Intent =
        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    fun notificationSettingsIntent(): Intent =
        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    private fun String.matchesService(serviceClass: String): Boolean {
        val packageName = context.packageName
        if (!startsWith("$packageName/")) return false
        val raw = substringAfter('/')
        val fullClass = if (raw.startsWith(".")) packageName + raw else raw
        return fullClass == serviceClass
    }
}
