package com.nicolascommandeur.cowkingsquest.view.screens.guide

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.nicolascommandeur.cowkingsquest.R
import com.nicolascommandeur.cowkingsquest.data.guide.crookedStaffReport
import com.nicolascommandeur.cowkingsquest.model.guide.GuideImage
import com.nicolascommandeur.cowkingsquest.model.guide.GuideStep
import com.nicolascommandeur.cowkingsquest.view.components.guide.GuideImageGallery

@Composable
internal fun GuideStepCard(
    step: GuideStep,
    checked: Boolean,
    celebrating: Boolean,
    sweepFinished: Boolean,
    sweepProgress: () -> Float,
    onCheckedChange: (Boolean) -> Unit,
    browse: (String) -> Unit,
    showImage: (List<GuideImage>, GuideImage) -> Unit,
    onViewCompleted: () -> Unit
) {
    var imagesExpanded by rememberSaveable(step.id) {
        mutableStateOf(false)
    }
    val sweeping = celebrating && !sweepFinished

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // Step card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .padding(start = if (step.groupTitle != null) 16.dp else 0.dp)
                .then(if (sweeping) Modifier.testTag("guide_rainbow_sweep") else Modifier)
        ) {
            Column {
                // Checkbox + title + description
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("step_${step.id}")
                        .clip(RoundedCornerShape(12.dp))
                        .then(if (sweeping) Modifier.rainbowSweep { sweepProgress() } else Modifier)
                        .then(
                            if (step.checkable) Modifier.toggleable(
                                value = checked,
                                role = Role.Checkbox,
                                onValueChange = { newValue ->
                                    if (newValue) imagesExpanded = false
                                    onCheckedChange(newValue)
                                }
                            ) else Modifier
                        )
                        .padding(
                            start = if (step.checkable) 0.dp else 16.dp,
                            end = 16.dp,
                            top = 12.dp,
                            bottom = 12.dp
                        ),
                ) {
                    // Checkbox
                    if (step.checkable) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = null,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    // Title + description
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

                // Crooked staff player report reddit link
                if (step.id == "staff") {
                    TextButton(
                        onClick = { browse(crookedStaffReport.url) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .testTag("${crookedStaffReport.id}_link")
                    ) {
                        Text("${crookedStaffReport.title} ↗")
                    }
                }

                // Step images
                if (step.images.isNotEmpty()) {
                    Column(
                        Modifier.padding(
                            start = 16.dp, end = 16.dp, bottom = 16.dp
                        )
                    ) {
                        if (checked) {
                            val arrowRotation by animateFloatAsState(
                                targetValue = if (imagesExpanded) 0f else -90f,
                                animationSpec = tween(180),
                                label = "Image disclosure chevron"
                            )

                            // Show/hide images button
                            TextButton(
                                onClick = {
                                    imagesExpanded = !imagesExpanded
                                },
                                modifier = Modifier.testTag("toggle_images_${step.id}")
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_expand_more),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .rotate(arrowRotation)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    if (imagesExpanded) "Hide images (${step.images.size})"
                                    else "Show images (${step.images.size})"
                                )
                            }
                        }
                        AnimatedVisibility(
                            visible = !checked || imagesExpanded,
                            enter = expandVertically(tween(180))
                                    + fadeIn(tween(180)),
                            exit = shrinkVertically(tween(180))
                                    + fadeOut(tween(120))
                        ) {
                            GuideImageGallery(step.images, step.id) {
                                showImage(step.images, it)
                            }
                        }
                    }
                }
            }
        }

        // Button to scroll to top of the screen if last step is checked
        if (celebrating && sweepFinished) {
            TextButton(
                onClick = onViewCompleted,
                modifier = Modifier.testTag("view_completed_guide")
            ) {
                Text("View completed guide ↑")
            }
        }
    }
}
