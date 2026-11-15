package dev.reclaim.app.analytics

/**
 * Records instead of sending, so tests can assert the exact taxonomy.
 */
class FakeAnalyticsTracker : AnalyticsTracker {
    data class Event(
        val name: String,
        val params: Map<String, Any>,
    )

    val events = mutableListOf<Event>()
    val userProperties = mutableMapOf<String, String?>()
    var enabled: Boolean = true
        private set
    var resetCount: Int = 0
        private set

    override fun logEvent(
        name: String,
        params: Map<String, Any>,
    ) {
        events += Event(name, params)
    }

    override fun setUserProperty(
        name: String,
        value: String?,
    ) {
        userProperties[name] = value
    }

    override fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    override fun reset() {
        events.clear()
        userProperties.clear()
        resetCount++
    }

    fun names(): List<String> = events.map { it.name }
}
