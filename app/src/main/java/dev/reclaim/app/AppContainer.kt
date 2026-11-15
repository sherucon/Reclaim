package dev.reclaim.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dev.reclaim.app.analytics.AnalyticsTracker
import dev.reclaim.app.analytics.CrashReporter
import dev.reclaim.app.analytics.createCrashReporter
import dev.reclaim.app.analytics.createTracker
import dev.reclaim.app.data.counters.CounterRepository
import dev.reclaim.app.data.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Manual dependency container. Deliberately small: everything here is a
 * singleton the whole app shares.
 */
class AppContainer(
    context: Context,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val settingsStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = scope,
        produceFile = { context.preferencesDataStoreFile("settings") },
    )

    private val countersStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = scope,
        produceFile = { context.preferencesDataStoreFile("counters") },
    )

    val packageName: String = context.packageName

    val settingsRepository = SettingsRepository(settingsStore)

    val counterRepository = CounterRepository(countersStore)

    // shortcut: real consent lands in Phase 3. Debug builds report so DebugView
    // works during development; release builds stay silent until the user opts in.
    private val telemetryEnabled = BuildConfig.DEBUG

    val analytics: AnalyticsTracker = createTracker(context, telemetryEnabled)

    val crashReporter: CrashReporter = createCrashReporter(context, telemetryEnabled)
}
