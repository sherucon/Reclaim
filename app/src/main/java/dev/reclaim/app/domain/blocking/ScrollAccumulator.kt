package dev.reclaim.app.domain.blocking

import dev.reclaim.app.domain.model.Sensitivity
import java.time.Instant
import kotlin.math.abs

/**
 * What the reels blocker remembers between scroll events.
 */
data class ScrollState(
    val accumulatedScreens: Float = 0f,
    val lastScrollAt: Instant? = null,
    val cooldownUntil: Instant? = null,
) {
    fun isCoolingDown(now: Instant): Boolean = cooldownUntil?.let { now.isBefore(it) } == true
}

data class ScrollUpdate(
    val state: ScrollState,
    val shouldBreak: Boolean,
)

/**
 * Accumulates vertical scroll inside a short-video surface and says when the
 * budget is spent. Pure: the service feeds it events and reacts to the result.
 */
object ScrollAccumulator {
    /** A gap this long means the user left the surface, so the session resets. */
    const val IDLE_RESET_SECONDS: Long = 60

    fun add(
        state: ScrollState,
        deltaY: Int,
        deltaX: Int,
        viewportHeightPx: Int,
        now: Instant,
        sensitivity: Sensitivity,
        surfaceActive: Boolean = true,
    ): ScrollUpdate {
        if (!surfaceActive || viewportHeightPx <= 0) {
            return ScrollUpdate(state = ScrollState(), shouldBreak = false)
        }

        val idleFor = state.lastScrollAt?.let { now.epochSecond - it.epochSecond }
        var current = if (idleFor != null && idleFor > IDLE_RESET_SECONDS) {
            state.copy(accumulatedScreens = 0f)
        } else {
            state
        }

        current = current.copy(lastScrollAt = now)

        // Horizontal swipes inside carousels are not reels scrolling.
        if (abs(deltaX.toLong()) >= abs(deltaY.toLong())) {
            return ScrollUpdate(state = current, shouldBreak = false)
        }

        current = current.copy(
            accumulatedScreens = current.accumulatedScreens + abs(deltaY.toFloat()) / viewportHeightPx,
        )

        if (current.isCoolingDown(now)) return ScrollUpdate(state = current, shouldBreak = false)
        if (current.accumulatedScreens < sensitivity.budgetScreens) {
            return ScrollUpdate(state = current, shouldBreak = false)
        }

        return ScrollUpdate(
            state = current.copy(
                accumulatedScreens = 0f,
                cooldownUntil = now.plusSeconds(sensitivity.cooldownSeconds.toLong()),
            ),
            shouldBreak = true,
        )
    }

    /** Called when the user leaves the platform, the screen goes off, or the service reconnects. */
    fun reset(): ScrollState = ScrollState()
}
