package dev.reclaim.app.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.reclaim.app.ReclaimApplication

/**
 * Debug builds only, so Crashlytics can be verified end to end:
 *
 * adb shell am broadcast -n dev.reclaim.app/.debug.DebugCrashReceiver -a dev.reclaim.app.DEBUG_CRASH
 *
 * The intent component is explicit on purpose. HyperOS silently drops implicit
 * broadcasts from shell to manifest receivers, so the action alone does nothing.
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
