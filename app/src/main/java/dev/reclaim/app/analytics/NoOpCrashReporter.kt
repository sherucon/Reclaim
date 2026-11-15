package dev.reclaim.app.analytics

object NoOpCrashReporter : CrashReporter {
    override fun log(message: String) = Unit

    override fun recordException(throwable: Throwable) = Unit

    override fun setEnabled(enabled: Boolean) = Unit
}
