package com.nicolascommandeur.cowkingsquest.view.screens.counter.dialogs

import androidx.compose.runtime.Composable
import com.nicolascommandeur.cowkingsquest.model.hunt.Hunt
import com.nicolascommandeur.cowkingsquest.model.hunt.HuntState
import com.nicolascommandeur.cowkingsquest.view.screens.counter.FarmingSetupSheet

@Composable
internal fun CounterDialogs(
    dialog: String?,
    state: HuntState,
    onDismiss: () -> Unit,
    onChange: (HuntState) -> Unit,
    update: (Hunt) -> Unit
) {
    val hunt = state.activeHunt
    val target = state.targetRelic
    val rng = state.isRngPhase
    when (dialog) {
        "setup" -> FarmingSetupSheet(
            state,
            onDismiss = onDismiss
        ) {
            onChange(it)
            onDismiss()
        }

        "character" -> CharacterDialog(
            hunt,
            target,
            onDismiss = onDismiss
        ) {
            update(hunt.copy(name = it))
            onDismiss()
        }

        "count" -> CountDialog(
            hunt.copy(relic = target),
            continueAfterGoal = rng,
            onDismiss = onDismiss
        ) {
            onDismiss()
            update(hunt.setCount(it, continueAfterGoal = rng))
        }

        "zone" -> ZoneReminderDialog(hunt.copy(relic = target), onDismiss = onDismiss)

        "final" -> FinalCowDialog(
            hunt = hunt,
            target = target,
            onDismiss = onDismiss,
            onConfirm = {
                update(hunt.recordFinalKill())
                onDismiss()
            }
        )

        "reset" -> ResetCharacterDialog(
            characterName = hunt.name,
            onDismiss = onDismiss,
            onConfirm = {
                update(hunt.reset())
                onDismiss()
            }
        )
    }
}
