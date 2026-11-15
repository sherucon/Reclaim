package dev.reclaim.app.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/**
 * Short-video surface detection lives here. Registered now so the onboarding
 * permission can be granted; detection arrives in Phase 5.
 */
class ContentBlockerService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit
}
