package dev.reclaim.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DailyCountsTest {
    private val today = 20_000L

    @Test
    fun recordingAccumulatesWithinTheSameDay() {
        val counts = DailyCounts()
            .record(today, DayCounts(appBlocks = 1))
            .record(today, DayCounts(appBlocks = 2, reelsBreaks = 1))
            .record(today, DayCounts(dnsBlocks = 5))

        assertEquals(DayCounts(appBlocks = 3, reelsBreaks = 1, dnsBlocks = 5), counts.days[today])
    }

    @Test
    fun recordingNothingKeepsTheStoreEmpty() {
        val counts = DailyCounts().record(today, DayCounts())

        assertEquals(emptyMap<Long, DayCounts>(), counts.days)
    }

    @Test
    fun windowIsZeroFilledAndOldestFirst() {
        val counts = DailyCounts()
            .record(today, DayCounts(appBlocks = 2))
            .record(today - 2, DayCounts(reelsBreaks = 4))

        val window = counts.window(today, days = 3)

        assertEquals(3, window.size)
        assertEquals(DayCounts(reelsBreaks = 4), window[0])
        assertEquals(DayCounts(), window[1])
        assertEquals(DayCounts(appBlocks = 2), window[2])
    }

    @Test
    fun windowTotalAddsEveryField() {
        val counts = DailyCounts()
            .record(today, DayCounts(appBlocks = 2, reelsBreaks = 1))
            .record(today - 1, DayCounts(dnsBlocks = 9))
            .record(today - 30, DayCounts(appBlocks = 100))

        assertEquals(
            DayCounts(appBlocks = 2, reelsBreaks = 1, dnsBlocks = 9),
            counts.windowTotal(today, days = 7),
        )
    }

    @Test
    fun pruneKeepsTheWindowAndDropsTheRest() {
        val counts = DailyCounts()
            .record(today, DayCounts(appBlocks = 1))
            .record(today - 89, DayCounts(appBlocks = 2))
            .record(today - 90, DayCounts(appBlocks = 3))
            .prune(today, keepDays = 90)

        assertEquals(setOf(today, today - 89), counts.days.keys)
    }

    @Test
    fun totalsAddUp() {
        val counts = DayCounts(appBlocks = 1, reelsBreaks = 2, dnsBlocks = 3)

        assertEquals(6, counts.total)
        assertEquals(2, (counts + counts).appBlocks)
    }
}
