package com.nicolascommandeur.cowkingsquest.view.screens.farming

import android.content.ActivityNotFoundException
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nicolascommandeur.cowkingsquest.R
import com.nicolascommandeur.cowkingsquest.data.farming.farmingRoutes
import com.nicolascommandeur.cowkingsquest.data.guide.COMMUNITY_GUIDE_URL
import com.nicolascommandeur.cowkingsquest.model.hunt.CowSource
import com.nicolascommandeur.cowkingsquest.model.hunt.Hunt
import com.nicolascommandeur.cowkingsquest.model.hunt.ZONE_REMINDER_COUNT
import com.nicolascommandeur.cowkingsquest.view.components.image.ImageViewerDialog
import com.nicolascommandeur.cowkingsquest.view.components.image.ViewerImage
import com.nicolascommandeur.cowkingsquest.view.components.image.roundedFittedImageCorners
import kotlinx.coroutines.launch

@Composable
fun FarmingRoutesScreen(
    hunt: Hunt,
    onBack: () -> Unit,
    openLink: ((String) -> Unit)? = null,
    rngPhase: Boolean = false,
    cowSource: CowSource = CowSource.WORLD_COWS,
    collected: Boolean = false
) {
    var enlargedMap by rememberSaveable { mutableStateOf<String?>(null) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    fun openSource() {
        try {
            if (openLink != null)
                openLink(COMMUNITY_GUIDE_URL) else uriHandler.openUri(COMMUNITY_GUIDE_URL)
        } catch (_: ActivityNotFoundException) {
            scope.launch { snackbar.showSnackbar("No browser available to open the route guide.") }
        } catch (_: IllegalArgumentException) {
            scope.launch { snackbar.showSnackbar("Could not open the route guide.") }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxSize()
                    .testTag("farming_routes"),
            ) {
                // Header
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Back button
                        TextButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Back")
                        }

                        Text(
                            "MAKE EVERY LAP COUNT",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 2.sp,
                        )
                        Text(
                            "Where to farm",
                            fontFamily = FontFamily.Serif,
                            fontSize = 34.sp,
                        )
                        Text(
                            "Three routes to keep beside your game. Tap a map to enlarge it.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                // Current selected character / relic hunt
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(16.dp),
                        ) {
                            Text(
                                when {
                                    cowSource == CowSource.BELL_OF_THE_BOVINE -> "${hunt.name} · Bell of the Bovine"
                                    rngPhase -> "${hunt.name} · ${hunt.count} cows recorded"
                                    else -> "${hunt.name} · ${hunt.count} / 666"
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                "Target relic: ${hunt.relic.shortName} · ${hunt.relic.zoneLabel}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                when {
                                    collected -> "This relic is already collected. Choose a missing relic on the counter screen."
                                    cowSource == CowSource.BELL_OF_THE_BOVINE -> "These maps show world cows. With the Bell equipped, attack enemies in ${hunt.relic.zoneInstruction} instead. Confirm relic pickups on the counter screen; Bell summons are not counted." +
                                            if (rngPhase) " Further relic drops are random, with no guaranteed kill count." else ""

                                    rngPhase -> "Farm cows in ${hunt.relic.zoneInstruction} until the relic drops. Further drops are random, with no guaranteed kill count."
                                    hunt.firstRelicObtained -> "This character already obtained its first relic. Choose One character in Farming setup to continue with random drops."
                                    hunt.count < 666 -> "Farm early kills wherever convenient. By $ZONE_REMINDER_COUNT recorded kills, travel to ${hunt.relic.zoneInstruction}. Counting mistakes can make the relic drop before the counter shows 666."
                                    else -> "Your first-drop count is complete. Confirm the relic pickup on the counter screen."
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Farming routes
                items(farmingRoutes, key = { it.id }) { route ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(16.dp),
                        ) {
                            Text(
                                route.region.uppercase(),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.sp
                            )
                            Text(
                                route.town,
                                style = MaterialTheme.typography.headlineSmall,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                "${route.estimate} · guide estimate",
                                style = MaterialTheme.typography.labelMedium
                            )
                            if (route.region in hunt.relic.zones) {
                                Text(
                                    if (rngPhase || cowSource == CowSource.BELL_OF_THE_BOVINE) "Farming zone for your target relic"
                                    else "Valid final-kill zone for your relic",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            val mapPainter = painterResource(route.map)
                            Surface(
                                onClick = { enlargedMap = route.id },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("map_${route.id}")
                            ) {
                                Image(
                                    painter = mapPainter,
                                    contentDescription = route.mapDescription,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 140.dp, max = 300.dp)
                                        .roundedFittedImageCorners(mapPainter.intrinsicSize)
                                )
                            }
                            Text(
                                "Map: DiabloFilter / Rizarjay · Diablo IV imagery © Blizzard",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                route.directions,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(
                                onClick = { enlargedMap = route.id }
                            ) {
                                Text("Enlarge map")
                            }
                        }
                    }
                }

                // Repeat instructions
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(16.dp),
                        ) {
                            Text(
                                "Repeat the route",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "After clearing a lap, leave combat and log out, then back in to refresh spawns. Counts vary: record actual kills, not the estimate.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Maps work offline. Routes checked October 5, 2026; spawns and reset behavior may change with game updates.",
                                style = MaterialTheme.typography.bodySmall
                            )

                            // Link to online guide
                            TextButton(
                                onClick = ::openSource,
                                modifier = Modifier.testTag("route_source")
                            ) {
                                Text("Full route guide · DiabloFilter ↗")
                            }
                        }
                    }
                }
            }
        }
    }

    farmingRoutes.find { it.id == enlargedMap }?.let { route ->
        ImageViewerDialog(
            images = listOf(
                ViewerImage(
                    id = route.id,
                    title = "${route.town} · ${route.region}",
                    resource = route.map,
                    contentDescription = route.mapDescription,
                    caption = route.directions,
                    sourceCredit = "Map: DiabloFilter / Rizarjay · Diablo IV imagery © Blizzard"
                )
            ),
            initialImageId = route.id,
            onDismiss = { enlargedMap = null }
        )
    }
}
