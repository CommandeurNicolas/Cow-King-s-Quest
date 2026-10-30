package com.nicolascommandeur.cowkingsquest.view.screens.counter.dialogs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.nicolascommandeur.cowkingsquest.model.hunt.Hunt
import com.nicolascommandeur.cowkingsquest.model.hunt.Relic
import com.nicolascommandeur.cowkingsquest.view.components.HuntDialog

@Composable
internal fun FinalCowDialog(
    hunt: Hunt,
    target: Relic,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    HuntDialog(
        title = "The final cow",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        confirmLabel = "Record cow #666",
        dismissLabel = "Not yet",
        text = {
            Text("For ${hunt.name}, kill cow #666 in ${target.zoneInstruction} to target the ${target.title}. Only record it once you have made that kill.")
        }
    )
}
