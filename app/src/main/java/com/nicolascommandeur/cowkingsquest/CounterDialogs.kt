package com.nicolascommandeur.cowkingsquest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun ZoneReminderDialog(hunt: Hunt, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                "Switch zones before more kills",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Text("Manual counting can miss kills. Your actual total may be higher, so your relic could drop before this counter shows 666.")
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            "CHARACTER",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            hunt.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                        Text(
                            "SWITCH NOW TO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Text(
                            hunt.relic.zoneInstruction,
                            fontFamily = FontFamily.Serif,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "RELIC TO COLLECT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                        Text(
                            hunt.relic.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Text(
                    "Switch early to avoid getting a relic you already have. If you are already in a target zone, keep farming there.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                Text("Got it")
            }
        }
    )
}

@Composable
internal fun CharacterDialog(
    hunt: Hunt,
    target: Relic,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by rememberSaveable(hunt.relic) { mutableStateOf(hunt.name) }
    val validName = name.isNotBlank()

    fun saveName() {
        if (validName) onSave(name.trim())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                "Edit character",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 32) name = it },
                    label = { Text("Character name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    isError = !validName,
                    supportingText = if (!validName) ({ Text("Enter a character name.") }) else null,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { saveName() }),
                    modifier = Modifier.fillMaxWidth(),
                )
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Text(
                            "DEFAULT RELIC TARGET",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Text(
                            target.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                        Text(
                            "TARGET ZONES",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Text(
                            target.zoneLabel,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            if (target.zones.size > 1)
                                "Either zone works for this relic."
                            else "Farm in this zone for this relic.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = ::saveName,
                enabled = validName,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save name")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
internal fun CountDialog(
    hunt: Hunt,
    continueAfterGoal: Boolean,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var value by rememberSaveable(hunt.relic) { mutableStateOf(hunt.count.toString()) }
    val number = value.toIntOrNull()
    val limit = if (continueAfterGoal) Int.MAX_VALUE else COW_GOAL
    val valid = number != null && number in 0..limit

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Correct count")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Enter the number of world cows recorded for ${hunt.name}. Bell summons are not included.")
                OutlinedTextField(
                    value = value,
                    onValueChange = { if (it.length <= 10 && it.all(Char::isDigit)) value = it },
                    label = { Text("Cows killed") },
                    singleLine = true,
                    isError = !valid,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = {
                        Text(if (continueAfterGoal) "Enter a non-negative recorded total" else "Enter 0–666")
                    },
                )

                if (number == 666 && !continueAfterGoal)
                    Text("Only use 666 if you have already made the final kill in ${hunt.relic.zoneInstruction}.")
            }
        },
        confirmButton = {
            TextButton(
                onClick = { number?.let(onSave) },
                enabled = valid
            ) {
                Text("Save count")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
