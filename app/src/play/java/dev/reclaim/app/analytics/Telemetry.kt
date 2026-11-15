package dev.reclaim.app.analytics

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * `play` flavor telemetry. Falls back to no-ops when the checkout has no
 * `google-services.json`, so a clean clone still builds and runs.
 */
fun createTracker(
    context: Context,
    enabled: Boolean,
): AnalyticsTracker {
    if (!hasFirebaseConfig(context)) return NoOpAnalyticsTracker
    FirebaseApp.initializeApp(context)
    val tracker = FirebaseAnalyticsTracker(FirebaseAnalytics.getInstance(context))
    tracker.setEnabled(enabled)
    return tracker
}

fun createCrashReporter(
    context: Context,
    enabled: Boolean,
): CrashReporter {
    if (!hasFirebaseConfig(context)) return NoOpCrashReporter
    FirebaseApp.initializeApp(context)
    val reporter = FirebaseCrashReporter(FirebaseCrashlytics.getInstance())
    reporter.setEnabled(enabled)
    return reporter
}

@Suppress("DiscouragedApi")
private fun hasFirebaseConfig(context: Context): Boolean =
    context.resources.getIdentifier("google_app_id", "string", context.packageName) != 0
