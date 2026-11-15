package dev.reclaim.app.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/**
 * Foreground app detection lives here. Phase 3 registers the service so the
 * onboarding permission is real; the blocking rules arrive in Phase 4.
 */
class AppBlockerService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit
}
