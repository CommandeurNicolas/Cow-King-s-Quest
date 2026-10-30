package com.nicolascommandeur.cowkingsquest.view.screens.counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nicolascommandeur.cowkingsquest.model.hunt.COW_GOAL
import com.nicolascommandeur.cowkingsquest.model.hunt.CowSource
import com.nicolascommandeur.cowkingsquest.model.hunt.HuntState
import com.nicolascommandeur.cowkingsquest.model.hunt.ZONE_REMINDER_COUNT
import com.nicolascommandeur.cowkingsquest.view.components.HuntCard

@Composable
internal fun ActiveHuntCard(
    state: HuntState,
    onCountBounds: ((Rect) -> Unit)?,
    onEditCharacter: () -> Unit,
    onCorrectCount: () -> Unit,
    onFinalKill: () -> Unit,
    onAdd: (Int) -> Unit,
    onUndo: () -> Unit
) {
    val hunt = state.activeHunt
    val target = state.targetRelic
    val bell = state.cowSource == CowSource.BELL_OF_THE_BOVINE
    val rng = state.isRngPhase

    HuntCard {
        // Character name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                hunt.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onEditCharacter) { Text("Edit") }
        }

        // Content
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

            // Counter text
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

            // Progress
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

            // Instructions
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

            // +1 Button
            Button(
                onClick = {
                    if (!rng && hunt.count == COW_GOAL - 1) onFinalKill()
                    else onAdd(1)
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

            // +5, +10, Undo buttons
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(5, 10).forEach { amount ->
                    OutlinedButton(
                        onClick = { onAdd(amount) },
                        enabled = countingAllowed && (rng || hunt.count < COW_GOAL - 1) && hunt.count < Int.MAX_VALUE,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                    ) { Text("+$amount") }
                }
                OutlinedButton(
                    onClick = onUndo,
                    enabled = hunt.previousCount != null,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                ) { Text("Undo") }
            }

            // Correct count button
            TextButton(
                onClick = onCorrectCount,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Correct count")
            }

            // Disclaimer
            Text(
                "Recorded totals include world cows only. If you used the Bell, your in-game total may be higher.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
