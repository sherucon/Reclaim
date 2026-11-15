package dev.reclaim.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.reclaim.app.analytics.AnalyticsEvents
import dev.reclaim.app.design.ReclaimTheme
import dev.reclaim.app.ui.home.PlaceholderHomeScreen

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
                PlaceholderHomeScreen()
            }
        }
    }
}
