package dev.reclaim.app.analytics

import android.content.Context

/**
 * `libre` flavor telemetry: no SDK, no network, no identifiers.
 */
@Suppress("UNUSED_PARAMETER")
fun createTracker(
    context: Context,
    enabled: Boolean,
): AnalyticsTracker = NoOpAnalyticsTracker

@Suppress("UNUSED_PARAMETER")
fun createCrashReporter(
    context: Context,
    enabled: Boolean,
): CrashReporter = NoOpCrashReporter
