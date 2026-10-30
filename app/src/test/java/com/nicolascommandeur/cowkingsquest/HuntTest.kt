package com.nicolascommandeur.cowkingsquest

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
        assertEquals(665, hunt.setCount(660).add(10, continueAfterGoal = true).count)
    }

    @Test
    fun finalCowRequiresExactly665AndNeverMarksRelicAutomatically() {
        assertEquals(0, hunt.recordFinalKill().count)
        val completed = hunt.setCount(665).recordFinalKill()
        assertEquals(666, completed.count)
        assertEquals(completed, completed.add(10))
        assertTrue(HuntState().updateActive(completed).collectedRelics.isEmpty())
    }

    @Test
    fun undoRestoresWholeBatchAndOnlyOnce() {
        val result = hunt.setCount(42).add(10).undo()
        assertEquals(42, result.count)
        assertNull(result.previousCount)
        assertEquals(result, result.undo())
    }

    @Test
    fun correctionsNeverUndoActualRelicPickups() {
        assertEquals(0, hunt.setCount(-10).count)
        assertEquals(666, hunt.setCount(999).count)
        val collected = HuntState().markRelic(Relic.SHARD, true)
        assertTrue(collected.activeHunt.firstRelicObtained)
        val corrected = collected.updateActive(collected.activeHunt.setCount(600))
        assertEquals(setOf(Relic.SHARD), corrected.collectedRelics)
        assertEquals(
            setOf(Relic.SHARD),
            corrected.updateActive(corrected.activeHunt.undo()).collectedRelics
        )
    }

    @Test
    fun randomDropCountingContinuesWithoutAnotherGate() {
        val completed = hunt.setCount(666)
        assertEquals(676, completed.add(10, continueAfterGoal = true).count)
        assertEquals(666, completed.add(10, continueAfterGoal = true).undo().count)
        assertEquals(1332, completed.setCount(1322, true).add(10, true).count)
        assertEquals(Int.MAX_VALUE, completed.add(Int.MAX_VALUE, true).count)
        assertEquals(Int.MAX_VALUE, completed.add(Int.MAX_VALUE, true).add(10, true).count)
    }

    @Test
    fun earlyPickupUnlocksRngEvenWhenManualCountIsLow() {
        val state =
            HuntState(strategy = CharacterStrategy.ONE_CHARACTER).markRelic(Relic.SHARD, true)
        assertTrue(state.isRngPhase)
        assertEquals(10, state.activeHunt.add(10, continueAfterGoal = true).count)
        assertEquals(Relic.TOME, state.targetRelic)
        assertFalse(state.markRelic(Relic.SHARD, false).activeHunt.firstRelicObtained)
    }

    @Test
    fun strategyAndSourceChangesKeepIndependentCharacterProgress() {
        val initial = HuntState(
            hunts = listOf(
                hunt.setCount(50), Hunt(Relic.TOME, "Druid", 842),
                Hunt(Relic.FRAGMENT, "Rogue", 200)
            ), selected = 2, collectedRelics = setOf(Relic.TOME)
        )
        val single = initial.copy(
            strategy = CharacterStrategy.ONE_CHARACTER, singleCharacter = 1,
            cowSource = CowSource.BELL_OF_THE_BOVINE
        )
        assertEquals("Druid", single.activeHunt.name)
        assertEquals(842, single.activeHunt.count)
        assertEquals(initial.hunts, single.hunts)
        val back = single.copy(
            strategy = CharacterStrategy.THREE_CHARACTERS,
            cowSource = CowSource.WORLD_COWS
        )
        assertEquals(initial.hunts, back.hunts)
        assertEquals("Rogue", back.activeHunt.name)
        assertEquals(initial.collectedRelics, back.collectedRelics)
    }

    @Test
    fun resetKeepsOtherCharactersAndCollectedRelics() {
        val state = HuntState().updateActive(hunt.setCount(666)).markRelic(Relic.SHARD, true)
        val reset = state.updateActive(state.activeHunt.reset())
        assertEquals(hunt, reset.activeHunt)
        assertEquals(state.collectedRelics, reset.collectedRelics)
        assertEquals(state.hunts.drop(1), reset.hunts.drop(1))
    }

    @Test
    fun pickupCreditsTheActiveCharacterRatherThanTheRelicSlot() {
        val state = HuntState(strategy = CharacterStrategy.ONE_CHARACTER, singleCharacter = 2)
            .markRelic(Relic.TOME, true)
        assertTrue(state.hunts[2].firstRelicObtained)
        assertFalse(state.hunts[1].firstRelicObtained)
        assertEquals(setOf(Relic.TOME), state.collectedRelics)
    }

    @Test
    fun undoingPickupAndCorrectingCountRestoresFirstDropCounting() {
        val state = HuntState().updateActive(hunt.setCount(750, true))
            .markRelic(Relic.SHARD, true).markRelic(Relic.SHARD, false)
        assertFalse(state.activeHunt.firstRelicObtained)
        assertEquals(750, state.activeHunt.count)
        assertTrue(state.collectedRelics.isEmpty())
        val finalCow = state.activeHunt.setCount(665)
        assertFalse(finalCow.pastFirstDrop)
        assertEquals(666, finalCow.recordFinalKill().count)
        val earlier = state.activeHunt.setCount(599)
        assertFalse(earlier.pastFirstDrop)
        assertEquals(600, earlier.add(1).count)
        assertEquals(604, earlier.add(5).count)
        assertEquals(609, earlier.add(10).count)
    }

    @Test
    fun undoingOnePickupPreservesOtherPickupsOnThatCharacter() {
        val state = HuntState(strategy = CharacterStrategy.ONE_CHARACTER)
            .markRelic(Relic.SHARD, true).markRelic(Relic.TOME, true)
            .markRelic(Relic.SHARD, false)
        assertEquals(setOf(Relic.TOME), state.activeHunt.obtainedRelics)
        assertTrue(state.isRngPhase)
        assertFalse(state.markRelic(Relic.TOME, false).isRngPhase)
    }

    @Test
    fun undoingPickupUpdatesItsOwnerEvenAfterSwitchingCharacters() {
        val state = HuntState().markRelic(Relic.SHARD, true).copy(selected = 1)
            .markRelic(Relic.TOME, true).markRelic(Relic.SHARD, false)
        assertFalse(state.hunts[0].firstRelicObtained)
        assertTrue(state.hunts[1].firstRelicObtained)
        assertEquals(setOf(Relic.TOME), state.collectedRelics)
    }
}
