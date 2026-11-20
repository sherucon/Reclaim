package dev.reclaim.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import dev.reclaim.app.analytics.AnalyticsEvents
import dev.reclaim.app.design.ReclaimColors
import dev.reclaim.app.design.ReclaimTheme
import dev.reclaim.app.domain.model.AppSettings
import dev.reclaim.app.ui.nav.ReclaimApp
import dev.reclaim.app.ui.onboarding.OnboardingFlow
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )

        val container = (application as ReclaimApplication).container
        container.analytics.logEvent(AnalyticsEvents.APP_OPEN)

        setContent {
            ReclaimTheme {
                ReclaimRoot(container)
            }
        }
    }
}

@Composable
private fun ReclaimRoot(container: AppContainer) {
    val settings by produceState<AppSettings?>(initialValue = null) {
        container.settingsRepository.settings.collectLatest { value = it }
    }

    when (val current = settings) {
        null -> Box(modifier = Modifier.fillMaxSize().background(ReclaimColors.Canvas))
        else -> if (current.onboardingComplete) {
            ReclaimApp(container, current)
        } else {
            OnboardingFlow(container = container, settings = current)
        }
    }
}
