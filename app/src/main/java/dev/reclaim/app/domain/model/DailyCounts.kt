package dev.reclaim.app.domain.model

/**
 * Local per-day counters. These feed the in-app charts, which must stay honest
 * even when analytics consent is off, so they never depend on a network call.
 */
data class DayCounts(
    val appBlocks: Int = 0,
    val reelsBreaks: Int = 0,
    val dnsBlocks: Int = 0,
) {
    val total: Int get() = appBlocks + reelsBreaks + dnsBlocks

    operator fun plus(other: DayCounts): DayCounts =
        DayCounts(
            appBlocks = appBlocks + other.appBlocks,
            reelsBreaks = reelsBreaks + other.reelsBreaks,
            dnsBlocks = dnsBlocks + other.dnsBlocks,
        )
}

/**
 * Counters keyed by epoch day, pruned to a rolling window.
 */
data class DailyCounts(
    val days: Map<Long, DayCounts> = emptyMap(),
) {
    fun record(
        epochDay: Long,
        counts: DayCounts,
    ): DailyCounts {
        if (counts.total == 0) return this
        val existing = days[epochDay] ?: DayCounts()
        return copy(days = days + (epochDay to existing + counts))
    }

    /** Drops anything older than [keepDays] ending at [today]. */
    fun prune(
        today: Long,
        keepDays: Int = DEFAULT_KEEP_DAYS,
    ): DailyCounts {
        val oldest = today - keepDays + 1
        return copy(days = days.filterKeys { it in oldest..today })
    }

    /** Oldest first, zero-filled, so a chart never shifts its axis. */
    fun window(
        today: Long,
        days: Int = DEFAULT_WINDOW_DAYS,
    ): List<DayCounts> {
        val start = today - days + 1
        return (start..today).map { this.days[it] ?: DayCounts() }
    }

    fun windowTotal(
        today: Long,
        days: Int = DEFAULT_WINDOW_DAYS,
    ): DayCounts = window(today, days).fold(DayCounts(), DayCounts::plus)

    companion object {
        const val DEFAULT_KEEP_DAYS = 90
        const val DEFAULT_WINDOW_DAYS = 7
    }
}
