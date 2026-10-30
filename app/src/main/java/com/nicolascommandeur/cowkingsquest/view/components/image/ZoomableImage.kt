package com.nicolascommandeur.cowkingsquest.view.components.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
internal fun ZoomableImage(image: ViewerImage) {
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
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
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
                image.contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale; scaleY = scale
                        translationX = offset.x; translationY = offset.y
                    }
                    .transformable(gestures)
                    .testTag("enlarged_image")
            )

            // Zoom
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
                            .testTag("zoom_out")
                            .semantics {
                                contentDescription = "Zoom out"
                            }
                    ) { Text("−") }
                    Text(
                        "${(scale * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.testTag("zoom_level"),
                    )
                    TextButton(
                        onClick = { transform(1.5f) }, enabled = scale < 6f,
                        modifier = Modifier
                            .testTag("zoom_in")
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
}
