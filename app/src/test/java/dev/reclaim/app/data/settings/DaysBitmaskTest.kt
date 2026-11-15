package dev.reclaim.app.data.settings

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek

class DaysBitmaskTest {
    @Test
    fun everyDaySurvivesTheRoundTrip() {
        DayOfWeek.entries.forEach { day ->
            assertEquals(setOf(day), setOf(day).toBitmask().toDays())
        }
    }

    @Test
    fun combinationsSurviveTheRoundTrip() {
        val days = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY, DayOfWeek.SUNDAY)

        assertEquals(days, days.toBitmask().toDays())
    }

    @Test
    fun emptyAndFullMasksAreStable() {
        assertEquals(emptySet<DayOfWeek>(), emptySet<DayOfWeek>().toBitmask().toDays())
        assertEquals(
            DayOfWeek.entries.toSet(),
            DayOfWeek.entries
                .toSet()
                .toBitmask()
                .toDays(),
        )
    }

    @Test
    fun mondayIsTheLowBit() {
        assertEquals(1, setOf(DayOfWeek.MONDAY).toBitmask())
        assertEquals(64, setOf(DayOfWeek.SUNDAY).toBitmask())
    }
}
