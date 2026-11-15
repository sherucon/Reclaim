package dev.reclaim.app.ui.onboarding

import dev.reclaim.app.analytics.AnalyticsEvents
import dev.reclaim.app.analytics.FakeAnalyticsTracker
import dev.reclaim.app.data.onboarding.OnboardingStore
import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.domain.model.DomainCategory
import dev.reclaim.app.domain.onboarding.DowntimeChoice
import dev.reclaim.app.domain.onboarding.OnboardingAnswers
import dev.reclaim.app.domain.onboarding.OnboardingStep
import dev.reclaim.app.domain.onboarding.Struggle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val analytics = FakeAnalyticsTracker()
    private var now = 1_000L

    private class FakeStore : OnboardingStore {
        var settings = AppSettings()
        var saved: Pair<OnboardingAnswers, OnboardingStep>? = null
        var completed: Pair<OnboardingAnswers, Boolean>? = null

        override suspend fun load(): AppSettings = settings

        override suspend fun save(
            answers: OnboardingAnswers,
            step: OnboardingStep,
        ) {
            saved = answers to step
        }

        override suspend fun complete(
            answers: OnboardingAnswers,
            accessibilityGranted: Boolean,
        ) {
            completed = answers to accessibilityGranted
            settings = settings.copy(onboardingComplete = true)
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(
        answers: OnboardingAnswers = OnboardingAnswers(),
        step: OnboardingStep = OnboardingStep.WELCOME,
    ): Pair<OnboardingViewModel, FakeStore> {
        val store = FakeStore()
        val vm = OnboardingViewModel(
            store = store,
            analytics = analytics,
            now = { now },
            initialAnswers = answers,
            initialStep = step,
        )
        return vm to store
    }

    @Test
    fun startsOnWelcomeAndLogsIt() {
        val (vm, _) = viewModel()

        assertEquals(OnboardingStep.WELCOME, vm.state.value.step)
        assertEquals(
            OnboardingStep.WELCOME,
            vm.state.value.steps
                .first(),
        )
        assertEquals(
            listOf(AnalyticsEvents.ONBOARDING_STARTED, AnalyticsEvents.ONBOARDING_STEP_VIEWED),
            analytics.names(),
        )
    }

    @Test
    fun nextAdvancesAndPersists() =
        runTest(dispatcher) {
            val (vm, store) = viewModel()

            now += 1_200
            vm.next(optionCode = "welcome")
            advanceUntilIdle()

            assertEquals(OnboardingStep.NAME, vm.state.value.step)
            assertEquals(OnboardingStep.NAME, store.saved?.second)
            val completed = analytics.events.first { it.name == AnalyticsEvents.ONBOARDING_STEP_COMPLETED }
            assertEquals("welcome", completed.params[AnalyticsEvents.Params.OPTION_CODE])
            assertEquals(1_200, completed.params[AnalyticsEvents.Params.DURATION_MS])
        }

    @Test
    fun selectingOnlyUndetectablePlatformsSkipsThePermissionSteps() {
        val (vm, _) = viewModel()

        vm.updateAnswers { it.copy(platforms = setOf("x")) }

        assertEquals(
            listOf(
                OnboardingStep.WELCOME,
                OnboardingStep.NAME,
                OnboardingStep.APPS,
                OnboardingStep.STRUGGLE,
                OnboardingStep.DOWNTIME,
                OnboardingStep.SENSITIVITY,
                OnboardingStep.NOTIFICATIONS,
                OnboardingStep.ANALYTICS,
                OnboardingStep.SUMMARY,
            ),
            vm.state.value.steps,
        )
    }

    @Test
    fun selectingInstagramUnlocksPermissionsAndTheWebStepFollowsTheStruggle() {
        val (vm, _) = viewModel()

        vm.updateAnswers {
            it.copy(
                platforms = setOf("instagram"),
                struggles = setOf(Struggle.WEBSITES),
                webCategories = setOf(DomainCategory.SOCIAL),
            )
        }

        assertTrue(OnboardingStep.APP_PERMISSION in vm.state.value.steps)
        assertTrue(OnboardingStep.WEB_SETUP in vm.state.value.steps)
    }

    @Test
    fun backRetreatsWithoutFinishing() {
        val (vm, _) = viewModel(step = OnboardingStep.APPS)

        vm.back()

        assertEquals(OnboardingStep.NAME, vm.state.value.step)
    }

    @Test
    fun finishWritesSettingsAndLogsCompletion() =
        runTest(dispatcher) {
            val (vm, store) = viewModel(step = OnboardingStep.SUMMARY)

            vm.finish(accessibilityGranted = true)
            advanceUntilIdle()

            assertEquals(true, store.completed?.second)
            assertTrue(analytics.names().contains(AnalyticsEvents.ONBOARDING_COMPLETED))
        }

    @Test
    fun skipCompletesTheFlowFromAnyStep() =
        runTest(dispatcher) {
            val (vm, store) = viewModel(step = OnboardingStep.APPS)

            vm.skipSetup(accessibilityGranted = false)
            advanceUntilIdle()

            assertEquals(false, store.completed?.second)
            assertTrue(analytics.names().contains(AnalyticsEvents.ONBOARDING_SKIPPED))
        }

    @Test
    fun answersSurviveInTheDraftAndNoEventCarriesFreeText() =
        runTest(dispatcher) {
            val (vm, store) = viewModel()
            val name = "Priya Example"

            vm.updateAnswers {
                it.copy(
                    name = name,
                    platforms = setOf("instagram"),
                    downtime = DowntimeChoice.LATE_NIGHT,
                )
            }

            advanceUntilIdle()

            assertEquals(name, vm.state.value.answers.name)
            assertEquals(name, store.saved?.first?.name)
            analytics.events.forEach { event ->
                event.params.values.forEach { value ->
                    assertFalse("event leaked free text: $event", value.toString().contains("Priya"))
                }
            }
        }
}
