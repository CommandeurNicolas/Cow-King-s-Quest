package com.nicolascommandeur.cowkingsquest.view.screens.counter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nicolascommandeur.cowkingsquest.model.hunt.CowSource
import com.nicolascommandeur.cowkingsquest.model.hunt.HuntState

@Composable
internal fun CharacterSelector(state: HuntState, onChange: (HuntState) -> Unit) {
    val bell = state.cowSource == CowSource.BELL_OF_THE_BOVINE

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        state.hunts.forEachIndexed { index, character ->
            val selected = index == state.selected

            Surface(
                onClick = { onChange(state.copy(selected = index)) },
                shape = RoundedCornerShape(12.dp),
                color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    1.dp,
                    if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        contentDescription =
                            if (bell) "Select ${character.name}, ${character.relic.shortName}"
                            else "Select ${character.name}, ${character.relic.shortName}, ${character.count} of 666 cows"
                    }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    // Relic short name
                    Text(
                        "0${index + 1}  ${character.relic.shortName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Character name
                    Text(
                        character.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge
                    )

                    // Collection state
                    Text(
                        when {
                            character.relic in state.collectedRelics -> "Collected"
                            bell -> "To collect"
                            character.pastFirstDrop -> "First drop reached"
                            else -> "${character.count} / 666"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
