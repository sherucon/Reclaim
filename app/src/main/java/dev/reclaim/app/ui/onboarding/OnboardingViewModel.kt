package dev.reclaim.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.reclaim.app.analytics.AnalyticsEvents
import dev.reclaim.app.analytics.AnalyticsTracker
import dev.reclaim.app.data.onboarding.OnboardingStore
import dev.reclaim.app.domain.onboarding.OnboardingAnswers
import dev.reclaim.app.domain.onboarding.OnboardingPlan
import dev.reclaim.app.domain.onboarding.OnboardingStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Drives the conversation. The step list, the branching, and the resume point
 * all come from the pure [OnboardingPlan], so what the screen shows and what a
 * test asserts cannot drift.
 */
class OnboardingViewModel(
    private val store: OnboardingStore,
    private val analytics: AnalyticsTracker,
    private val now: () -> Long,
    initialAnswers: OnboardingAnswers,
    initialStep: OnboardingStep,
) : ViewModel() {
    data class UiState(
        val answers: OnboardingAnswers,
        val step: OnboardingStep,
        val steps: List<OnboardingStep>,
    ) {
        val index: Int get() = steps.indexOf(step).coerceAtLeast(0)
        val total: Int get() = steps.size
        val isSummary: Boolean get() = step == OnboardingStep.SUMMARY
        val canGoBack: Boolean get() = step != OnboardingStep.WELCOME
    }

    private val startedAt = now()
    private var stepStartedAt = startedAt

    private val _state = MutableStateFlow(
        UiState(
            answers = initialAnswers,
            step = initialStep,
            steps = OnboardingPlan.visibleSteps(initialAnswers),
        ),
    )
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        analytics.logEvent(AnalyticsEvents.ONBOARDING_STARTED)
        logStepViewed(_state.value)
    }

    fun updateAnswers(transform: (OnboardingAnswers) -> OnboardingAnswers) {
        val current = _state.value
        val answers = transform(current.answers)
        val steps = OnboardingPlan.visibleSteps(answers)
        val step = if (current.step in steps) {
            current.step
        } else {
            OnboardingPlan.resumeAt(current.step.id, answers)
        }
        _state.value = current.copy(answers = answers, step = step, steps = steps)
        persist()
    }

    fun next(optionCode: String? = null) {
        val current = _state.value
        logStepCompleted(current.step, optionCode)
        val next = OnboardingPlan.next(current.step, current.answers) ?: return
        _state.value = current.copy(step = next)
        logStepViewed(_state.value)
        persist()
    }

    fun back() {
        val current = _state.value
        val previous = OnboardingPlan.previous(current.step, current.answers) ?: return
        _state.value = current.copy(step = previous)
        logStepViewed(_state.value)
        persist()
    }

    fun skipSetup(accessibilityGranted: Boolean) {
        val current = _state.value
        analytics.logEvent(
            AnalyticsEvents.ONBOARDING_SKIPPED,
            mapOf(AnalyticsEvents.Params.STEP_ID to current.step.id),
        )
        logStepCompleted(current.step, optionCode = "skipped")
        finish(accessibilityGranted)
    }

    fun finish(accessibilityGranted: Boolean) {
        val current = _state.value
        viewModelScope.launch {
            store.complete(current.answers, accessibilityGranted)
            analytics.logEvent(
                AnalyticsEvents.ONBOARDING_COMPLETED,
                mapOf(
                    AnalyticsEvents.Params.DURATION_SEC to ((now() - startedAt) / 1_000L).toInt(),
                ),
            )
        }
    }

    private fun persist() {
        val current = _state.value
        viewModelScope.launch { store.save(current.answers, current.step) }
    }

    private fun logStepViewed(state: UiState) {
        stepStartedAt = now()
        analytics.logEvent(
            AnalyticsEvents.ONBOARDING_STEP_VIEWED,
            mapOf(
                AnalyticsEvents.Params.STEP_ID to state.step.id,
                AnalyticsEvents.Params.STEP_INDEX to state.index,
            ),
        )
    }

    private fun logStepCompleted(
        step: OnboardingStep,
        optionCode: String?,
    ) {
        val params = mutableMapOf<String, Any>(
            AnalyticsEvents.Params.STEP_ID to step.id,
            AnalyticsEvents.Params.DURATION_MS to (now() - stepStartedAt).toInt(),
        )
        if (optionCode != null) params[AnalyticsEvents.Params.OPTION_CODE] = optionCode
        analytics.logEvent(AnalyticsEvents.ONBOARDING_STEP_COMPLETED, params)
    }

    companion object {
        fun factory(
            store: OnboardingStore,
            analytics: AnalyticsTracker,
            initialAnswers: OnboardingAnswers,
            initialStep: OnboardingStep,
            now: () -> Long = System::currentTimeMillis,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    OnboardingViewModel(
                        store = store,
                        analytics = analytics,
                        now = now,
                        initialAnswers = initialAnswers,
                        initialStep = initialStep,
                    )
                }
            }
    }
}
