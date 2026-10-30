package com.nicolascommandeur.cowkingsquest.view.screens.counter.dialogs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.nicolascommandeur.cowkingsquest.view.components.HuntDialog

@Composable
internal fun ResetCharacterDialog(
    characterName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    HuntDialog(
        title = "Reset $characterName?",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        confirmLabel = "Reset count",
        text = {
            Text("This clears this character’s recorded count, undo history and first-drop history. Collected relics and other characters keep their progress. Reset only if you are starting with a fresh in-game character.")
        }
    )
}
