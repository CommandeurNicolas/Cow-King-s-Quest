package com.nicolascommandeur.cowkingsquest.view.screens.guide

import android.animation.ValueAnimator
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.nicolascommandeur.cowkingsquest.data.guide.guideStepIds
import com.nicolascommandeur.cowkingsquest.data.guide.requiredGuideStepIds
import com.nicolascommandeur.cowkingsquest.data.guide.unlockGuide
import com.nicolascommandeur.cowkingsquest.data.guide.unlockGuideImages
import com.nicolascommandeur.cowkingsquest.model.guide.GuideImage
import com.nicolascommandeur.cowkingsquest.view.components.guide.toViewerImage
import com.nicolascommandeur.cowkingsquest.view.components.image.ImageViewerDialog
import com.nicolascommandeur.cowkingsquest.view.screens.guide.dialogs.ResetGuideDialog
import kotlinx.coroutines.launch

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

    // Set defaults once per visit; checking the last step must not collapse its section.
    // Keep this out of savedPages so returning to the guide uses the latest progress.
    var expanded by remember {
        mutableStateOf(
            unlockGuide.filter { section ->
                section.steps.any { it.checkable && it.id !in completedSteps }
            }.map { it.id }
        )
    }

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
                // Header
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "THE PATH TO THE PORTAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 2.sp
                        )
                        Text(
                            "Unlock guide",
                            fontFamily = FontFamily.Serif,
                            fontSize = 34.sp
                        )

                        // Progress bar
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
                                                "Udderly complete. All ${requiredGuideStepIds.size} required steps and ${guideStepIds.size - requiredGuideStepIds.size} optional steps completed."
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

                        // Progress complete
                        if (requiredComplete) {
                            Text(
                                progressDescription,
                                color = progressColor,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Description
                        Text(
                            "${requiredGuideStepIds.size} required steps + ${guideStepIds.size - requiredGuideStepIds.size} optional steps. The bar tracks required steps; finish the optional steps too for ${guideStepIds.size} / ${requiredGuideStepIds.size}. Tap checklist steps to check or uncheck them. Route instructions have no checkboxes. Saved separately from your cow counts.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Requirements
                        Text(
                            "Full quest route • Requires Vessel of Hatred and Lord of Hatred, access to Skovos, the Horadric Cube and fishing. Show Common items in your loot filter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // 666,666 second window warning
                        Text(
                            "Relic farming: Wowhead reports a 666,666-second window (about 7 days 17 hours) from the first cow. This app does not track that timer.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Online guides & sources
                item {
                    GuideSourcesCard(::browse)
                }

                // Cow farming routes
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

                // Guide sections and steps
                unlockGuide.forEach { section ->
                    // Section card
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

                    // Section content
                    if (section.id in expanded) {
                        // Section reference
                        section.reference?.let { reference ->
                            item(key = "reference_${section.id}") {
                                GuideReferenceCard(reference, section.id, ::showImage)
                            }
                        }

                        // Section description
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

                        // Section steps
                        section.steps.forEachIndexed { index, step ->
                            // Group title
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

                            // Step card
                            item(key = "step_${step.id}") {
                                GuideStepCard(
                                    step = step,
                                    checked = step.id in completed,
                                    celebrating = step.checkable && allComplete && celebratedStep == step.id,
                                    sweepFinished = sweepFinished,
                                    sweepProgress = { sweepProgress.value },
                                    onCheckedChange = { checked ->
                                        val next =
                                            if (checked) completed + step.id else completed - step.id
                                        celebratedStep =
                                            if (checked && !allComplete && next.containsAll(
                                                    guideStepIds
                                                )
                                            ) step.id else null
                                        sweepFinished = false
                                        onChange(next)
                                    },
                                    browse = ::browse,
                                    showImage = ::showImage,
                                    onViewCompleted = {
                                        scope.launch {
                                            listState.animateScrollToItem(
                                                0
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Footer
                item {
                    // Reset checklist
                    TextButton(
                        onClick = { resetDialog = true },
                        enabled = completed.isNotEmpty(),
                    ) {
                        Text("Reset checklist")
                    }

                    // Disclaimer
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
        if (images.isNotEmpty()) ImageViewerDialog(images.map { it.toViewerImage() }, initialId) { enlargedImageId = null }
    }

    // Reset dialog
    if (resetDialog) ResetGuideDialog(
        onDismiss = { resetDialog = false },
        onConfirm = {
            onChange(emptySet())
            resetDialog = false
        }
    )
}
