package com.nicolascommandeur.diablo4cowcompanion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nicolascommandeur.diablo4cowcompanion.ui.theme.DiabloCowCounterTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CowCounterTest {
    @get:Rule val compose = createComposeRule()

    @Test fun countingSwitchingAndFinalConfirmation() {
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
            state = state.copy(hunts = state.hunts.mapIndexed { index, hunt ->
                if (index == 1) hunt.setCount(664) else hunt
            })
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
            assertFalse(state.hunts[1].collected)
        }
        compose.onNode(isToggleable() and hasAnySibling(hasText("Relic collected")))
            .performScrollTo().performClick()
        compose.runOnIdle { assertTrue(state.hunts[1].collected) }
    }

    @Test fun storageRoundTripPreservesAllSlotsAndPreferences() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val store = HuntStore(context)
        val previous = store.load()
        try {
            val expected = HuntState(
                hunts = listOf(
                    Hunt(Relic.SHARD, "Lilith").setCount(666).markCollected(true),
                    Hunt(Relic.TOME, "Druid").add(10),
                    Hunt(Relic.FRAGMENT, "Rogue").setCount(665)
                ), selected = 2, keepAwake = true
            )
            store.save(expected)
            assertEquals(expected, HuntStore(context).load())
            val reset = expected.copy(hunts = expected.hunts.mapIndexed { index, hunt ->
                if (index == 1) hunt.reset() else hunt
            })
            store.save(reset)
            assertEquals(reset, HuntStore(context).load())
        } finally {
            store.save(previous)
        }
    }
}
