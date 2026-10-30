package com.nicolascommandeur.cowkingsquest.data.guide

import com.nicolascommandeur.cowkingsquest.model.guide.GuideSourceLink

const val COMMUNITY_GUIDE_URL = "https://diablofilter.com/guide/the-ultimate-secret-cow-level-guide"

// Image publishers first, in descending image-count order; written sources follow.
// Detailed article links and attribution: assets/unlock-guide-sources.md and README.md.
internal val guideSources = listOf(
    GuideSourceLink(
        "vulkk",
        "VULKK",
        "https://vulkk.com/2026/05/07/the-complete-guide-to-diablo-4s-secret-cow-level/"
    ),
    GuideSourceLink(
        "diablofilter",
        "DiabloFilter",
        COMMUNITY_GUIDE_URL
    ),
    GuideSourceLink(
        "icy_veins",
        "Icy Veins",
        "https://www.icy-veins.com/d4/news/easily-unlock-the-bovine-sanctuary-without-the-secret-cow-level-in-diablo-4-season-15/"
    ),
    GuideSourceLink(
        "nerds_chalk",
        "Nerd’s Chalk",
        "https://nerdschalk.com/diablo-4-how-to-obtain-all-cow-level-items-before-lord-of-hatred-locations-requirements-and-items-guide/"
    ),
    GuideSourceLink(
        "wowhead",
        "Wowhead",
        "https://www.wowhead.com/diablo-4/guide/zones/secret-cow-level"
    ),
    GuideSourceLink(
        "games_fuze",
        "Games Fuze",
        "https://gamesfuze.com/guides/cow-level-full-guide-lord-of-hatred-in-diablo-4/"
    ),
    GuideSourceLink(
        "save_point_syndicate",
        "Save Point Syndicate",
        "https://savepointsyndicate.com/how-to-unlock-diablo-4s-secret-cow-level-full-quest-and-moo-emote-skip/"
    )
)

internal val crookedStaffReport = GuideSourceLink(
    "crooked_staff_report",
    "Crooked Staff player report · Reddit",
    "https://www.reddit.com/r/diablo4/comments/1tm1vsa/kichuk_stronghold_cow_level_farming/"
)
