package com.nicolascommandeur.diablo4cowcompanion

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HuntTest {
    private val hunt = Hunt(Relic.SHARD, "Barbarian")

    @Test
    fun bulkAdditionStopsBeforeFinalCow() {
        assertEquals(665, hunt.setCount(660).add(10).count)
        assertEquals(665, hunt.setCount(664).add(5).count)
        assertEquals(665, hunt.setCount(665).add(1).count)
        assertEquals(665, hunt.add(Int.MAX_VALUE).count)
    }

    @Test
    fun finalCowRequiresExactly665AndNeverMarksRelicAutomatically() {
        assertEquals(0, hunt.recordFinalKill().count)
        val completed = hunt.setCount(665).recordFinalKill()
        assertEquals(666, completed.count)
        assertFalse(completed.collected)
        assertEquals(completed, completed.add(10))
    }

    @Test
    fun undoRestoresWholeBatchAndOnlyOnce() {
        val result = hunt.setCount(42).add(10).undo()
        assertEquals(42, result.count)
        assertNull(result.previousCount)
        assertEquals(result, result.undo())
    }

    @Test
    fun correctionBoundsAndCollectionStayConsistent() {
        assertEquals(0, hunt.setCount(-10).count)
        assertEquals(666, hunt.setCount(999).count)
        assertFalse(hunt.markCollected(true).collected)
        val completed = hunt.setCount(666).markCollected(true)
        assertTrue(completed.collected)
        assertFalse(completed.setCount(665).collected)
        assertFalse(completed.undo().collected)
    }

    @Test
    fun resetKeepsIdentityAndRelic() {
        val configured = hunt.setCount(666).markCollected(true)
        assertEquals(hunt, configured.reset())
    }

    @Test
    fun allThreeSlotsHaveSeparateProgressAndDifferentRelics() {
        val initial = HuntState()
        val updated = initial.copy(hunts = initial.hunts.mapIndexed { index, value ->
            if (index == 1) value.add(10) else value
        })
        assertEquals(listOf(0, 10, 0), updated.hunts.map { it.count })
        assertEquals(3, updated.hunts.map { it.relic }.distinct().size)
        assertEquals(3, updated.hunts.map { it.relic.zones }.distinct().size)
        assertEquals(listOf(0, 0, 0), initial.hunts.map { it.count })
    }
}
