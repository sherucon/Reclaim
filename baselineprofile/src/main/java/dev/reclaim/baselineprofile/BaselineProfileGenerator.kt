package dev.reclaim.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import org.junit.Rule
import org.junit.Test

/**
 * Builds the startup profile. A fresh install stops at onboarding, so this
 * journey covers launch and the first Compose frames rather than the tab shell.
 */
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun startup() = baselineProfileRule.collect(
        packageName = "dev.reclaim.app",
        includeInStartupProfile = true,
    ) {
        pressHome()
        startActivityAndWait()
    }
}
