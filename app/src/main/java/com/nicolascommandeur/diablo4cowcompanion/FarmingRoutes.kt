package com.nicolascommandeur.diablo4cowcompanion

import android.content.ActivityNotFoundException
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch

private data class FarmingRoute(
    val id: String,
    val town: String,
    val region: String,
    val estimate: String,
    val directions: String,
    @param:DrawableRes val map: Int,
    val mapDescription: String
)

private val farmingRoutes = listOf(
    FarmingRoute(
        "farobru",
        "Farobru",
        "Dry Steppes",
        "Up to 14 cows / lap",
        "Start at the waypoint. Head south and follow the yellow loop through the nearby farms, returning to town.",
        R.drawable.route_farobru,
        "Farobru map: blue waypoint at the upper right; yellow farming loop runs south and west before returning."
    ),
    FarmingRoute(
        "cerrigar",
        "Cerrigar",
        "Scosglen",
        "Up to 18 cows / lap",
        "Start at the waypoint. Check the farms along the yellow branches west and east of town; retrace your path between branches.",
        R.drawable.route_cerrigar,
        "Cerrigar map: blue waypoint near the center; yellow farming branches extend west and east."
    ),
    FarmingRoute(
        "zarbinzet",
        "Zarbinzet",
        "Hawezar",
        "Up to 9 cows / lap",
        "Leave the waypoint toward the southeast. Follow the yellow path through the farm areas south of town, then return.",
        R.drawable.route_zarbinzet,
        "Zarbinzet map: blue waypoint at the top; yellow path heads south into the farms and branches west."
    )
)

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
            Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxSize()
                    .testTag("farming_routes"),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                        .roundedMapCorners(mapPainter.intrinsicSize),
                                )
                            }
                            Text(
                                route.directions,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(
                                onClick = { enlargedMap = route.id }
                            ) {
                                Text("Enlarge map")
                            }
                            Text(
                                "Map: DiabloFilter / Rizarjay · Diablo IV imagery © Blizzard",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
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
        RouteMapDialog(route, onDismiss = { enlargedMap = null })
    }
}

@Composable
private fun RouteMapDialog(route: FarmingRoute, onDismiss: () -> Unit) {
    var scale by remember(route.id) { mutableFloatStateOf(1f) }
    var offset by remember(route.id) { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .safeDrawingPadding()
                .padding(8.dp)
                .clip(RoundedCornerShape(24.dp)),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${route.town} · ${route.region}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onDismiss) { Text("Close") }
                }
                Text(
                    "Pinch to zoom · Drag to pan",
                    style = MaterialTheme.typography.bodySmall
                )
                BoxWithConstraints(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    val painter = painterResource(route.map)
                    val viewportWidth = constraints.maxWidth.toFloat()
                    val viewportHeight = constraints.maxHeight.toFloat()
                    Box(
                        Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        val fittedScale = minOf(
                            viewportWidth / painter.intrinsicSize.width,
                            viewportHeight / painter.intrinsicSize.height
                        )
                        val fittedWidth = painter.intrinsicSize.width * fittedScale
                        val fittedHeight = painter.intrinsicSize.height * fittedScale
                        val gestures = rememberTransformableState { zoom, pan, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            val xLimit =
                                ((fittedWidth * scale - viewportWidth) / 2).coerceAtLeast(0f)
                            val yLimit =
                                ((fittedHeight * scale - viewportHeight) / 2).coerceAtLeast(0f)
                            offset = Offset(
                                (offset.x + pan.x).coerceIn(-xLimit, xLimit),
                                (offset.y + pan.y).coerceIn(-yLimit, yLimit)
                            )
                        }

                        Image(
                            painter = painter,
                            contentDescription = route.mapDescription,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = scale; scaleY = scale
                                    translationX = offset.x; translationY = offset.y
                                }
                                .transformable(gestures)
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "${(scale * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium
                    )
                    TextButton(
                        onClick = {
                            scale = 1f; offset = Offset.Zero
                        }
                    ) {
                        Text("Reset zoom")
                    }
                }
                Text(
                    "Map: DiabloFilter / Rizarjay · Diablo IV imagery © Blizzard",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Clip the fitted image, rather than the letterboxed bounds of its container.
private fun Modifier.roundedMapCorners(imageSize: Size): Modifier = drawWithCache {
    val fit = minOf(size.width / imageSize.width, size.height / imageSize.height)
    val width = imageSize.width * fit
    val height = imageSize.height * fit
    val left = (size.width - width) / 2f
    val top = (size.height - height) / 2f
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                left = left, top = top, right = left + width, bottom = top + height,
                cornerRadius = CornerRadius(12.dp.toPx())
            )
        )
    }
    onDrawWithContent {
        clipPath(path) { this@onDrawWithContent.drawContent() }
    }
}
