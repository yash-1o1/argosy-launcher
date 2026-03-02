package com.nendo.argosy.data.repository

import com.nendo.argosy.data.repository.SaveSyncApiClient.Companion.isTimestampSaveName
import com.nendo.argosy.data.repository.SaveSyncApiClient.Companion.parseTimestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class SaveSyncTimestampTest {

    // --- parseTimestamp ---

    @Test
    fun `parseTimestamp parses ISO instant format`() {
        val result = parseTimestamp("2025-01-15T12:00:00Z")
        assertEquals(Instant.parse("2025-01-15T12:00:00Z"), result)
    }

    @Test
    fun `parseTimestamp parses ISO with positive timezone offset`() {
        val result = parseTimestamp("2025-01-15T12:00:00+05:30")
        val expected = java.time.OffsetDateTime.parse("2025-01-15T12:00:00+05:30").toInstant()
        assertEquals(expected, result)
    }

    @Test
    fun `parseTimestamp parses ISO with negative timezone offset`() {
        val result = parseTimestamp("2025-01-15T12:00:00-08:00")
        val expected = java.time.OffsetDateTime.parse("2025-01-15T12:00:00-08:00").toInstant()
        assertEquals(expected, result)
    }

    @Test
    fun `parseTimestamp parses ZonedDateTime format`() {
        val result = parseTimestamp("2025-01-15T12:00:00+00:00[UTC]")
        assertEquals(Instant.parse("2025-01-15T12:00:00Z"), result)
    }

    @Test
    fun `parseTimestamp returns fallback for malformed timestamp`() {
        val before = Instant.now()
        val result = parseTimestamp("not-a-date")
        val after = Instant.now()
        assertTrue(
            "Fallback should be approximately now",
            !result.isBefore(before.minusSeconds(1)) && !result.isAfter(after.plusSeconds(1))
        )
    }

    @Test
    fun `parseTimestamp handles RomM format without timezone`() {
        // RomM sometimes returns timestamps without timezone info.
        // OffsetDateTime.parse handles "2026-02-28T12:09:00" if we fall through to Instant.parse
        // which will fail, then OffsetDateTime which will fail, then ZonedDateTime which will fail,
        // so this should return ~now as fallback. This documents the current behavior.
        val before = Instant.now()
        val result = parseTimestamp("2026-02-28T12:09:00")
        val after = Instant.now()
        // Without timezone, all parsers fail -> falls through to Instant.now()
        assertTrue(
            "No-timezone format should fallback to now",
            !result.isBefore(before.minusSeconds(1)) && !result.isAfter(after.plusSeconds(1))
        )
    }

    // --- isTimestampSaveName (tested via TIMESTAMP_ONLY_PATTERN) ---

    @Test
    fun `isTimestampSaveName matches date-time with hyphens`() {
        assertTrue(isTimestampSaveName("2024-01-15-14-30-00"))
    }

    @Test
    fun `isTimestampSaveName matches date-time with underscores`() {
        assertTrue(isTimestampSaveName("2024-01-15_14_30_00"))
    }

    @Test
    fun `isTimestampSaveName rejects rom name`() {
        assertFalse(isTimestampSaveName("Pokemon Violet"))
    }

    @Test
    fun `isTimestampSaveName rejects timestamp with extra text`() {
        assertFalse(isTimestampSaveName("2024-01-15_14-30-00_backup"))
    }

}
