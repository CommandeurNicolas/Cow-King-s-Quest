package com.nicolascommandeur.cowkingsquest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.nicolascommandeur.cowkingsquest.data.farming.farmingRoutes
import com.nicolascommandeur.cowkingsquest.model.hunt.Hunt
import com.nicolascommandeur.cowkingsquest.model.hunt.Relic
import com.nicolascommandeur.cowkingsquest.view.screens.farming.FarmingRoutesScreen
import com.nicolascommandeur.cowkingsquest.view.theme.DiabloCowCounterTheme
import org.junit.Rule
import org.junit.Test

class FarmingRoutesTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun routeMapsUseTheSharedViewerWithZoomControlsAndRouteDetails() {
        compose.setContent {
            DiabloCowCounterTheme {
                FarmingRoutesScreen(Hunt(Relic.FRAGMENT, "Farmer"), onBack = {})
            }
        }

        compose.onNodeWithTag("farming_routes").performScrollToNode(hasTestTag("map_farobru"))
        compose.onNodeWithTag("map_farobru").performClick()
        compose.onNodeWithTag("image_viewer").assertIsDisplayed()
        compose.onNodeWithTag("image_title").assertTextEquals("Farobru · Dry Steppes")
        compose.onNodeWithTag("image_caption").assertTextEquals(farmingRoutes.first().directions)
        compose.onNodeWithTag("image_source")
            .assertTextEquals("Map: DiabloFilter / Rizarjay · Diablo IV imagery © Blizzard")
        compose.onNodeWithTag("zoom_out").assertIsNotEnabled()

        repeat(5) { compose.onNodeWithTag("zoom_in").performClick() }
        compose.onNodeWithTag("zoom_level").assertTextEquals("600%")
        compose.onNodeWithTag("zoom_in").assertIsNotEnabled()
        compose.onNodeWithTag("zoom_out").performClick()
        compose.onNodeWithTag("zoom_level").assertTextEquals("400%")
        compose.onNodeWithText("Reset zoom").performClick()
        compose.onNodeWithTag("zoom_level").assertTextEquals("100%")
        compose.onNodeWithText("Next").assertDoesNotExist()
        compose.onNodeWithText("Close").performClick()
        compose.onNodeWithTag("image_viewer").assertDoesNotExist()
    }
}
