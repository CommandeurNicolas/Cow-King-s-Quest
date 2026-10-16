package com.nicolascommandeur.diablo4cowcompanion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import com.nicolascommandeur.diablo4cowcompanion.ui.theme.DiabloCowCounterTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CowVisitorTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun onlyTheCounterFinalRelicCheckboxRequestsTheVisitor() {
        val session = CowVisitorSession(lucky = false)
        var state by mutableStateOf(HuntState(collectedRelics = setOf(Relic.SHARD, Relic.TOME)))
        var steps by mutableStateOf(setOf("shard", "tome"))

        compose.setContent {
            DiabloCowCounterTheme {
                CowHuntApp(
                    state, steps,
                    onHuntChange = { state = it },
                    onGuideChange = { steps = it },
                    visitorSession = session,
                    // Test the checkbox wiring independently of the animation clock.
                    splashFinished = false
                )
            }
        }
        compose.onNodeWithText("Unlock guide").performClick()
        compose.onNodeWithTag("guide_list")
            .performScrollToNode(hasTestTag("step_fragment"))
        compose.onNodeWithTag("step_fragment").performClick()
        compose.runOnIdle {
            assertTrue("fragment" in steps)
            assertFalse(session.canVisit)
        }
        compose.onNodeWithText("Counter", useUnmergedTree = true).performClick()
        compose.onNodeWithContentDescription("${Relic.FRAGMENT.title} collected")
            .performScrollTo().performClick()
        compose.runOnIdle {
            assertTrue(state.collectedRelics.containsAll(Relic.entries))
            assertTrue(session.advance(0)!!.counterOnly)
        }
    }

    @Test
    fun stationaryCowIsVisibleAndTappableWithoutAnimation() {
        val session = CowVisitorSession(lucky = true)
        var visit by mutableStateOf(session.advance(600)!!)

        compose.setContent {
            DiabloCowCounterTheme {
                CowVisitorStage(visit, animate = false) { visit = session.tap()!! }
            }
        }
        compose.onNodeWithTag("hell_bovine").assertIsDisplayed().performClick()
        compose.onNodeWithText("There is no cow level.").assertIsDisplayed()
    }

    @Test
    fun animatedCowShowsBubbleDuringPauseAndRespondsToTap() {
        val session = CowVisitorSession(lucky = true)
        var visit by mutableStateOf(session.advance(5_600)!!)

        compose.setContent {
            DiabloCowCounterTheme {
                CowVisitorStage(visit, animate = true) { visit = session.tap()!! }
            }
        }
        compose.onNodeWithText("Moo.").assertIsDisplayed()
        compose.onNodeWithTag("hell_bovine").performClick()
        compose.onNodeWithText("There is no cow level.").assertIsDisplayed()
    }
}
