package com.nicolascommandeur.cowkingsquest.view.screens.counter.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nicolascommandeur.cowkingsquest.model.hunt.COW_GOAL
import com.nicolascommandeur.cowkingsquest.model.hunt.Hunt
import com.nicolascommandeur.cowkingsquest.view.components.HuntDialog

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

    HuntDialog(
        title = "Correct count",
        onDismiss = onDismiss,
        onConfirm = { number?.let(onSave) },
        confirmLabel = "Save count",
        confirmEnabled = valid,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Enter the number of world cows recorded for ${hunt.name}. Bell summons are not included.")
                OutlinedTextField(
                    value = value,
                    onValueChange = { if (it.length <= 10 && it.all(Char::isDigit)) value = it },
                    label = { Text("Cows killed") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
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
    )
}
