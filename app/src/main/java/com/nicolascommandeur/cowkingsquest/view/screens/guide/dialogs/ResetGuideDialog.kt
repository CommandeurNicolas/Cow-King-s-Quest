package com.nicolascommandeur.cowkingsquest.view.screens.guide.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
internal fun ResetGuideDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset checklist?") },
        text = {
            Text("Clear all guide checkmarks? Your character names, cow counts and collected relics will stay saved.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Clear checkmarks") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
