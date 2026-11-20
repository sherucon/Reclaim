package dev.reclaim.app.service.blocking

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OverlayThrottleTest {
    private val throttle = OverlayThrottle(cooldownMs = 1_500)

    @Test
    fun firstBlockShowsImmediately() {
        assertTrue(throttle.shouldShow("com.example.feed", now = 1_000))
    }

    @Test
    fun repeatedEventsInsideTheCooldownAreIgnored() {
        assertTrue(throttle.shouldShow("com.example.feed", now = 1_000))
        assertFalse(throttle.shouldShow("com.example.feed", now = 1_100))
        assertFalse(throttle.shouldShow("com.example.feed", now = 2_400))
    }

    @Test
    fun aLaterVisitShowsAgain() {
        assertTrue(throttle.shouldShow("com.example.feed", now = 1_000))
        assertTrue(throttle.shouldShow("com.example.feed", now = 2_600))
    }

    @Test
    fun cooldownIsPerPackage() {
        assertTrue(throttle.shouldShow("com.example.feed", now = 1_000))
        assertTrue(throttle.shouldShow("com.example.chat", now = 1_100))
    }

    @Test
    fun resetForgetsEverything() {
        assertTrue(throttle.shouldShow("com.example.feed", now = 1_000))
        throttle.reset()
        assertTrue(throttle.shouldShow("com.example.feed", now = 1_100))
    }
}
