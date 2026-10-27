package com.aksamitsah.canvasos.en

import org.junit.Assert.assertEquals
import org.junit.Test

/** Guards the French wording of a memory cleanup's result (plurals, the clamp at zero, the cache-only case) shown in the Control Center and Réglages. */
class CleanerResultTest {
    private fun result(before: Long, after: Long, closed: List<String> = emptyList(), spared: List<String> = emptyList(), viaAdb: Boolean = true) =
        Cleaner.Result(before, after, closed.size, viaAdb, closed, spared)

    @Test fun freedMemoryIsNeverNegative() {
        assertEquals(0, result(500, 480).freedMb)
        assertEquals("0 MB freed", result(500, 480).freedText)
        assertEquals(0, result(500, 500).freedMb)
        assertEquals(1, result(500, 501).freedMb)
    }

    @Test fun freedTextIsSingularUpToOneMegabyte() {
        assertEquals("0 MB freed", result(100, 100).freedText)
        assertEquals("1 MB freed", result(100, 101).freedText)
        assertEquals("2 MB freed", result(100, 102).freedText)
        assertEquals("1024 MB freed", result(0, 1024).freedText)
    }

    @Test fun closedTextCountsTheClosedApps() {
        assertEquals("no apps closed", result(0, 0).closedText)
        assertEquals("1 app closed", result(0, 0, closed = listOf("a.b")).closedText)
        assertEquals("2 apps closed", result(0, 0, closed = listOf("a.b", "c.d")).closedText)
        assertEquals("12 apps closed", result(0, 0, closed = (1..12).map { "app.n$it" }).closedText)
    }

    @Test fun closedTextIgnoresTheStoppedCount() {
        // `stopped` is how many force-stops were sent; the text reads the apps that really had a process.
        val r = Cleaner.Result(0, 0, stopped = 9, viaAdb = true, closed = listOf("a.b"))
        assertEquals("1 app closed", r.closedText)
    }

    @Test fun theSummaryListsOnlyWhatHappened() {
        assertEquals("5 MB freed · no apps closed", result(10, 15).summary())
        assertEquals("5 MB freed · 1 app closed", result(10, 15, closed = listOf("a.b")).summary())
        assertEquals("0 MB freed · no apps closed · 3 in use kept",
            result(10, 10, spared = listOf("a.b", "c.d", "e.f")).summary())
        assertEquals("1 in use kept", result(0, 0, spared = listOf("a.b")).summary().substringAfterLast(" · "))
    }

    @Test fun withoutAdbOnlyTheCacheWasCleanedAndNoAppIsListed() {
        val r = result(100, 130, closed = listOf("a.b", "c.d"), spared = listOf("e.f"), viaAdb = false)
        assertEquals("30 MB freed (cache only)", r.summary())
        assertEquals("the separator makes no difference there", "30 MB freed (cache only)", r.summary(", "))
        assertEquals("1 MB freed (cache only)", result(100, 101, viaAdb = false).summary())
    }

    @Test fun theSeparatorIsWhatTheCallerAsks() {
        val r = result(0, 8, closed = listOf("a.b"), spared = listOf("c.d"))
        assertEquals("8 MB freed, 1 app closed, 1 in use kept", r.summary(", "))
        assertEquals("8 MB freed|1 app closed|1 in use kept", r.summary("|"))
    }

    @Test fun theLastCleanLineSplitsBackIntoWhenAndWhat() {
        // Cleaner.clean stores "d MMM HH:mm, <summary(", ")>"; Réglages splits it on the first ", ".
        val stored = "22 oct. 21:45, " + result(0, 312, closed = listOf("a.b", "c.d"), spared = listOf("e.f")).summary(", ")
        assertEquals("22 oct. 21:45", stored.substringBefore(", "))
        assertEquals("312 MB freed, 2 apps closed, 1 in use kept", stored.substringAfter(", ", ""))
    }
}
