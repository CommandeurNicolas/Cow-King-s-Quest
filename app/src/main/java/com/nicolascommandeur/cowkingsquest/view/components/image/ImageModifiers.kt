package com.nicolascommandeur.cowkingsquest.view.components.image

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Clip the fitted image, rather than the letterboxed bounds of its container.
internal fun Modifier.roundedFittedImageCorners(imageSize: Size, radius: Dp = 12.dp): Modifier = drawWithCache {
    val fit = minOf(size.width / imageSize.width, size.height / imageSize.height)
    val width = imageSize.width * fit
    val height = imageSize.height * fit
    val left = (size.width - width) / 2f
    val top = (size.height - height) / 2f
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                left = left, top = top, right = left + width, bottom = top + height,
                cornerRadius = CornerRadius(radius.toPx())
            )
        )
    }
    onDrawWithContent {
        clipPath(path) { this@onDrawWithContent.drawContent() }
    }
}
