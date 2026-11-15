package dev.reclaim.app

import android.content.Context
import dev.reclaim.app.analytics.AnalyticsTracker
import dev.reclaim.app.analytics.CrashReporter
import dev.reclaim.app.analytics.createCrashReporter
import dev.reclaim.app.analytics.createTracker

/**
 * Manual dependency container. Deliberately small: everything here is a
 * singleton the whole app shares.
 */
class AppContainer(
    context: Context,
) {
    // shortcut: real consent lands in Phase 3. Debug builds report so DebugView
    // works during development; release builds stay silent until the user opts in.
    private val telemetryEnabled = BuildConfig.DEBUG

    val analytics: AnalyticsTracker = createTracker(context, telemetryEnabled)

    val crashReporter: CrashReporter = createCrashReporter(context, telemetryEnabled)
}
