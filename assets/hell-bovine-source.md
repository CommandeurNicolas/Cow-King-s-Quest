# Hell Bovine walking animation

Original Diablo II / Lord of Destruction game artwork © Blizzard Entertainment.
Sprite sheet ripped and submitted by **napalm22**, hosted by The Spriters Resource:

- Asset page: https://www.spriters-resource.com/pc_computer/diablo2diablo2lordofdestruction/asset/54312/
- Sheet: https://www.spriters-resource.com/media/assets/51/54312.gif?updated=1755473106
- Retrieved October 6, 2026. Original sheet: 4978 × 5755 pixels.

`app/src/main/res/drawable-nodpi/hell_bovine_walk.png` contains the original
eight-frame Walk cycles facing forward, right, and away. Each cell is 157 × 151 pixels.
The source Walk grid begins at (0, 4548); directions 0, 6, and 4 were extracted into
three rows without scaling or recoloring. The sheet's flat #AAAAAA background was
converted to transparency. No generated or redrawn character art is used.

The app renders the frames at 10 fps during movement, holds a forward frame
during the pause, and uses nearest-neighbor sampling to retain the sprite style.
Only this compact atlas is bundled; no sound is included.

This third-party game artwork is not covered by this repository's MIT license.
