package com.nicolascommandeur.cowkingsquest.view.screens.counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.nicolascommandeur.cowkingsquest.model.hunt.CharacterStrategy
import com.nicolascommandeur.cowkingsquest.model.hunt.CowSource
import com.nicolascommandeur.cowkingsquest.model.hunt.HuntState
import com.nicolascommandeur.cowkingsquest.model.hunt.Relic
import com.nicolascommandeur.cowkingsquest.view.components.HuntCard
import com.nicolascommandeur.cowkingsquest.view.components.guide.BellHelp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FarmingSetupSheet(
    state: HuntState,
    onDismiss: () -> Unit,
    onSave: (HuntState) -> Unit
) {
    var strategy by rememberSaveable { mutableStateOf(state.strategy) }
    var source by rememberSaveable { mutableStateOf(state.cowSource) }
    var character by rememberSaveable {
        mutableIntStateOf(
            if (state.strategy == CharacterStrategy.ONE_CHARACTER) state.singleCharacter
            else state.selected
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            Text(
                "Farming setup",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                "Choose your character strategy and cow source. Changing setup keeps your characters, recorded counts and collected relics.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Character strategy",
                style = MaterialTheme.typography.titleMedium
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.selectableGroup()
            ) {
                SetupChoice(
                    "Three characters",
                    "One first drop at 666 cow kills per character. Each slot targets a different relic.",
                    strategy == CharacterStrategy.THREE_CHARACTERS
                ) { strategy = CharacterStrategy.THREE_CHARACTERS }
                SetupChoice(
                    "One character",
                    "First drop at 666 cow kills. Keep farming in the other regions for random drops.",
                    strategy == CharacterStrategy.ONE_CHARACTER
                ) { strategy = CharacterStrategy.ONE_CHARACTER }
            }
            if (strategy == CharacterStrategy.ONE_CHARACTER) {
                Text(
                    "Continue with",
                    style = MaterialTheme.typography.titleMedium
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.selectableGroup()
                ) {
                    state.hunts.forEachIndexed { index, hunt ->
                        SetupChoice(
                            hunt.name,
                            "Character slot ${index + 1}" +
                                    if (hunt.pastFirstDrop) " · First drop reached" else " · First drop not confirmed",
                            index == character
                        ) { character = index }
                    }
                }
            }
            Text(
                "Cow source",
                style = MaterialTheme.typography.titleMedium
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.selectableGroup()
            ) {
                SetupChoice(
                    "World cows",
                    "Follow farming routes and record cow kills manually.",
                    source == CowSource.WORLD_COWS
                ) { source = CowSource.WORLD_COWS }
                SetupChoice(
                    "Bell of the Bovine",
                    "Attack enemies to summon exploding cows. Track relic pickups without counting cows.",
                    source == CowSource.BELL_OF_THE_BOVINE
                ) { source = CowSource.BELL_OF_THE_BOVINE }
            }
            if (source == CowSource.BELL_OF_THE_BOVINE) {
                Text(
                    "Requires level 70 to equip. Share the Bell through the common stash; each receiving character must also be level 70.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                HuntCard { BellHelp() }
            }
            Text(
                "Bell kills are not recorded. If you switch to world cows, saved totals cover only manually recorded kills; confirm relics whenever they drop.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) { Text("Cancel") }
                Button(
                    onClick = {
                        val target =
                            if (state.strategy == strategy && state.singleCharacter == character)
                                state.singleTarget else state.hunts[character].relic
                        onSave(
                            state.copy(
                                strategy = strategy,
                                cowSource = source,
                                singleCharacter = if (strategy == CharacterStrategy.ONE_CHARACTER) character else state.singleCharacter,
                                singleTarget = if (strategy == CharacterStrategy.ONE_CHARACTER)
                                    target.takeIf { it !in state.collectedRelics }
                                        ?: Relic.entries.firstOrNull { it !in state.collectedRelics }
                                        ?: target
                                else state.singleTarget
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Save setup") }
            }
        }
    }
}
