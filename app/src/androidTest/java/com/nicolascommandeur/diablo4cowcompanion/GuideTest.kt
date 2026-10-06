package com.nicolascommandeur.diablo4cowcompanion

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nicolascommandeur.diablo4cowcompanion.ui.theme.DiabloCowCounterTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuideTest {
    @get:Rule val compose = createComposeRule()

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
            store.save(setOf("shard", "king"))
            assertEquals(setOf("shard", "king"), GuideStore(context).load())
            store.save(emptySet())
            assertEquals(before, hunts.load())
            assertTrue(GuideStore(context).load().isEmpty())
        } finally {
            store.save(original)
        }
    }
}
