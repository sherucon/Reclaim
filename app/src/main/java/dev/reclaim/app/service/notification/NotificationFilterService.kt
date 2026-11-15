package dev.reclaim.app.service.notification

import android.service.notification.NotificationListenerService

/**
 * Notification suppression lives here. Registered now so the onboarding
 * permission can be granted; filtering arrives in Phase 4.
 */
class NotificationFilterService : NotificationListenerService()
