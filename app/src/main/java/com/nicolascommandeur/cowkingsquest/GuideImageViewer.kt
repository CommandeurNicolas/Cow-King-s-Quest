package com.nicolascommandeur.cowkingsquest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
internal fun GuideImageGallery(
    images: List<GuideImage>,
    owner: String,
    onOpen: (GuideImage) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            if (images.size == 1) "Location reference · Tap to enlarge"
            else "${images.size} location images · Swipe to browse · Tap to enlarge",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val singleImage = images.size == 1
            val cardWidth = if (singleImage) maxWidth else minOf(maxWidth * 0.88f, 360.dp)

            // Reserve the tallest wrapped title and credit at the current width and font scale.
            val density = LocalDensity.current
            val textMeasurer = rememberTextMeasurer()
            val titleStyle = MaterialTheme.typography.titleSmall
            val titleHeight = if (singleImage) 0.dp else with(density) {
                val titleWidth = (cardWidth.roundToPx() - 2 * 12.dp.roundToPx()).coerceAtLeast(1)
                val height = images.maxOfOrNull {
                    textMeasurer.measure(
                        text = it.title,
                        style = titleStyle,
                        constraints = Constraints(maxWidth = titleWidth)
                    ).size.height
                } ?: 0
                height.toDp()
            }
            val sourceHeight = if (singleImage) 0.dp else with(density) {
                val sourceWidth = (cardWidth.roundToPx() - 2 * 12.dp.roundToPx()).coerceAtLeast(1)
                val height = images.maxOfOrNull {
                    textMeasurer.measure(
                        text = it.sourceCredit,
                        style = MaterialTheme.typography.labelSmall,
                        constraints = Constraints(maxWidth = sourceWidth)
                    ).size.height
                } ?: 0
                height.toDp()
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.testTag("images_$owner")
            ) {
                items(images, key = { it.id }) { image ->
                    val painter = painterResource(image.resource)
                    val aspectRatio = painter.intrinsicSize.width / painter.intrinsicSize.height

                    Surface(
                        onClick = { onOpen(image) },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .width(cardWidth)
                            .testTag("image_${owner}_${image.id}")
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Image(
                                painter,
                                contentDescription = "${image.title}. ${image.caption}",
                                contentScale = if (singleImage) ContentScale.Fit else ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (singleImage) Modifier.aspectRatio(aspectRatio)
                                        else Modifier.height(190.dp)
                                    )
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Text(
                                image.title,
                                style = titleStyle,
                                modifier = Modifier
                                    .padding(
                                        start = 12.dp,
                                        end = 12.dp,
                                    )
                                    .fillMaxWidth()
                                    .then(if (singleImage) Modifier else Modifier.height(titleHeight))
                            )
                            GuideImageSource(
                                image,
                                modifier = Modifier
                                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                                    .fillMaxWidth()
                                    .then(if (singleImage) Modifier else Modifier.height(sourceHeight))
                                    .testTag("image_source_${owner}_${image.id}")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideImageSource(image: GuideImage, modifier: Modifier = Modifier) {
    Text(
        image.sourceCredit,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
internal fun GuideItemReference(image: GuideImage) {
    val painter = painterResource(image.resource)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Item reference", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Image(
                    painter,
                    contentDescription = "${image.title}. ${image.caption}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height)
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("bell_item_reference")
                )
                Text(
                    image.title,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                )
            }
        }
        GuideImageSource(image)
        Text(
            image.caption,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun GuideImageViewer(
    images: List<GuideImage>,
    initialImageId: String,
    onDismiss: () -> Unit
) {
    var index by rememberSaveable(initialImageId) {
        mutableIntStateOf(images.indexOfFirst { it.id == initialImageId }.coerceAtLeast(0))
    }
    val image = images[index]

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .safeDrawingPadding()
                .padding(8.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("guide_image_viewer")
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        image.title, style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("guide_image_title")
                    )
                    TextButton(onClick = onDismiss) { Text("Close") }
                }
                Text("Pinch to zoom · Drag to pan", style = MaterialTheme.typography.bodySmall)
                BoxWithConstraints(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    val painter = painterResource(image.resource)
                    val viewportWidth = constraints.maxWidth.toFloat()
                    val viewportHeight = constraints.maxHeight.toFloat()
                    Box(
                        Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        // Reset on image or viewport changes so rotation cannot leave an image offscreen.
                        var scale by remember(
                            image.id,
                            viewportWidth,
                            viewportHeight
                        ) { mutableFloatStateOf(1f) }
                        var offset by remember(
                            image.id,
                            viewportWidth,
                            viewportHeight
                        ) { mutableStateOf(Offset.Zero) }
                        val fit = minOf(
                            viewportWidth / painter.intrinsicSize.width,
                            viewportHeight / painter.intrinsicSize.height
                        )

                        fun transform(zoom: Float, pan: Offset = Offset.Zero) {
                            scale = (scale * zoom).coerceIn(1f, 6f)
                            val xLimit =
                                ((painter.intrinsicSize.width * fit * scale - viewportWidth) / 2).coerceAtLeast(
                                    0f
                                )
                            val yLimit =
                                ((painter.intrinsicSize.height * fit * scale - viewportHeight) / 2).coerceAtLeast(
                                    0f
                                )
                            offset = Offset(
                                (offset.x + pan.x).coerceIn(-xLimit, xLimit),
                                (offset.y + pan.y).coerceIn(-yLimit, yLimit)
                            )
                        }

                        val gestures =
                            rememberTransformableState { zoom, pan, _ -> transform(zoom, pan) }

                        Image(
                            painter,
                            "${image.title}. ${image.caption}",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = scale; scaleY = scale
                                    translationX = offset.x; translationY = offset.y
                                }
                                .transformable(gestures)
                                .testTag("guide_enlarged_image")
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(
                                    onClick = { transform(1 / 1.5f) }, enabled = scale > 1f,
                                    modifier = Modifier
                                        .testTag("guide_zoom_out")
                                        .semantics {
                                            contentDescription = "Zoom out"
                                        }
                                ) { Text("−") }
                                Text(
                                    "${(scale * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.testTag("guide_zoom_level"),
                                )
                                TextButton(
                                    onClick = { transform(1.5f) }, enabled = scale < 6f,
                                    modifier = Modifier
                                        .testTag("guide_zoom_in")
                                        .semantics { contentDescription = "Zoom in" }
                                ) { Text("+") }
                                TextButton(
                                    onClick = {
                                        scale = 1f; offset = Offset.Zero
                                    }
                                ) { Text("Reset zoom") }
                            }
                        }
                    }
                }
                GuideImageSource(image)
                Text(image.caption, style = MaterialTheme.typography.bodySmall)
                if (images.size > 1) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { index-- },
                            enabled = index > 0
                        ) { Text("Previous") }
                        Text(
                            "${index + 1} / ${images.size}",
                            style = MaterialTheme.typography.labelMedium
                        )
                        TextButton(
                            onClick = { index++ },
                            enabled = index < images.lastIndex
                        ) { Text("Next") }
                    }
                }
            }
        }
    }
}
