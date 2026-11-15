package dev.reclaim.app.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dev.reclaim.app.BuildConfig

internal class FirebaseAnalyticsTracker(
    private val analytics: FirebaseAnalytics,
) : AnalyticsTracker {
    override fun logEvent(
        name: String,
        params: Map<String, Any>,
    ) {
        analytics.logEvent(name, params.toBundle())
    }

    override fun setUserProperty(
        name: String,
        value: String?,
    ) {
        analytics.setUserProperty(name, value)
    }

    override fun setEnabled(enabled: Boolean) {
        analytics.setAnalyticsCollectionEnabled(enabled)
    }

    override fun reset() {
        analytics.resetAnalyticsData()
    }
}

private fun Map<String, Any>.toBundle(): Bundle =
    Bundle().apply {
        forEach { (key, value) ->
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Double -> putDouble(key, value)
                is Boolean -> putBoolean(key, value)
                // Only the five scalar shapes above are allowed by the taxonomy.
                else ->
                    if (BuildConfig.DEBUG) {
                        error("Unsupported analytics parameter type for $key: ${value::class.java.name}")
                    }
            }
        }
    }
