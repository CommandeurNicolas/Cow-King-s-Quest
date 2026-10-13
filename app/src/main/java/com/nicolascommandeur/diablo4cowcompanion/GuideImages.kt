package com.nicolascommandeur.diablo4cowcompanion

import androidx.annotation.DrawableRes

data class GuideImage(
    val id: String,
    val title: String,
    val caption: String,
    @param:DrawableRes val resource: Int,
    val source: String
)

// Original image URLs and attribution: assets/unlock-guide-images.json.
val unlockGuideImages = listOf(
    GuideImage(
        "hovel_map",
        "Forlorn Hovel · Scosglen",
        "Travel to the marked cellar at Scosglen’s eastern tip.",
        R.drawable.guide_hovel_map,
        "VULKK"
    ),
    GuideImage(
        "graves_map",
        "Graves · Kurast Docks",
        "The graves are just south of Kurast Docks.",
        R.drawable.guide_graves_map,
        "VULKK"
    ),
    GuideImage(
        "graves",
        "Thanks emote · grave order",
        "Use Thanks at the middle, middle-left, then middle-right grave.",
        R.drawable.guide_graves,
        "VULKK"
    ),
    GuideImage(
        "kichuk_map",
        "Kichuk · scroll location",
        "Clear the stronghold to reach the Uacax scroll marked here.",
        R.drawable.guide_kichuk_map,
        "VULKK"
    ),
    GuideImage(
        "scroll",
        "The Triumph of Lord Uacax",
        "The scroll is a clue; reading it is not confirmed as a spawn requirement.",
        R.drawable.guide_scroll,
        "VULKK"
    ),
    GuideImage(
        "lightning_map",
        "Blood Lightning · Nahantu",
        "Find the elite southwest of Kichuk; you can approach from Ichorfall.",
        R.drawable.guide_lightning_map,
        "VULKK"
    ),
    GuideImage(
        "samuk_map",
        "Samuk · Five Hills",
        "Use Samuk as your starting waypoint, then head east to statue 1.",
        R.drawable.guide_samuk_map,
        "VULKK"
    ),
    GuideImage(
        "birds_route",
        "The twelve bird statues",
        "Follow 1–12, then return to 1. Move right up to each statue.",
        R.drawable.guide_birds_route,
        "VULKK"
    ),
    GuideImage(
        "zombies_map",
        "Three Cordycepic Zombies",
        "The marked spawns are in the Field of Giants, north of Ichorfall.",
        R.drawable.guide_zombies_map,
        "VULKK"
    ),
    GuideImage(
        "temple_map",
        "Oka’bo Temple · Nahantu",
        "Travel southeast from Kurast Bazaar to the marked temple.",
        R.drawable.guide_temple_map,
        "VULKK"
    ),
    GuideImage(
        "temple_braziers",
        "Oka’bo Temple · three braziers",
        "Offer one corrupted relic at each brazier near the entrance.",
        R.drawable.guide_temple_braziers,
        "VULKK"
    ),
    GuideImage(
        "burrow_map",
        "Forlorn Burrow · Nahantu",
        "Enter the marked cellar west of Kurast Bazaar with the Unusual Key.",
        R.drawable.guide_burrow_map,
        "VULKK"
    ),
    GuideImage(
        "burrow_wall",
        "Forlorn Burrow · hidden wall",
        "Break this wall with the Unusual Key, then follow the passage into the cow chamber.",
        R.drawable.guide_burrow_wall,
        "VULKK"
    ),
    GuideImage(
        "journal_map",
        "Outcast’s Journal · Skartara",
        "Return to this nook north of Fool’s Quarry after changing sins.",
        R.drawable.guide_journal_map,
        "VULKK"
    ),
    GuideImage(
        "journal",
        "Inside the journal chamber",
        "Read the journal here; this is also the destination for the Moo portal.",
        R.drawable.guide_journal,
        "VULKK"
    ),
    GuideImage(
        "crow_map",
        "Crow · Ruins of Broken Reason",
        "Find the large crow east of the Xanderos waypoint.",
        R.drawable.guide_crow_map,
        "VULKK"
    ),
    GuideImage(
        "crow_route",
        "Follow the crow",
        "Stay close and wait while it circles. Follow the player route shown on the map.",
        R.drawable.guide_crow_route,
        "VULKK"
    ),
    GuideImage(
        "life_map",
        "Temple of Life · brazier location",
        "Bring the Hierophant’s Skull to the marked brazier in Lycander.",
        R.drawable.guide_life_map,
        "VULKK"
    ),
    GuideImage(
        "life_brazier",
        "Temple of Life · signal brazier",
        "Interact with this brazier to offer the skull.",
        R.drawable.guide_life_brazier,
        "VULKK"
    ),
    GuideImage(
        "dummy_map",
        "Training Grounds · Temis",
        "Enter Temis’s Training Grounds to find the dummy.",
        R.drawable.guide_dummy_map,
        "VULKK"
    ),
    GuideImage(
        "amazons_map",
        "Corrupted Amazons · Athalua",
        "Hunt around the observatory and equip each Amazon armor piece as it drops.",
        R.drawable.guide_amazons_map,
        "VULKK"
    ),
    GuideImage(
        "vasha_map",
        "Vasha the Unbroken",
        "Keep all five Amazon armor pieces equipped for this fight.",
        R.drawable.guide_vasha_map,
        "VULKK"
    ),
    GuideImage(
        "courage_map",
        "Temple of Courage · brazier location",
        "Bring Mother’s Rose to this marked location.",
        R.drawable.guide_courage_map,
        "VULKK"
    ),
    GuideImage(
        "courage_brazier",
        "Temple of Courage · signal brazier",
        "Interact with this brazier to offer the rose.",
        R.drawable.guide_courage_brazier,
        "VULKK"
    ),
    GuideImage(
        "ballistas_map",
        "Ballistas · Athalua",
        "After burning Pride and reading the journal, destroy a ballista here.",
        R.drawable.guide_ballistas_map,
        "VULKK"
    ),
    GuideImage(
        "idyllic_map",
        "Idyllic Reach · Philios",
        "Take the Amazonian Bolt to this cellar.",
        R.drawable.guide_idyllic_map,
        "VULKK"
    ),
    GuideImage(
        "antia_map",
        "Antia’s Flame · brazier location",
        "Bring Father’s Chains to the marked brazier in Philios.",
        R.drawable.guide_antia_map,
        "VULKK"
    ),
    GuideImage(
        "antia_brazier",
        "Antia’s Flame · signal brazier",
        "Interact with this brazier to offer the chains.",
        R.drawable.guide_antia_brazier,
        "VULKK"
    ),
    GuideImage(
        "coffin_map",
        "Creaking Hulls · Hawezar",
        "Travel to the end of the path at the Temple of the Deathspeaker.",
        R.drawable.guide_coffin_map,
        "VULKK"
    ),
    GuideImage(
        "coffin_spot",
        "Fish from the very tip",
        "Use this exact fishing position to retrieve the coffin.",
        R.drawable.guide_coffin_spot,
        "VULKK"
    ),
    GuideImage(
        "boat",
        "Outcast’s Boat · eastern Philios",
        "Carry the Torn Page and use the marked boat to sail to Scylara.",
        R.drawable.guide_boat,
        "VULKK"
    ),
    GuideImage(
        "cedar",
        "Holy Cedar Tablets · Kyovashad",
        "Return to the tablets at Kyovashad’s entrance to inscribe and burn Moo.",
        R.drawable.guide_cedar,
        "VULKK"
    ),
    GuideImage(
        "moo_portal",
        "Moo portal · journal chamber",
        "After burning Moo, return here to enter the secret cow level.",
        R.drawable.guide_moo_portal,
        "VULKK"
    ),
    GuideImage(
        "burrow_jump",
        "Behind the wall · jump point",
        "After breaking the wall, use the Jump prompt to enter the hidden passage.",
        R.drawable.guide_burrow_jump,
        "Sliver of time / Nerd’s Chalk"
    ),
    GuideImage(
        "burrow_passage",
        "Forlorn Burrow · hidden passage",
        "Cross the gap and follow the passage to the cow chamber.",
        R.drawable.guide_burrow_passage,
        "Sliver of time / Nerd’s Chalk"
    ),
).associateBy { it.id }

internal fun guideImages(vararg ids: String): List<GuideImage> =
    ids.map { unlockGuideImages.getValue(it) }
