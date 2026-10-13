package com.nicolascommandeur.diablo4cowcompanion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nicolascommandeur.diablo4cowcompanion.ui.theme.DiabloCowCounterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuideTest {
    @get:Rule val compose = createComposeRule()

    @Test fun optionalStepDoesNotReplaceRequiredProgressAndCanExceedTheTotal() {
        var completed by mutableStateOf(setOf("travel_pouch"))
        compose.setContent {
            DiabloCowCounterTheme {
                GuideScreen(completed, onChange = { completed = it })
            }
        }
        compose.onNodeWithTag("guide_progress").assertTextEquals("1 / 27 steps complete")
        compose.onNodeWithTag("guide_progress_bar").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(0f, 0f..1f)
            )
        )
        compose.runOnIdle { completed = guideStepIds - "king" }
        compose.onNodeWithTag("guide_progress").assertTextEquals("27 / 27 steps complete")
        compose.onNodeWithTag("guide_progress_bar").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(26f / 27f, 0f..1f)
            )
        )
        compose.onNodeWithText("The hunt is complete. Enjoy the cow level!").assertDoesNotExist()
        compose.runOnIdle { completed = requiredGuideStepIds }
        compose.onNodeWithTag("guide_progress").assertTextEquals("27 / 27 steps complete")
        compose.onNodeWithText("The hunt is complete. Enjoy the cow level!").assertIsDisplayed()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("step_travel_pouch"))
        compose.onNodeWithTag("step_travel_pouch").assertIsOff().performClick().assertIsOn()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("view_completed_guide"))
        compose.onNodeWithTag("view_completed_guide").performClick()
        compose.onNodeWithTag("guide_progress").assertTextEquals("28 / 27 steps complete")
        compose.onNodeWithTag("guide_progress_bar").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(1f, 0f..1f)
            )
        )
        compose.onNodeWithText("Udderly complete.")
            .assertIsDisplayed()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("step_travel_pouch"))
        compose.onNodeWithTag("step_travel_pouch").performClick().assertIsOff()
        compose.onNodeWithTag("guide_list").performScrollToIndex(0)
        compose.onNodeWithTag("guide_progress").assertTextEquals("27 / 27 steps complete")
        compose.onNodeWithText("The hunt is complete. Enjoy the cow level!").assertIsDisplayed()
    }

    @Test fun finalRequiredStepCelebratesLocallyWithoutReplayingSavedCompletion() {
        var completed by mutableStateOf(guideStepIds)
        compose.setContent {
            DiabloCowCounterTheme {
                GuideScreen(completed, onChange = { completed = it })
            }
        }
        compose.onNodeWithText("Udderly complete.").assertIsDisplayed()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("step_king"))
        compose.onNodeWithTag("guide_rainbow_sweep").assertDoesNotExist()
        compose.onNodeWithTag("view_completed_guide").assertDoesNotExist()
        compose.onNodeWithTag("step_king").performClick().assertIsOff()
        compose.mainClock.autoAdvance = false
        compose.onNodeWithTag("step_king").performClick()
        compose.mainClock.advanceTimeBy(400)
        compose.onNodeWithTag("guide_rainbow_sweep", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithTag("view_completed_guide").assertDoesNotExist()
        // Undo during the sweep cancels both the animation and the completion action.
        compose.onNodeWithTag("step_king").performClick()
        compose.mainClock.advanceTimeByFrame()
        compose.onNodeWithTag("guide_rainbow_sweep", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithTag("step_king").performClick()
        compose.mainClock.advanceTimeBy(1500)
        compose.mainClock.autoAdvance = true
        compose.onNodeWithTag("guide_rainbow_sweep", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithTag("step_king").assertIsDisplayed().assertIsOn()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("view_completed_guide"))
        compose.onNodeWithTag("view_completed_guide").performClick()
        compose.onNodeWithTag("guide_progress").assertIsDisplayed()
            .assertTextEquals("28 / 27 steps complete")
        compose.onNodeWithText("Udderly complete.").assertIsDisplayed()
    }

    @Test fun checklistNavigationRestorationLinksAndReset() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val guideStore = GuideStore(context)
        val original = guideStore.load()
        val hunt = HuntState().let { it.copy(hunts = it.hunts.map { h -> h.add(10) }) }
        var currentHunt = hunt
        var openedUrl: String? = null
        try {
            guideStore.save(emptySet())
            val restoration = StateRestorationTester(compose)
            restoration.setContent {
                var completed by remember { mutableStateOf(GuideStore(context).load()) }
                DiabloCowCounterTheme {
                    CowHuntApp(currentHunt, completed,
                        onHuntChange = { currentHunt = it },
                        onGuideChange = { completed = it; guideStore.save(it) },
                        openLink = { openedUrl = it })
                }
            }
            compose.onNodeWithText("Unlock guide").performClick()
            compose.onNodeWithTag("guide_progress").assertTextEquals("0 / 27 steps complete")
            compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("wowhead_link"))
            compose.onNodeWithTag("wowhead_link").performClick()
            compose.runOnIdle { assertEquals(WOWHEAD_GUIDE_URL, openedUrl) }
            compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("step_shard"))
            compose.onNodeWithTag("step_shard").performClick().assertIsOn()
            compose.runOnIdle { assertEquals(setOf("shard"), GuideStore(context).load()) }
            compose.onNodeWithText("Counter", useUnmergedTree = true).performClick()
            compose.onNodeWithText("Unlock guide").performClick()
            compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("step_shard"))
            compose.onNodeWithTag("step_shard").assertIsOn()
            restoration.emulateSavedInstanceStateRestore()
            compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("step_shard"))
            compose.onNodeWithTag("step_shard").assertIsOn().performClick().assertIsOff()
            compose.onNodeWithTag("step_shard").performClick()
            compose.onNodeWithTag("guide_list").performScrollToNode(hasText("Reset checklist"))
            compose.onNodeWithText("Reset checklist").performClick()
            compose.onNodeWithText("Cancel").performClick()
            compose.runOnIdle { assertEquals(setOf("shard"), guideStore.load()) }
            compose.onNodeWithText("Reset checklist").performClick()
            compose.onNodeWithText("Clear checkmarks").performClick()
            compose.runOnIdle {
                assertTrue(guideStore.load().isEmpty())
                assertEquals(hunt, currentHunt)
            }
        } finally {
            guideStore.save(original)
        }
    }

    @Test fun guideStorageNeverChangesLegacyHuntData() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val hunts = HuntStore(context)
        val before = hunts.load()
        val store = GuideStore(context)
        val original = store.load()
        try {
            store.save(setOf("shard", "king", "travel_pouch"))
            assertEquals(setOf("shard", "king", "travel_pouch"), GuideStore(context).load())
            store.save(emptySet())
            assertEquals(before, hunts.load())
            assertTrue(GuideStore(context).load().isEmpty())
        } finally {
            store.save(original)
        }
    }
}
