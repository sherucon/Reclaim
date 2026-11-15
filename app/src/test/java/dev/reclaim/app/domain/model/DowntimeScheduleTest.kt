package dev.reclaim.app.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

class DowntimeScheduleTest {
    private val zone: ZoneId = ZoneId.of("Asia/Kolkata")
    private val monday = LocalDate.of(2026, 11, 16)

    private fun at(
        date: LocalDate,
        hour: Int,
        minute: Int = 0,
    ): ZonedDateTime = date.atTime(hour, minute).atZone(zone)

    private fun next(day: DayOfWeek): LocalDate = monday.with(TemporalAdjusters.nextOrSame(day))

    @Test
    fun disabledScheduleNeverBlocks() {
        val schedule = DowntimeSchedule(
            enabled = false,
            days = setOf(DayOfWeek.MONDAY),
            startMinutes = 9 * 60,
            endMinutes = 17 * 60,
        )

        assertFalse(schedule.isActive(at(monday, 10)))
    }

    @Test
    fun sameDayWindowIncludesStartAndExcludesEnd() {
        val schedule = DowntimeSchedule(
            enabled = true,
            days = setOf(DayOfWeek.MONDAY),
            startMinutes = 9 * 60,
            endMinutes = 17 * 60,
        )

        assertFalse(schedule.isActive(at(monday, 8, 59)))
        assertTrue(schedule.isActive(at(monday, 9, 0)))
        assertTrue(schedule.isActive(at(monday, 16, 59)))
        assertFalse(schedule.isActive(at(monday, 17, 0)))
        assertFalse(schedule.isActive(at(next(DayOfWeek.TUESDAY), 10)))
    }

    @Test
    fun overnightWindowRunsIntoTheNextMorning() {
        val schedule = DowntimeSchedule(
            enabled = true,
            days = setOf(DayOfWeek.MONDAY),
            startMinutes = 22 * 60,
            endMinutes = 7 * 60,
        )

        assertFalse(schedule.isActive(at(monday, 21, 59)))
        assertTrue(schedule.isActive(at(monday, 22, 0)))
        assertTrue(schedule.isActive(at(monday, 23, 59)))
        assertTrue(schedule.isActive(at(next(DayOfWeek.TUESDAY), 0, 0)))
        assertTrue(schedule.isActive(at(next(DayOfWeek.TUESDAY), 6, 59)))
        assertFalse(schedule.isActive(at(next(DayOfWeek.TUESDAY), 7, 0)))
        assertFalse(schedule.isActive(at(next(DayOfWeek.WEDNESDAY), 3, 0)))
    }

    @Test
    fun overnightWindowOnlyBlocksTheMorningAfterAnEnabledDay() {
        val schedule = DowntimeSchedule(
            enabled = true,
            days = setOf(DayOfWeek.FRIDAY),
            startMinutes = 22 * 60,
            endMinutes = 7 * 60,
        )

        assertTrue(schedule.isActive(at(next(DayOfWeek.FRIDAY), 23)))
        assertTrue(schedule.isActive(at(next(DayOfWeek.SATURDAY), 6)))
        assertFalse(schedule.isActive(at(next(DayOfWeek.SATURDAY), 23)))
        assertFalse(schedule.isActive(at(next(DayOfWeek.SUNDAY), 6)))
    }

    @Test
    fun equalStartAndEndIsATurnedOffWindow() {
        val schedule = DowntimeSchedule(
            enabled = true,
            days = DayOfWeek.entries.toSet(),
            startMinutes = 10 * 60,
            endMinutes = 10 * 60,
        )

        assertFalse(schedule.isActive(at(monday, 10)))
    }

    @Test
    fun defaultCoversEveryEvening() {
        val schedule = DowntimeSchedule(enabled = true)

        assertTrue(schedule.isOvernight)
        assertTrue(schedule.isActive(at(monday, 23)))
        assertTrue(schedule.isActive(at(next(DayOfWeek.SUNDAY), 1)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsOutOfRangeMinutes() {
        DowntimeSchedule(startMinutes = DowntimeSchedule.MINUTES_PER_DAY)
    }
}
