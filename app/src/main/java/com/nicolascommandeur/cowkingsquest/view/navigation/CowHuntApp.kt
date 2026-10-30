package com.nicolascommandeur.cowkingsquest.view.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nicolascommandeur.cowkingsquest.model.hunt.CowSource
import com.nicolascommandeur.cowkingsquest.model.hunt.HuntState
import com.nicolascommandeur.cowkingsquest.model.visitor.CowVisitorSession
import com.nicolascommandeur.cowkingsquest.view.components.visitor.CowVisitor
import com.nicolascommandeur.cowkingsquest.view.screens.counter.CounterScreen
import com.nicolascommandeur.cowkingsquest.view.screens.farming.FarmingRoutesScreen
import com.nicolascommandeur.cowkingsquest.view.screens.guide.GuideScreen

@Composable
internal fun CowHuntApp(
    state: HuntState,
    completedSteps: Set<String>,
    onHuntChange: (HuntState) -> Unit,
    onGuideChange: (Set<String>) -> Unit,
    openLink: ((String) -> Unit)? = null,
    visitorSession: CowVisitorSession? = null,
    splashFinished: Boolean = true
) {
    var page by rememberSaveable { mutableStateOf("counter") }
    var routesFrom by rememberSaveable { mutableStateOf("counter") }
    val savedPages = rememberSaveableStateHolder()
    var countBounds by remember { mutableStateOf<Rect?>(null) }
    var celebrationRequested by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = page != "counter") {
        page = if (page == "routes") routesFrom else "counter"
    }

    val selectedTab = if (page == "routes") routesFrom else page
    val navigationColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                tonalElevation = 0.dp
            ) {
                // Counter screen
                NavigationBarItem(
                    selected = selectedTab == "counter",
                    onClick = { page = "counter" },
                    colors = navigationColors,
                    icon = { Text("666", fontFamily = FontFamily.Serif) },
                    label = { Text("Counter") },
                )
                // Guide screen
                NavigationBarItem(
                    selected = selectedTab == "guide", onClick = { page = "guide" },
                    colors = navigationColors,
                    icon = { Text("☷", fontSize = 22.sp) }, label = { Text("Unlock guide") },
                )
            }
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            // Navigation
            savedPages.SaveableStateProvider(page) {
                when (page) {
                    "counter" -> CounterScreen(
                        state,
                        onChange = { next ->
                            onHuntChange(next)
                            if (
                                visitorSession?.onRelicsChanged(
                                    state.collectedRelics,
                                    next.collectedRelics
                                ) == true
                            ) {
                                celebrationRequested = true
                            }
                        },
                        onCountBounds = { countBounds = it },
                        onOpenRoutes = { routesFrom = "counter"; page = "routes" }
                    )

                    "routes" -> FarmingRoutesScreen(
                        state.activeHunt.copy(relic = state.targetRelic),
                        onBack = { page = routesFrom },
                        openLink = openLink,
                        rngPhase = state.isRngPhase,
                        cowSource = state.cowSource,
                        collected = state.targetRelic in state.collectedRelics
                    )

                    else -> GuideScreen(
                        completedSteps,
                        onGuideChange,
                        openLink,
                        onOpenRoutes = {
                            routesFrom = "guide"
                            page = "routes"
                        }
                    )
                }
            }

            // Moo.
            CowVisitor(
                visitorSession,
                splashFinished,
                countBounds = countBounds.takeIf { page == "counter" && state.cowSource == CowSource.WORLD_COWS },
                celebrationRequested = celebrationRequested,
                isCounterScreen = page == "counter"
            )
        }
    }
}
