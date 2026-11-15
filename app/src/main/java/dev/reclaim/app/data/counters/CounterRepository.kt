package dev.reclaim.app.data.counters

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.reclaim.app.domain.model.DailyCounts
import dev.reclaim.app.domain.model.DayCounts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Stores one line per day, so a read never deserializes more than the window it
 * needs and a corrupt line costs one day, not the whole history.
 */
class CounterRepository(
    private val dataStore: DataStore<Preferences>,
) {
    fun observe(
        today: Long,
        days: Int = DailyCounts.DEFAULT_WINDOW_DAYS,
    ): Flow<List<DayCounts>> = preferences().map { read(it).window(today, days) }

    suspend fun record(
        today: Long,
        counts: DayCounts,
    ) {
        dataStore.edit { preferences ->
            write(preferences, read(preferences).record(today, counts).prune(today))
        }
    }

    suspend fun prune(today: Long) {
        dataStore.edit { preferences ->
            write(preferences, read(preferences).prune(today))
        }
    }

    private fun preferences(): Flow<Preferences> =
        dataStore.data
            .catch { error ->
                if (error is IOException) emit(emptyPreferences()) else throw error
            }

    private fun read(preferences: Preferences): DailyCounts {
        val days = preferences
            .asMap()
            .entries
            .mapNotNull { (key, value) ->
                val epochDay = key.name.toEpochDayOrNull() ?: return@mapNotNull null
                val counts = decodeCounts(value as? String) ?: return@mapNotNull null
                epochDay to counts
            }.toMap()
        return DailyCounts(days)
    }

    private fun write(
        preferences: MutablePreferences,
        counts: DailyCounts,
    ) {
        val stale = preferences.asMap().keys.filter { key ->
            val epochDay = key.name.toEpochDayOrNull()
            epochDay != null && epochDay !in counts.days
        }
        stale.forEach { key -> preferences.remove(key) }

        counts.days.forEach { (epochDay, dayCounts) ->
            preferences[stringPreferencesKey(PREFIX + epochDay)] = encodeCounts(dayCounts)
        }
    }

    private companion object {
        const val PREFIX = "counters.day."

        fun String.toEpochDayOrNull(): Long? = if (startsWith(PREFIX)) removePrefix(PREFIX).toLongOrNull() else null
    }
}

internal fun encodeCounts(counts: DayCounts): String = "${counts.appBlocks},${counts.reelsBreaks},${counts.dnsBlocks}"

internal fun decodeCounts(raw: String?): DayCounts? {
    val parts = raw?.split(',') ?: return null
    if (parts.size != 3) return null
    val appBlocks = parts[0].toIntOrNull() ?: return null
    val reelsBreaks = parts[1].toIntOrNull() ?: return null
    val dnsBlocks = parts[2].toIntOrNull() ?: return null
    return DayCounts(appBlocks = appBlocks, reelsBreaks = reelsBreaks, dnsBlocks = dnsBlocks)
}
