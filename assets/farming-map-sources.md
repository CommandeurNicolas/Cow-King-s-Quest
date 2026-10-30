# Farming route maps

Cow King’s Quest bundles maps for Farobru, Cerrigar, and Zarbinzet for offline reference while farming world cows. I use the maps, route summaries, and yield estimates from [The Ultimate Secret Cow Level Guide](https://diablofilter.com/guide/the-ultimate-secret-cow-level-guide), by **Rizarjay on DiabloFilter**.

## Map inventory

| Route | Region | Bundled resource | Source image |
| --- | --- | --- | --- |
| Farobru | Dry Steppes | [route_farobru.webp](../app/src/main/res/drawable-nodpi/route_farobru.webp) | [Original map](https://diablofilter.com/uploads/guides/2_1778234779_126813f77b1e64f8.webp) |
| Cerrigar | Scosglen | [route_cerrigar.webp](../app/src/main/res/drawable-nodpi/route_cerrigar.webp) | [Original map](https://diablofilter.com/uploads/guides/2_1778235084_10c2a2e0b8792322.webp) |
| Zarbinzet | Hawezar | [route_zarbinzet.webp](../app/src/main/res/drawable-nodpi/route_zarbinzet.webp) | [Original map](https://diablofilter.com/uploads/guides/2_1778235319_4964bdcd9f4599a4.webp) |

The [image manifest](unlock-guide-images.json) records these resources under the `farming-map` category, including their source URLs, route instructions, and accessibility descriptions. [FarmingRoutes.kt](../app/src/main/java/com/nicolascommandeur/diablo4cowcompanion/FarmingRoutes.kt) defines the routes displayed by the app.

## Display and use

The bundled images retain their original pixels and yellow route annotations. Rounded corners, zoom, and panning are applied by the interface. Each map displays a DiabloFilter / Rizarjay credit beneath it, including in the enlarged viewer.

Cow counts per lap are estimates rather than guaranteed spawns. Viewing a map or completing a route does not add kills automatically. The maps show world-cow routes; Bell of the Bovine farming uses enemies in the target relic’s region instead.

## Ownership and licensing

Diablo IV game imagery belongs to Blizzard Entertainment. Route-map credit: Rizarjay / DiabloFilter. These third-party images are excluded from the repository’s [MIT license](../LICENSE). See [guide sources](unlock-guide-sources.md) for the wider quest walkthrough and attribution reference.
