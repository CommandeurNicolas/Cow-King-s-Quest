package com.nicolascommandeur.diablo4cowcompanion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.nicolascommandeur.diablo4cowcompanion.ui.theme.DiabloCowCounterTheme
import org.junit.Rule
import org.junit.Test

class CowVisitorTest {
    @get:Rule
    val compose = createComposeRule()

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
