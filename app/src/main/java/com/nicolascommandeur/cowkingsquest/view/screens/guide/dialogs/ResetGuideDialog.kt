package com.nicolascommandeur.cowkingsquest.view.screens.guide.dialogs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.nicolascommandeur.cowkingsquest.view.components.HuntDialog

@Composable
internal fun ResetGuideDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    HuntDialog(
        title = "Reset checklist?",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        confirmLabel = "Clear checkmarks",
        text = {
            Text("Clear all guide checkmarks? Your character names, cow counts and collected relics will stay saved.")
        }
    )
}
