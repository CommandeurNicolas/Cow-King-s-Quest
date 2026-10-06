package com.nicolascommandeur.diablo4cowcompanion

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun CowHuntApp(
    state: HuntState,
    completedSteps: Set<String>,
    onHuntChange: (HuntState) -> Unit,
    onGuideChange: (Set<String>) -> Unit,
    openLink: ((String) -> Unit)? = null
) {
    var page by rememberSaveable { mutableStateOf("counter") }
    var routesFrom by rememberSaveable { mutableStateOf("counter") }
    val savedPages = rememberSaveableStateHolder()
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
                NavigationBarItem(
                    selected = selectedTab == "counter",
                    onClick = { page = "counter" },
                    colors = navigationColors,
                    icon = { Text("666", fontFamily = FontFamily.Serif) },
                    label = { Text("Counter") },
                )
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
            savedPages.SaveableStateProvider(page) {
                when (page) {
                    "counter" -> CowCounter(
                        state, onChange = onHuntChange,
                        onOpenRoutes = { routesFrom = "counter"; page = "routes" })

                    "routes" -> FarmingRoutesScreen(
                        state.hunts[state.selected],
                        onBack = { page = routesFrom }, openLink = openLink
                    )

                    else -> GuideScreen(
                        completedSteps, onGuideChange, openLink,
                        onOpenRoutes = { routesFrom = "guide"; page = "routes" })
                }
            }
        }
    }
}

@Composable
fun GuideScreen(
    completedSteps: Set<String>,
    onChange: (Set<String>) -> Unit,
    openLink: ((String) -> Unit)? = null,
    onOpenRoutes: (() -> Unit)? = null
) {
    val uriHandler = LocalUriHandler.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var resetDialog by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(unlockGuide.map { it.id }) }
    val completed = completedSteps.intersect(guideStepIds)
    fun browse(url: String) {
        try {
            if (openLink != null) openLink(url) else uriHandler.openUri(url)
        } catch (_: android.content.ActivityNotFoundException) {
            scope.launch { snackbar.showSnackbar("No browser available. Install a browser to open this guide.") }
        } catch (_: IllegalArgumentException) {
            scope.launch { snackbar.showSnackbar("Could not open this guide in your browser.") }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = rememberLazyListState(), contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxSize()
                    .testTag("guide_list"),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "THE PATH TO THE PORTAL", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary, letterSpacing = 2.sp
                        )
                        Text("Unlock guide", fontFamily = FontFamily.Serif, fontSize = 34.sp)
                        Text(
                            "${completed.size} / ${guideStepIds.size} steps complete",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("guide_progress"),
                        )
                        LinearProgressIndicator(
                            progress = { completed.size.toFloat() / guideStepIds.size },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "One shared checklist for your full journey. Tap a step to check or uncheck it. Saved separately from your cow counts.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Full quest route • Requires Vessel of Hatred and Lord of Hatred, access to Skovos, the Horadric Cube and fishing. Show Common items in your loot filter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Relic farming: Wowhead reports a 666,666-second window (about 7 days 17 hours) from the first cow. This app does not track that timer.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(16.dp),
                        ) {
                            Text(
                                "Maps & detailed walkthroughs",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                "Checked October 5, 2026. Wowhead currently covers the earlier stages; use the complete guide for the final unlock and alternate access routes.",
                                style = MaterialTheme.typography.bodySmall
                            )
                            TextButton(
                                onClick = { browse(WOWHEAD_GUIDE_URL) },
                                modifier = Modifier.testTag("wowhead_link")
                            ) {
                                Text("Open Wowhead ↗")
                            }
                            TextButton(
                                onClick = { browse(COMPLETE_GUIDE_URL) },
                            ) {
                                Text("Complete walkthrough · VULKK ↗")
                            }
                            TextButton(
                                onClick = { browse(COMMUNITY_GUIDE_URL) },
                            ) {
                                Text("Community walkthrough · DiabloFilter ↗")
                            }
                        }
                    }
                }
                if (onOpenRoutes != null) {
                    item {
                        OutlinedButton(
                            onClick = onOpenRoutes,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Where to farm cows · Maps & routes")
                        }
                    }
                }
                unlockGuide.forEach { section ->
                    item(key = "section_${section.id}") {
                        Surface(
                            onClick = {
                                expanded =
                                    if (section.id in expanded) expanded - section.id else expanded + section.id
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("section_${section.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp),
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        section.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        section.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "${section.steps.count { it.id in completed }} / ${section.steps.size} complete",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Text(
                                    if (section.id in expanded) "Hide −" else "Show +",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                    if (section.id in expanded) {
                        items(section.steps, key = { "step_${it.id}" }) { step ->
                            val checked = step.id in completed
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("step_${step.id}")
                                        .toggleable(
                                            value = checked,
                                            role = Role.Checkbox,
                                            onValueChange = {
                                                onChange(if (it) completed + step.id else completed - step.id)
                                            })
                                        .padding(end = 16.dp, top = 12.dp, bottom = 12.dp),
                                ) {
                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = null,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text(
                                            step.title, style = MaterialTheme.typography.titleSmall,
                                            color = if (checked) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            step.detail,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    if (completed.size == guideStepIds.size) {
                        Text(
                            "The hunt is complete. Enjoy the cow level!",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    TextButton(
                        onClick = { resetDialog = true },
                        enabled = completed.isNotEmpty(),
                    ) {
                        Text("Reset checklist")
                    }
                    Text(
                        "Unofficial checklist. Follow the linked maps for exact locations; game updates may change the steps.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    if (resetDialog) AlertDialog(
        onDismissRequest = { resetDialog = false }, title = { Text("Reset checklist?") },
        text = {
            Text("Clear all guide checkmarks? Your character names, cow counts and collected relics will stay saved.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onChange(emptySet()); resetDialog = false
                },
            ) {
                Text("Clear checkmarks")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { resetDialog = false },
            ) {
                Text("Cancel")
            }
        }
    )
}
