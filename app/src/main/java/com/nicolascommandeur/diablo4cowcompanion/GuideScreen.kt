package com.nicolascommandeur.diablo4cowcompanion

import android.animation.ValueAnimator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

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
                        state,
                        onChange = onHuntChange,
                        onCountBounds = { countBounds = it },
                        onOpenRoutes = { routesFrom = "counter"; page = "routes" }
                    )

                    "routes" -> FarmingRoutesScreen(
                        state.hunts[state.selected],
                        onBack = { page = routesFrom },
                        openLink = openLink
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

            CowVisitor(
                visitorSession,
                splashFinished,
                countBounds = countBounds.takeIf { page == "counter" }
            )
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
    val listState = rememberLazyListState()

    // Transient, click-triggered celebration: loading saved progress never replays it.
    var celebratedStep by remember { mutableStateOf<String?>(null) }
    var sweepFinished by remember { mutableStateOf(false) }
    val sweepProgress = remember { Animatable(0f) }

    // Reset dialog
    var resetDialog by rememberSaveable { mutableStateOf(false) }

    // Expanded image dialog
    var imageIds by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var enlargedImageId by rememberSaveable { mutableStateOf<String?>(null) }

    fun showImage(images: List<GuideImage>, image: GuideImage) {
        imageIds = images.map { it.id }
        enlargedImageId = image.id
    }

    // Expanded sections
    var expanded by rememberSaveable { mutableStateOf(unlockGuide.map { it.id }) }

    // Completed steps
    val completed = completedSteps.intersect(guideStepIds)
    val requiredCompleted = completed.intersect(requiredGuideStepIds).size
    val requiredComplete = requiredCompleted == requiredGuideStepIds.size
    val allComplete = completed.containsAll(guideStepIds)

    // Progress bar
    val progressColor = MaterialTheme.colorScheme.primary
    val progressDescription = when {
        allComplete -> "Udderly complete."
        requiredComplete -> "The hunt is complete. Enjoy the cow level!"
        else -> "$requiredCompleted / ${requiredGuideStepIds.size} required steps complete"
    }

    // Rainbow sweep animation
    LaunchedEffect(celebratedStep, allComplete) {
        if (!allComplete) {
            celebratedStep = null
            sweepFinished = false
        } else if (celebratedStep != null) {
            sweepProgress.snapTo(0f)
            if (ValueAnimator.areAnimatorsEnabled()) {
                sweepProgress.animateTo(1f, tween(1200, easing = LinearEasing))
            }
            sweepFinished = true
        }
    }

    // Open online guides
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
                state = listState, contentPadding = PaddingValues(20.dp),
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
                        Column {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.zIndex(1f)
                            ) {
                                Text(
                                    "${completed.size} / ${requiredGuideStepIds.size} steps complete",
                                    color = progressColor,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("guide_progress"),
                                )
                                if (allComplete) GuideCompletionCrown()
                            }
                            if (allComplete) {
                                CowPatternProgressBar(
                                    modifier = Modifier
                                        .testTag("guide_progress_bar")
                                        .semantics {
                                            progressBarRangeInfo = ProgressBarRangeInfo(1f, 0f..1f)
                                            stateDescription =
                                                "Udderly complete. All 27 required steps and the optional step completed."
                                        }
                                )
                            } else LinearProgressIndicator(
                                progress = { requiredCompleted.toFloat() / requiredGuideStepIds.size },
                                color = progressColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("guide_progress_bar")
                                    .semantics { stateDescription = progressDescription }
                            )
                        }
                        if (requiredComplete) {
                            Text(
                                progressDescription,
                                color = progressColor,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            "27 required steps + 1 optional shortcut for future seasons. The bar tracks required steps; finish the bonus step too for 28 / 27. Tap to check or uncheck. Saved separately from your cow counts.",
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
                                "Offline images & guide sources",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                "Location maps and screenshots are included below each relevant step. Tap to enlarge, zoom and browse without leaving the app. Quest details checked October 13, 2026; community reports can differ after game updates.",
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
                                        "${section.steps.count { it.id in completed }} / ${section.steps.count { it.checkable }} complete",
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
                        section.reference?.let { reference ->
                            item(key = "reference_${section.id}") {
                                GuideReferenceCard(reference, section.id, ::showImage)
                            }
                        }
                        section.description?.let { description ->
                            item(key = "description_${section.id}") {
                                Text(
                                    description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp, vertical = 4.dp)
                                        .testTag("description_${section.id}")
                                )
                            }
                        }
                        section.steps.forEachIndexed { index, step ->
                            val groupTitle = step.groupTitle
                            if (groupTitle != null && groupTitle != section.steps.getOrNull(index - 1)?.groupTitle) {
                                item(key = "group_${section.id}_${step.id}") {
                                    Text(
                                        groupTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .semantics { heading() }
                                    )
                                }
                            }
                            item(key = "step_${step.id}") {
                                val checked = step.id in completed
                                val celebrating =
                                    step.checkable && allComplete && celebratedStep == step.id
                                val sweeping = celebrating && !sweepFinished

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier
                                            .padding(start = if (step.groupTitle != null) 16.dp else 0.dp)
                                            .then(if (sweeping) Modifier.testTag("guide_rainbow_sweep") else Modifier)
                                    ) {
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.Top,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("step_${step.id}")
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .then(if (sweeping) Modifier.rainbowSweep { sweepProgress.value } else Modifier)
                                                    .then(
                                                        if (step.checkable) Modifier.toggleable(
                                                            value = checked,
                                                            role = Role.Checkbox,
                                                            onValueChange = {
                                                                val next =
                                                                    if (it) completed + step.id else completed - step.id
                                                                if (it && !allComplete && next.containsAll(
                                                                        guideStepIds
                                                                    )
                                                                ) {
                                                                    celebratedStep = step.id
                                                                    sweepFinished = false
                                                                } else {
                                                                    celebratedStep = null
                                                                    sweepFinished = false
                                                                }
                                                                onChange(next)
                                                            }) else Modifier
                                                    )
                                                    .padding(
                                                        start = if (step.checkable) 0.dp else 16.dp,
                                                        end = 16.dp,
                                                        top = 12.dp,
                                                        bottom = 12.dp
                                                    ),
                                            ) {
                                                if (step.checkable) {
                                                    Checkbox(
                                                        checked = checked,
                                                        onCheckedChange = null,
                                                        modifier = Modifier.padding(12.dp)
                                                    )
                                                }
                                                Column(
                                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                                    modifier = Modifier.weight(1f),
                                                ) {
                                                    Text(
                                                        if (step.optional) "${step.title} · Optional" else step.title,
                                                        style = MaterialTheme.typography.titleSmall,
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
                                            if (step.images.isNotEmpty()) {
                                                Column(
                                                    Modifier.padding(
                                                        start = 16.dp,
                                                        end = 16.dp,
                                                        bottom = 16.dp
                                                    )
                                                ) {
                                                    GuideImageGallery(step.images, step.id) {
                                                        showImage(step.images, it)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (celebrating && sweepFinished) {
                                        TextButton(
                                            onClick = {
                                                scope.launch {
                                                    listState.animateScrollToItem(
                                                        0
                                                    )
                                                }
                                            },
                                            modifier = Modifier.testTag("view_completed_guide")
                                        ) {
                                            Text("View completed guide ↑")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    TextButton(
                        onClick = { resetDialog = true },
                        enabled = completed.isNotEmpty(),
                    ) {
                        Text("Reset checklist")
                    }
                    Text(
                        "Unofficial checklist with offline location images. Sources are credited beside the images. Game updates may change the steps; source walkthroughs are linked above.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Expanded image dialog
    enlargedImageId?.let { initialId ->
        val images = imageIds.mapNotNull { unlockGuideImages[it] }
        if (images.isNotEmpty()) GuideImageViewer(images, initialId) { enlargedImageId = null }
    }

    // Reset dialog
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

@Composable
private fun GuideReferenceCard(
    reference: GuideReference,
    sectionId: String,
    showImage: (List<GuideImage>, GuideImage) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.testTag("reference_$sectionId")
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                reference.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { heading() }
            )
            Text(reference.detail, style = MaterialTheme.typography.bodyMedium)
            GuideImageGallery(reference.images, "reference_$sectionId") {
                showImage(reference.images, it)
            }
        }
    }
}
