package dev.reclaim.app.domain.onboarding

import dev.reclaim.app.domain.model.DomainCategory
import dev.reclaim.app.domain.model.Sensitivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingPlanTest {
    private val answers = OnboardingAnswers()

    @Test
    fun everyStepIsVisibleWithNoAnswers() {
        val steps = OnboardingPlan.visibleSteps(answers)

        assertTrue(OnboardingStep.WELCOME in steps)
        assertTrue(OnboardingStep.SUMMARY in steps)
        // No platforms and no web struggle means the permission and web steps are out.
        assertTrue(OnboardingStep.APP_PERMISSION !in steps)
        assertTrue(OnboardingStep.WEB_SETUP !in steps)
    }

    @Test
    fun detectablePlatformsUnlockBothPermissionSteps() {
        val steps = OnboardingPlan.visibleSteps(answers.copy(platforms = setOf("instagram")))

        assertTrue(OnboardingStep.APP_PERMISSION in steps)
        assertTrue(OnboardingStep.CONTENT_PERMISSION in steps)
    }

    @Test
    fun platformsWithoutNativeDetectionDoNotUnlockPermissions() {
        val steps = OnboardingPlan.visibleSteps(answers.copy(platforms = setOf("x", "linkedin", "reddit")))

        assertTrue(OnboardingStep.APP_PERMISSION !in steps)
        assertTrue(OnboardingStep.CONTENT_PERMISSION !in steps)
    }

    @Test
    fun websitesStruggleUnlocksTheWebStep() {
        val steps = OnboardingPlan.visibleSteps(answers.copy(struggles = setOf(Struggle.WEBSITES)))

        assertTrue(OnboardingStep.WEB_SETUP in steps)
    }

    @Test
    fun nextSkipsInvisibleSteps() {
        val none = OnboardingAnswers(platforms = emptySet())
        // Permissions and the web step are gone, but notifications and analytics remain.
        assertEquals(OnboardingStep.NOTIFICATIONS, OnboardingPlan.next(OnboardingStep.SENSITIVITY, none))

        val instagram = none.copy(platforms = setOf("instagram"))
        assertEquals(OnboardingStep.APP_PERMISSION, OnboardingPlan.next(OnboardingStep.SENSITIVITY, instagram))
    }

    @Test
    fun previousSkipsThemToo() {
        val answers = OnboardingAnswers(platforms = setOf("instagram"), struggles = setOf(Struggle.WEBSITES))

        assertEquals(OnboardingStep.WEB_SETUP, OnboardingPlan.previous(OnboardingStep.NOTIFICATIONS, answers))
        assertEquals(OnboardingStep.SENSITIVITY, OnboardingPlan.previous(OnboardingStep.APP_PERMISSION, answers))
        assertNull(OnboardingPlan.previous(OnboardingStep.WELCOME, answers))
    }

    @Test
    fun resumeKeepsAStillValidStep() {
        assertEquals(
            OnboardingStep.SENSITIVITY,
            OnboardingPlan.resumeAt("sensitivity", answers),
        )
    }

    @Test
    fun resumeJumpsForwardWhenTheStoredStepDisappeared() {
        // Permissions were visible, then the user cleared every app.
        val resumed = OnboardingPlan.resumeAt("app_permission", answers.copy(platforms = emptySet()))

        assertEquals(OnboardingStep.NOTIFICATIONS, resumed)
    }

    @Test
    fun resumeFallsBackToWelcomeForJunk() {
        assertEquals(OnboardingStep.WELCOME, OnboardingPlan.resumeAt("nonsense", answers))
        assertEquals(OnboardingStep.WELCOME, OnboardingPlan.resumeAt("", answers))
        assertEquals(OnboardingStep.WELCOME, OnboardingPlan.resumeAt(null, answers))
    }

    @Test
    fun summaryReflectsTheChoices() {
        val lines = OnboardingPlan.summaryLines(
            OnboardingAnswers(
                platforms = setOf("instagram", "youtube"),
                downtime = DowntimeChoice.LATE_NIGHT,
                sensitivity = Sensitivity.STRICT,
                struggles = setOf(Struggle.WEBSITES),
                webCategories = setOf(DomainCategory.SOCIAL, DomainCategory.GAMBLING),
            ),
        )

        assertEquals("Instagram, YouTube blocked", lines[0])
        assertEquals("Quiet from 23:00 to 06:00", lines[1])
        assertEquals("Strict scroll budget", lines[2])
        assertEquals("Gambling, Social sites filtered", lines[3])
    }

    @Test
    fun summaryOmitsWhatWasTurnedOff() {
        val lines = OnboardingPlan.summaryLines(
            OnboardingAnswers(
                platforms = emptySet(),
                downtime = DowntimeChoice.OFF,
                sensitivity = Sensitivity.RELAXED,
            ),
        )

        assertEquals(listOf("Gentle scroll budget"), lines)
    }

    @Test
    fun timeFormattingPads() {
        assertEquals("07:05", OnboardingPlan.formatTime(7 * 60 + 5))
    }
}
