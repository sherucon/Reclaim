package dev.reclaim.app.domain.blocking

import dev.reclaim.app.domain.model.Sensitivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ScrollAccumulatorTest {
    private val viewport = 2000
    private val start = Instant.parse("2026-11-15T18:00:00Z")

    private fun scroll(
        state: ScrollState,
        screens: Float,
        at: Instant = start,
        deltaX: Int = 0,
        active: Boolean = true,
    ): ScrollUpdate =
        ScrollAccumulator.add(
            state = state,
            deltaY = (screens * viewport).toInt(),
            deltaX = deltaX,
            viewportHeightPx = viewport,
            now = at,
            sensitivity = Sensitivity.BALANCED,
            surfaceActive = active,
        )

    @Test
    fun balancedBudgetBreaksAfterOneAndAHalfScreens() {
        val first = scroll(ScrollState(), screens = 1.0f)
        assertFalse(first.shouldBreak)
        assertEquals(1.0f, first.state.accumulatedScreens, 0.01f)

        val second = scroll(first.state, screens = 0.6f)
        assertTrue(second.shouldBreak)
        assertEquals(0f, second.state.accumulatedScreens, 0.01f)
    }

    @Test
    fun strictBreaksSoonerAndRelaxedLater() {
        val strict = ScrollAccumulator.add(
            state = ScrollState(),
            deltaY = 1600,
            deltaX = 0,
            viewportHeightPx = viewport,
            now = start,
            sensitivity = Sensitivity.STRICT,
        )
        assertTrue(strict.shouldBreak)

        val relaxed = ScrollAccumulator.add(
            state = ScrollState(),
            deltaY = 4000,
            deltaX = 0,
            viewportHeightPx = viewport,
            now = start,
            sensitivity = Sensitivity.RELAXED,
        )
        assertFalse(relaxed.shouldBreak)
    }

    @Test
    fun cooldownSuppressesASecondBreak() {
        val broken = scroll(ScrollState(), screens = 2.0f)
        assertTrue(broken.shouldBreak)

        val duringCooldown = scroll(
            state = broken.state,
            screens = 3.0f,
            at = start.plusSeconds(Sensitivity.BALANCED.cooldownSeconds.toLong() - 1),
        )
        assertFalse(duringCooldown.shouldBreak)

        val afterCooldown = scroll(
            state = duringCooldown.state,
            screens = Sensitivity.BALANCED.budgetScreens,
            at = start.plusSeconds(Sensitivity.BALANCED.cooldownSeconds.toLong() + 1),
        )
        assertTrue(afterCooldown.shouldBreak)
    }

    @Test
    fun idleGapResetsTheSession() {
        val first = scroll(ScrollState(), screens = 1.0f)
        val afterIdle = scroll(
            state = first.state,
            screens = 0.5f,
            at = start.plusSeconds(ScrollAccumulator.IDLE_RESET_SECONDS + 1),
        )

        assertFalse(afterIdle.shouldBreak)
        assertEquals(0.5f, afterIdle.state.accumulatedScreens, 0.01f)
    }

    @Test
    fun horizontalSwipesDoNotCount() {
        val swiped = ScrollAccumulator.add(
            state = ScrollState(),
            deltaY = 400,
            deltaX = 900,
            viewportHeightPx = viewport,
            now = start,
            sensitivity = Sensitivity.STRICT,
        )

        assertFalse(swiped.shouldBreak)
        assertEquals(0f, swiped.state.accumulatedScreens, 0.01f)
    }

    @Test
    fun leavingTheSurfaceResetsEverything() {
        val first = scroll(ScrollState(), screens = 1.0f)
        val left = scroll(first.state, screens = 1.0f, active = false)

        assertFalse(left.shouldBreak)
        assertEquals(ScrollState(), left.state)
    }

    @Test
    fun unknownViewportIsIgnoredInsteadOfDividingByZero() {
        val update = ScrollAccumulator.add(
            state = ScrollState(),
            deltaY = 900,
            deltaX = 0,
            viewportHeightPx = 0,
            now = start,
            sensitivity = Sensitivity.BALANCED,
        )

        assertFalse(update.shouldBreak)
        assertEquals(ScrollState(), update.state)
    }

    @Test
    fun upwardScrollAccumulatesToo() {
        val update = scroll(ScrollState(), screens = -2.0f)

        assertTrue(update.shouldBreak)
    }
}
