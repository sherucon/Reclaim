package dev.reclaim.app.analytics

/**
 * Crash reporting follows the same consent switch as analytics, so a user only
 * has one privacy decision to make.
 */
interface CrashReporter {
    fun log(message: String)

    fun recordException(throwable: Throwable)

    fun setEnabled(enabled: Boolean)
}
