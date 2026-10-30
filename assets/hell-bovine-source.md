# Hell Bovine artwork

The animated cow visitor uses Diablo II / Lord of Destruction artwork owned by Blizzard Entertainment. **napalm22** extracted and submitted the source sprite sheet to The Spriters Resource.

## Attribution

- [Source asset page](https://www.spriters-resource.com/pc_computer/diablo2diablo2lordofdestruction/asset/54312/)
- [Original sprite sheet](https://www.spriters-resource.com/media/assets/51/54312.gif?updated=1755473106)
- [Bundled animation sprite sheet](../app/src/main/res/drawable-nodpi/hell_bovine_walk.png)
- [Image manifest](unlock-guide-images.json): the `hell_bovine_walk` entry records the source and processing details.

## Sprite sheet layout

| Property | Value |
| --- | --- |
| Original sheet dimensions | 4978 × 5755 pixels |
| Walk grid origin in the source sheet | (0, 4548) |
| Frame dimensions | 157 × 151 pixels |
| Frames per direction | 8 |
| Sprite sheet dimensions | 1256 × 453 pixels |
| Sprite sheet rows, top to bottom | Forward (direction 0), right (direction 6), away (direction 4) |

I assembled the bundled sprite sheet from three walk cycles without resizing the frames and converted the source sheet’s flat `#AAAAAA` background to transparency. Only the animation sprite sheet is bundled; the full source sheet and audio are not included.

## Rendering

[CowVisitor.kt](../app/src/main/java/com/nicolascommandeur/cowkingsquest/CowVisitor.kt) selects frames from the sprite sheet and renders them with nearest-neighbor sampling. Movement advances at 10 frames per second; pauses hold a forward-facing frame. Attribution is provided in this document rather than beneath the animated visitor.

The [README](../README.md#preview-the-rare-visitor) explains how to preview the visitor with a debug build.

## Ownership and licensing

Original game artwork © Blizzard Entertainment. Sprite extraction and submission credit: napalm22 / The Spriters Resource. This third-party artwork is excluded from the repository’s [MIT license](../LICENSE).
