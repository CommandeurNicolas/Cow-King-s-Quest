package com.nicolascommandeur.cowkingsquest.view.components.guide

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nicolascommandeur.cowkingsquest.model.guide.GuideImage

@Composable
internal fun GuideImageSource(image: GuideImage, modifier: Modifier = Modifier) {
    Text(
        image.sourceCredit,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}
