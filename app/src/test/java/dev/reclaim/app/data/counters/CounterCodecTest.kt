package dev.reclaim.app.data.counters

import dev.reclaim.app.domain.model.DayCounts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CounterCodecTest {
    @Test
    fun countsRoundTrip() {
        val counts = DayCounts(appBlocks = 3, reelsBreaks = 12, dnsBlocks = 480)

        assertEquals(counts, decodeCounts(encodeCounts(counts)))
    }

    @Test
    fun zerosRoundTrip() {
        assertEquals(DayCounts(), decodeCounts(encodeCounts(DayCounts())))
    }

    @Test
    fun garbageDecodesToNullInsteadOfCrashing() {
        assertNull(decodeCounts(null))
        assertNull(decodeCounts(""))
        assertNull(decodeCounts("1,2"))
        assertNull(decodeCounts("a,b,c"))
        assertNull(decodeCounts("1,2,3,4"))
    }
}
