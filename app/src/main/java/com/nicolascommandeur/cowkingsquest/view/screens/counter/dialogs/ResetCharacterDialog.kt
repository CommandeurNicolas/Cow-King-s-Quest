package com.nicolascommandeur.cowkingsquest.view.screens.counter.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
internal fun ResetCharacterDialog(
    characterName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset $characterName?") },
        text = {
            Text("This clears this character’s recorded count, undo history and first-drop history. Collected relics and other characters keep their progress. Reset only if you are starting with a fresh in-game character.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Reset count") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
