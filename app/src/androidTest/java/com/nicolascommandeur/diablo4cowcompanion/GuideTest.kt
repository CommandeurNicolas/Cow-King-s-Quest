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
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun imageViewerZoomNavigationAndRestorationNeverChangeCheckmarks() {
        var completed by mutableStateOf(setOf("shard"))
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            DiabloCowCounterTheme {
                GuideScreen(completedSteps = completed, onChange = { completed = it })
            }
        }
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("images_gemstone"))
        compose.onNodeWithTag("image_gemstone_graves_map").performClick()
        compose.onNodeWithTag("guide_image_viewer").assertIsDisplayed()
        compose.onNodeWithTag("guide_image_title").assertTextEquals("Graves · Kurast Docks")
        compose.onNodeWithTag("guide_zoom_in").performClick()
        compose.onNodeWithTag("guide_zoom_level").assertTextEquals("150%")
        compose.onNodeWithText("Reset zoom").performClick()
        compose.onNodeWithTag("guide_zoom_level").assertTextEquals("100%")
        compose.onNodeWithTag("guide_zoom_in").performClick()
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithTag("guide_image_title").assertTextEquals("Thanks emote · grave order")
        compose.onNodeWithTag("guide_zoom_level").assertTextEquals("100%")
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithTag("guide_image_title").assertTextEquals("Thanks emote · grave order")
        compose.onNodeWithText("Previous").performClick()
        compose.onNodeWithTag("guide_image_title").assertTextEquals("Graves · Kurast Docks")
        compose.onNodeWithText("Close").performClick()
        compose.onNodeWithTag("guide_image_viewer").assertDoesNotExist()
        compose.runOnIdle { assertEquals(setOf("shard"), completed) }
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("step_gemstone"))
        compose.onNodeWithTag("step_gemstone").assertIsOff()
    }

    @Test
    fun checkingStepCollapsesImagesAndUncheckingShowsThem() {
        var completed by mutableStateOf(emptySet<String>())
        compose.setContent {
            DiabloCowCounterTheme {
                GuideScreen(completedSteps = completed, onChange = { completed = it })
            }
        }
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("images_stamina"))
        compose.onNodeWithTag("images_stamina").assertIsDisplayed()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("step_stamina"))
        compose.onNodeWithTag("step_stamina").performClick().assertIsOn()
        compose.onNodeWithTag("images_stamina").assertDoesNotExist()
        compose.onNodeWithTag("toggle_images_stamina").performClick()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("images_stamina"))
        compose.onNodeWithTag("images_stamina").assertIsDisplayed()
        compose.onNodeWithTag("image_stamina_hovel_map").performClick()
        compose.onNodeWithTag("guide_image_viewer").assertIsDisplayed()
        compose.onNodeWithText("Close").performClick()
        compose.runOnIdle { assertEquals(setOf("stamina"), completed) }
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("step_stamina"))
        compose.onNodeWithTag("step_stamina").performClick().assertIsOff()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("images_stamina"))
        compose.onNodeWithTag("images_stamina").assertIsDisplayed()
        compose.onNodeWithTag("toggle_images_stamina").assertDoesNotExist()
    }

    @Test
    fun journalReferencePrecedesHandStepsAndSectionCanCollapse() {
        compose.setContent {
            DiabloCowCounterTheme { GuideScreen(completedSteps = emptySet(), onChange = {}) }
        }
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("reference_hand"))
        compose.onNodeWithTag("reference_hand").assertIsDisplayed()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("images_reference_hand"))
        compose.onNodeWithTag("images_reference_hand").performScrollToNode(hasTestTag("image_reference_hand_journal_map"))
        compose.onNodeWithTag("image_reference_hand_journal_map").performClick()
        compose.onNodeWithTag("guide_image_title").assertTextEquals("Outcast’s Journal · Skartara")
        compose.onNodeWithText("Close").performClick()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("section_hand"))
        compose.onNodeWithTag("section_hand").performClick()
        compose.onNodeWithTag("reference_hand").assertDoesNotExist()
        compose.onNodeWithTag("images_reference_hand").assertDoesNotExist()
    }

    @Test
    fun optionalStepDoesNotReplaceRequiredProgressAndCanExceedTheTotal() {
        var completed by mutableStateOf(setOf("travel_pouch"))
        compose.setContent {
            DiabloCowCounterTheme {
                GuideScreen(completedSteps = completed, onChange = { completed = it })
            }
        }
        compose.onNodeWithTag("guide_progress").assertTextEquals("1 / 27 steps complete")
        compose.onNodeWithTag("guide_progress_bar").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(0f, 0f..1f)
            )
        )
        compose.runOnIdle { completed = guideStepIds - "king" }
        compose.onNodeWithTag("guide_progress").assertTextEquals("28 / 27 steps complete")
        compose.onNodeWithTag("guide_progress_bar").assert(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(26f / 27f, 0f..1f)
            )
        )
        compose.onNodeWithText("The hunt is complete. Enjoy the cow level!").assertDoesNotExist()
        compose.runOnIdle { completed = requiredGuideStepIds }
        compose.onNodeWithTag("guide_progress").assertTextEquals("27 / 27 steps complete")
        compose.onNodeWithText("The hunt is complete. Enjoy the cow level!").assertIsDisplayed()
        compose.onNodeWithTag("guide_list")
            .performScrollToNode(hasTestTag("section_future_seasons"))
        compose.onNodeWithTag("section_future_seasons").performClick()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("step_travel_pouch"))
        compose.onNodeWithTag("step_travel_pouch").assertIsOff().performClick().assertIsOn()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("step_vivid_whimsy"))
        compose.onNodeWithTag("step_vivid_whimsy").assertIsOff().performClick().assertIsOn()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("view_completed_guide"))
        compose.onNodeWithTag("view_completed_guide").performClick()
        compose.onNodeWithTag("guide_progress").assertTextEquals("29 / 27 steps complete")
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
        compose.onNodeWithTag("guide_progress").assertTextEquals("28 / 27 steps complete")
        compose.onNodeWithText("The hunt is complete. Enjoy the cow level!").assertIsDisplayed()
    }

    @Test
    fun finalStepCelebratesLocallyAndSectionCollapsesOnlyOnNextVisit() {
        var completed by mutableStateOf(guideStepIds)
        compose.setContent {
            DiabloCowCounterTheme {
                CowHuntApp(
                    state = HuntState(),
                    completedSteps = completed,
                    onHuntChange = {},
                    onGuideChange = { completed = it }
                )
            }
        }
        compose.onNodeWithText("Unlock guide").performClick()
        compose.onNodeWithText("Udderly complete.").assertIsDisplayed()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("section_portal"))
        compose.onNodeWithTag("section_portal").assert(hasText("Show +")).performClick()
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
            .assertTextEquals("29 / 27 steps complete")
        compose.onNodeWithText("Udderly complete.").assertIsDisplayed()
        compose.onNodeWithText("Counter", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Unlock guide").performClick()
        compose.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("section_portal"))
        compose.onNodeWithTag("section_portal").assert(hasText("Show +"))
        compose.onNodeWithTag("guide_rainbow_sweep").assertDoesNotExist()
    }

    @Test
    fun checklistNavigationRestorationLinksAndReset() {
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
                    CowHuntApp(
                        state = currentHunt,
                        completedSteps = completed,
                        onHuntChange = { currentHunt = it },
                        onGuideChange = { completed = it; guideStore.save(it) },
                        openLink = { openedUrl = it }
                    )
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

    @Test
    fun guideStorageNeverChangesLegacyHuntData() {
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
