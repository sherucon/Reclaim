package dev.reclaim.app.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.reclaim.app.ReclaimApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Debug builds only. Lets a tester put the app into a state that would otherwise
 * need a clock change or a completed schedule screen.
 *
 * adb shell am broadcast -n dev.reclaim.app/.debug.DebugSettingsReceiver \
 *   -a dev.reclaim.app.DEBUG_SETTINGS --es action downtime --ei start 0 --ei end 1439 --ez enabled true
 */
class DebugSettingsReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val container = (context.applicationContext as ReclaimApplication).container
        val pending = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        scope.launch {
            try {
                when (intent.getStringExtra("action")) {
                    "downtime" -> container.settingsRepository.update { current ->
                        current.copy(
                            downtime = current.downtime.copy(
                                enabled = intent.getBooleanExtra("enabled", true),
                                startMinutes = intent.getIntExtra("start", 0),
                                endMinutes = intent.getIntExtra("end", 23 * 60 + 59),
                            ),
                        )
                    }
                    "pause" -> container.settingsRepository.update { current ->
                        current.copy(
                            pauseUntil = if (intent.getBooleanExtra("enabled", false)) {
                                java.time.Instant
                                    .now()
                                    .plusSeconds(intent.getLongExtra("seconds", 300))
                            } else {
                                null
                            },
                        )
                    }
                }
            } finally {
                pending.finish()
            }
        }
    }
}
