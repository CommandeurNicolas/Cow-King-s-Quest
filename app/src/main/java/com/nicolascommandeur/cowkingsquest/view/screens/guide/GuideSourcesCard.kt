package com.nicolascommandeur.cowkingsquest.view.screens.guide

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nicolascommandeur.cowkingsquest.data.guide.guideSources

@Composable
internal fun GuideSourcesCard(browse: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            // Title
            Text(
                "Online guides & sources",
                style = MaterialTheme.typography.titleSmall
            )

            // Source links
            guideSources.chunked(2).forEach { sources ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    sources.forEach { source ->
                        TextButton(
                            onClick = { browse(source.url) },
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("${source.id}_link")
                        ) {
                            Text("${source.title} ↗", textAlign = TextAlign.Center)
                        }
                    }

                    // Spacer for the last row if sources count is odd
                    if (sources.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
