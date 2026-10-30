package com.nicolascommandeur.cowkingsquest.view.components.image

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
internal fun ImageViewerDialog(
    images: List<ViewerImage>,
    initialImageId: String,
    onDismiss: () -> Unit
) {
    require(images.isNotEmpty())

    var index by rememberSaveable(initialImageId, images.map { it.id }) {
        mutableIntStateOf(images.indexOfFirst { it.id == initialImageId }.coerceAtLeast(0))
    }
    val image = images[index]

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 8.dp,
            modifier = Modifier
                .safeDrawingPadding()
                .widthIn(max = 720.dp)
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("image_viewer")
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                // Image name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        image.title, style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("image_title")
                    )
                    TextButton(onClick = onDismiss) { Text("Close") }
                }

                // Interaction help
                Text("Pinch to zoom · Drag to pan", style = MaterialTheme.typography.bodySmall)

                // Image
                ZoomableImage(image)

                // Credits
                Text(
                    image.sourceCredit,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("image_source")
                )

                // Caption
                Text(
                    image.caption,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.testTag("image_caption")
                )

                // Navigation to previous or next image in the gallery
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
