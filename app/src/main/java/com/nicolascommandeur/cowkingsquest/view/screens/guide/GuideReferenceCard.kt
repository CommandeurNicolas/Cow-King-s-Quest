package com.nicolascommandeur.cowkingsquest.view.screens.guide

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.nicolascommandeur.cowkingsquest.model.guide.GuideImage
import com.nicolascommandeur.cowkingsquest.model.guide.GuideReference
import com.nicolascommandeur.cowkingsquest.view.components.guide.GuideImageGallery

@Composable
internal fun GuideReferenceCard(
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
