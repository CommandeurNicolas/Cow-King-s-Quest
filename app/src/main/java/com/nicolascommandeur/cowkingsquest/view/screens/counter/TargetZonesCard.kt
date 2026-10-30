package com.nicolascommandeur.cowkingsquest.view.screens.counter

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.nicolascommandeur.cowkingsquest.model.hunt.CharacterStrategy
import com.nicolascommandeur.cowkingsquest.model.hunt.CowSource
import com.nicolascommandeur.cowkingsquest.model.hunt.HuntState
import com.nicolascommandeur.cowkingsquest.model.hunt.Relic
import com.nicolascommandeur.cowkingsquest.model.hunt.ZONE_REMINDER_COUNT
import com.nicolascommandeur.cowkingsquest.view.components.HuntCard

@Composable
internal fun TargetZonesCard(
    state: HuntState, onChange: (HuntState) -> Unit
) {
    val hunt = state.activeHunt
    val target = state.targetRelic
    val oneCharacter = state.strategy == CharacterStrategy.ONE_CHARACTER
    val bell = state.cowSource == CowSource.BELL_OF_THE_BOVINE
    val rng = state.isRngPhase

    HuntCard {
        // Title
        Text(
            if (rng || bell || hunt.firstRelicObtained) "TARGET ZONES" else "FINAL COW LOCATION",
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.primary
        )

        // Zone
        Text(
            target.zoneLabel,
            style = MaterialTheme.typography.headlineSmall,
            fontFamily = FontFamily.Serif
        )

        // Relic name
        Text(target.title)

        // Description
        Text(
            when {
                state.collectedRelics.size == Relic.entries.size -> "All three relics are collected. Continue with the unlock guide."
                target in state.collectedRelics -> "This relic is already collected. Select a character targeting a missing relic, or change your farming setup."
                rng -> "Farm in ${target.zoneInstruction} until this relic drops. There is no guaranteed kill count for the remaining relics."
                hunt.firstRelicObtained -> "This character has already obtained its first relic. Use a character that has not obtained a relic for another guaranteed first drop, or choose One character to continue with random drops."
                bell -> "Farm in ${target.zoneInstruction} from the start so your first relic drops in the right region. The first drop requires 666 cow kills on this character."
                else -> "Switch to ${target.zoneInstruction} by $ZONE_REMINDER_COUNT recorded kills to allow for counting mistakes. Kill cow #666 there, then confirm you picked up the relic."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Checkbox
        if (!oneCharacter) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = target in state.collectedRelics,
                    onCheckedChange = { onChange(state.markRelic(target, it)) }
                )
                Text("Relic collected")
            }
        }
    }
}
