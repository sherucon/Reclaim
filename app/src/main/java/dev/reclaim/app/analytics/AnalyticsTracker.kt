package dev.reclaim.app.analytics

/**
 * The only way feature code reaches an analytics backend.
 *
 * Implementations must never receive free text, names, package names, domains,
 * URLs, or page content. Parameters are enums, booleans, or bucketed numbers.
 */
interface AnalyticsTracker {
    fun logEvent(
        name: String,
        params: Map<String, Any> = emptyMap(),
    )

    fun setUserProperty(
        name: String,
        value: String?,
    )

    fun setEnabled(enabled: Boolean)

    fun reset()
}
