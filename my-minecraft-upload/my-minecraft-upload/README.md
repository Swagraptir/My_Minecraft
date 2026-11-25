# My Minecraft (Fabric, Minecraft 26.3)

One mod with everything: **800+ furniture blocks (bathroom, kitchen, living room, bedroom, closet, basement, outdoors, pool, yard)**, **79 mobs** (incl. every mob-vote loser except the Great Hunger), **bicycle + car**, **illager overhaul**,
**Iceologer + Frost Golems + Ice Castle boss**, **medieval villages**, **walled medieval towns with knights and archers**,
**haunted pillager outposts** and **churches with an Illusioner**.

> Status: this is complete source code, but I could not compile it here (no Minecraft jars / internet in my sandbox).
> It is written against the 26.x unobfuscated (Mojang-name) API. Expect to fix a few compile errors on first build
> (renamed classes/methods between 26.x versions). Send me the error list and I will fix them.

## Build
1. Install **Java 25** and **Gradle 9.x** (or copy `gradlew`, `gradlew.bat` and `gradle/wrapper/` from the `26.x` branch of
   https://github.com/FabricMC/fabric-example-mod into this folder).
2. `gradle build`  ->  jar in `build/libs/my-minecraft-1.0.0.jar`
3. Put the jar in your Modrinth profile's `mods` folder (needs Fabric Loader 0.19.3+ and Fabric API for 26.3).
   Test with `gradle runClient` first.

If Gradle complains about a version: `gradle.properties` holds `loom_version`, `loader_version`, `fabric_api_version`
(look them up on https://fabricmc.net/develop).

## What is in the game
| Content | How to use it |
|---|---|
| Furniture (creative tab "My Minecraft") | Sit on chairs/sofas/benches/toilets/swings (right-click). Wardrobes, fridge, dressers, desks, counters etc. open a 27-slot inventory. Lamps toggle with right-click. Fireplace and aquarium glow. Wood furniture comes in oak/spruce/birch/dark oak, cloth furniture in 6 colours. Pool: `pool_tile`, `pool_tile_white`, ladder, diving board. |
| Bicycle / Car | Place the item, right-click to ride, WASD to drive (car steers where you look, sprint = faster), sneak + right-click to pick it up. |
| Animals (spawn eggs + natural spawning) | capybara, brown/black bear, lion, tiger, gorilla, red panda, raccoon, hedgehog, squirrel, kangaroo, elephant, giraffe, zebra, rhino, hippo, bison, deer, moose, crocodile, eagle, owl, flamingo, penguin. Breed with their food; predators hunt chickens/rabbits. |
| Illagers | Pillagers kite away from you and see farther; vindicators/evokers are tougher. |
| Iceologer | Throws fans of ice shards, summons Frost Golems. Spawns in snowy biomes. |
| Ice Castle | Snowy biomes. 200 HP boss "Iceologer Warlord" on the throne, loot chests. `/locate structure myminecraft:ice_castle` |
| Medieval Village | Plains/forest/taiga. Timber houses furnished with this mod's furniture, church (Illusioner inside), farms, market, knights + archers. |
| Medieval Town | Walled town with gate, 4 towers, keep, church, patrols of knights and archers on the walls. |
| Wildfire Castle | Nether: lava-moat blackstone fortress, 4 towers, keep, boss, blazes, wither skeletons, bastion loot. `/locate structure myminecraft:wildfire_castle` |
| Iceologer Cabin / Illusioner Shack | small hideouts, see above |
| Haunted Outpost | Dark tower with cobwebs, soul lanterns, ravager pen, pillagers, vindicators and an evoker. |

## Mob vote losers (all official ones that are not already in vanilla)
| Mob | Where | Behaviour |
|---|---|---|
| Iceologer (2020) | snowy biomes + Ice Castle | ice shards, summons Frost Golems, boss version |
| Moobloom (2020) | flower forest, meadow | plants buttercups; shear it for buttercup flowers |
| Glare (2021) | lush caves | flies to dark spots and glows to warn you; likes glow berries |
| Rascal (2022) | dark caves, whole overworld | hide and seek: find it 3 times for a reward |
| Tuff Golem (2022) | caves on tuff | holds an item you give it, freezes into a statue at times |
| Crab (2023) | beach, mangrove swamp, stony shore | drops Crab Claw: +2 block reach in hand |
| Penguin (2023) | snowy beach/plains | |
| Meerkat, Ostrich, Vulture (older votes) | desert/savanna/badlands | |
| Wildfire | Nether wastes / crimson forest + **Wildfire Castle** | floating fire spirit with orbiting rods and horns; fireball fans, fire rings; 300 HP Warlord boss in the castle throne room |
| Barnacle | deep ocean floors + inside **ocean monuments** | stationary shell creature with waving tentacles that reel in and squeeze swimmers |
Not included: Copper Golem (vanilla now) and the Great Hunger (you asked to leave it out).
Species do not overlap: caves are split (Glare = lush caves, Rascal = dripstone caves, Tuff Golem = only on tuff), every other animal has its own biome set and none duplicates a vanilla mob.
The Tuff Golem does not visibly show its held item yet.

## Biome vote losers (2018 Biome Chooser: Taiga won; 2019: Mountains won)
Already in vanilla: Fox, Goat, Frog, Tadpole, mangrove swamp, powder snow, cactus flower. What this mod adds from the losing ideas
(content only - no new terrain biomes, so it works with Tectonic / TerraBlender / World Weaver):
| Idea | Where | What you get |
|---|---|---|
| Desert: Meerkat | desert | Meerkat + hidden **Meerkat Burrow** (sand shaft, chamber, loot) |
| Savanna: Ostrich, Termites, Baobab | savanna | Ostrich, Termites, **Termite Mound**, huge walk-in **Baobab** tree with a chest |
| Badlands: Vulture, Tumbleweed | badlands, desert | Vulture, rolling Tumbleweed (flowery cactus is vanilla now) |
| Swamp: Fireflies | swamp, mangrove swamp | glowing Fireflies (switched off automatically if Naturalist is installed) |

## Inspired by Nekoma / Nekoma's Fixed / Friends&Foes / Lost Features
(All code and art here is original; only the ideas come from those projects and Nekoma's videos.)
* **Clam** (warm oceans): right-click to open/close; open it and use sand on it to get a **Clam Pearl**.
* **Underwater Torch**: lights only while it is under water.
* **Totem of Freezing**: use it to freeze everything within 9 blocks (16 uses).
* **Iceologer** also summons falling packed ice (the "Chillager" idea).
* **Iceologer Cabin** (snowy biomes) and **Illusioner Shack** (dark forest, swamps): small hideouts with the mob inside.
Not done (too big to do safely without testing): turtle armour set, ocean illager patrols, nautilus block, melon block.

## Compatibility (config/myminecraft.json is created on first start)
* **Naturalist**: deer, bears, lion, zebra, elephant, giraffe, rhino, hippo, vulture, duck, boar and crocodile stop spawning
  naturally (their spawn eggs still work). **Macaw's Furniture**: overlapping furniture (chairs, tables, desks, wardrobes...)
  is hidden from the creative tab and its recipes are disabled. **Nekoma's Fixed**: Clam and Underwater Torch are disabled.
  Set `"autoCompat": false` to turn this off, or list your own `disabledSpawns` / `hiddenItems`.
* Structures use vanilla/convention biome tags, so they also generate in biomes from Tectonic, TerraBlender, World Weaver and
  Incendium, and keep 5 chunks away from vanilla villages/outposts.
* Sodium, Iris, Lithostitched, Jade, JEI, Xaero's, Simple Voice Chat, Mod Menu: nothing special is needed (standard models,
  no custom shaders, one small Pillager mixin).

## Optimisation notes
* FPS: furniture model faces on the block edge use `cullface` (hidden faces are skipped), furniture no longer blocks view/light
  calculations, mobs are tracked at 6-8 chunks instead of 10, and natural spawn weights/group sizes are lower (fewer entities).
* Structures only draw the chunk being generated (loops clipped to the chunk) and reuse one mutable block position.
* Furniture shapes are built once per piece (wood/colour variants share them) and merged with `optimize()`.
* Predators scan for prey about every 1.5 s instead of constantly; Rascal, Tuff Golem, Barnacle and Wildfire throttle
  their player lookups/particles; the Iceologer caches its golem count.
* Data files are minified and PNGs optimised, so the jar is smaller and loads faster.
* One entity class + one model class per mob family (no per-mob classes), 4 mixin-free hooks except one small Pillager mixin.

## Editing the content
All data-driven content is generated by Python (needs Pillow):
* `tools/furniture_data.py` - furniture boxes, materials, recipes (add a piece = 3-6 lines)
* `tools/animal_data.py` - animal dimensions, palette, AI, spawn biomes, drops
* `python3 tools/generate.py` regenerates models, blockstates, items, recipes, loot, lang, textures and the Java tables
  `FurnitureDefs.java` / `AnimalSpecs.java`.

Hand-written code: `registry/` (registration), `furniture/` (block behaviour, sitting, storage), `animal/`,
`illager/` (Iceologer, ice shards, frost golem, pillager AI), `guard/` (knights/archers), `vehicle/`,
`worldgen/` (`Blueprints.java` has the four structures), `client/` (models and renderers).

## Known weak spots (be aware)
* Textures are procedurally generated placeholders; replace the PNGs in `assets/myminecraft/textures` with real art any time.
* Beds, trampolines and a swimmable "pool water" block are not included (use vanilla beds and water).
* The village/town do not replace vanilla villages; they are extra structures.

## Easiest way to get the jar (no installs)
1. Create a free GitHub account and a new empty repository.
2. Upload the contents of this folder to it (Add file > Upload files; keep the `.github` folder).
3. Open the **Actions** tab, open the latest "Build mod jar" run, and download `my-minecraft-jar` at the bottom.
4. If the run fails, open the red step and copy the error text - that is exactly what is needed to fix the code.
5. Unzip the download, put `my-minecraft-1.0.0.jar` (not the `-sources` one) into your Modrinth profile's mods folder.
NOTE: generated models/textures/recipes are rebuilt automatically by the GitHub build (tools/generate.py).
