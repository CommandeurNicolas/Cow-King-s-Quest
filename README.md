# Cow King’s Quest

An unofficial, offline Android companion for Diablo IV’s secret cow level. Choose a farming method, track world-cow kills and relic pickups, follow the unlock checklist, and keep farming maps beside your game.

<img src="assets/cow-king-icon.png" alt="Cow King’s Quest crowned cow icon" width="128">

**Version 1 · Android 10+ (API 29) · Kotlin / Jetpack Compose / Material 3**

## Features

- A **Farming setup** sheet with independent character-strategy and cow-source choices.
- Three named character slots for guaranteed first drops, or one selected character for continued RNG farming.
- Bell of the Bovine farming with relic pickup tracking and no cow-counting controls.
- World-cow **+1**, **+5**, and **+10** buttons, one-step undo, and manual correction; one-character RNG totals continue beyond 666.
- Before the first drop, an early zone reminder at **600**, plus a stop at **665** with a separate confirmation for the final cow.
- A shared three-relic checklist, independent of manual counts, with target-zone selection during one-character farming.
- A **27-step unlock checklist plus two optional steps**, with saved checkmarks and collapsible sections.
- Offline quest maps and screenshots beside the unlock steps, plus farming maps for Farobru, Cerrigar, and Zarbinzet.
- Compact, rounded map viewers with pinch zoom, bounded panning, and **Reset zoom**.
- A charcoal-and-gold interface, crowned cow launcher icon, and matching dark splash screen.
- Locally saved progress and an optional **Keep screen awake** setting while the counter is open.
- A little surprise might be waiting when you launch the app.

## Using the app

### Choose a farming setup

Open **Counter → Farming setup / Change**. Choose both settings, then **Save setup**:

| Setting | Choices |
| --- | --- |
| Character strategy | • **Three characters**: each character's first 666 cow kills targets a different relic.<br>• **One character**: first relic at 666, then random drops in the remaining regions. |
| Cow source | • **World cows**: follow maps and record kills.<br>• **Bell of the Bovine**: generate cows by hitting enemies and confirm relic pickups without counting summons. |

When choosing one character, select which saved character to continue with. Returning to three characters restores access to all three slots. Changing either setting preserves every character's name, count, undo history, first-drop history, and the shared relic checklist.

### World-cow counting

1. Select a character card in three-character mode, or use the character chosen in Farming setup. Tap **Edit** to change its name.
2. Record actual kills with **+1**, **+5**, or **+10**. **Undo** reverses the last count change, including an entire batch.
3. Before the first drop, additions stop at 665. Travel to the target region by **600 recorded kills** to account for missed kills. The first time an addition or correction crosses 600, the app shows a reminder.
4. After making the final kill in the correct region, tap **Record final cow**, then **Record cow #666**.
5. Confirm the item under **Your relics** when you pick it up. Collection can be recorded at any count, since manual totals can be inaccurate. Reaching 666 never automatically checks a relic.
6. In one-character mode, counting continues beyond 666 or after an actual relic pickup is confirmed. Choose the next target beneath the relic checklist; its zones appear above it. Further drops are random, without a new guarantee at multiples of 666.

| Default character slot | Target relic | Valid relic regions |
| --- | --- | --- |
| Shard | Bloody Wooden Shard | Hawezar / Kehjistan |
| Tome | Musty Tome | Scosglen / Fractured Peaks |
| Fragment | Intricate Metallic Fragment | Dry Steppes |

Either listed region works. **Correct count** accepts 0–666 before the first-drop threshold, and larger non-negative totals during one-character RNG farming. Corrections and undo leave confirmed relic pickups intact. Unchecking a relic reverses its recorded pickup for the character that obtained it. If that character has no other confirmed pickups and its count is below 666, first-drop counting becomes available again.

**Reset this character** asks for confirmation, then clears only that character's count, undo history, and first-drop history. Its name, the other characters, and collected relics remain saved. Use this when replacing a slot with a fresh in-game character; resetting the app cannot reset the game's hidden progress. Uncheck individual relics to clear collection confirmations.

### Bell of the Bovine farming

Bell mode shows the active character, target regions and shared relic checklist. It has no count display, kill buttons, undo, count correction, progress percentage, or final-cow confirmation. Farm in the region of the desired relic and confirm each pickup. Both character strategies work with the Bell.

The amulet can be transferred through the common stash. **Every character must be level 70 to equip it**, including the receiving alts in a three-character strategy. These restrictions were verified in game by the app author.

Expand **How to get and use the Bell** for three spaced instruction sections and offline location images. The Old Tristram gallery starts with the app author’s annotated quest map, brightened for readability on dark screens, followed by the Wounded Townsman screenshot. Tap the quest or altar screenshots to open the shared zoom viewer; swipe through the altar map, mainland crossing and arena summon references. A static, uncropped Bell item screenshot appears after Equip and farm in its full vertical proportions, with Icy Veins credit and a note that item stats may vary. The two Prime Rib routes are listed separately: obtain five Prime Ribs from the Wounded Townsman's Butcher quest in Old Tristram, or from repeated Cow King kills if the cow level is already accessible. With five ribs, the mainland Altar of the Heifer east of Tyrael's Encampment in Skovos transports you to the Bovine Sanctum without consuming them. Unlock its waypoint. The northern arena altar consumes five ribs to summon Moophisto; the Bell is a random drop, so one summon is not guaranteed to provide it.

Bell summons are never recorded by the app. Switching back to world cows restores the saved manual total, which may be lower than actual in-game kills. Confirm relic pickups whenever they happen.

### Follow the unlock guide

The **Unlock guide** tab covers four required stages and an optional final stage:

1. Stamina Potion — the original three relics and Forlorn Hovel.
2. Rusted Bardiche — the Nahantu relics and Forlorn Burrow.
3. Neyrelle’s Hand — the Skovos sin quests.
4. The portal and Cow King — combining the rewards, optionally catching Vivid Whimsy on Scylara, and entering the level.
5. *Optional:* Outcast’s Travel Pouch — combine the Torn Page with 3 fished Rusted Cow Bells in the Horadric Cube to unlock purchases from Squirt the Peddler in Temis for future seasons.

The progress bar tracks the 27 required steps. Leaving either optional step unchecked still counts as completing the hunt. A purchased pouch supplies the three final rewards, so return to **Combine the three rewards** to skip the relic quests in a new season.

Tap a step to check or uncheck it, and tap a section header to collapse or expand it. This is one shared checklist for the journey. Its checkmarks are independent of the counter’s **Relic collected** checkboxes. **Reset checklist** asks for confirmation and leaves character progress untouched.

Swipe through the location images inside each illustrated step card, then tap an image to open the enlarged viewer. Completed steps collapse their images behind a **Show images** button; unfinished steps and shared reference images stay visible. Pinch to zoom, drag to pan, or use the zoom buttons. Previous and Next browse the images for that step. Images are bundled for offline use, and opening them never checks off a step.

The Outcast’s Journal reference appears above the Neyrelle’s Hand steps because you return there between sins. Crooked Staff includes Kichuk and scroll preparation, the default-Attack restrictions, and a note that the scroll is not a confirmed spawn requirement. Inside **Open the secret cow level**, the Scylara route includes an optional Vivid Whimsy fishing step before its Tuesday entrance. The titled Moo route follows with its own instruction cards for everyday access. The shared portal checkmark applies to either entrance.

The guide includes expansion prerequisites and source links to Wowhead, VULKK, and DiabloFilter.

### Find a farming route

Tap **Where to farm cows · Maps & routes** from either tab. Each route card includes an annotated map, brief directions, and an estimated number of cows per lap. The active character’s target zones appear above the cards, and matching routes are marked. The route reminder follows the selected strategy, including continued RNG farming. Bell mode shows an explanation that these maps cover world cows when opened from the guide.

Tap an image or **Enlarge map** for the enlarged viewer. Maps are bundled with the app and work offline. Their rounded corners are drawn by the UI; the source images are unchanged. Route estimates never add kills automatically.

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

The app stores character names, counts, undo history, first-drop history, a shared relic checklist, farming setup, selected characters and target, and the screen-awake preference on the device. Existing three-character progress migrates automatically. Checklist completion uses separate storage.

This is a manual companion: it does not connect to Diablo IV, detect kills, verify drops, or track the in-game relic timer. The guide describes the community-reported farming window, but there is no countdown or automatic expiry. Game updates may change the route or unlock requirements.

Counters, checklists, and bundled maps need no network connection. External guide links open in a browser and require internet access. There is no app-managed account or cloud sync.

To keep progress when updating, install a build with the same application ID and signing key over the existing app. Do not uninstall or clear app data first.

The application ID and source namespace are `com.nicolascommandeur.diablo4cowcompanion`. Builds installed before this package rename are treated as a separate app; their saved progress is not automatically migrated.

## Guide sources and artwork

Farming strategies and Bell/Prime Rib instructions were checked on **October 14, 2026**. The original bundled guide and routes were researched on **October 5, 2026**. These links provide the source walkthroughs; they are not fetched or updated automatically by the app.
The optional Season 15 pouch shortcut was checked against VULKK’s complete walkthrough on **October 12, 2026**. Location imagery, Crooked Staff preparation, zombie respawns, and alternate portal access were checked on **October 13, 2026**. See [unlock guide sources](assets/unlock-guide-sources.md) for findings, conflicting reports, and image provenance.

- [Wowhead — cow level guide](https://www.wowhead.com/diablo-4/guide/zones/secret-cow-level). At the time of review, its article covered the earlier stages rather than the final unlock.
- [VULKK — Nahantu relics and maps](https://vulkk.com/2024/10/16/secrets-of-diablo-4-the-secret-cow-level-continues/).
- [VULKK — complete unlock walkthrough](https://vulkk.com/2026/05/07/the-complete-guide-to-diablo-4s-secret-cow-level/).
- [DiabloFilter — Bell farming](https://diablofilter.com/guide/the-fastest-way-to-farm-666-cows-in-diablo-4).
- [Icy Veins — Butcher quest and five Prime Ribs](https://www.icy-veins.com/d4/news/easily-unlock-the-bovine-sanctuary-without-the-secret-cow-level-in-diablo-4-season-15/).
- [VULKK — Bovine Sanctum and Moophisto](https://vulkk.com/2026/09/17/secrets-of-diablo-4-bovine-sanctum-and-moophisto-the-divine-bovine/).
- [DiabloFilter — full route and farming maps](https://diablofilter.com/guide/the-ultimate-secret-cow-level-guide).
- [Games Fuze — Skovos quest sequence](https://gamesfuze.com/guides/cow-level-full-guide-lord-of-hatred-in-diablo-4/).

Quest and farming screenshots are credited in the app. Their original URLs and attribution are recorded in [farming map sources](assets/farming-map-sources.md). Diablo IV game imagery belongs to Blizzard Entertainment. Cow King’s Quest is an unofficial project and is not affiliated with Blizzard.

Additional game artwork is credited to Blizzard Entertainment, with extraction by
napalm22. See [artwork credits and provenance (contains spoilers)](assets/hell-bovine-source.md).
Third-party game artwork is excluded from the repository's MIT license.
