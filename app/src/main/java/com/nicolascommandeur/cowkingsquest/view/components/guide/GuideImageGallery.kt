package com.nicolascommandeur.cowkingsquest.view.components.guide

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.nicolascommandeur.cowkingsquest.model.guide.GuideImage
import com.nicolascommandeur.cowkingsquest.view.components.image.roundedFittedImageCorners

@Composable
internal fun GuideImageGallery(
    images: List<GuideImage>,
    owner: String,
    onOpen: (GuideImage) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Image count + interaction help
        Text(
            if (images.size == 1) "Location reference · Tap to enlarge"
            else "${images.size} location images · Swipe to browse · Tap to enlarge",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Image gallery
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val singleImage = images.size == 1
            val cardWidth = if (singleImage) maxWidth else minOf(maxWidth * 0.88f, 360.dp)

            // Reserve the tallest wrapped title and credit at the current width and font scale.
            val density = LocalDensity.current
            val textMeasurer = rememberTextMeasurer()
            val titleStyle = MaterialTheme.typography.titleSmall

            // Make every title and source texts the same height for even cards heights in the row
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
                                    .then(
                                        if (singleImage) Modifier.roundedFittedImageCorners(painter.intrinsicSize)
                                        else Modifier.clip(RoundedCornerShape(12.dp))
                                    )
                            )

                            // Image title
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

                            // Image source credit
                            GuideImageSource(
                                image,
                                modifier = Modifier
                                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                                    .fillMaxWidth()
                                    .then(
                                        if (singleImage) Modifier else Modifier.height(
                                            sourceHeight
                                        )
                                    )
                                    .testTag("image_source_${owner}_${image.id}")
                            )
                        }
                    }
                }
            }
        }
    }
}
