package dev.reclaim.app.domain.blocking

import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.domain.model.BlockReason
import dev.reclaim.app.domain.model.DowntimeSchedule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class BlockEvaluatorTest {
    private val zone: ZoneId = ZoneId.of("Asia/Kolkata")
    private val monday = LocalDate.of(2026, 11, 16)
    private val ownPackage = "dev.reclaim.app"
    private val blockedApp = "com.example.endlessfeed"

    private fun at(
        hour: Int,
        minute: Int = 0,
    ): ZonedDateTime = monday.atTime(hour, minute).atZone(zone)

    private fun settings(
        blocking: Boolean = false,
        packages: Set<String> = setOf(blockedApp),
        downtime: DowntimeSchedule = DowntimeSchedule(),
    ) = AppSettings(
        appBlockingEnabled = blocking,
        blockedPackages = packages,
        downtime = downtime,
    )

    private val eveningWindow = DowntimeSchedule(
        enabled = true,
        days = setOf(DayOfWeek.MONDAY),
        startMinutes = 22 * 60,
        endMinutes = 7 * 60,
    )

    @Test
    fun blocksSelectedAppWhenAlwaysBlockingIsOn() {
        val decision = BlockEvaluator.evaluate(
            packageName = blockedApp,
            now = at(14),
            settings = settings(blocking = true),
            ownPackage = ownPackage,
        )

        assertTrue(decision.blocked)
        assertEquals(BlockReason.ALWAYS, decision.reason)
    }

    @Test
    fun doesNotBlockWhenAlwaysBlockingIsOff() {
        val decision = BlockEvaluator.evaluate(
            packageName = blockedApp,
            now = at(14),
            settings = settings(blocking = false),
            ownPackage = ownPackage,
        )

        assertFalse(decision.blocked)
        assertNull(decision.reason)
    }

    @Test
    fun downtimeWorksWithoutTheMasterSwitch() {
        val decision = BlockEvaluator.evaluate(
            packageName = blockedApp,
            now = at(23),
            settings = settings(blocking = false, downtime = eveningWindow),
            ownPackage = ownPackage,
        )

        assertTrue(decision.blocked)
        assertEquals(BlockReason.DOWNTIME, decision.reason)
    }

    @Test
    fun downtimeWinsOverAlwaysAsTheReason() {
        val decision = BlockEvaluator.evaluate(
            packageName = blockedApp,
            now = at(23),
            settings = settings(blocking = true, downtime = eveningWindow),
            ownPackage = ownPackage,
        )

        assertEquals(BlockReason.DOWNTIME, decision.reason)
    }

    @Test
    fun downtimeDoesNothingOutsideTheWindow() {
        val decision = BlockEvaluator.evaluate(
            packageName = blockedApp,
            now = at(14),
            settings = settings(blocking = false, downtime = eveningWindow),
            ownPackage = ownPackage,
        )

        assertFalse(decision.blocked)
    }

    @Test
    fun appsOutsideTheSelectedSetAreNeverBlocked() {
        val decision = BlockEvaluator.evaluate(
            packageName = "com.example.note",
            now = at(23),
            settings = settings(blocking = true, downtime = eveningWindow),
            ownPackage = ownPackage,
        )

        assertFalse(decision.blocked)
    }

    @Test
    fun emergencyPauseWinsOverEveryRule() {
        val paused = settings(blocking = true, downtime = eveningWindow)
            .copy(pauseUntil = at(23).toInstant().plusSeconds(300))

        val decision = BlockEvaluator.evaluate(
            packageName = blockedApp,
            now = at(23),
            settings = paused,
            ownPackage = ownPackage,
        )

        assertFalse(decision.blocked)
    }

    @Test
    fun expiredPauseStopsMattering() {
        val paused = settings(blocking = true)
            .copy(pauseUntil = at(14).toInstant().minusSeconds(1))

        val decision = BlockEvaluator.evaluate(
            packageName = blockedApp,
            now = at(14),
            settings = paused,
            ownPackage = ownPackage,
        )

        assertTrue(decision.blocked)
    }

    @Test
    fun neverBlocksReclaimItself() {
        val decision = BlockEvaluator.evaluate(
            packageName = ownPackage,
            now = at(23),
            settings = settings(blocking = true, packages = setOf(ownPackage), downtime = eveningWindow),
            ownPackage = ownPackage,
        )

        assertFalse(decision.blocked)
    }

    @Test
    fun neverBlocksSystemUiOrSettings() {
        listOf("com.android.systemui", "com.android.settings").forEach { packageName ->
            val decision = BlockEvaluator.evaluate(
                packageName = packageName,
                now = at(23),
                settings = settings(
                    blocking = true,
                    packages = setOf(packageName),
                    downtime = eveningWindow,
                ),
                ownPackage = ownPackage,
            )

            assertFalse("$packageName must stay reachable", decision.blocked)
        }
    }

    @Test
    fun honoursExtraIgnoredPackages() {
        val decision = BlockEvaluator.evaluate(
            packageName = blockedApp,
            now = at(23),
            settings = settings(blocking = true),
            ownPackage = ownPackage,
            ignoredPackages = setOf(blockedApp),
        )

        assertFalse(decision.blocked)
    }

    @Test
    fun blankPackageIsAllowed() {
        val decision = BlockEvaluator.evaluate(
            packageName = "",
            now = at(23),
            settings = settings(blocking = true),
            ownPackage = ownPackage,
        )

        assertFalse(decision.blocked)
    }
}
