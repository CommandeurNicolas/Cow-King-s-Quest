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
    val optional: Boolean = false,
    val images: List<GuideImage> = emptyList(),
    val checkable: Boolean = true
)

data class GuideSection(
    val id: String,
    val title: String,
    val subtitle: String,
    val steps: List<GuideStep>,
    val description: String? = null,
    val reference: GuideReference? = null
)

data class GuideReference(val title: String, val detail: String, val images: List<GuideImage>)

/** Offline checklist and location references. IDs remain stable for saved progress. */
val unlockGuide = listOf(
    GuideSection(
        "potion",
        "Stamina Potion",
        "01 / Base game",
        listOf(
            GuideStep(
                "shard",
                "Bloody Wooden Shard",
                "Farm in Hawezar or Kehjistan. Each character’s first relic drops at 666 cow kills; subsequent relics on that character are random. Pick up the shard."
            ),
            GuideStep(
                "tome",
                "Musty Tome",
                "Farm in Scosglen or Fractured Peaks. Use a different character’s first 666 kills for a guaranteed first drop, or keep farming with your original character for a random drop. Collect the tome."
            ),
            GuideStep(
                "fragment",
                "Intricate Metallic Fragment",
                "Farm in Dry Steppes. Use a third character’s first 666 kills for a guaranteed first drop, or keep farming with your original character for a random drop. Collect the fragment."
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
                "Unlock the hovel at Scosglen’s eastern tip. Kill its cows; keep the Stamina Potion.",
                images = guideImages("hovel_map")
            )
        ),
        description = "Choose your character strategy and cow source in Counter → Farming setup. World cows and Bell of the Bovine summons both contribute to the hidden cow-kill progress. The Bell requires level 70 to equip and can be shared through the common stash; the app tracks its relic pickups without counting summons. Farm in the correct region when a relic may drop.",
    ),
    GuideSection(
        "bardiche",
        "Rusted Bardiche",
        "02 / Vessel of Hatred · Nahantu",
        listOf(
            GuideStep(
                "gemstone",
                "Jabbering Gemstone",
                "At the graves south of Kurast Docks, use Thanks in order: middle, middle-left, middle-right.",
                images = guideImages("graves_map", "graves")
            ),
            GuideStep(
                "staff",
                "Crooked Staff",
                "1. For the lore clue, clear Kichuk stronghold and read ‘The Triumph of Lord Uacax’ scroll. This is optional preparation: players report obtaining the staff by approaching from Ichorfall without clearing Kichuk. Reading the scroll is not confirmed as a requirement to spawn Blood Lightning.\n\n2. Refund every skill point and remove all gear and talismans that grant skill ranks. Dismiss mercenaries and disable thorns, summons and other passive damage.\n\n3. Go southwest of Kichuk and kill Blood Lightning using only the default Attack, then collect the Crooked Staff. A skill from the Basic category does not count.",
                images = guideImages("kichuk_map", "scroll", "lightning_map")
            ),
            GuideStep(
                "bell",
                "Rusted Old Bell",
                "Start east of Samuk. Follow the numbered map through all 12 Five Hills bird statues, getting close to each one, then return to statue 1 to collect the bell.",
                images = guideImages("samuk_map", "birds_route")
            ),
            GuideStep(
                "corrupt",
                "Corrupt the relics",
                "Clear the route around the three marked spawn points north of Ichorfall first. The Cordycepic Zombies can respawn before the other enemies, so return to their spawn points after clearing everything.\n\nRemove passive damage and summons, then gather all three zombies while keeping them on screen. Kill them together and drop the relics into their mushrooms.\n\nBefore picking them up, take a few seconds to check for purple smoke around each dropped relic. This confirms corruption: only corrupted relics can be offered at Oka’bo Temple’s braziers.",
                images = guideImages("zombies_map")
            ),
            GuideStep(
                "unusual_key",
                "Unusual Key",
                "Offer the corrupted relics at Oka’bo Temple’s three braziers, southeast of Kurast Bazaar.",
                images = guideImages("temple_map", "temple_braziers")
            ),
            GuideStep(
                "bardiche",
                "Forlorn Burrow",
                "Take the Unusual Key to this cellar west of Kurast Bazaar. Break the hidden wall, cross the passage behind it, then kill the cows and keep the bardiche.",
                images = guideImages("burrow_map", "burrow_wall", "burrow_jump", "burrow_passage")
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
                groupTitle = "Fear",
                images = guideImages("crow_map", "crow_route", "life_map", "life_brazier")
            ),
            GuideStep(
                "anger",
                "Get the Amazon Training Helm",
                "Burn Anger at the Kyovashad shrine; reread the journal. Attack the training dummy in Temis until the Amazon Training Helm drops, then equip it.",
                groupTitle = "Anger",
                images = guideImages("dummy_map")
            ),
            GuideStep(
                "armor",
                "Complete the Amazon set",
                "Farm Corrupted Amazons near Athulua’s Observatory. Equip tunic, pants, gloves and boots as they drop.",
                groupTitle = "Anger",
                images = guideImages("amazons_map")
            ),
            GuideStep(
                "rose",
                "Collect and offer Mother’s Rose",
                "With all five Amazon pieces equipped, defeat Vasha the Unbroken and pick up Mother’s Rose. Burn it at the Temple of Courage brazier.",
                groupTitle = "Anger",
                images = guideImages("vasha_map", "courage_map", "courage_brazier")
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
                groupTitle = "Pride",
                images = guideImages("ballistas_map", "idyllic_map")
            ),
            GuideStep(
                "chains",
                "Collect and offer Father’s Chains",
                "Activate the Inarius statue to summon N’Facl, the Jailer of Angels. Defeat him, pick up Father’s Chains, then burn them at Antia’s Flame.",
                groupTitle = "Pride",
                images = guideImages("antia_map", "antia_brazier")
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
                groupTitle = "Greed",
                images = guideImages("coffin_map", "coffin_spot")
            ),
            GuideStep(
                "hand",
                "Retrieve Neyrelle’s Hand",
                "Enter the coffin’s dungeon. At its far end, interact with the Bloated Calf to collect the hand.",
                groupTitle = "Greed"
            )
        ),
        reference = GuideReference(
            "Sin route · Outcast’s Journal",
            """
                Complete Fear, Anger and Pride in any order. For each, inscribe the word on a Holy Cedar Tablet at Kyovashad’s entrance and burn it at the shrine, then follow its steps to collect and offer the relic.

                Return to the Outcast’s Journal in Skartara, north of Fool’s Quarry, after changing sins. Reading it is required for Pride’s Amazonian Bolt and recommended for Fear and Anger. Greed has a clue here too.

                After all three offerings, collect the Hadopelagic Key, burn Greed at the shrine and follow the final steps to retrieve Neyrelle’s Hand.
            """.trimIndent(),
            guideImages("cedar_map", "cedar", "journal_map", "journal")
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
                "In Temis’s Horadric Cube, transmute Stamina Potion + Rusted Bardiche + Neyrelle’s Hand using the ??? recipe.",
                groupTitle = "Reach Scylara · Tuesday entrance"
            ),
            GuideStep(
                "page",
                "Open Trophy of the Faithful",
                "Open the cache for the Torn Page and Moo emote.",
                groupTitle = "Reach Scylara · Tuesday entrance"
            ),
            GuideStep(
                "boat",
                "Sail to Scylara",
                "With the Torn Page, take the Outcast’s Boat on eastern Philios’s shore in Skovos. Enter the island’s Forlorn Cellar for the Tuesday cow-level entrance.",
                groupTitle = "Reach Scylara · Tuesday entrance",
                images = guideImages("boat")
            ),
            GuideStep(
                "vivid_whimsy",
                "Catch Vivid Whimsy",
                "On Scylara, fish in the rainbow-colored pool beside the horse skeleton near the dock to catch the Vivid Whimsy Mythic Fish.",
                groupTitle = "Reach Scylara · Tuesday entrance",
                optional = true,
                images = guideImages("vivid_whimsy_pool")
            ),
            GuideStep(
                "scylara_entrance",
                "Open the Scylara portal",
                "On Tuesday, kill the cows in the Forlorn Cellar’s ritual circle on Scylara and enter the portal.\n\nIf the cows are invulnerable, make sure you inscribed Greed on a Holy Cedar Tablet, burned it, then visited the Outcast’s Journal. Inscribing and burning Moo to unlock the any-day portal makes the Scylara entrance unavailable, even on Tuesday. Either switch back to Greed and repeat the steps above, or use the any-day portal in the Outcast’s Journal chamber.",
                groupTitle = "Reach Scylara · Tuesday entrance",
                checkable = false
            ),
            GuideStep(
                "moo_tablet",
                "1. Burn Moo at the Holy Cedar Tablets",
                "Return to the tablets outside Kyovashad, inscribe Moo and burn it. If Moo is missing, have a player who has completed the cow level or has Moo selected use their Moo emote near you. This route does not require a prior visit to Scylara.",
                groupTitle = "Enter on any day · Moo portal",
                images = guideImages("cedar"),
                checkable = false
            ),
            GuideStep(
                "moo_journal",
                "2. Enter through the Outcast’s Journal alcove",
                "Go to the journal chamber north of Fool’s Quarry and take the portal. Community guides report that players who receive Moo can use it without completing the relic quests or Neyrelle’s Hand. This does not award your own Moo emote; that comes from Trophy of the Faithful. Older reports disagree about shortcut access, so check that the portal appears after you burn Moo.",
                groupTitle = "Enter on any day · Moo portal",
                images = guideImages("journal_map", "moo_portal"),
                checkable = false
            ),
            GuideStep(
                "portal",
                "Enter the secret cow level",
                "Take either entrance above, then check off this step. You do not need to visit Scylara before using the Moo portal."
            ),
            GuideStep(
                "king",
                "Defeat the Cow King",
                "Clear bovines until the king appears; defeat him and collect his crown."
            )
        )
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
                optional = true,
                images = guideImages("squirt_peddler", "squirt_peddler_map", "travel_pouch_recipe", "travel_pouch_transmuted")
            )
        ),
    )
)

val guideStepIds: Set<String> = unlockGuide.flatMap { it.steps }
    .filter { it.checkable }.map { it.id }.toSet()
val requiredGuideStepIds: Set<String> = unlockGuide.flatMap { it.steps }
    .filter { it.checkable && !it.optional }.map { it.id }.toSet()

class GuideStore(context: Context) {
    // Separate from cow_hunts_v1: checking or resetting the guide never changes counters.
    private val prefs = context.getSharedPreferences("cow_guide_v1", Context.MODE_PRIVATE)
    fun load(): Set<String> = prefs.getStringSet("completed_steps", emptySet())
        .orEmpty().intersect(guideStepIds)

    fun save(completed: Set<String>) {
        prefs.edit { putStringSet("completed_steps", completed.intersect(guideStepIds)) }
    }
}
