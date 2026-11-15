package dev.reclaim.app.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.reclaim.app.ReclaimApplication

/**
 * Debug builds only, so Crashlytics can be verified end to end:
 *
 * adb shell am broadcast -a dev.reclaim.app.DEBUG_CRASH
 */
class DebugCrashReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val container = (context.applicationContext as ReclaimApplication).container
        container.crashReporter.recordException(IllegalStateException("Reclaim debug crash test"))
        throw IllegalStateException("Reclaim debug crash test")
    }
}
