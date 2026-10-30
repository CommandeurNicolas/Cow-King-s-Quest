package com.nicolascommandeur.cowkingsquest.view.components.guide

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nicolascommandeur.cowkingsquest.R
import com.nicolascommandeur.cowkingsquest.data.guide.guideImages
import com.nicolascommandeur.cowkingsquest.data.guide.unlockGuideImages
import com.nicolascommandeur.cowkingsquest.view.components.image.ImageViewerDialog

@Composable
internal fun BellHelp() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var enlargedImageId by rememberSaveable { mutableStateOf<String?>(null) }
    val questImages = guideImages("bell_quest_map", "bell_townsman")
    val altarImages = guideImages("bell_altar_map", "bell_altar", "bell_summon_altar")
    val itemImage = unlockGuideImages.getValue("bell_item")
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 0f else -90f,
        animationSpec = tween(180),
        label = "Bell instructions disclosure chevron"
    )
    TextButton(onClick = { expanded = !expanded }) {
        Icon(
            painter = painterResource(R.drawable.ic_expand_more),
            contentDescription = null,
            modifier = Modifier.rotate(arrowRotation)
        )
        Spacer(Modifier.width(4.dp))
        Text(if (expanded) "Hide Bell instructions" else "How to get and use the Bell")
    }
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(tween(180)) + fadeIn(tween(180)),
        exit = shrinkVertically(tween(180)) + fadeOut(tween(120))
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Get five Prime Ribs", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Old Tristram quest", style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Available before unlocking the Secret Cow Level. Unlock Old Tristram and accept the Wounded Townsman’s Butcher quest outside the Cathedral in the northern part of town. Kill the Butcher in the Labyrinth to obtain five Prime Ribs.")
                }
                GuideImageGallery(questImages, "bell_quest") { enlargedImageId = it.id }
                Text(
                    "Map brightened to make the outlines clearer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Cow King", style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Requires access to the Secret Cow Level. Defeat the Cow King repeatedly until you have collected five Prime Ribs.")
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Reach Moophisto", style = MaterialTheme.typography.titleMedium)
                Text("With five Prime Ribs, use the Altar of the Heifer east of Tyrael’s Encampment in Skovos to reach the Bovine Sanctum. This crossing does not consume the ribs. Activate the waypoint, then use the altar in the northern arena: summoning Moophisto, the Divine Bovine, consumes five ribs.")
                GuideImageGallery(altarImages, "bell_altars") { enlargedImageId = it.id }
                Text("The Bell of the Bovine is a random drop, so one summon may not give you the amulet. Further attempts require more ribs or another player’s summon.")
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Equip and farm", style = MaterialTheme.typography.titleMedium)
                Text("The Bell can be passed between characters through the common stash. Each character must be level 70 to equip it. This also applies to all three characters if you choose that strategy.")
                Text("Hit enemies in the region of the relic you need. Lucky Hit can trigger a Moo, then Lucky Moo can summon an exploding cow. Higher attack speed lets you hit more often, while equipment with Lucky Hit Chance increases the chance of triggering the Bell’s Lucky Hit effect. Together, they can help summon cows more frequently. Bell farming works with either character strategy.")
                GuideItemReference(itemImage)
            }
            Text(
                "Guide sources: DiabloFilter’s Bell farming guide, Icy Veins’ Old Tristram/Butcher guide and VULKK’s Bovine Sanctum guide. Checked October 14, 2026. Level and stash restrictions verified in game by the app author.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    enlargedImageId?.let { imageId ->
        ImageViewerDialog((questImages + altarImages).map { it.toViewerImage() }, imageId) { enlargedImageId = null }
    }
}
