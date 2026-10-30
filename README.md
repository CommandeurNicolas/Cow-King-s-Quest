# Cow King’s Quest

An unofficial, offline Android companion for Diablo IV’s secret cow level. Track cow kills and relic pickups, follow the unlock checklist, and consult quest screenshots and farming maps beside your game.

<img src="assets/cow-king-icon.png" alt="Cow King’s Quest crowned cow icon" width="128">

**Version 1 · Android 10+ (API 29) · Kotlin / Jetpack Compose / Material 3**

## Features

- World-cow counting with batch additions, undo, count correction, and reminders before the first relic drop.
- Three-character and one-character farming strategies, plus Bell of the Bovine pickup tracking.
- A shared relic checklist and a separate unlock guide with 27 required steps and two optional steps.
- Offline quest images and farming maps with source captions, zoom, and panning.
- Locally saved progress and an optional **Keep screen awake** setting for the counter.

## Using the app

### Choose a farming setup

Open **Counter → Farming setup / Change**, choose both settings, and tap **Save setup**.

| Setting | Choices |
| --- | --- |
| Character strategy | **Three characters:** target a different relic with each character’s first 666 kills. **One character:** obtain the first relic at 666, then continue farming for random drops. |
| Cow source | **World cows:** record kills manually. **Bell of the Bovine:** attack enemies to summon exploding cows and track relic pickups without a manual counter. |

For one-character farming, select which saved character to use. Changing setup preserves character names, counts, undo history, first-drop history, and collected relics.

### Count world cows

1. Select a character, or use the character chosen in Farming setup. Tap **Edit** to change its name.
2. Record kills with **+1**, **+5**, or **+10**. **Undo** reverses the last count change, including a batch.
3. Travel to the target region by **600 recorded kills** to allow for missed kills. The app shows a reminder when an addition or correction first crosses 600.
4. Before the first drop, additions stop at **665**. After the final kill in the correct region, tap **Record final cow**, then **Record cow #666**.
5. Check the item under **Your relics** after picking it up. Reaching 666 does not confirm collection automatically; pickups can be recorded at any count.
6. In one-character mode, counting continues beyond 666 or after a confirmed relic pickup. Choose the next target beneath the relic checklist and farm in its regions. Later drops are random, with no new guarantee at multiples of 666.

| Character slot | Target relic | Valid relic regions |
| --- | --- | --- |
| Shard | Bloody Wooden Shard | Hawezar / Kehjistan |
| Tome | Musty Tome | Scosglen / Fractured Peaks |
| Fragment | Intricate Metallic Fragment | Dry Steppes |

**Correct count** accepts 0–666 before the first-drop threshold and larger non-negative totals during one-character RNG farming. Corrections and undo preserve confirmed pickups. Unchecking a relic reverses its recorded pickup for the character that obtained it; first-drop counting becomes available again if that character has no other confirmed pickups and its count is below 666.

**Reset this character** clears that character’s count, undo history, and first-drop history after confirmation. Its name, the other characters, and collected relics remain saved. Use this when replacing a slot with a fresh in-game character. Resetting the app cannot reset the game’s hidden progress; uncheck individual relics to clear collection confirmations.

### Farm with the Bell of the Bovine

Bell mode shows the active character, target regions, and shared relic checklist. Farm enemies in the desired relic’s region and confirm each pickup. Both character strategies work with the Bell.

The Bell can be shared through the common stash. **Each character must be level 70 to equip it**, including alts used for a three-character strategy.

In Farming setup, expand **How to get and use the Bell** for instructions and offline reference images:

1. Obtain five Prime Ribs from the Wounded Townsman’s Butcher quest in Old Tristram, or from repeated Cow King kills if you already have cow-level access.
2. Use the mainland Altar of the Heifer east of Tyrael’s Encampment in Skovos. It transports you to the Bovine Sanctum without consuming the ribs. Activate the waypoint.
3. Use the northern arena altar to consume five ribs and summon Moophisto. The Bell is a random drop, so further attempts may require more ribs or another player’s summon.
4. Equip the Bell and attack enemies in the target relic’s region to trigger cow summons.

The app does not count or estimate Bell summons. Switching back to world cows restores the saved manual total, which may differ from the game’s hidden count.

### Follow the unlock guide

The **Unlock guide** tab covers:

1. **Stamina Potion:** the base-game relics and Forlorn Hovel.
2. **Rusted Bardiche:** the Nahantu relics and Forlorn Burrow.
3. **Neyrelle’s Hand:** the Skovos sin quests.
4. **Open the secret cow level:** combine the rewards, use the Tuesday Scylara entrance or everyday Moo portal, and defeat the Cow King. Catching Vivid Whimsy on Scylara is optional.
5. **Return in future seasons:** optionally combine the Torn Page with three fished Rusted Cow Bells in the Horadric Cube to craft the Outcast’s Travel Pouch and unlock purchases from Squirt the Peddler. A purchased pouch supplies the three final rewards; resume at **Combine the three rewards**.

The full quest route requires Vessel of Hatred, Lord of Hatred, access to Skovos, the Horadric Cube, and fishing. Make Common items visible in your loot filter.

Tap a checklist step to check or uncheck it. Section headers collapse or expand their contents. The progress bar tracks the 27 required steps; leaving either optional step unchecked still counts as completing the hunt. The guide’s checkmarks are independent of the counter’s relic checklist. **Reset checklist** clears only guide completion after confirmation.

Swipe through a step’s images and tap one to open the enlarged viewer. Pinch to zoom, drag to pan, or use the zoom controls; **Previous** and **Next** browse that step’s images. Completed steps hide their images behind **Show images**. Shared references, including the Outcast’s Journal, remain visible. Viewing images never checks off a step.

The **Online guides & sources** card links to the source walkthroughs. The Crooked Staff player report is linked beside that step’s instructions. See [guide sources](assets/unlock-guide-sources.md) for attribution and qualifications about disputed requirements.

### Find a farming route

Open **Where to farm cows · Maps & routes** from either tab. Farobru, Cerrigar, and Zarbinzet each have an annotated map, directions, and an estimated number of cows per lap. Matching routes are marked for the active character’s target regions. These maps cover world cows; Bell mode includes a reminder to farm enemies in the target region instead.

Tap a map or **Enlarge map** to zoom and pan. Maps work offline, and route estimates never add kills automatically.

## Saved progress and limitations

Character names, counts, undo history, first-drop history, relic pickups, farming setup, selected targets, guide checkmarks, and the screen-awake preference are saved on the device. There is no app-managed account or cloud sync.

The app does not connect to Diablo IV, detect kills, verify drops, or track the game’s hidden relic timer. Counts and checkmarks are manual. Game updates may change quest requirements, and community sources can disagree; see [guide qualifications](assets/unlock-guide-sources.md#guide-qualifications).

Counters, checklists, and bundled images work offline. External guide links open in a browser and require internet access.

To preserve progress when updating, install over the existing app. Do not uninstall or clear app data first.

## Build and test

Use Android Studio or the included Gradle wrapper. The project uses JDK 25 and Android SDK 37. Configure the Android SDK location through Android Studio or `sdk.dir` in your local `local.properties` file.

```sh
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

With an Android device or emulator connected, run the instrumented tests:

```sh
./gradlew :app:connectedDebugAndroidTest
```

### Preview the rare visitor

Some launch behavior is intended to be discovered while using the app.

<details>
<summary>Debug preview (contains spoilers)</summary>

With a debug build installed and a device or emulator connected through ADB:

```sh
adb shell am force-stop com.nicolascommandeur.cowkingsquest
adb shell am start -n com.nicolascommandeur.cowkingsquest/.MainActivity --ez cow_visitor_preview true
```

These commands restart the app with the visitor preview enabled while preserving saved progress. Release builds ignore the preview option. To return to normal behavior, force-stop the app and open it from its launcher icon.

</details>

## Sources and licensing

- [Guide sources and image attribution](assets/unlock-guide-sources.md): quest walkthroughs, source qualifications, and image inventory.
- [Bundled image manifest](assets/unlock-guide-images.json): guide images, farming maps, and the animated cow sprite sheet, with source URLs, filenames, captions, and credits.
- [Farming map sources](assets/farming-map-sources.md): route-map URLs and attribution.
- [Additional artwork credits (contains spoilers)](assets/hell-bovine-source.md): animated cow provenance.

The source code is available under the [MIT license](LICENSE). Third-party game artwork is excluded from that license. Diablo game imagery belongs to Blizzard Entertainment. Cow King’s Quest is an unofficial project and is not affiliated with Blizzard.
