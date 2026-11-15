package dev.reclaim.app.analytics

object NoOpAnalyticsTracker : AnalyticsTracker {
    override fun logEvent(
        name: String,
        params: Map<String, Any>,
    ) = Unit

    override fun setUserProperty(
        name: String,
        value: String?,
    ) = Unit

    override fun setEnabled(enabled: Boolean) = Unit

    override fun reset() = Unit
}
