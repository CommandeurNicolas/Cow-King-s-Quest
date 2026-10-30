# Unlock guide sources and image attribution

I use the walkthroughs, images, and community reports listed here for Cow King’s Quest. This document also explains how the guide handles uncertain requirements. It describes the bundled content; online sources are not fetched or updated automatically.

## Source walkthroughs

### VULKK — RubyRose

- [Complete cow-level walkthrough][vulkk-complete]: the full relic chain, Skovos sin quests, portal entrances, Vivid Whimsy, and Outcast’s Travel Pouch. Supplies quest maps and screenshots, including Squirt the Peddler, his Temis location, the pouch recipe, and the resulting pouch.
- [Nahantu walkthrough][vulkk-nahantu]: relic puzzles, bird statue route, zombie corruption, Oka’bo Temple, and Forlorn Burrow imagery and instructions.
- [Bovine Sanctum and Moophisto][vulkk-sanctum]: Cow King Prime Ribs, the mainland crossing, the arena summoning altar, and Bell acquisition. Supplies the mainland altar map and arena altar screenshot.

### DiabloFilter — Rizarjay

- [Full cow-level walkthrough][diablofilter-complete]: relic regions, quest instructions, and the Farobru, Cerrigar, and Zarbinzet farming maps and yield estimates.
- [Bell of the Bovine farming][diablofilter-bell]: summoned exploding cows contributing to the hidden cow counter and farming enemies in the target relic’s region.

### Icy Veins

- [Butcher quest and Prime Ribs][icy-butcher]: obtaining five Prime Ribs before unlocking the cow level and reaching the Bovine Sanctum. Supplies the Wounded Townsman and mainland altar screenshots.
- [Bell of the Bovine farming][icy-bell]: Bell farming information and the Bell item screenshot. Item stats may vary.

### Nerd’s Chalk — Sliver of time gameplay

The [pre-expansion item walkthrough][nerds-chalk] reproduces screenshots from **Sliver of time’s YouTube gameplay**. The app uses two Forlorn Burrow screenshots from steps 21–22 for the jump point and hidden passage. Credits identify both the original gameplay creator and the publishing site.

### Other written sources

- [Wowhead — relic farming][wowhead]: the first guaranteed relic, later random drops, region alternatives, and the reported 666,666-second farming window. Its coverage concerns the earlier quest stages.
- [Games Fuze — Skovos quest sequence][games-fuze]: Neyrelle’s Hand sin quests and the final cube/portal sequence.
- [Save Point Syndicate — Moo shortcut][save-point]: everyday journal-portal access after receiving Moo, including access without completing the relic chain.
- [Reddit — Crooked Staff player report][staff-report]: a player’s successful Ichorfall approach without clearing Kichuk. This is a community report rather than a confirmed spawn rule.
- [Blizzard — Moophisto loot hotfix][blizzard-hotfix]: supporting verification of a loot fix. It does not establish a numerical Bell drop rate.

## Image inventory

Images are bundled in [`app/src/main/res/drawable-nodpi`](../app/src/main/res/drawable-nodpi) for offline use. The [image manifest](unlock-guide-images.json) covers every file in that directory: guide and Bell images, farming maps, and the animated cow sprite sheet. The [farming map reference](farming-map-sources.md) and [artwork reference](hell-bovine-source.md) explain how those assets are used.

| Provider | Guide and Bell images | Farming maps | Animation sprite sheets | Total |
| --- | ---: | ---: | ---: | ---: |
| RubyRose / VULKK | 41 | 0 | 0 | 41 |
| Rizarjay / DiabloFilter | 0 | 3 | 0 | 3 |
| Icy Veins | 3 | 0 | 0 | 3 |
| Sliver of time / Nerd’s Chalk | 2 | 0 | 0 | 2 |
| My screenshot | 1 | 0 | 0 | 1 |
| Blizzard / napalm22 / The Spriters Resource | 0 | 0 | 1 | 1 |
| **Total** | **47** | **3** | **1** | **51** |

Counts represent unique bundled files. An image reused in several steps counts once; individual animation frames share one sprite sheet entry. Launcher icons, interface vectors, and shapes drawn in Compose are outside this content-image inventory.

### Manifest fields

The manifest is a JSON array used for documentation; the app defines its image content in Kotlin. Every entry has an `id`, `title`, `caption`, `filename`, `source`, and `category`.

| Field | Meaning |
| --- | --- |
| `id` | Unique image identifier; guide IDs match `GuideImages.kt`. |
| `filename` | Bundled filename relative to `app/src/main/res/drawable-nodpi`. |
| `category` | `guide` for quest and Bell references, `farming-map` for route maps, or `animation` for the cow sprite sheet. |
| `title`, `caption` | Image subject and context; guide values match the app’s gallery metadata. |
| `source` | Publisher or creator credit. |
| `url` | Original image or sprite-sheet URL; omitted for my local screenshot. |
| `sourcePage` | Article or asset page supplying an externally sourced image. |
| `originalFile` | Repository-relative path to a retained local original, where available. |
| `modifications` | Processing applied to a bundled image, such as brightness adjustment or sprite extraction. |
| `providedBy` | Personal capture or annotation details, where applicable. |
| `description` | Accessibility description for a farming map. |
| `sourceDocument` | Supporting artwork document, relative to `assets`. |

When adding or replacing an image, update its manifest entry alongside the resource and Kotlin metadata. Keep IDs and filenames unique, retain the original source URL, and document any image processing.

### Bell reference images

| Resource | Subject | Provider |
| --- | --- | --- |
| `guide_bell_quest_map.png` | Old Tristram Butcher quest location | My screenshot |
| `guide_bell_townsman.jpg` | Wounded Townsman outside the Cathedral | [Icy Veins][icy-butcher] |
| `guide_bell_altar_map.jpg` | Mainland Altar of the Heifer location | [RubyRose / VULKK][vulkk-sanctum] |
| `guide_bell_altar.jpg` | Mainland altar appearance | [Icy Veins][icy-butcher] |
| `guide_bell_summon_altar.jpg` | Northern arena summoning altar | [RubyRose / VULKK][vulkk-sanctum] |
| `guide_bell_item.png` | Bell of the Bovine item description | [Icy Veins][icy-bell] |

I captured and annotated the Old Tristram map. My orange arrow and circle identify the blue quest marker. I brightened the bundled image for readability and keep the [unmodified original](source/old-tristram_butcher-quest_location.png) separately. Other bundled screenshots retain their source pixels and annotations. Preview cropping and rounded corners are applied by the interface.

### Attribution in the app

Every guide image, Bell reference, and farming map displays its own source credit beneath the image. Enlarged viewers also display the credit. The **Online guides & sources** card links to each publisher’s primary walkthrough; the Crooked Staff report is linked beside the relevant instructions. Supporting article links are listed above.

The animated cow’s attribution is documented in [artwork credits and provenance](hell-bovine-source.md), without an on-screen caption.

## Guide qualifications

### Relic farming

[Wowhead][wowhead] and [VULKK][vulkk-complete] describe one guaranteed first relic at 666 cow kills per character, followed by random drops in the remaining relic regions. The guide includes both valid region alternatives for the shard and tome. It does not guarantee drop-rate estimates or comparative farming speeds.

Wowhead reports a 666,666-second window from the first cow. The app attributes this timer to Wowhead and does not track it or expire saved counts automatically.

### Nahantu relics

**Crooked Staff:** Clearing Kichuk and reading the Uacax scroll provide a lore clue, but are presented as optional preparation. The [player report][staff-report] describes obtaining the staff by approaching from Ichorfall without clearing the stronghold. Reading the scroll is not established as a spawn prerequisite. The instructions distinguish the default Attack from Basic-category skills and require removing allocated skills, gear or talisman skill ranks, mercenaries, thorns, summons, and other damage sources.

**Zombie corruption:** [VULKK’s Nahantu guide][vulkk-nahantu] and [DiabloFilter][diablofilter-complete] describe zombies respawning after accidental kills while clearing nearby enemies. The instructions recommend clearing the route, returning to the marked spawns, gathering all three zombies without passive damage, and checking for purple smoke on the dropped relics. No fixed respawn timer is promised.

### Journal visits and portal access

[VULKK’s complete walkthrough][vulkk-complete] distinguishes the required journal visit for Pride’s Amazonian Bolt from recommended visits for Fear and Anger. The shared journal reference and individual sin instructions reflect that distinction.

VULKK’s “The Power of Moo” section and [Save Point Syndicate][save-point] report everyday portal access for characters that receive Moo without completing the relic chain or Neyrelle’s Hand. Receiving the Moo sin and burning it on a Holy Cedar Tablet are distinct from earning the Moo emote through Trophy of the Faithful. Community reports differ, so the instructions ask players to check that the portal appears after burning Moo.

### Bell farming and Prime Ribs

[DiabloFilter][diablofilter-bell] reports that summoned exploding cows advance the game’s hidden cow counter. An attack is not equivalent to a cow kill; the app tracks relic pickups in Bell mode without estimating summons or converting them into manual counts.

[Icy Veins][icy-butcher] describes accepting the Wounded Townsman’s quest and killing the Butcher for five Prime Ribs. [VULKK][vulkk-sanctum] describes the Cow King rib source and the two altars: the mainland crossing preserves five ribs, while the northern arena altar consumes them to summon Moophisto. The Bell is a random drop, with no numerical drop rate assumed.

I verified in game that the Bell can be transferred through the common stash and requires level 70 to equip. That requirement applies to every receiving character, including all three characters in that farming strategy.

## Ownership and licensing

Diablo game imagery belongs to Blizzard Entertainment. Publisher and creator credits identify the sources of screenshots and annotations. Third-party game artwork is excluded from the repository’s [MIT license](../LICENSE). Cow King’s Quest is unofficial and is not affiliated with Blizzard.

[vulkk-complete]: https://vulkk.com/2026/05/07/the-complete-guide-to-diablo-4s-secret-cow-level/
[vulkk-nahantu]: https://vulkk.com/2024/10/16/secrets-of-diablo-4-the-secret-cow-level-continues/
[vulkk-sanctum]: https://vulkk.com/2026/09/17/secrets-of-diablo-4-bovine-sanctum-and-moophisto-the-divine-bovine/
[diablofilter-complete]: https://diablofilter.com/guide/the-ultimate-secret-cow-level-guide
[diablofilter-bell]: https://diablofilter.com/guide/the-fastest-way-to-farm-666-cows-in-diablo-4
[icy-butcher]: https://www.icy-veins.com/d4/news/easily-unlock-the-bovine-sanctuary-without-the-secret-cow-level-in-diablo-4-season-15/
[icy-bell]: https://www.icy-veins.com/d4/news/skip-the-cow-grind-and-unlock-the-secret-sheep-level-in-diablo-4-season-15/
[nerds-chalk]: https://nerdschalk.com/diablo-4-how-to-obtain-all-cow-level-items-before-lord-of-hatred-locations-requirements-and-items-guide/
[wowhead]: https://www.wowhead.com/diablo-4/guide/zones/secret-cow-level
[games-fuze]: https://gamesfuze.com/guides/cow-level-full-guide-lord-of-hatred-in-diablo-4/
[save-point]: https://savepointsyndicate.com/how-to-unlock-diablo-4s-secret-cow-level-full-quest-and-moo-emote-skip/
[staff-report]: https://www.reddit.com/r/diablo4/comments/1tm1vsa/kichuk_stronghold_cow_level_farming/
[blizzard-hotfix]: https://us.forums.blizzard.com/en/d4/t/hotfix-1-october-2-2026-322/270741
