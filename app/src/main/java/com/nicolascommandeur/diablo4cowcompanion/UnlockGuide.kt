package com.nicolascommandeur.diablo4cowcompanion

import android.content.Context
import androidx.core.content.edit

const val WOWHEAD_GUIDE_URL = "https://www.wowhead.com/diablo-4/guide/zones/secret-cow-level"
const val COMPLETE_GUIDE_URL =
    "https://vulkk.com/2026/05/07/the-complete-guide-to-diablo-4s-secret-cow-level/"
const val COMMUNITY_GUIDE_URL = "https://diablofilter.com/guide/the-ultimate-secret-cow-level-guide"

data class GuideStep(val id: String, val title: String, val detail: String)
data class GuideSection(
    val id: String,
    val title: String,
    val subtitle: String,
    val steps: List<GuideStep>
)

/** Short, independently worded checklist. Linked walkthroughs provide the location maps. */
val unlockGuide = listOf(
    GuideSection(
        "potion",
        "Stamina Potion",
        "01 / Base game",
        listOf(
            GuideStep(
                "shard",
                "Bloody Wooden Shard",
                "666th cow: Hawezar or Kehjistan. Pick up the relic."
            ),
            GuideStep(
                "tome",
                "Musty Tome",
                "666th cow on another character: Scosglen or Fractured Peaks. Collect it."
            ),
            GuideStep(
                "fragment",
                "Intricate Metallic Fragment",
                "666th cow on your third character: Dry Steppes. Collect it."
            ),
            GuideStep(
                "cleanse",
                "Cleanse the relics",
                "Bring all three to Ked Bardu. Drop them into the oxen fountain, then retrieve them."
            ),
            GuideStep(
                "strange_key",
                "Strange Key",
                "Offer the cleansed relics to the north, west and south oxen statues. Collect the key."
            ),
            GuideStep(
                "stamina",
                "Forlorn Hovel",
                "Unlock the hovel at Scosglen’s eastern tip. Kill its cows; keep the Stamina Potion."
            )
        ),
    ),
    GuideSection(
        "bardiche",
        "Rusted Bardiche",
        "02 / Vessel of Hatred · Nahantu",
        listOf(
            GuideStep(
                "gemstone",
                "Jabbering Gemstone",
                "At the graves south of Kurast Docks, use Thanks in order: middle, middle-left, middle-right."
            ),
            GuideStep(
                "staff",
                "Crooked Staff",
                "Reset skills, remove skill-rank gear and dismiss mercenaries. Defeat Blood Lightning east of the Den using the default Attack."
            ),
            GuideStep(
                "bell",
                "Rusted Old Bell",
                "Visit all 12 Five Hills bird statues in the walkthrough’s mapped order, starting east of Samuk; return to the first."
            ),
            GuideStep(
                "corrupt",
                "Corrupt the relics",
                "Group three Cordycepic Zombies north of Ichorfall; kill them together. Drop the relics into their mushrooms; retrieve them after corruption."
            ),
            GuideStep(
                "unusual_key",
                "Unusual Key",
                "Offer the corrupted relics at Oka’bo Temple’s three braziers, southeast of Kurast Bazaar."
            ),
            GuideStep(
                "bardiche",
                "Forlorn Burrow",
                "Take the key to this cellar west of Kurast Bazaar. Break the hidden wall; kill the cows and keep the bardiche."
            )
        ),
    ),
    GuideSection(
        "hand",
        "Neyrelle’s Hand",
        "03 / Lord of Hatred · Skovos",
        listOf(
            GuideStep(
                "pride",
                "Pride: begin",
                "At Kyovashad’s Holy Cedar shrine, burn Pride. Read the Outcast’s Journal north of Fool’s Quarry in Skovos."
            ),
            GuideStep(
                "bolt",
                "Amazonian Bolt",
                "Destroy ballistas around Akarat’s Bulwark for a bolt. Load it into the ballista inside Idyllic Reach; cross the rope."
            ),
            GuideStep(
                "chains",
                "Father’s Chains",
                "Activate the Inarius statue; defeat N’Facl. Burn his chains at Antia’s Flame."
            ),
            GuideStep(
                "anger",
                "Anger: training armor",
                "Burn Anger in Kyovashad; reread the journal. Attack Temis’s training dummy for the Amazon helm; equip it."
            ),
            GuideStep(
                "armor",
                "Complete the Amazon set",
                "Farm Corrupted Amazons near Athulua’s Observatory. Equip tunic, pants, gloves and boots as they drop."
            ),
            GuideStep(
                "rose",
                "Mother’s Rose",
                "Wearing all five pieces, defeat Vasha the Unbroken. Burn the rose at the Temple of Courage."
            ),
            GuideStep(
                "fear",
                "Fear: Hierophant’s Skull",
                "Burn Fear; reread the journal. Dismiss mercenaries. Follow the crow in Lycander’s Ruins of Broken Reason without attacking or losing it."
            ),
            GuideStep(
                "deep_key",
                "Hadopelagic Key",
                "Burn the skull at the Temple of Life. After all three offerings, collect the key."
            ),
            GuideStep(
                "greed",
                "Greed: the coffin",
                "Burn Greed. With fishing unlocked, fish off the tip of Creaking Hulls in northeast Hawezar. Unlock the coffin with the key."
            ),
            GuideStep(
                "hand",
                "Retrieve Neyrelle’s Hand",
                "Enter the coffin’s dungeon. At its far end, interact with the Bloated Calf to collect the hand."
            )
        ),
    ),
    GuideSection(
        "portal",
        "Open the secret cow level",
        "04 / Cube · Scylara · Portal",
        listOf(
            GuideStep(
                "cube",
                "Combine the three rewards",
                "In Temis’s Horadric Cube, transmute Stamina Potion + Rusted Bardiche + Neyrelle’s Hand using the ??? recipe."
            ),
            GuideStep(
                "page",
                "Open Trophy of the Faithful",
                "Open the cache for the Torn Page and Moo emote."
            ),
            GuideStep(
                "boat",
                "Sail to Scylara",
                "With the Torn Page, take the Outcast’s Boat on eastern Philios’s shore in Skovos. Enter the island’s Forlorn Cellar."
            ),
            GuideStep(
                "portal",
                "Open the portal",
                "On Tuesday, kill the cows in the cellar’s ritual circle and enter the portal. If they are invulnerable, check the walkthrough’s current timing notes."
            ),
            GuideStep(
                "king",
                "Defeat the Cow King",
                "Clear bovines until the king appears; defeat him and collect his crown."
            )
        ),
    )
)

val guideStepIds: Set<String> = unlockGuide.flatMap { it.steps }.map { it.id }.toSet()

class GuideStore(context: Context) {
    // Separate from cow_hunts_v1: checking or resetting the guide never changes counters.
    private val prefs = context.getSharedPreferences("cow_guide_v1", Context.MODE_PRIVATE)
    fun load(): Set<String> = prefs.getStringSet("completed_steps", emptySet())
        .orEmpty().intersect(guideStepIds)

    fun save(completed: Set<String>) {
        prefs.edit { putStringSet("completed_steps", completed.intersect(guideStepIds)) }
    }
}
