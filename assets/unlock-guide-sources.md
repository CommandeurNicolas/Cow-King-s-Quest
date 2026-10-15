# Unlock guide imagery and verification

Checked October 14, 2026. Images are bundled in `app/src/main/res/drawable-nodpi` for offline use.
The source screenshots and annotations are unchanged except for the documented brightness adjustment to the Old Tristram map; the app fits them in previews and provides a
compact zoom/pan viewer. Individual URLs, resource filenames, titles, and captions are recorded
in [unlock-guide-images.json](unlock-guide-images.json).

Maps and screenshots:
RubyRose / [VULKK’s complete cow-level walkthrough](https://vulkk.com/2026/05/07/the-complete-guide-to-diablo-4s-secret-cow-level/)
and [Nahantu walkthrough](https://vulkk.com/2024/10/16/secrets-of-diablo-4-the-secret-cow-level-continues/).
Diablo IV imagery belongs to Blizzard Entertainment. Source credit appears in each gallery and the
enlarged viewer. Third-party artwork is excluded from the repository’s MIT license.

The two additional Forlorn Burrow jump/passage screenshots are from Sliver of time’s gameplay,
reproduced
in [Nerd’s Chalk’s pre-expansion item walkthrough](https://nerdschalk.com/diablo-4-how-to-obtain-all-cow-level-items-before-lord-of-hatred-locations-requirements-and-items-guide/),
steps 21–22. Their separate source credit is shown in the app. There are 43 bundled quest and Bell reference images in
total.

## Findings and limitations

- **Crooked Staff:** The Uacax scroll is a lore clue available after clearing Kichuk. Neither VULKK
  nor [DiabloFilter](https://diablofilter.com/guide/the-ultimate-secret-cow-level-guide) establishes
  reading it as the event that spawns Blood Lightning.
  A [player’s successful follow-up](https://www.reddit.com/r/diablo4/comments/1tm1vsa/kichuk_stronghold_cow_level_farming/)
  reports obtaining the staff by walking from Ichorfall without clearing Kichuk. The guide therefore
  includes the requested stronghold and scroll instructions as optional preparation rather than an
  asserted spawn prerequisite. The kill uses the default Attack with no allocated skills or
  gear/talisman skill ranks, and no other damage sources.
- **Zombie respawns:** VULKK’s Nahantu walkthrough and DiabloFilter explicitly describe immediate
  respawns after accidental kills while clearing nearby enemies. The app explains clearing the route
  first, returning to the marked spawns, then gathering all three without passive damage. It does
  not promise a fixed respawn timer.
- **Journal visits:** The complete VULKK walkthrough distinguishes the required Pride visit from
  recommended visits for Fear and Anger. The shared reference is above all Neyrelle's Hand steps;
  the individual sin instructions still remind the player to return.
- **Everyday access:** VULKK’s “The Power of Moo” section
  and [Save Point Syndicate’s Moo skip instructions](https://savepointsyndicate.com/how-to-unlock-diablo-4s-secret-cow-level-full-quest-and-moo-emote-skip/)
  report an everyday journal portal even on characters that have not completed the relic chain. A
  qualified player’s nearby Moo grants the hidden sin; inscribing/burning Moo is distinct from
  earning the Moo emote through Trophy of the Faithful. These sources do not support a mandatory
  Neyrelle’s Hand completion requirement for the recipient.

## Farming setup and Bell of the Bovine — October 14, 2026

- [Wowhead's cow-level guide](https://www.wowhead.com/diablo-4/guide/zones/secret-cow-level)
  and [VULKK's complete walkthrough](https://vulkk.com/2026/05/07/the-complete-guide-to-diablo-4s-secret-cow-level/)
  support one first guaranteed relic after 666 cow kills per character, then random drops
  in the remaining relic regions on that character. No drop-rate estimate or speed comparison
  is presented as a guarantee. Region choices include both of each relic's valid alternatives.
- [DiabloFilter's Bell farming guide](https://diablofilter.com/guide/the-fastest-way-to-farm-666-cows-in-diablo-4)
  reports that exploding summoned cows contribute to the hidden counter. Lucky Hit can trigger
  a Moo, and Lucky Moo can summon an exploding cow. Attacks are not equivalent to cow kills.
  The app therefore tracks relic pickups without exposing a counter in Bell mode.
- [Icy Veins' Butcher/Prime Rib guide](https://www.icy-veins.com/d4/news/easily-unlock-the-bovine-sanctuary-without-the-secret-cow-level-in-diablo-4-season-15/)
  describes accepting the Wounded Townsman's quest in Old Tristram and killing the Butcher
  for five Prime Ribs, providing a route before unlocking the cow level.
- [VULKK's Sanctum walkthrough](https://vulkk.com/2026/09/17/secrets-of-diablo-4-bovine-sanctum-and-moophisto-the-divine-bovine/)
  describes the Cow King rib source and the two altars: the mainland crossing preserves the ribs;
  the island arena consumes five to summon Moophisto. The Bell is not a guaranteed drop.
  [Blizzard's October 2 hotfix](https://us.forums.blizzard.com/en/d4/t/hotfix-1-october-2-2026-322/270741)
  confirms a fix to Moophisto's loot. No numerical Bell drop rate is assumed.
- **Author's in-game verification:** the Bell can be shared through the common stash.
  Each receiving character must be level 70 to equip it. The UI explicitly applies this
  requirement to all three characters when using that strategy.
- The shared relic checklist is independent of manual counts and the unlock-guide checklist.
  Bell summons are never estimated or converted into recorded kills when changing methods.

## Bell instruction images

Six screenshots are bundled for the Bell help in Counter and Farming setup. Their
original pixels are unchanged except for the Old Tristram map’s baked brightness adjustment. The shared guide gallery and zoom viewer display the
location images; the item screenshot uses a static card. Both show captions and source
credits without requiring a network connection.

- Butcher quest map: screenshot captured and annotated by the app author, captured on October 15, 2026. A brightened PNG is bundled and shown before the Wounded Townsman screenshot. The unmodified source is retained at `assets/source/old-tristram_butcher-quest_location.png`. Its orange arrow and circle identify the blue quest marker; the map opens in the shared zoom viewer.
- Wounded Townsman outside the Cathedral in northern Old Tristram: Icy Veins' Butcher/Prime Rib guide.
- Mainland Altar of the Heifer map east of Tyrael's Encampment: RubyRose / VULKK's Bovine Sanctum guide.
- Mainland altar appearance: Icy Veins' Butcher/Prime Rib guide.
- Northern arena summoning altar: RubyRose / VULKK's Bovine Sanctum guide.
- Bell of the Bovine item description: screenshot from [Icy Veins' Bell farming article](https://www.icy-veins.com/d4/news/skip-the-cow-grind-and-unlock-the-secret-sheep-level-in-diablo-4-season-15/). Its caption explains that item stats may vary.

The captions distinguish the mainland crossing from the arena altar that consumes five
Prime Ribs. Public source URLs, user-supplied image provenance and resource names are in `unlock-guide-images.json`.
