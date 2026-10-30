package com.nicolascommandeur.cowkingsquest.view.screens.counter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nicolascommandeur.cowkingsquest.R
import com.nicolascommandeur.cowkingsquest.model.hunt.COW_GOAL
import com.nicolascommandeur.cowkingsquest.model.hunt.CharacterStrategy
import com.nicolascommandeur.cowkingsquest.model.hunt.CowSource
import com.nicolascommandeur.cowkingsquest.model.hunt.Hunt
import com.nicolascommandeur.cowkingsquest.model.hunt.HuntState
import com.nicolascommandeur.cowkingsquest.model.hunt.Relic
import com.nicolascommandeur.cowkingsquest.model.hunt.ZONE_REMINDER_COUNT
import com.nicolascommandeur.cowkingsquest.view.components.HuntCard
import com.nicolascommandeur.cowkingsquest.view.components.guide.BellHelp
import com.nicolascommandeur.cowkingsquest.view.screens.counter.dialogs.CounterDialogs
import com.nicolascommandeur.cowkingsquest.view.theme.DiabloCowCounterTheme

@Composable
fun CounterScreen(
    state: HuntState,
    onOpenRoutes: (() -> Unit)? = null,
    onCountBounds: ((Rect) -> Unit)? = null,
    onChange: (HuntState) -> Unit
) {
    val hunt = state.activeHunt
    val oneCharacter = state.strategy == CharacterStrategy.ONE_CHARACTER
    val bell = state.cowSource == CowSource.BELL_OF_THE_BOVINE
    var dialog by rememberSaveable { mutableStateOf<String?>(null) }
    val haptics = LocalHapticFeedback.current
    val view = LocalView.current

    DisposableEffect(view, state.keepAwake) {
        val previous = view.keepScreenOn
        view.keepScreenOn = state.keepAwake
        onDispose { view.keepScreenOn = previous }
    }

    fun update(next: Hunt) {
        onChange(state.updateActive(next))
        if (
            !hunt.pastFirstDrop &&
            hunt.count < ZONE_REMINDER_COUNT &&
            next.count in ZONE_REMINDER_COUNT until COW_GOAL
        ) dialog = "zone"
    }

    fun add(amount: Int) {
        update(hunt.add(amount, continueAfterGoal = oneCharacter))
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "DIABLO IV  /  FIELD NOTES",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall, letterSpacing = 2.sp
                    )
                    Text(
                        stringResource(R.string.app_name),
                        fontFamily = FontFamily.Serif,
                        fontSize = 34.sp
                    )
                    Text(
                        if (oneCharacter) "One character. Three relics. Follow your own hunt."
                        else "Three characters. Three relics. Follow your own hunt.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Setup card
                Surface(
                    onClick = { dialog = "setup" }, shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Farming setup",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "${state.strategy.title} · ${state.cowSource.title}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            "Change",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }

                // Collected relics counter
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (oneCharacter) "YOUR CHARACTER" else "YOUR CHARACTERS",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        "${state.collectedRelics.size} / 3 relics",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                // Characters
                if (!oneCharacter) CharacterSelector(state, onChange)

                // Counter card
                ActiveHuntCard(
                    state = state,
                    onCountBounds = onCountBounds,
                    onEditCharacter = { dialog = "character" },
                    onCorrectCount = { dialog = "count" },
                    onFinalKill = { dialog = "final" },
                    onAdd = { amount -> add(amount) },
                    onUndo = { update(hunt.undo()) }
                )

                // Target zone
                TargetZonesCard(state, onChange)

                // Collected relics
                RelicChecklist(state, onChange)

                // Bell guide
                if (bell) HuntCard { BellHelp() }

                // Cow farming routes
                if (onOpenRoutes != null && !bell) {
                    OutlinedButton(
                        onClick = onOpenRoutes,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Where to farm cows · Maps & routes")
                    }
                }

                // All relics collected text
                if (state.collectedRelics.size == Relic.entries.size) Text(
                    "All three relics collected. The next part of your hunt awaits.",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Keep screen awake
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Keep screen awake")
                        Text(
                            "While the counter is open",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.keepAwake,
                        onCheckedChange = { onChange(state.copy(keepAwake = it)) }
                    )
                }

                // Reset character
                TextButton(
                    onClick = { dialog = "reset" },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Reset this character",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Disclaimer
                Text(
                    if (bell) "Saved on this device • Confirm relic pickups in game."
                    else "Saved on this device • Manual companion counter\nBefore the first drop, bulk additions stop at 665. Game progress is not synced.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    CounterDialogs(
        dialog = dialog,
        state = state,
        onDismiss = { dialog = null },
        onChange = onChange,
        update = { next -> update(next) }
    )
}

@Preview(showBackground = true)
@Composable
private fun CounterPreview() {
    DiabloCowCounterTheme { CounterScreen(HuntState()) {} }
}
