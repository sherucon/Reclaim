package dev.reclaim.app.domain.model

import java.time.DayOfWeek
import java.time.ZonedDateTime

/**
 * One daily quiet window.
 *
 * The day bit refers to the day the window starts, so Monday with 22:00 to 07:00
 * blocks Monday 22:00 through Tuesday 07:00.
 */
data class DowntimeSchedule(
    val enabled: Boolean = false,
    val days: Set<DayOfWeek> = DayOfWeek.entries.toSet(),
    val startMinutes: Int = DEFAULT_START_MINUTES,
    val endMinutes: Int = DEFAULT_END_MINUTES,
) {
    init {
        require(startMinutes in 0 until MINUTES_PER_DAY) { "startMinutes out of range" }
        require(endMinutes in 0 until MINUTES_PER_DAY) { "endMinutes out of range" }
    }

    val isOvernight: Boolean get() = startMinutes > endMinutes

    fun isActive(now: ZonedDateTime): Boolean {
        if (!enabled || startMinutes == endMinutes) return false
        val minuteOfDay = now.hour * MINUTES_PER_HOUR + now.minute

        return if (startMinutes < endMinutes) {
            now.dayOfWeek in days && minuteOfDay >= startMinutes && minuteOfDay < endMinutes
        } else {
            val startedToday = now.dayOfWeek in days && minuteOfDay >= startMinutes
            val startedYesterday = now.minusDays(1).dayOfWeek in days && minuteOfDay < endMinutes
            startedToday || startedYesterday
        }
    }

    companion object {
        const val MINUTES_PER_HOUR = 60
        const val MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR
        const val DEFAULT_START_MINUTES = 22 * MINUTES_PER_HOUR
        const val DEFAULT_END_MINUTES = 7 * MINUTES_PER_HOUR
    }
}
