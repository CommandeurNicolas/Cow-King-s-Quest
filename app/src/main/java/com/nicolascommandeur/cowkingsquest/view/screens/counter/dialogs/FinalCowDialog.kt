package com.nicolascommandeur.cowkingsquest.view.screens.counter.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.nicolascommandeur.cowkingsquest.model.hunt.Hunt
import com.nicolascommandeur.cowkingsquest.model.hunt.Relic

@Composable
internal fun FinalCowDialog(
    hunt: Hunt,
    target: Relic,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("The final cow") },
        text = {
            Text("For ${hunt.name}, kill cow #666 in ${target.zoneInstruction} to target the ${target.title}. Only record it once you have made that kill.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Record cow #666") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Not yet") }
        }
    )
}
