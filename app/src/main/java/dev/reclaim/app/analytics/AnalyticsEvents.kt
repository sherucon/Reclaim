package dev.reclaim.app.analytics

/**
 * Event names and parameter keys live here so the taxonomy stays reviewable in
 * one file and no call site invents its own spelling.
 */
object AnalyticsEvents {
    const val APP_OPEN = "app_open"

    const val SCREEN_VIEW = "screen_view"

    object Params {
        const val SCREEN_NAME = "screen_name"
    }
}
