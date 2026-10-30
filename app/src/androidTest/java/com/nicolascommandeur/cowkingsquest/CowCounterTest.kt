package com.nicolascommandeur.cowkingsquest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nicolascommandeur.cowkingsquest.ui.theme.DiabloCowCounterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CowCounterTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun earlyZoneReminderHandlesSingleAndBulkKillsForEachRelic() {
        var state by mutableStateOf(
            HuntState(
                hunts = Relic.entries.mapIndexed { index, relic ->
                    Hunt(
                        relic,
                        name = "Character ${index + 1}",
                        count = listOf(598, 596, 595)[index]
                    )
                }
            )
        )

        compose.setContent { DiabloCowCounterTheme { CowCounter(state) { state = it } } }

        compose.onNodeWithText("+1   Cow killed").performScrollTo().performClick()
        compose.onNodeWithText("Switch zones before more kills").assertDoesNotExist()

        listOf("+1   Cow killed", "+5", "+10").forEachIndexed { index, button ->
            compose.runOnIdle { state = state.copy(selected = index) }
            compose.onNodeWithText(button).performScrollTo().performClick()
            compose.onNodeWithText("Switch zones before more kills").assertIsDisplayed()

            val relic = Relic.entries[index]

            compose.onNode(hasText("Character ${index + 1}") and hasAnyAncestor(isDialog()))
                .performScrollTo().assertIsDisplayed()
            compose.onNode(hasText(relic.zoneInstruction) and hasAnyAncestor(isDialog()))
                .performScrollTo().assertIsDisplayed()
            compose.onNode(hasText(relic.title) and hasAnyAncestor(isDialog()))
                .performScrollTo().assertIsDisplayed()
            compose.runOnIdle {
                assertEquals(listOf(600, 601, 605)[index], state.hunts[index].count)
                assertFalse(relic in state.collectedRelics)
            }
            compose.onNodeWithText("Got it").performClick()
            compose.onNodeWithText("+1   Cow killed").performScrollTo().performClick()
            compose.onNodeWithText("Switch zones before more kills").assertDoesNotExist()
        }
    }

    @Test
    fun correctingPastReminderThresholdWarnsWithoutChangingTheSavedCount() {
        var state by mutableStateOf(HuntState())

        compose.setContent { DiabloCowCounterTheme { CowCounter(state) { state = it } } }
        compose.onNodeWithText("Correct count").performScrollTo().performClick()
        compose.onNode(hasSetTextAction()).performTextReplacement("645")
        compose.onNodeWithText("Save count").performClick()
        compose.onNodeWithText("Switch zones before more kills").assertIsDisplayed()
        compose.onNodeWithText("Got it").performClick()
        compose.runOnIdle { assertEquals(645, state.hunts[0].count) }
        compose.onNodeWithText("Undo").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(0, state.hunts[0].count) }
    }

    @Test
    fun undoAndRecrossingThresholdShowsReminderAgain() {
        var state by mutableStateOf(
            HuntState(
                hunts = Relic.entries.map { relic ->
                    Hunt(relic, name = "Test", count = 599)
                }
            )
        )

        compose.setContent { DiabloCowCounterTheme { CowCounter(state) { state = it } } }
        compose.onNodeWithText("+1   Cow killed").performScrollTo().performClick()
        compose.onNodeWithText("Got it").performClick()
        compose.onNodeWithText("Undo").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(599, state.hunts[0].count) }
        compose.onNodeWithText("+1   Cow killed").performScrollTo().performClick()
        compose.onNodeWithText("Switch zones before more kills").assertIsDisplayed()
    }

    @Test
    fun countingSwitchingAndFinalConfirmation() {
        var state by mutableStateOf(HuntState())

        compose.setContent { DiabloCowCounterTheme { CowCounter(state) { state = it } } }
        compose.onNodeWithText("+1   Cow killed").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(listOf(1, 0, 0), state.hunts.map { it.count }) }
        compose.onNodeWithContentDescription("Select Character 2, Tome, 0 of 666 cows")
            .performScrollTo().performClick()
        compose.onNodeWithText("+10").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(listOf(1, 10, 0), state.hunts.map { it.count }) }
        compose.onNodeWithText("Undo").performClick()
        compose.runOnIdle {
            assertEquals(0, state.hunts[1].count)
            state = state.copy(
                hunts = state.hunts.mapIndexed { index, hunt ->
                    if (index == 1) hunt.setCount(664) else hunt
                }
            )
        }
        compose.onNodeWithText("+5").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(665, state.hunts[1].count) }
        compose.onNodeWithText("Record final cow").performScrollTo().performClick()
        compose.onNodeWithText("Not yet").performClick()
        compose.runOnIdle { assertEquals(665, state.hunts[1].count) }
        compose.onNodeWithText("Record final cow").performScrollTo().performClick()
        compose.onNodeWithText("Record cow #666").performClick()
        compose.runOnIdle {
            assertEquals(666, state.hunts[1].count)
            assertFalse(Relic.TOME in state.collectedRelics)
        }
        compose.onNode(isToggleable() and hasAnySibling(hasText("Relic collected")))
            .performScrollTo()
            .performClick()
        compose.runOnIdle { assertTrue(Relic.TOME in state.collectedRelics) }
    }

    @Test
    fun correctingUncheckedPickupRestoresCountingAndRouteInstructions() {
        var state by mutableStateOf(
            HuntState()
                .updateActive(Hunt(Relic.SHARD, name = "Character 1", count = 750))
                .markRelic(Relic.SHARD, true)
        )
        compose.setContent {
            DiabloCowCounterTheme {
                CowHuntApp(
                    state,
                    emptySet(),
                    onHuntChange = { state = it },
                    onGuideChange = {}
                )
            }
        }
        compose.onNode(isToggleable() and hasAnySibling(hasText("Relic collected")))
            .performScrollTo()
            .performClick()
        compose.onNodeWithText("Correct count").performScrollTo().performClick()
        compose.onNode(hasSetTextAction()).performTextReplacement("665")
        compose.onNodeWithText("Save count").performClick()
        compose.onNodeWithText("First drop reached").assertDoesNotExist()
        compose.onNodeWithText("Record final cow").performScrollTo().assertIsEnabled()
            .performClick()
        compose.onNodeWithText("Not yet").performClick()
        compose.onNodeWithText("Correct count").performScrollTo().performClick()
        compose.onNode(hasSetTextAction()).performTextReplacement("599")
        compose.onNodeWithText("Save count").performClick()
        listOf("+1   Cow killed", "+5", "+10").forEach {
            compose.onNodeWithText(it).performScrollTo().assertIsEnabled()
        }
        compose.onNodeWithText("Where to farm cows · Maps & routes").performScrollTo()
            .performClick()
        compose.onNodeWithText("Farm early kills wherever convenient.", substring = true)
            .assertIsDisplayed()
        compose.onNodeWithText("This character already obtained its first relic.", substring = true)
            .assertDoesNotExist()
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("+1   Cow killed").performScrollTo().performClick()
        compose.onNodeWithText("Got it").performClick()
        compose.runOnIdle {
            assertEquals(600, state.activeHunt.count)
            assertFalse(state.activeHunt.firstRelicObtained)
            assertTrue(state.collectedRelics.isEmpty())
        }
    }

    @Test
    fun storageRoundTripPreservesAllSlotsAndPreferences() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val store = HuntStore(context)
        val previous = store.load()

        try {
            val expected = HuntState(
                hunts = listOf(
                    Hunt(Relic.SHARD, "Lilith", obtainedRelics = setOf(Relic.SHARD)).setCount(666),
                    Hunt(Relic.TOME, "Druid").add(10),
                    Hunt(Relic.FRAGMENT, "Rogue").setCount(665)
                ),
                selected = 2,
                keepAwake = true,
                collectedRelics = setOf(Relic.SHARD)
            )
            store.save(expected)
            assertEquals(expected, HuntStore(context).load())

            val reset = expected.copy(
                hunts = expected.hunts.mapIndexed { index, hunt ->
                    if (index == 1) hunt.reset() else hunt
                }
            )
            store.save(reset)
            assertEquals(reset, HuntStore(context).load())
        } finally {
            store.save(previous)
        }
    }

    @Test
    fun bellModeHidesAllCountingForBothCharacterStrategies() {
        var state by mutableStateOf(HuntState(cowSource = CowSource.BELL_OF_THE_BOVINE))

        compose.setContent { DiabloCowCounterTheme { CowCounter(state) { state = it } } }

        fun assertNoCounting() {
            listOf(
                "+1   Cow killed",
                "+5",
                "+10",
                "Undo",
                "Correct count",
                "COWS RECORDED",
                "of 666"
            ).forEach { compose.onNodeWithText(it).assertDoesNotExist() }
        }
        assertNoCounting()
        compose.onNodeWithContentDescription("Bloody Wooden Shard collected").performScrollTo()
            .performClick()
        compose.runOnIdle {
            assertEquals(setOf(Relic.SHARD), state.collectedRelics)
            assertTrue(state.hunts[0].firstRelicObtained)
            assertEquals(0, state.hunts[0].count)
            state = state.copy(strategy = CharacterStrategy.ONE_CHARACTER)
        }
        assertNoCounting()
        compose.onNodeWithText("Target Tome").assertDoesNotExist()
        compose.onNodeWithContentDescription("Musty Tome collected").performScrollTo()
            .performClick()
        compose.runOnIdle { assertEquals(setOf(Relic.SHARD, Relic.TOME), state.collectedRelics) }
    }

    @Test
    fun singleCharacterContinuesPast666WithoutFinalCowGate() {
        var state by mutableStateOf(
            HuntState(
                strategy = CharacterStrategy.ONE_CHARACTER,
                hunts = HuntState().hunts.mapIndexed { index, hunt ->
                    if (index == 0) hunt.setCount(666) else hunt
                }
            )
        )

        compose.setContent { DiabloCowCounterTheme { CowCounter(state) { state = it } } }
        compose.onNodeWithText("+10").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(listOf(676, 0, 0), state.hunts.map { it.count }) }
        compose.onNodeWithText("Record final cow").assertDoesNotExist()
        compose.onNodeWithText("of 666").assertDoesNotExist()
        compose.onNodeWithText("Undo").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(666, state.activeHunt.count) }
    }

    @Test
    fun setupSelectsAnExistingCharacterAndPreservesCounts() {
        var state by mutableStateOf(
            HuntState(
                hunts = listOf(
                    Hunt(Relic.SHARD, "Barbarian", 123),
                    Hunt(Relic.TOME, "Druid", 456),
                    Hunt(Relic.FRAGMENT, "Rogue", 789)
                )
            )
        )

        compose.setContent { DiabloCowCounterTheme { CowCounter(state) { state = it } } }
        compose.onNodeWithText("Change").performScrollTo().performClick()
        compose.onNodeWithText("One character", substring = false).performScrollTo().performClick()
        compose.onNode(hasText("Druid") and isSelectable()).performScrollTo().performClick()
        compose.onNodeWithText("Bell of the Bovine", substring = false).performScrollTo()
            .performClick()
        compose.onNodeWithText("Requires level 70 to equip.", substring = true).performScrollTo()
            .assertIsDisplayed()
        compose.onNodeWithText("Save setup").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(CharacterStrategy.ONE_CHARACTER, state.strategy)
            assertEquals(CowSource.BELL_OF_THE_BOVINE, state.cowSource)
            assertEquals("Druid", state.activeHunt.name)
            assertEquals(listOf(123, 456, 789), state.hunts.map { it.count })
        }
        compose.onNodeWithText("Correct count").assertDoesNotExist()
        compose.onNodeWithText("Change").performScrollTo().performClick()
        compose.onNodeWithText("World cows", substring = false).performScrollTo().performClick()
        compose.onNodeWithText("Cancel").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(CowSource.BELL_OF_THE_BOVINE, state.cowSource) }
        compose.onNodeWithText("Change").performScrollTo().performClick()
        compose.onNodeWithText("Three characters", substring = false).performScrollTo()
            .performClick()
        compose.onNodeWithText("World cows", substring = false).performScrollTo().performClick()
        compose.onNodeWithText("Save setup").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(CharacterStrategy.THREE_CHARACTERS, state.strategy)
            assertEquals(CowSource.WORLD_COWS, state.cowSource)
            assertEquals(1, state.singleCharacter)
            assertEquals(listOf(123, 456, 789), state.hunts.map { it.count })
        }
        compose.onNodeWithText("+1   Cow killed").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun storageMigratesLegacyProgressAndRoundTripsNewModes() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val store = HuntStore(context)
        val previous = store.load()
        val prefs = context.getSharedPreferences(
            "cow_hunts_v1",
            android.content.Context.MODE_PRIVATE
        )

        try {
            prefs
                .edit()
                .clear()
                .putInt("TOME_count", 666)
                .putBoolean("TOME_collected", true)
                .putString("TOME_name", "Old druid")
                .putInt("selected", 1)
                .commit()

            val legacy = HuntStore(context).load()

            assertEquals(CharacterStrategy.THREE_CHARACTERS, legacy.strategy)
            assertEquals(CowSource.WORLD_COWS, legacy.cowSource)
            assertEquals(setOf(Relic.TOME), legacy.collectedRelics)
            assertTrue(legacy.hunts[1].firstRelicObtained)
            assertEquals("Old druid", legacy.hunts[1].name)

            val expected = legacy
                .copy(
                    strategy = CharacterStrategy.ONE_CHARACTER,
                    cowSource = CowSource.BELL_OF_THE_BOVINE,
                    singleCharacter = 1,
                    singleTarget = Relic.FRAGMENT
                )
                .updateActive(
                    legacy.hunts[1].setCount(1342, true)
                )
            store.save(expected)
            assertEquals(expected, HuntStore(context).load())

            val early = expected
                .markRelic(Relic.FRAGMENT, true)
                .updateActive(
                    expected.activeHunt.setCount(12, true)
                )
            store.save(early)
            assertEquals(early, HuntStore(context).load())
        } finally {
            prefs.edit().clear().commit()
            store.save(previous)
        }
    }

    @Test
    fun storageRepairsLegacyUncheckedPickupAndPersistsOwnership() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val store = HuntStore(context)
        val previous = store.load()
        val prefs = context.getSharedPreferences(
            "cow_hunts_v1",
            android.content.Context.MODE_PRIVATE
        )

        try {
            prefs
                .edit()
                .clear()
                .putInt("SHARD_count", 599)
                .putBoolean("SHARD_first_relic", true)
                .putBoolean("SHARD_collected", false)
                .commit()

            val repaired = store.load()
            assertFalse(repaired.activeHunt.firstRelicObtained)
            assertEquals(600, repaired.activeHunt.add(1).count)

            val pickups = repaired.copy(strategy = CharacterStrategy.ONE_CHARACTER)
                .markRelic(Relic.SHARD, true)
                .markRelic(Relic.TOME, true)
            store.save(pickups)
            assertEquals(pickups, store.load())

            val corrected = store.load().markRelic(Relic.SHARD, false)
            assertTrue(corrected.activeHunt.firstRelicObtained)
            store.save(corrected.markRelic(Relic.TOME, false))
            assertFalse(store.load().activeHunt.firstRelicObtained)
        } finally {
            prefs.edit().clear().commit()
            store.save(previous)
        }
    }
}
