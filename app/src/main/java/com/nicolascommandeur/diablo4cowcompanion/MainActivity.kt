package com.nicolascommandeur.diablo4cowcompanion

import android.animation.ValueAnimator
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nicolascommandeur.diablo4cowcompanion.ui.theme.DiabloCowCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Rotation restores the activity without showing another launch splash.
        var splashFinished by mutableStateOf(savedInstanceState != null)
        splashScreen.setOnExitAnimationListener { splash ->
            if (ValueAnimator.areAnimatorsEnabled()) {
                splash.view.animate()
                    .alpha(0f)
                    .setDuration(160L)
                    .withEndAction { splash.remove(); splashFinished = true }
                    .start()
            } else {
                splash.remove()
                splashFinished = true
            }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        // Load local data
        val store = HuntStore(this)
        val guideStore = GuideStore(this)

        // Moo ?
        val visitorSession = if (
            applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0 &&
            intent.getBooleanExtra("cow_visitor_preview", false)
        ) {
            // Forced Moo.
            CowVisitorLaunch.previewSession
        } else {
            // Random Moo, with a guaranteed visit every 666 cold launches.
            CowVisitorLaunch.session(this)
        }

        setContent {
            var state by remember { mutableStateOf(store.load()) }
            var completedSteps by remember { mutableStateOf(guideStore.load()) }
            DiabloCowCounterTheme {
                CowHuntApp(
                    state = state,
                    completedSteps = completedSteps,
                    onHuntChange = { next -> state = next; store.save(next) },
                    onGuideChange = { next -> completedSteps = next; guideStore.save(next) },
                    visitorSession = visitorSession,
                    splashFinished = splashFinished
                )
            }
        }
    }
}

@Composable
fun CowCounter(
    state: HuntState,
    onOpenRoutes: (() -> Unit)? = null,
    onCountBounds: ((Rect) -> Unit)? = null,
    onChange: (HuntState) -> Unit
) {
    val hunt = state.hunts[state.selected]
    var dialog by rememberSaveable { mutableStateOf<String?>(null) }
    val haptics = LocalHapticFeedback.current
    val view = LocalView.current

    DisposableEffect(view, state.keepAwake) {
        val previous = view.keepScreenOn
        view.keepScreenOn = state.keepAwake
        onDispose { view.keepScreenOn = previous }
    }

    fun update(next: Hunt) {
        onChange(state.copy(hunts = state.hunts.mapIndexed { index, old ->
            if (index == state.selected) next else old
        }))
    }

    fun add(amount: Int) {
        update(hunt.add(amount))
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.TopCenter
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "DIABLO IV  /  FIELD NOTES",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 2.sp
                    )
                    Text(
                        stringResource(R.string.app_name),
                        fontFamily = FontFamily.Serif,
                        fontSize = 34.sp
                    )
                    Text(
                        "Three characters. Three relics. Every cow counts.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "YOUR CHARACTERS",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        "${state.hunts.count { it.collected }} / 3 relics",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    state.hunts.forEachIndexed { index, character ->
                        val selected = index == state.selected
                        Surface(
                            onClick = { onChange(state.copy(selected = index)) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .semantics {
                                    contentDescription =
                                        "Select ${character.name}, ${character.relic.shortName}, ${character.count} of 666 cows"
                                },
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(12.dp),
                            ) {
                                Text(
                                    "0${index + 1}  ${character.relic.shortName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    character.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    if (character.collected) "Collected" else "${character.count} / 666",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF2B231B),
                                        Color(0xFF1D1B18)
                                    )
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                hunt.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f),
                            )
                            TextButton(
                                onClick = { dialog = "character" }
                            ) {
                                Text("Edit")
                            }
                        }
                        Text(
                            "COWS SLAIN",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 2.sp
                        )
                        Text(
                            "${hunt.count}",
                            fontSize = 88.sp,
                            lineHeight = 96.sp,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .onGloballyPositioned { onCountBounds?.invoke(it.boundsInRoot()) }
                                .semantics {
                                    contentDescription = "${hunt.count} cows slain out of 666"
                                },
                        )
                        Text(
                            "of 666",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        LinearProgressIndicator(
                            progress = { hunt.count / COW_GOAL.toFloat() },
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                        )
                        Text(
                            when {
                                hunt.collected -> "Relic secured. One step closer."
                                hunt.count == COW_GOAL -> "Count complete. Check for your relic."
                                hunt.count == COW_GOAL - 1 -> "One left. Head to ${hunt.relic.zoneInstruction}."
                                else -> "${COW_GOAL - hunt.count} cows to go"
                            },
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = { if (hunt.count == 665) dialog = "final" else add(1) },
                            enabled = hunt.count < COW_GOAL,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9C352E),
                                contentColor = Color(0xFFFFF0DD)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 80.dp),
                        ) {
                            Text(
                                when (hunt.count) {
                                    665 -> "Record final cow"; 666 -> "666 cows recorded"; else -> "+1   Cow killed"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { add(5) },
                                enabled = hunt.count < 665,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                            ) { Text("+5") }
                            OutlinedButton(
                                onClick = { add(10) },
                                enabled = hunt.count < 665,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                            ) { Text("+10") }
                            OutlinedButton(
                                onClick = { update(hunt.undo()) },
                                enabled = hunt.previousCount != null,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                            ) { Text("Undo") }
                        }
                        TextButton(
                            onClick = { dialog = "count" }
                        ) {
                            Text("Correct count")
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (hunt.count == 665) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(18.dp),
                    ) {
                        Text(
                            if (hunt.count == 665) "BEFORE THE LAST KILL" else "FINAL COW LOCATION",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            hunt.relic.zoneLabel,
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = FontFamily.Serif
                        )
                        Text(hunt.relic.title, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Kill cow #666 in ${hunt.relic.zoneInstruction}, then confirm you picked up the relic.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (hunt.count == COW_GOAL) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = hunt.collected,
                                    onCheckedChange = {
                                        update(hunt.markCollected(it))
                                    },
                                )
                                Text(
                                    "Relic collected",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                if (onOpenRoutes != null) {
                    OutlinedButton(
                        onClick = onOpenRoutes,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Where to farm cows · Maps & routes")
                    }
                }
                if (state.hunts.all { it.collected }) {
                    Text(
                        "All three relics collected. The next part of your hunt awaits.",
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            "Keep screen awake",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            "While the counter is open",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = state.keepAwake,
                        onCheckedChange = { onChange(state.copy(keepAwake = it)) },
                    )
                }
                TextButton(
                    onClick = { dialog = "reset" },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Reset this character", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    "Saved on this device • Manual companion counter\nBulk additions stop at 665. Game progress is not synced.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    when (dialog) {
        "character" -> CharacterDialog(hunt, onDismiss = { dialog = null }) { name ->
            update(hunt.copy(name = name)); dialog = null
        }

        "count" -> CountDialog(hunt, onDismiss = { dialog = null }) {
            update(hunt.setCount(it)); dialog = null
        }

        "final" -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text("The final cow") },
            text = {
                Text("For ${hunt.name}, kill cow #666 in ${hunt.relic.zoneInstruction} to target the ${hunt.relic.title}. Only record it once you have made that kill.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        update(hunt.recordFinalKill()); dialog = null
                    },
                ) {
                    Text("Record cow #666")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { dialog = null }
                ) {
                    Text("Not yet")
                }
            }
        )

        "reset" -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text("Reset ${hunt.name}?") },
            text = {
                Text("This clears this character’s count and collected status. Your other characters keep their progress.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        update(hunt.reset()); dialog = null
                    },
                ) {
                    Text("Reset count")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { dialog = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CharacterDialog(
    hunt: Hunt,
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
                            "RELIC TO COLLECT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Text(hunt.relic.title, style = MaterialTheme.typography.titleMedium)
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                        Text(
                            "FINAL COW LOCATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Text(
                            hunt.relic.zoneLabel,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            if (hunt.relic.zones.size > 1)
                                "Either zone works. Kill cow #666 there to target this relic."
                            else "Kill cow #666 here to target this relic.",
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
private fun CountDialog(
    hunt: Hunt,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var value by rememberSaveable(hunt.relic) { mutableStateOf(hunt.count.toString()) }
    val number = value.toIntOrNull()
    val valid = number != null && number in 0..COW_GOAL

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Correct count")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Enter the actual number killed by ${hunt.name}.")
                OutlinedTextField(
                    value = value,
                    onValueChange = { if (it.length <= 3 && it.all(Char::isDigit)) value = it },
                    label = { Text("Cows killed") },
                    singleLine = true,
                    isError = !valid,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = { Text("Enter 0–666") },
                )
                if (number == 666) Text("Only use 666 if you have already made the final kill in ${hunt.relic.zoneInstruction}.")
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

@Preview(showBackground = true)
@Composable
private fun CounterPreview() {
    DiabloCowCounterTheme { CowCounter(HuntState()) {} }
}
