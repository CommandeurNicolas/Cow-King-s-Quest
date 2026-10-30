package com.nicolascommandeur.cowkingsquest.view.screens.counter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import com.nicolascommandeur.cowkingsquest.model.hunt.CharacterStrategy
import com.nicolascommandeur.cowkingsquest.model.hunt.HuntState
import com.nicolascommandeur.cowkingsquest.model.hunt.Relic
import com.nicolascommandeur.cowkingsquest.view.components.HuntCard

@Composable
internal fun RelicChecklist(
    state: HuntState, onChange: (HuntState) -> Unit
) {
    val hunt = state.activeHunt
    val target = state.targetRelic
    val oneCharacter = state.strategy == CharacterStrategy.ONE_CHARACTER

    HuntCard {
        // Title
        Text(
            "YOUR RELICS",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        // Description
        Text(
            "Confirm pickups made with ${hunt.name}, even if the recorded count is lower.",
            style = MaterialTheme.typography.bodySmall
        )

        // Relics list
        Relic.entries.forEach { relic ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = relic in state.collectedRelics,
                    onCheckedChange = { onChange(state.markRelic(relic, it)) },
                    modifier = Modifier.semantics {
                        contentDescription = "${relic.title} collected"
                    }
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        relic.title,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        relic.zoneLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (oneCharacter && relic !in state.collectedRelics) {
                    TextButton(
                        onClick = { onChange(state.copy(singleTarget = relic)) },
                        enabled = target != relic
                    ) {
                        Text(if (target == relic) "Current target" else "Target ${relic.shortName}")
                    }
                }
            }
        }
    }
}
