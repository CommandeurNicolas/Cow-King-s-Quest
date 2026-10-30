package com.nicolascommandeur.cowkingsquest.view.components.guide

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nicolascommandeur.cowkingsquest.model.guide.GuideImage
import com.nicolascommandeur.cowkingsquest.view.components.image.roundedFittedImageCorners

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
                        .roundedFittedImageCorners(painter.intrinsicSize)
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
