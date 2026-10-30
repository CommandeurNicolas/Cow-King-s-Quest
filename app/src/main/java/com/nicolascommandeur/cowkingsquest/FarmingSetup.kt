package com.nicolascommandeur.cowkingsquest

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

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
                CounterCard { BellHelp() }
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

@Composable
private fun SetupChoice(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
                .padding(12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected, onClick = null)
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(
                    description, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
internal fun BellHelp() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var enlargedImageId by rememberSaveable { mutableStateOf<String?>(null) }
    val questImages = guideImages("bell_quest_map", "bell_townsman")
    val altarImages = guideImages("bell_altar_map", "bell_altar", "bell_summon_altar")
    val itemImage = unlockGuideImages.getValue("bell_item")
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 0f else -90f,
        animationSpec = tween(180),
        label = "Bell instructions disclosure chevron"
    )
    TextButton(onClick = { expanded = !expanded }) {
        Icon(
            painter = painterResource(R.drawable.ic_expand_more),
            contentDescription = null,
            modifier = Modifier.rotate(arrowRotation)
        )
        Spacer(Modifier.width(4.dp))
        Text(if (expanded) "Hide Bell instructions" else "How to get and use the Bell")
    }
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(tween(180)) + fadeIn(tween(180)),
        exit = shrinkVertically(tween(180)) + fadeOut(tween(120))
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Get five Prime Ribs", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Old Tristram quest", style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Available before unlocking the Secret Cow Level. Unlock Old Tristram and accept the Wounded Townsman’s Butcher quest outside the Cathedral in the northern part of town. Kill the Butcher in the Labyrinth to obtain five Prime Ribs.")
                }
                GuideImageGallery(questImages, "bell_quest") { enlargedImageId = it.id }
                Text(
                    "Map brightened to make the outlines clearer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Cow King", style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Requires access to the Secret Cow Level. Defeat the Cow King repeatedly until you have collected five Prime Ribs.")
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Reach Moophisto", style = MaterialTheme.typography.titleMedium)
                Text("With five Prime Ribs, use the Altar of the Heifer east of Tyrael’s Encampment in Skovos to reach the Bovine Sanctum. This crossing does not consume the ribs. Activate the waypoint, then use the altar in the northern arena: summoning Moophisto, the Divine Bovine, consumes five ribs.")
                GuideImageGallery(altarImages, "bell_altars") { enlargedImageId = it.id }
                Text("The Bell of the Bovine is a random drop, so one summon may not give you the amulet. Further attempts require more ribs or another player’s summon.")
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Equip and farm", style = MaterialTheme.typography.titleMedium)
                Text("The Bell can be passed between characters through the common stash. Each character must be level 70 to equip it. This also applies to all three characters if you choose that strategy.")
                Text("Hit enemies in the region of the relic you need. Lucky Hit can trigger a Moo, then Lucky Moo can summon an exploding cow. Higher attack speed lets you hit more often, while equipment with Lucky Hit Chance increases the chance of triggering the Bell’s Lucky Hit effect. Together, they can help summon cows more frequently. Bell farming works with either character strategy.")
                GuideItemReference(itemImage)
            }
            Text(
                "Guide sources: DiabloFilter’s Bell farming guide, Icy Veins’ Old Tristram/Butcher guide and VULKK’s Bovine Sanctum guide. Checked October 14, 2026. Level and stash restrictions verified in game by the app author.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    enlargedImageId?.let { imageId ->
        GuideImageViewer(questImages + altarImages, imageId) { enlargedImageId = null }
    }
}
