package com.nicolascommandeur.cowkingsquest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nicolascommandeur.cowkingsquest.ui.theme.DiabloCowCounterTheme

@Composable
fun CowCounter(
    state: HuntState,
    onOpenRoutes: (() -> Unit)? = null,
    onCountBounds: ((Rect) -> Unit)? = null,
    onChange: (HuntState) -> Unit
) {
    val hunt = state.activeHunt
    val target = state.targetRelic
    val oneCharacter = state.strategy == CharacterStrategy.ONE_CHARACTER
    val bell = state.cowSource == CowSource.BELL_OF_THE_BOVINE
    val rng = state.isRngPhase
    var dialog by rememberSaveable { mutableStateOf<String?>(null) }
    val haptics = LocalHapticFeedback.current
    val view = LocalView.current

    DisposableEffect(view, state.keepAwake) {
        val previous = view.keepScreenOn
        view.keepScreenOn = state.keepAwake
        onDispose { view.keepScreenOn = previous }
    }

    fun update(next: Hunt) {
        onChange(state.updateActive(next))
        if (
            !hunt.pastFirstDrop &&
            hunt.count < ZONE_REMINDER_COUNT &&
            next.count in ZONE_REMINDER_COUNT until COW_GOAL
        ) dialog = "zone"
    }

    fun add(amount: Int) {
        update(hunt.add(amount, continueAfterGoal = oneCharacter))
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "DIABLO IV  /  FIELD NOTES",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall, letterSpacing = 2.sp
                    )
                    Text(
                        stringResource(R.string.app_name),
                        fontFamily = FontFamily.Serif,
                        fontSize = 34.sp
                    )
                    Text(
                        if (oneCharacter) "One character. Three relics. Follow your own hunt."
                        else "Three characters. Three relics. Follow your own hunt.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    onClick = { dialog = "setup" }, shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Farming setup",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "${state.strategy.title} · ${state.cowSource.title}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            "Change",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (oneCharacter) "YOUR CHARACTER" else "YOUR CHARACTERS",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        "${state.collectedRelics.size} / 3 relics",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                if (!oneCharacter) {
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
                                    Text(
                                        "0${index + 1}  ${character.relic.shortName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        character.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.labelLarge
                                    )
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
                CounterCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            hunt.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { dialog = "character" }) { Text("Edit") }
                    }
                    if (bell) {
                        Text(
                            "FARM WITH THE BELL",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall, letterSpacing = 1.sp
                        )
                        Text(
                            "Attack enemies in the target zones",
                            fontFamily = FontFamily.Serif,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text("Equip the Bell of the Bovine. Its exploding cows contribute to your character’s hidden cow-kill progress. Confirm each relic below when you pick it up.")
                        Text(
                            "Bell summons are not counted in this app.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        val countingAllowed = rng || !hunt.pastFirstDrop && hunt.count < COW_GOAL
                        Text(
                            "COWS RECORDED",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 2.sp
                        )
                        Text(
                            "${hunt.count}",
                            fontSize = if (hunt.count < 1_000_000) 88.sp else 48.sp,
                            lineHeight = if (hunt.count < 1_000_000) 96.sp else 56.sp,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .onGloballyPositioned { onCountBounds?.invoke(it.boundsInRoot()) }
                                .semantics {
                                    contentDescription = if (rng) "${hunt.count} cows recorded"
                                    else "${hunt.count} cows slain out of 666"
                                }
                        )
                        if (!rng && !hunt.firstRelicObtained) {
                            Text(
                                "of 666",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            LinearProgressIndicator(
                                progress = { (hunt.count / COW_GOAL.toFloat()).coerceIn(0f, 1f) },
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                            )
                        }
                        Text(
                            when {
                                rng -> "Keep farming for the remaining relics. Further drops are random."
                                hunt.firstRelicObtained -> "First relic already obtained on this character."
                                hunt.count >= COW_GOAL -> "Count complete. Check for your relic."
                                hunt.count == COW_GOAL - 1 -> "One left. Head to ${target.zoneInstruction}."
                                hunt.count >= ZONE_REMINDER_COUNT -> "Switch to ${target.zoneInstruction} before more kills."
                                else -> "${COW_GOAL - hunt.count} cows to go"
                            },
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (!rng && hunt.count == COW_GOAL - 1) dialog = "final"
                                else add(1)
                            },
                            enabled = countingAllowed && hunt.count < Int.MAX_VALUE,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9C352E),
                                contentColor = Color(0xFFFFF0DD)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 80.dp)
                        ) {
                            Text(
                                when {
                                    rng -> "+1   Cow killed"
                                    hunt.firstRelicObtained -> "First relic obtained"
                                    hunt.count >= COW_GOAL -> "666 cows recorded"
                                    hunt.count == COW_GOAL - 1 -> "Record final cow"
                                    else -> "+1   Cow killed"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf(5, 10).forEach { amount ->
                                OutlinedButton(
                                    onClick = { add(amount) },
                                    enabled = countingAllowed && (rng || hunt.count < COW_GOAL - 1) && hunt.count < Int.MAX_VALUE,
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                ) { Text("+$amount") }
                            }
                            OutlinedButton(
                                onClick = { update(hunt.undo()) },
                                enabled = hunt.previousCount != null,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                            ) { Text("Undo") }
                        }
                        TextButton(
                            onClick = { dialog = "count" },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Correct count")
                        }
                        Text(
                            "Recorded totals include world cows only. If you used the Bell, your in-game total may be higher.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                CounterCard {
                    Text(
                        if (rng || bell || hunt.firstRelicObtained) "TARGET ZONES" else "FINAL COW LOCATION",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        target.zoneLabel,
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = FontFamily.Serif
                    )
                    Text(target.title)
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
                CounterCard {
                    Text(
                        "YOUR RELICS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Confirm pickups made with ${hunt.name}, even if the recorded count is lower.",
                        style = MaterialTheme.typography.bodySmall
                    )
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

                if (bell) CounterCard { BellHelp() }

                if (onOpenRoutes != null && !bell) {
                    OutlinedButton(
                        onClick = onOpenRoutes,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Where to farm cows · Maps & routes")
                    }
                }

                if (state.collectedRelics.size == Relic.entries.size) Text(
                    "All three relics collected. The next part of your hunt awaits.",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Keep screen awake")
                        Text(
                            "While the counter is open",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.keepAwake,
                        onCheckedChange = { onChange(state.copy(keepAwake = it)) }
                    )
                }
                TextButton(
                    onClick = { dialog = "reset" },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Reset this character",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    if (bell) "Saved on this device • Confirm relic pickups in game."
                    else "Saved on this device • Manual companion counter\nBefore the first drop, bulk additions stop at 665. Game progress is not synced.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    when (dialog) {
        "setup" -> FarmingSetupSheet(
            state,
            onDismiss = { dialog = null }
        ) {
            onChange(it)
            dialog = null
        }

        "character" -> CharacterDialog(
            hunt,
            target,
            onDismiss = { dialog = null }
        ) {
            update(hunt.copy(name = it))
            dialog = null
        }

        "count" -> CountDialog(
            hunt.copy(relic = target),
            continueAfterGoal = rng,
            onDismiss = { dialog = null }
        ) {
            dialog = null
            update(hunt.setCount(it, continueAfterGoal = rng))
        }

        "zone" -> ZoneReminderDialog(hunt.copy(relic = target), onDismiss = { dialog = null })

        "final" -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text("The final cow") },
            text = {
                Text("For ${hunt.name}, kill cow #666 in ${target.zoneInstruction} to target the ${target.title}. Only record it once you have made that kill.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        update(hunt.recordFinalKill())
                        dialog = null
                    }
                ) { Text("Record cow #666") }
            },
            dismissButton = {
                TextButton(
                    onClick = { dialog = null }
                ) { Text("Not yet") }
            }
        )

        "reset" -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text("Reset ${hunt.name}?") },
            text = { Text("This clears this character’s recorded count, undo history and first-drop history. Collected relics and other characters keep their progress. Reset only if you are starting with a fresh in-game character.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        update(hunt.reset())
                        dialog = null
                    }
                ) { Text("Reset count") }
            },
            dismissButton = {
                TextButton(
                    onClick = { dialog = null }
                ) { Text("Cancel") }
            }
        )
    }
}

@Composable
internal fun CounterCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF2B231B), Color(0xFF1D1B18))))
                .padding(18.dp),
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CounterPreview() {
    DiabloCowCounterTheme { CowCounter(HuntState()) {} }
}
