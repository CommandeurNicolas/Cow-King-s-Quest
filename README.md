# Cow King’s Quest

An unofficial, offline Android companion for Diablo IV’s secret cow level. Track cow kills across three characters, follow the unlock checklist, and keep farming maps beside your game.

<img src="assets/cow-king-icon.png" alt="Cow King’s Quest crowned cow icon" width="128">

**Version 1 · Android 10+ (API 29) · Kotlin / Jetpack Compose / Material 3**

## Features

- Three named character counters, each assigned to a different relic.
- Quick **+1**, **+5**, and **+10** buttons, one-step undo, and manual count correction.
- A stop at **665** with a separate confirmation for the final cow.
- A **27-step unlock checklist** with saved checkmarks and collapsible sections.
- Offline farming maps for Farobru, Cerrigar, and Zarbinzet, with directions and estimated yields.
- Full-screen maps with pinch zoom, bounded panning, and **Reset zoom**.
- A charcoal-and-gold interface, crowned cow launcher icon, and matching dark splash screen.
- Locally saved progress and an optional **Keep screen awake** setting while the counter is open.
- A little surprise might be waiting when you launch the app.

## Using the app

### Count cows

1. Open **Counter** and select a character card.
2. Tap **Edit**, enter a name, and choose **Save name**. The assigned relic and all valid final-kill zones are shown as read-only information.
3. Record actual kills with **+1**, **+5**, or **+10**. **Undo** reverses the last count change for that character, including an entire batch. **Correct count** accepts a total from 0 to 666.
4. At **665**, travel to a valid zone below. Count buttons stop at 665 even when a batch would cross it.
5. After making the final kill, tap **Record final cow** and confirm with **Record cow #666**.
6. Check **Relic collected** after picking up the item in game. Reaching 666 does not check it automatically.

| Character slot | Target relic | Valid final-kill zones |
| --- | --- | --- |
| Shard | Bloody Wooden Shard | Hawezar / Kehjistan |
| Tome | Musty Tome | Scosglen / Fractured Peaks |
| Fragment | Intricate Metallic Fragment | Dry Steppes |

Either listed zone works for its relic. Earlier kills can be farmed elsewhere. **Reset this character** asks for confirmation and clears only that character’s count, undo history, and collected status; its name and the other characters remain intact.

### Follow the unlock guide

The **Unlock guide** tab covers four stages:

1. Stamina Potion — the original three relics and Forlorn Hovel.
2. Rusted Bardiche — the Nahantu relics and Forlorn Burrow.
3. Neyrelle’s Hand — the Skovos sin quests.
4. The portal and Cow King — combining the rewards and entering the level.

Tap a step to check or uncheck it, and tap a section header to collapse or expand it. This is one shared checklist for the journey. Its checkmarks are independent of the counter’s **Relic collected** checkboxes. **Reset checklist** asks for confirmation and leaves character progress untouched.

The guide includes expansion prerequisites and links to Wowhead, VULKK, and DiabloFilter for detailed instructions and maps.

### Find a farming route

Tap **Where to farm cows · Maps & routes** from either tab. Each route card includes an annotated map, brief directions, and an estimated number of cows per lap. The current character’s valid final-kill zones appear above the cards, and matching routes are marked.

Tap an image or **Enlarge map** for the full-screen viewer. Maps are bundled with the app and work offline. Their rounded corners are drawn by the UI; the source images are unchanged. Route estimates never add kills automatically.

### A rare visitor

Keep an eye out when you open the app. Some secrets are best discovered for yourself.

<details>
<summary>Trigger a preview (contains spoilers)</summary>

With a **debug build** installed and a device or emulator connected through ADB,
run these commands to preview the surprise without waiting for a lucky launch:

```sh
adb shell am force-stop com.nicolascommandeur.diablo4cowcompanion
adb shell am start -n com.nicolascommandeur.diablo4cowcompanion/.MainActivity --ez cow_visitor_preview true
```

The first command stops the app so the preview starts fresh; your saved progress
is preserved. The second launches it with the preview enabled. Release builds
ignore this option. To return to normal behavior, force-stop the app again and
open it from its launcher icon.

</details>

## Saved progress and scope

The app stores character names, counts, undo history, relic collection status, the selected character, and the screen-awake preference on the device. Checklist completion uses separate storage. Older single-zone preferences are ignored in favor of displaying all valid zones.

This is a manual companion: it does not connect to Diablo IV, detect kills, verify drops, or track the in-game relic timer. The guide describes the community-reported farming window, but there is no countdown or automatic expiry. Game updates may change the route or unlock requirements.

Counters, checklists, and bundled maps need no network connection. External guide links open in a browser and require internet access. There is no app-managed account or cloud sync.

To keep progress when updating, install a build with the same application ID and signing key over the existing app. Do not uninstall or clear app data first.

The application ID and source namespace are `com.nicolascommandeur.diablo4cowcompanion`. Builds installed before this package rename are treated as a separate app; their saved progress is not automatically migrated.

## Guide sources and artwork

The bundled guide and routes were researched on **October 5, 2026**. These links provide the source walkthroughs; they are not fetched or updated automatically by the app.

- [Wowhead — cow level guide](https://www.wowhead.com/diablo-4/guide/zones/secret-cow-level). At the time of review, its article covered the earlier stages rather than the final unlock.
- [VULKK — Nahantu relics and maps](https://vulkk.com/2024/10/16/secrets-of-diablo-4-the-secret-cow-level-continues/).
- [VULKK — complete unlock walkthrough](https://vulkk.com/2026/05/07/the-complete-guide-to-diablo-4s-secret-cow-level/).
- [DiabloFilter — full route and farming maps](https://diablofilter.com/guide/the-ultimate-secret-cow-level-guide).
- [Games Fuze — Skovos quest sequence](https://gamesfuze.com/guides/cow-level-full-guide-lord-of-hatred-in-diablo-4/).

Farming screenshots are credited in the app. Their original URLs and attribution are recorded in [farming map sources](assets/farming-map-sources.md). Diablo IV game imagery belongs to Blizzard Entertainment. Cow King’s Quest is an unofficial project and is not affiliated with Blizzard.

Additional game artwork is credited to Blizzard Entertainment, with extraction by
napalm22. See [artwork credits and provenance (contains spoilers)](assets/hell-bovine-source.md).
Third-party game artwork is excluded from the repository's MIT license.
