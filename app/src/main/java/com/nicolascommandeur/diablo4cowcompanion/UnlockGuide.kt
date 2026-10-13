package com.nicolascommandeur.diablo4cowcompanion

import android.content.Context
import androidx.core.content.edit

const val WOWHEAD_GUIDE_URL = "https://www.wowhead.com/diablo-4/guide/zones/secret-cow-level"
const val COMPLETE_GUIDE_URL =
    "https://vulkk.com/2026/05/07/the-complete-guide-to-diablo-4s-secret-cow-level/"
const val COMMUNITY_GUIDE_URL = "https://diablofilter.com/guide/the-ultimate-secret-cow-level-guide"

data class GuideStep(
    val id: String,
    val title: String,
    val detail: String,
    val groupTitle: String? = null,
    val optional: Boolean = false
)

data class GuideSection(
    val id: String,
    val title: String,
    val subtitle: String,
    val steps: List<GuideStep>,
    val description: String? = null
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
                "fear",
                "Follow the crow and offer the skull",
                "Burn Fear at Kyovashad’s Holy Cedar shrine. Read the Outcast’s Journal north of Fool’s Quarry in Skovos. Dismiss mercenaries, then interact with and follow the crow in Lycander’s Ruins of Broken Reason without attacking or losing it. Pick up the Hierophant’s Skull and burn it at the Temple of Life brazier.",
                groupTitle = "Fear"
            ),
            GuideStep(
                "anger",
                "Get the Amazon Training Helm",
                "Burn Anger at the Kyovashad shrine; reread the journal. Attack the training dummy in Temis until the Amazon Training Helm drops, then equip it.",
                groupTitle = "Anger"
            ),
            GuideStep(
                "armor",
                "Complete the Amazon set",
                "Farm Corrupted Amazons near Athulua’s Observatory. Equip tunic, pants, gloves and boots as they drop.",
                groupTitle = "Anger"
            ),
            GuideStep(
                "rose",
                "Collect and offer Mother’s Rose",
                "With all five Amazon pieces equipped, defeat Vasha the Unbroken and pick up Mother’s Rose. Burn it at the Temple of Courage brazier.",
                groupTitle = "Anger"
            ),
            GuideStep(
                "pride",
                "Burn the sin and read the journal",
                "Burn Pride at the Kyovashad shrine, then reread the Outcast’s Journal. Reading the journal is required for the Amazonian Bolt step.",
                groupTitle = "Pride"
            ),
            GuideStep(
                "bolt",
                "Amazonian Bolt and Idyllic Reach",
                "Destroy ballistas around Akarat’s Bulwark and pick up an Amazonian Bolt. Enter the Idyllic Reach cellar, load and fire its unloaded ballista, then cross the rope into the hidden area.",
                groupTitle = "Pride"
            ),
            GuideStep(
                "chains",
                "Collect and offer Father’s Chains",
                "Activate the Inarius statue to summon N’Facl, the Jailer of Angels. Defeat him, pick up Father’s Chains, then burn them at Antia’s Flame.",
                groupTitle = "Pride"
            ),
            GuideStep(
                "deep_key",
                "Hadopelagic Key",
                "After the skull, rose and chains have all been burned, collect the key dropped by the final brazier. In this order, that is Antia’s Flame."
            ),
            GuideStep(
                "greed",
                "Fish up the coffin",
                "Burn Greed at the Kyovashad shrine before fishing for the coffin. With fishing unlocked, fish from the very tip of Creaking Hulls in northeast Hawezar. Unlock the Locked Wooden Coffin with the Hadopelagic Key.",
                groupTitle = "Greed"
            ),
            GuideStep(
                "hand",
                "Retrieve Neyrelle’s Hand",
                "Enter the coffin’s dungeon. At its far end, interact with the Bloated Calf to collect the hand.",
                groupTitle = "Greed"
            )
        ),
        description = """
            Fear, Anger and Pride can be completed in any order. For each word, inscribe it on a Holy Cedar Tablet at Kyovashad’s entrance and burn it, then visit the chamber in Skartara to read the Outcast’s Journal. Follow the matching steps to collect and offer its relic, then repeat for the other two words. After all three offerings, collect the Hadopelagic Key, burn Greed at the shrine and follow the final steps to retrieve Neyrelle’s Hand.
        """.trimIndent(),
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
    ),
    GuideSection(
        "future_seasons",
        "Return in future seasons",
        "05 / Optional · Season 15 onward",
        listOf(
            GuideStep(
                "travel_pouch",
                "Craft the Outcast’s Travel Pouch",
                "In the Horadric Cube, combine the Torn Page from Trophy of the Faithful with 3 Rusted Cow Bells obtained by fishing. Crafting the pouch once unlocks future purchases from Squirt the Peddler in Temis, including in new seasons. Open a purchased pouch for the Stamina Potion, Rusted Bardiche and Neyrelle’s Hand, then resume at ‘Combine the three rewards’ to return without repeating the relic quests.\n\nYou can also give the pouch’s Stamina Potion and Rusted Bardiche to friends so they can skip the first two sections; they still need to obtain their own Neyrelle’s Hand, combine the three rewards and open Trophy of the Faithful to unlock the Moo emote.",
                optional = true
            )
        ),
    )
)

val guideStepIds: Set<String> = unlockGuide.flatMap { it.steps }.map { it.id }.toSet()
val requiredGuideStepIds: Set<String> = unlockGuide.flatMap { it.steps }
    .filterNot { it.optional }.map { it.id }.toSet()

class GuideStore(context: Context) {
    // Separate from cow_hunts_v1: checking or resetting the guide never changes counters.
    private val prefs = context.getSharedPreferences("cow_guide_v1", Context.MODE_PRIVATE)
    fun load(): Set<String> = prefs.getStringSet("completed_steps", emptySet())
        .orEmpty().intersect(guideStepIds)

    fun save(completed: Set<String>) {
        prefs.edit { putStringSet("completed_steps", completed.intersect(guideStepIds)) }
    }
}
