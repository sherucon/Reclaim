package dev.reclaim.app.service.blocking

/**
 * Keeps a blocked app from stacking overlays or relaunching one immediately
 * after it was dismissed. One cooldown per package, so two apps blocking in a
 * row still work.
 */
class OverlayThrottle(
    private val cooldownMs: Long = DEFAULT_COOLDOWN_MS,
) {
    private val lastShownAt = mutableMapOf<String, Long>()

    fun shouldShow(
        packageName: String,
        now: Long,
    ): Boolean {
        val last = lastShownAt[packageName]
        if (last != null && now - last < cooldownMs) return false
        lastShownAt[packageName] = now
        return true
    }

    fun reset() = lastShownAt.clear()

    companion object {
        const val DEFAULT_COOLDOWN_MS = 1_500L
    }
}
