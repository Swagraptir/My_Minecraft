"""
Furniture definitions for the "My Minecraft" mod.  This file is the single source of truth:
tools/generate.py turns it into block models, blockstates, item definitions, recipes, loot tables,
lang entries AND the Java file FurnitureDefs.java (collision shapes).

Coordinates are Minecraft pixel units 0..16, designed facing NORTH (front is the z=0 side).
box = (x1, y1, z1, x2, y2, z2, role)   -> role is looked up in the piece's `mats`.

kind:  decor | seat | storage | lamp (toggle with right click) | glow (always lit, `light` = level)
"""

WOODS = ["oak", "spruce", "birch", "dark_oak"]
COLORS = ["red", "blue", "gray", "green", "brown", "light_gray"]

FURNITURE = []


def piece(name, kind, boxes, mats, recipe, variants=None, light=0, title=None):
    FURNITURE.append(dict(name=name, kind=kind, boxes=boxes, mats=mats, recipe=recipe,
                          variants=variants, light=light, title=title))


# material shortcuts (vanilla block textures)
M_METAL = "iron_block"
M_WHITE = "white_concrete"
M_DARK = "black_concrete"
M_STONE = "smooth_stone"

# ------------------------------------------------------------------ LIVING ROOM
piece("armchair", "seat", [
    (1, 0, 2, 15, 8, 15, "cloth"), (0, 0, 2, 2, 12, 15, "cloth"), (14, 0, 2, 16, 12, 15, "cloth"),
    (2, 8, 12, 14, 16, 16, "cloth"), (2, 8, 3, 14, 10, 12, "cloth2")],
    {"cloth": "{c}_wool", "cloth2": "white_wool"}, ["cloth"] * 5 + ["stick"] * 2, variants="cloth")
piece("sofa", "seat", [
    (0, 0, 3, 16, 8, 16, "cloth"), (0, 0, 3, 3, 12, 16, "cloth"), (13, 0, 3, 16, 12, 16, "cloth"),
    (3, 8, 13, 13, 16, 16, "cloth"), (3, 8, 4, 13, 10, 13, "cloth2")],
    {"cloth": "{c}_wool", "cloth2": "white_wool"}, ["cloth"] * 6 + ["stick"] * 2, variants="cloth")
piece("sofa_middle", "seat", [
    (0, 0, 3, 16, 8, 16, "cloth"), (0, 8, 13, 16, 16, 16, "cloth"), (0, 8, 4, 16, 10, 13, "cloth2")],
    {"cloth": "{c}_wool", "cloth2": "white_wool"}, ["cloth"] * 5 + ["stick"], variants="cloth")
piece("rug", "decor", [(0, 0, 0, 16, 1, 16, "cloth"), (1, 1, 1, 15, 1.2, 15, "cloth2")],
      {"cloth": "{c}_wool", "cloth2": "white_wool"}, ["cloth"] * 4, variants="cloth")
piece("coffee_table", "decor", [
    (1, 6, 1, 15, 8, 15, "wood"), (1, 0, 1, 3, 6, 3, "wood"), (13, 0, 1, 15, 6, 3, "wood"),
    (1, 0, 13, 3, 6, 15, "wood"), (13, 0, 13, 15, 6, 15, "wood")],
    {"wood": "{w}_planks"}, ["wood"] * 4 + ["stick"] * 2, variants="wood")
piece("tv_stand", "storage", [
    (0, 0, 0, 16, 8, 16, "wood"), (1, 1, 0, 15, 7, 1, "dark"), (0, 8, 0, 16, 9, 16, "wood")],
    {"wood": "{w}_planks", "dark": M_DARK}, ["wood"] * 6, variants="wood")
piece("tv", "decor", [
    (6, 0, 6, 10, 1, 10, "metal"), (7, 1, 7.5, 9, 3, 8.5, "metal"),
    (0.5, 3, 7, 15.5, 14, 9, "dark"), (1.5, 4, 6.4, 14.5, 13, 7, "screen")],
    {"metal": M_METAL, "dark": M_DARK, "screen": "blue_concrete"}, ["iron", "iron", "glass", "glass", "redstone"])
piece("bookcase", "decor", [
    (0, 0, 13, 16, 16, 16, "wood"), (0, 0, 0, 2, 16, 13, "wood"), (14, 0, 0, 16, 16, 13, "wood"),
    (2, 0, 0, 14, 2, 13, "wood"), (2, 7, 0, 14, 9, 13, "wood"), (2, 14, 0, 14, 16, 13, "wood"),
    (2, 2, 2, 14, 7, 12, "books"), (2, 9, 2, 14, 14, 12, "books")],
    {"wood": "{w}_planks", "books": "bookshelf"}, ["wood"] * 6 + ["book"] * 3, variants="wood")
piece("floor_lamp", "lamp", [
    (5, 0, 5, 11, 1, 11, "metal"), (7.5, 1, 7.5, 8.5, 12, 8.5, "metal"), (4, 12, 4, 12, 16, 12, "shade")],
    {"metal": M_METAL, "shade": "white_wool", "shade_on": "glowstone"}, ["iron", "stick", "white_wool", "glowstone_dust"], light=14)
piece("table_lamp", "lamp", [
    (6, 0, 6, 10, 2, 10, "wood"), (7.5, 2, 7.5, 8.5, 6, 8.5, "wood"), (4, 6, 4, 12, 12, 12, "shade")],
    {"wood": "oak_planks", "shade": "white_wool", "shade_on": "glowstone"}, ["oak_planks", "stick", "white_wool", "glowstone_dust"], light=12)
piece("chandelier", "lamp", [
    (7.5, 10, 7.5, 8.5, 16, 8.5, "metal"), (3, 5, 3, 13, 10, 13, "shade"), (1, 5, 7, 15, 6, 9, "metal"), (7, 5, 1, 9, 6, 15, "metal")],
    {"metal": M_METAL, "shade": "yellow_concrete", "shade_on": "glowstone"}, ["iron", "iron", "iron", "glowstone_dust", "glowstone_dust"], light=15)
piece("wall_lamp", "lamp", [
    (6, 4, 13, 10, 5, 16, "metal"), (5, 5, 11, 11, 11, 15, "shade")],
    {"metal": M_METAL, "shade": "white_wool", "shade_on": "glowstone"}, ["iron", "white_wool", "glowstone_dust"], light=13)
piece("fireplace", "glow", [
    (0, 0, 0, 3, 16, 16, "brick"), (13, 0, 0, 16, 16, 16, "brick"), (3, 11, 0, 13, 16, 16, "brick"),
    (3, 0, 13, 13, 11, 16, "brick"), (3, 0, 3, 13, 1, 13, "dark"), (5, 1, 6, 11, 3, 10, "wood")],
    {"brick": "bricks", "dark": M_DARK, "wood": "spruce_planks"}, ["brick"] * 6 + ["coal", "stick"], light=11)
piece("grandfather_clock", "decor", [
    (3, 0, 4, 13, 2, 12, "wood"), (4, 2, 5, 12, 16, 11, "wood"), (5, 9, 4, 11, 15, 5, "face"), (7, 2, 4, 9, 8, 5, "metal")],
    {"wood": "{w}_planks", "face": M_WHITE, "metal": M_METAL}, ["wood"] * 6 + ["iron", "clock"], variants="wood")
piece("piano", "decor", [
    (0, 0, 3, 16, 9, 16, "dark"), (0, 0, 1, 2, 7, 3, "dark"), (14, 0, 1, 16, 7, 3, "dark"),
    (1, 9, 3, 15, 10, 6, "white"), (0, 10, 9, 16, 16, 16, "dark")],
    {"dark": M_DARK, "white": M_WHITE}, ["black_concrete"] * 5 + ["white_concrete"] * 3 + ["stick"])
piece("painting", "decor", [
    (1, 2, 15, 15, 14, 16, "wood"), (2, 3, 14.4, 14, 13, 15, "canvas")],
    {"wood": "oak_planks", "canvas": "{c}_concrete"}, ["stick"] * 4 + ["{c}_dye"] * 2 + ["paper"], variants="cloth")
piece("curtain", "decor", [
    (0, 14, 13, 16, 15, 14, "metal"), (1, 0, 13, 15, 14, 14.5, "cloth")],
    {"metal": M_METAL, "cloth": "{c}_wool"}, ["cloth"] * 3 + ["stick"], variants="cloth")
piece("wall_shelf", "decor", [
    (0, 7, 10, 16, 9, 16, "wood"), (2, 9, 11, 6, 13, 15, "books"), (8, 9, 12, 14, 11, 15, "metal")],
    {"wood": "{w}_planks", "books": "bookshelf", "metal": M_METAL}, ["wood"] * 3 + ["stick"], variants="wood")
piece("plant_pot", "decor", [
    (4, 0, 4, 12, 6, 12, "pot"), (5, 6, 5, 11, 7, 11, "soil"), (7, 7, 7, 9, 12, 9, "leaf"), (4, 10, 4, 12, 16, 12, "leaf")],
    {"pot": "terracotta", "soil": "dirt", "leaf": "green_wool"}, ["terracotta", "dirt", "green_wool", "sapling"])
piece("aquarium", "glow", [
    (1, 0, 2, 15, 1, 14, "metal"), (1, 9, 2, 15, 10, 14, "metal"), (1, 1, 2, 2, 9, 14, "metal"), (14, 1, 2, 15, 9, 14, "metal"),
    (2, 1, 3, 14, 9, 13, "water"), (4, 1, 6, 6, 3, 8, "fish"), (9, 4, 8, 11, 6, 10, "fish")],
    {"metal": M_METAL, "water": "light_blue_concrete", "fish": "orange_concrete"}, ["iron"] * 4 + ["glass"] * 4 + ["water_bucket"], light=8)

# ------------------------------------------------------------------ DINING / KITCHEN
piece("chair", "seat", [
    (3, 0, 3, 5, 7, 5, "wood"), (11, 0, 3, 13, 7, 5, "wood"), (3, 0, 11, 5, 7, 13, "wood"), (11, 0, 11, 13, 7, 13, "wood"),
    (3, 7, 3, 13, 9, 13, "wood"), (3, 9, 11, 13, 16, 13, "wood")],
    {"wood": "{w}_planks"}, ["wood"] * 4 + ["stick"] * 3, variants="wood")
piece("stool", "seat", [
    (4, 0, 4, 6, 8, 6, "wood"), (10, 0, 4, 12, 8, 6, "wood"), (4, 0, 10, 6, 8, 12, "wood"), (10, 0, 10, 12, 8, 12, "wood"),
    (3, 8, 3, 13, 10, 13, "wood")],
    {"wood": "{w}_planks"}, ["wood"] * 2 + ["stick"] * 3, variants="wood")
piece("bar_stool", "seat", [
    (7, 0, 7, 9, 12, 9, "metal"), (4, 0, 4, 12, 1, 12, "metal"), (3, 12, 3, 13, 15, 13, "cloth")],
    {"metal": M_METAL, "cloth": "{c}_wool"}, ["iron"] * 2 + ["cloth"] * 2, variants="cloth")
piece("table", "decor", [
    (0, 12, 0, 16, 14, 16, "wood"), (1, 0, 1, 3, 12, 3, "wood"), (13, 0, 1, 15, 12, 3, "wood"),
    (1, 0, 13, 3, 12, 15, "wood"), (13, 0, 13, 15, 12, 15, "wood")],
    {"wood": "{w}_planks"}, ["wood"] * 6 + ["stick"] * 2, variants="wood")
piece("kitchen_counter", "storage", [
    (0, 0, 0, 16, 14, 16, "wood"), (0, 14, 0, 16, 16, 16, "top"), (6, 8, -0.2, 10, 9, 0.5, "metal")],
    {"wood": "{w}_planks", "top": M_STONE, "metal": M_METAL}, ["wood"] * 4 + ["smooth_stone"] * 2 + ["iron"], variants="wood")
piece("kitchen_sink", "decor", [
    (0, 0, 0, 16, 14, 16, "wood"), (0, 14, 0, 16, 16, 3, "top"), (0, 14, 13, 16, 16, 16, "top"),
    (0, 14, 3, 3, 16, 13, "top"), (13, 14, 3, 16, 16, 13, "top"), (3, 13, 3, 13, 14, 13, "metal"),
    (7, 16, 11, 9, 19, 12, "metal"), (7, 18, 8, 9, 19, 12, "metal")],
    {"wood": "{w}_planks", "top": M_STONE, "metal": M_METAL}, ["wood"] * 3 + ["smooth_stone"] * 2 + ["iron"] * 2 + ["bucket"], variants="wood")
piece("wall_cabinet", "storage", [
    (0, 4, 8, 16, 16, 16, "wood"), (1, 5, 7, 8, 15, 8, "wood2"), (8, 5, 7, 15, 15, 8, "wood2"), (7, 8, 6.4, 8, 12, 7, "metal")],
    {"wood": "{w}_planks", "wood2": "{w}_planks", "metal": M_METAL}, ["wood"] * 5 + ["iron"], variants="wood")
piece("fridge", "storage", [
    (1, 0, 1, 15, 16, 15, "white"), (1, 7.6, 0.4, 15, 8.4, 1, "dark"), (12, 10, 0, 13, 15, 1, "metal"), (12, 2, 0, 13, 6, 1, "metal")],
    {"white": M_WHITE, "dark": M_DARK, "metal": M_METAL}, ["iron"] * 6 + ["glass"] + ["redstone"] + ["white_concrete"])
piece("stove", "decor", [
    (0, 0, 0, 16, 14, 16, "metal"), (0, 14, 0, 16, 16, 16, "dark"), (2, 8, -0.3, 4, 12, 0.5, "dark"), (6, 8, -0.3, 8, 12, 0.5, "dark"),
    (12, 8, -0.3, 14, 12, 0.5, "dark"), (2, 2, -0.2, 14, 7, 0.4, "glassy")],
    {"metal": M_METAL, "dark": M_DARK, "glassy": "gray_concrete"}, ["iron"] * 5 + ["furnace"] + ["black_concrete"] * 2)
piece("microwave", "decor", [
    (2, 0, 4, 14, 7, 12, "metal"), (3, 1, 3.2, 10, 6, 4, "dark"), (11, 1, 3.2, 13, 6, 4, "metal")],
    {"metal": M_METAL, "dark": M_DARK}, ["iron"] * 4 + ["glass", "redstone", "black_concrete"])
piece("washing_machine", "decor", [
    (1, 0, 1, 15, 15, 15, "white"), (4, 3, 0, 12, 11, 1, "dark"), (5, 4, -0.3, 11, 10, 0.4, "glassy"), (2, 12, 0, 6, 14, 1, "metal")],
    {"white": M_WHITE, "dark": M_DARK, "glassy": "light_blue_concrete", "metal": M_METAL}, ["iron"] * 5 + ["glass"] * 2 + ["water_bucket"])

# ------------------------------------------------------------------ BEDROOM / OFFICE / KIDS
piece("nightstand", "storage", [
    (1, 10, 1, 15, 12, 15, "wood"), (2, 0, 2, 14, 10, 14, "wood"), (3, 6, 1.2, 13, 9, 2, "dark"), (3, 2, 1.2, 13, 5, 2, "dark")],
    {"wood": "{w}_planks", "dark": "{w}_planks"}, ["wood"] * 5 + ["stick"], variants="wood")
piece("dresser", "storage", [
    (0, 12, 1, 16, 14, 15, "wood"), (1, 0, 2, 15, 12, 14, "wood"),
    (2, 8, 1.2, 14, 11, 2, "wood2"), (2, 4, 1.2, 14, 7, 2, "wood2"), (2, 1, 1.2, 14, 3.5, 2, "wood2"), (7, 9, 0.8, 9, 10, 1.4, "metal")],
    {"wood": "{w}_planks", "wood2": "{w}_planks", "metal": M_METAL}, ["wood"] * 8 + ["iron"], variants="wood")
piece("wardrobe", "storage", [
    (1, 0, 1, 15, 16, 15, "wood"), (1, 0, 0, 8, 16, 1, "wood2"), (8, 0, 0, 15, 16, 1, "wood2"), (6, 6, -0.4, 7, 10, 0.2, "metal"), (9, 6, -0.4, 10, 10, 0.2, "metal")],
    {"wood": "{w}_planks", "wood2": "{w}_planks", "metal": M_METAL}, ["wood"] * 8 + ["iron"], variants="wood")
piece("toy_chest", "storage", [
    (2, 0, 3, 14, 8, 13, "wood"), (1, 8, 2, 15, 10, 14, "wood2"), (7, 7, 2.6, 9, 9, 3.2, "metal")],
    {"wood": "{w}_planks", "wood2": "{w}_planks", "metal": M_METAL}, ["wood"] * 5 + ["iron"], variants="wood")
piece("pillow", "decor", [(3, 0, 3, 13, 3, 13, "cloth"), (4, 3, 4, 12, 4, 12, "cloth")],
      {"cloth": "{c}_wool"}, ["cloth"] * 3, variants="cloth")
piece("desk", "storage", [
    (0, 12, 0, 16, 14, 16, "wood"), (0, 0, 1, 2, 12, 15, "wood"), (11, 0, 1, 16, 12, 15, "wood"), (11.5, 8, 0.3, 15.5, 11, 1, "wood2"),
    (11.5, 4, 0.3, 15.5, 7, 1, "wood2"), (11.5, 0.5, 0.3, 15.5, 3, 1, "wood2")],
    {"wood": "{w}_planks", "wood2": "{w}_planks"}, ["wood"] * 7 + ["stick"], variants="wood")
piece("office_chair", "seat", [
    (3, 0, 3, 13, 1, 13, "metal"), (7, 1, 7, 9, 6, 9, "metal"), (3, 6, 3, 13, 8, 13, "cloth"), (3, 8, 11, 13, 16, 13, "cloth")],
    {"metal": M_METAL, "cloth": "{c}_wool"}, ["iron"] * 3 + ["cloth"] * 3, variants="cloth")
piece("computer", "decor", [
    (4, 0, 2, 12, 1, 6, "dark"), (6, 0, 8, 10, 1, 12, "metal"), (7.5, 1, 9.5, 8.5, 4, 10.5, "metal"),
    (2, 4, 8, 14, 13, 10, "dark"), (3, 5, 7.4, 13, 12, 8, "screen")],
    {"metal": M_METAL, "dark": M_DARK, "screen": "cyan_concrete"}, ["iron"] * 3 + ["glass"] * 2 + ["redstone"] * 2 + ["black_concrete"])
piece("printer", "decor", [
    (2, 0, 3, 14, 5, 13, "white"), (4, 5, 5, 12, 6, 9, "paper"), (3, 3, 2.4, 6, 4, 3, "dark")],
    {"white": M_WHITE, "paper": "white_wool", "dark": M_DARK}, ["iron"] * 3 + ["white_concrete"] * 2 + ["paper"] * 2)
piece("file_cabinet", "storage", [
    (2, 0, 2, 14, 16, 14, "metal"), (3, 11, 1.2, 13, 15, 2, "dark"), (3, 6, 1.2, 13, 10, 2, "dark"), (3, 1, 1.2, 13, 5, 2, "dark"),
    (7, 12.5, 0.8, 9, 13.5, 1.3, "metal")],
    {"metal": M_METAL, "dark": "gray_concrete"}, ["iron"] * 7 + ["gray_concrete"])

# ------------------------------------------------------------------ BATHROOM
piece("bathtub", "decor", [
    (0, 0, 0, 16, 8, 2, "white"), (0, 0, 14, 16, 8, 16, "white"), (0, 0, 2, 2, 8, 14, "white"), (14, 0, 2, 16, 8, 14, "white"),
    (2, 0, 2, 14, 2, 14, "white"), (6, 8, 13, 10, 11, 15, "metal"), (7, 10, 10, 9, 11, 14, "metal")],
    {"white": M_WHITE, "metal": M_METAL}, ["white_concrete"] * 5 + ["iron"] * 2 + ["bucket"])
piece("toilet", "seat", [
    (4, 0, 2, 12, 7, 11, "white"), (3, 7, 1, 13, 8, 11, "white"), (4, 8, 11, 12, 15, 15, "white"), (11, 13, 10, 12.6, 14.6, 11.4, "metal")],
    {"white": M_WHITE, "metal": M_METAL}, ["white_concrete"] * 5 + ["iron"] + ["bucket"])
piece("bathroom_sink", "decor", [
    (6, 0, 6, 10, 10, 10, "white"), (2, 10, 3, 14, 14, 13, "white"), (7, 14, 3.2, 9, 17, 4.4, "metal"), (3, 12, 4, 13, 14.2, 12, "water")],
    {"white": M_WHITE, "metal": M_METAL, "water": "light_blue_concrete"}, ["white_concrete"] * 4 + ["iron"] * 2 + ["bucket"])
piece("shower", "decor", [
    (0, 0, 0, 16, 1, 16, "white"), (7, 0, 14, 9, 16, 16, "metal"), (5, 13, 8, 11, 15, 14, "metal"), (6, 12, 9, 10, 13, 13, "metal")],
    {"white": M_WHITE, "metal": M_METAL}, ["iron"] * 4 + ["white_concrete"] * 2 + ["bucket"])
piece("towel_rack", "decor", [
    (2, 8, 13, 14, 9, 15, "metal"), (4, 1, 13, 12, 12, 14, "cloth"), (1, 7, 14, 2, 10, 16, "metal"), (14, 7, 14, 15, 10, 16, "metal")],
    {"metal": M_METAL, "cloth": "{c}_wool"}, ["iron"] * 3 + ["cloth"] * 2, variants="cloth")
piece("mirror", "decor", [
    (1, 2, 14, 15, 15, 16, "wood"), (2, 3, 13.4, 14, 14, 14, "glass")],
    {"wood": "{w}_planks", "glass": "light_blue_concrete"}, ["wood"] * 4 + ["glass"] * 4 + ["iron"], variants="wood")

# ------------------------------------------------------------------ EXTERIOR / GARDEN
piece("park_bench", "seat", [
    (0, 6, 3, 16, 8, 11, "wood"), (0, 8, 10, 16, 15, 12, "wood"), (1, 0, 3, 3, 6, 11, "metal"), (13, 0, 3, 15, 6, 11, "metal"), (0, 8, 4, 2, 11, 11, "metal"), (14, 8, 4, 16, 11, 11, "metal")],
    {"wood": "{w}_planks", "metal": M_METAL}, ["wood"] * 5 + ["iron"] * 3, variants="wood")
piece("garden_chair", "seat", [
    (3, 0, 3, 5, 7, 5, "metal"), (11, 0, 3, 13, 7, 5, "metal"), (3, 0, 11, 5, 7, 13, "metal"), (11, 0, 11, 13, 7, 13, "metal"),
    (3, 7, 3, 13, 8, 13, "wood"), (3, 8, 11, 13, 15, 12, "wood")],
    {"wood": "{w}_planks", "metal": M_METAL}, ["wood"] * 2 + ["iron"] * 4, variants="wood")
piece("deck_chair", "seat", [
    (2, 0, 3, 4, 4, 13, "wood"), (12, 0, 3, 14, 4, 13, "wood"), (3, 4, 2, 13, 6, 14, "cloth"), (3, 6, 12, 13, 16, 14, "cloth")],
    {"wood": "oak_planks", "cloth": "{c}_wool"}, ["oak_planks"] * 3 + ["cloth"] * 3, variants="cloth")
piece("patio_table", "decor", [
    (0, 11, 0, 16, 13, 16, "metal"), (7, 0, 7, 9, 11, 9, "metal"), (4, 0, 4, 12, 1, 12, "metal"), (7, 13, 7, 9, 16, 9, "metal")],
    {"metal": M_METAL}, ["iron"] * 6)
piece("parasol", "decor", [
    (7.5, 0, 7.5, 8.5, 15, 8.5, "white"), (0, 13, 0, 16, 15, 16, "cloth"), (3, 15, 3, 13, 16, 13, "cloth"), (4, 0, 4, 12, 1, 12, "white")],
    {"white": M_WHITE, "cloth": "{c}_wool"}, ["cloth"] * 5 + ["stick"] * 2, variants="cloth")
piece("picnic_table", "decor", [
    (0, 10, 2, 16, 12, 14, "wood"), (0, 5, 0, 16, 7, 2, "wood"), (0, 5, 14, 16, 7, 16, "wood"), (2, 0, 3, 4, 10, 13, "wood"), (12, 0, 3, 14, 10, 13, "wood")],
    {"wood": "{w}_planks"}, ["wood"] * 7 + ["stick"] * 2, variants="wood")
piece("bbq_grill", "decor", [
    (2, 5, 2, 14, 9, 14, "dark"), (3, 9, 3, 13, 10, 13, "metal"), (3, 0, 3, 5, 5, 5, "metal"), (11, 0, 3, 13, 5, 5, "metal"),
    (3, 0, 11, 5, 5, 13, "metal"), (11, 0, 11, 13, 5, 13, "metal"), (3, 9.2, 3, 13, 9.8, 13, "coal")],
    {"dark": M_DARK, "metal": M_METAL, "coal": "coal_block"}, ["iron"] * 6 + ["coal", "black_concrete"])
piece("mailbox", "decor", [
    (7, 0, 7, 9, 10, 9, "wood"), (3, 10, 4, 13, 15, 12, "red"), (13, 11, 5, 14.4, 16, 6.4, "red"), (6, 10.4, 3.4, 10, 12.4, 4, "dark")],
    {"wood": "oak_planks", "red": "red_concrete", "dark": M_DARK}, ["iron"] * 3 + ["red_concrete"] * 2 + ["stick"] * 2)
piece("trash_can", "storage", [
    (3, 0, 3, 13, 12, 13, "metal"), (2, 12, 2, 14, 14, 14, "metal"), (6, 14, 6, 10, 15, 10, "dark")],
    {"metal": "gray_concrete", "dark": M_DARK}, ["iron"] * 5 + ["gray_concrete"] * 2)
piece("flower_planter", "decor", [
    (1, 0, 3, 15, 6, 13, "wood"), (2, 6, 4, 14, 7, 12, "soil"), (3, 7, 5, 13, 11, 11, "leaf"), (5, 11, 6, 7, 13, 8, "bloom"), (9, 11, 8, 11, 13, 10, "bloom2")],
    {"wood": "{w}_planks", "soil": "dirt", "leaf": "green_wool", "bloom": "red_wool", "bloom2": "yellow_wool"}, ["wood"] * 4 + ["dirt"] * 2 + ["poppy", "dandelion"], variants="wood")
piece("bird_bath", "decor", [
    (6, 0, 6, 10, 8, 10, "stone"), (3, 8, 3, 13, 9, 13, "stone"), (2, 9, 2, 14, 11, 3, "stone"), (2, 9, 13, 14, 11, 14, "stone"),
    (2, 9, 3, 3, 11, 13, "stone"), (13, 9, 3, 14, 11, 13, "stone"), (3, 9, 3, 13, 9.8, 13, "water")],
    {"stone": "stone", "water": "light_blue_concrete"}, ["stone"] * 6 + ["bucket"])
piece("garden_gnome", "decor", [
    (5, 0, 5, 11, 6, 11, "blue"), (6, 6, 6, 10, 9, 10, "skin"), (6, 5, 4.4, 10, 7, 6, "white"), (6.5, 9, 6.5, 9.5, 14, 9.5, "red"), (5, 9, 5, 11, 10, 11, "red")],
    {"blue": "blue_wool", "skin": "pink_wool", "white": "white_wool", "red": "red_wool"}, ["red_wool"] * 2 + ["blue_wool"] * 2 + ["white_wool"] + ["pink_wool"])
piece("doghouse", "decor", [
    (1, 0, 1, 15, 9, 15, "wood"), (0, 9, 0, 16, 11, 16, "roof"), (3, 11, 2, 13, 13, 14, "roof"), (5, 0, 0.2, 11, 7, 1.0, "dark")],
    {"wood": "{w}_planks", "roof": "red_concrete", "dark": M_DARK}, ["wood"] * 6 + ["red_concrete"] * 2 + ["bone"], variants="wood")
piece("swing", "seat", [
    (0, 0, 6, 2, 16, 10, "wood"), (14, 0, 6, 16, 16, 10, "wood"), (0, 14, 6, 16, 16, 10, "wood"),
    (4, 5, 7.5, 4.6, 14, 8.5, "metal"), (11.4, 5, 7.5, 12, 14, 8.5, "metal"), (3, 3, 5, 13, 5, 11, "wood")],
    {"wood": "{w}_planks", "metal": M_METAL}, ["wood"] * 6 + ["iron"] * 2, variants="wood")
piece("hammock", "seat", [
    (0, 0, 7, 2, 14, 9, "wood"), (14, 0, 7, 16, 14, 9, "wood"), (2, 5, 3, 14, 6, 13, "cloth"), (2, 6, 3, 3, 9, 13, "cloth"), (13, 6, 3, 14, 9, 13, "cloth")],
    {"wood": "oak_planks", "cloth": "{c}_wool"}, ["oak_planks"] * 2 + ["cloth"] * 4 + ["stick"], variants="cloth")
piece("sandbox", "decor", [
    (0, 0, 0, 16, 5, 2, "wood"), (0, 0, 14, 16, 5, 16, "wood"), (0, 0, 2, 2, 5, 14, "wood"), (14, 0, 2, 16, 5, 14, "wood"), (2, 0, 2, 14, 3, 14, "sand")],
    {"wood": "{w}_planks", "sand": "sand"}, ["wood"] * 4 + ["sand"] * 4, variants="wood")
piece("pool_ladder", "decor", [
    (2, 0, 14, 3, 16, 16, "metal"), (13, 0, 14, 14, 16, 16, "metal"), (3, 3, 14.4, 13, 4, 15.4, "metal"), (3, 8, 14.4, 13, 9, 15.4, "metal"), (3, 13, 14.4, 13, 14, 15.4, "metal")],
    {"metal": M_METAL}, ["iron"] * 7)
piece("diving_board", "decor", [
    (4, 0, 0, 12, 8, 5, "metal"), (5, 8, 0, 11, 9, 16, "white"), (5, 9, 12, 11, 9.4, 16, "blue")],
    {"metal": M_METAL, "white": M_WHITE, "blue": "light_blue_concrete"}, ["iron"] * 3 + ["white_concrete"] * 3)
piece("garden_lamp", "lamp", [
    (6, 0, 6, 10, 3, 10, "metal"), (7, 3, 7, 9, 11, 9, "metal"), (5, 11, 5, 11, 16, 11, "shade")],
    {"metal": M_METAL, "shade": "white_concrete", "shade_on": "glowstone"}, ["iron"] * 3 + ["glass", "glowstone_dust"], light=14)

# ================================================================== BATCH 2: more furniture
piece("ottoman", "seat", [(2, 0, 2, 14, 7, 14, "cloth"), (3, 7, 3, 13, 8, 13, "cloth2")],
      {"cloth": "{c}_wool", "cloth2": "white_wool"}, ["cloth"] * 4 + ["stick"], variants="cloth")
piece("bean_bag", "seat", [(2, 0, 2, 14, 5, 14, "cloth"), (3, 5, 3, 13, 8, 13, "cloth"), (5, 8, 5, 11, 9, 11, "cloth")],
      {"cloth": "{c}_wool"}, ["cloth"] * 6, variants="cloth")
piece("crib", "decor", [
    (0, 0, 1, 2, 12, 3, "wood"), (14, 0, 1, 16, 12, 3, "wood"), (0, 0, 13, 2, 12, 15, "wood"), (14, 0, 13, 16, 12, 15, "wood"),
    (0, 10, 1, 16, 12, 3, "wood"), (0, 10, 13, 16, 12, 15, "wood"), (2, 4, 3, 14, 6, 13, "cloth"), (2, 6, 5, 14, 8, 9, "cloth2")],
    {"wood": "{w}_planks", "cloth": "white_wool", "cloth2": "pink_wool"}, ["wood"] * 6 + ["white_wool"] * 2 + ["stick"], variants="wood")
piece("vanity_table", "storage", [
    (0, 12, 2, 16, 14, 12, "wood"), (1, 0, 3, 3, 12, 5, "wood"), (13, 0, 3, 15, 12, 5, "wood"), (1, 0, 9, 3, 12, 11, "wood"), (13, 0, 9, 15, 12, 11, "wood"),
    (3, 14, 10, 13, 16, 11, "glass"), (3, 9, 2.4, 13, 11, 3.2, "wood2")],
    {"wood": "{w}_planks", "wood2": "{w}_planks", "glass": "light_blue_concrete"}, ["wood"] * 6 + ["glass"] * 2 + ["stick"], variants="wood")
piece("easel", "decor", [
    (3, 0, 6, 4, 16, 7, "wood"), (12, 0, 6, 13, 16, 7, "wood"), (7, 0, 10, 8, 12, 11, "wood"), (3, 5, 5, 13, 6, 6, "wood"), (3, 6, 4.6, 13, 14, 5, "canvas")],
    {"wood": "{w}_planks", "canvas": "white_wool"}, ["wood"] * 3 + ["stick"] * 3 + ["white_wool"], variants="wood")
piece("globe", "decor", [(5, 0, 5, 11, 2, 11, "wood"), (7.5, 2, 7.5, 8.5, 6, 8.5, "metal"), (4, 6, 4, 12, 14, 12, "globe"), (7.5, 14, 7.5, 8.5, 15, 8.5, "metal")],
      {"wood": "dark_oak_planks", "metal": M_METAL, "globe": "light_blue_concrete"}, ["iron", "dark_oak_planks", "light_blue_concrete", "paper", "stick"])
piece("trophy", "decor", [(5, 0, 5, 11, 2, 11, "wood"), (7, 2, 7, 9, 6, 9, "gold"), (4, 6, 4, 12, 12, 12, "gold"), (2, 8, 7, 4, 11, 9, "gold"), (12, 8, 7, 14, 11, 9, "gold")],
      {"wood": "dark_oak_planks", "gold": "gold_block"}, ["gold_ingot"] * 4 + ["dark_oak_planks"])
piece("telescope", "decor", [(4, 0, 4, 12, 2, 12, "metal"), (7, 2, 7, 9, 9, 9, "metal"), (3, 8, 6, 13, 11, 10, "dark"), (13, 8.5, 7, 15, 10.5, 9, "glass")],
      {"metal": M_METAL, "dark": M_DARK, "glass": "light_blue_concrete"}, ["iron"] * 4 + ["glass"] * 2 + ["black_concrete"])
piece("floor_vase", "decor", [(5, 0, 5, 11, 2, 11, "pot"), (4, 2, 4, 12, 9, 12, "pot"), (6, 9, 6, 10, 12, 10, "pot"), (7, 12, 7, 9, 16, 9, "leaf"), (5, 13, 5, 11, 16, 11, "bloom")],
      {"pot": "terracotta", "leaf": "green_wool", "bloom": "yellow_wool"}, ["terracotta"] * 4 + ["dandelion", "green_wool"])
piece("oven", "decor", [
    (0, 0, 0, 16, 16, 16, "metal"), (2, 2, -0.3, 14, 11, 0.4, "glassy"), (2, 12, -0.3, 14, 15, 0.4, "dark"), (3, 13, -0.6, 13, 14, 0.2, "metal")],
    {"metal": M_METAL, "dark": M_DARK, "glassy": "gray_concrete"}, ["iron"] * 6 + ["furnace", "glass"])
piece("dishwasher", "decor", [(1, 0, 1, 15, 15, 15, "white"), (2, 12, -0.3, 14, 14, 0.4, "dark"), (3, 11, -0.6, 13, 12, 0.2, "metal"), (2, 2, -0.2, 14, 10, 0.3, "metal")],
      {"white": M_WHITE, "dark": M_DARK, "metal": M_METAL}, ["iron"] * 5 + ["white_concrete"] * 2 + ["water_bucket"])
piece("range_hood", "decor", [(2, 8, 6, 14, 12, 16, "metal"), (5, 12, 10, 11, 16, 16, "metal"), (3, 7, 7, 13, 8, 15, "dark")],
      {"metal": M_METAL, "dark": M_DARK}, ["iron"] * 5 + ["black_concrete"])
piece("coffee_machine", "decor", [(3, 0, 6, 13, 10, 14, "dark"), (4, 10, 6, 12, 13, 12, "dark"), (5, 0, 2, 11, 1, 6, "metal"), (6, 1, 3, 10, 4, 5, "cup")],
      {"dark": M_DARK, "metal": M_METAL, "cup": "white_concrete"}, ["iron"] * 3 + ["black_concrete"] * 2 + ["white_concrete", "redstone"])
piece("bar_counter", "storage", [(0, 0, 2, 16, 13, 14, "wood"), (0, 13, 0, 16, 15, 16, "top"), (1, 2, 0.5, 15, 8, 2, "dark")],
      {"wood": "{w}_planks", "top": M_STONE, "dark": M_DARK}, ["wood"] * 5 + ["smooth_stone"] * 3, variants="wood")
piece("wine_rack", "storage", [
    (0, 0, 2, 16, 16, 14, "wood"), (1, 1, 0.5, 5, 5, 2, "bottle"), (6, 1, 0.5, 10, 5, 2, "bottle"), (11, 1, 0.5, 15, 5, 2, "bottle"),
    (1, 6, 0.5, 5, 10, 2, "bottle"), (6, 6, 0.5, 10, 10, 2, "bottle"), (11, 6, 0.5, 15, 10, 2, "bottle")],
    {"wood": "{w}_planks", "bottle": "green_concrete"}, ["wood"] * 6 + ["glass"] * 3, variants="wood")
piece("treadmill", "decor", [
    (1, 0, 0, 15, 3, 16, "dark"), (3, 3, 2, 13, 4, 14, "gray"), (2, 3, 1, 4, 12, 3, "metal"), (12, 3, 1, 14, 12, 3, "metal"), (2, 12, 1, 14, 13, 3, "metal"), (4, 9, 0.6, 12, 12, 1, "screen")],
    {"dark": M_DARK, "gray": "gray_concrete", "metal": M_METAL, "screen": "cyan_concrete"}, ["iron"] * 5 + ["black_concrete"] * 2 + ["redstone", "gray_concrete"])
piece("dumbbell_rack", "decor", [
    (1, 0, 5, 15, 2, 11, "metal"), (2, 2, 6, 3, 9, 10, "metal"), (13, 2, 6, 14, 9, 10, "metal"), (3, 4, 6, 13, 5, 10, "dark"), (3, 7, 6, 13, 8, 10, "dark"),
    (3, 3, 5.4, 5, 6, 10.6, "dark"), (11, 3, 5.4, 13, 6, 10.6, "dark")],
    {"metal": M_METAL, "dark": M_DARK}, ["iron"] * 7 + ["black_concrete"])
piece("punching_bag", "decor", [(7.5, 13, 7.5, 8.5, 16, 8.5, "metal"), (5, 2, 5, 11, 13, 11, "cloth"), (5, 7, 4.6, 11, 8, 5, "cloth2")],
      {"metal": M_METAL, "cloth": "{c}_wool", "cloth2": "white_wool"}, ["cloth"] * 5 + ["iron"] * 2 + ["leather"], variants="cloth")
piece("fire_pit", "glow", [
    (2, 0, 2, 14, 3, 3, "stone"), (2, 0, 13, 14, 3, 14, "stone"), (2, 0, 3, 3, 3, 13, "stone"), (13, 0, 3, 14, 3, 13, "stone"),
    (5, 0, 5, 11, 2, 7, "wood"), (5, 0, 9, 11, 2, 11, "wood"), (6, 2, 6, 10, 5, 10, "fire")],
    {"stone": "cobblestone", "wood": "spruce_planks", "fire": "orange_concrete"}, ["cobblestone"] * 6 + ["coal", "stick"], light=13)
piece("wheelbarrow", "decor", [
    (3, 5, 2, 13, 8, 10, "metal"), (3, 8, 2, 4, 10, 10, "metal"), (12, 8, 2, 13, 10, 10, "metal"), (6, 0, 10, 10, 5, 12, "dark"),
    (2, 4, 10, 4, 5, 16, "wood"), (12, 4, 10, 14, 5, 16, "wood"), (3, 0, 14, 5, 4, 15, "wood"), (11, 0, 14, 13, 4, 15, "wood")],
    {"metal": M_METAL, "dark": M_DARK, "wood": "oak_planks"}, ["iron"] * 4 + ["oak_planks"] * 2 + ["stick"] * 3)
piece("scarecrow", "decor", [
    (7, 0, 7, 9, 16, 9, "wood"), (1, 10, 7, 15, 12, 9, "wood"), (5, 13, 5, 11, 16, 11, "hay"), (4, 4, 5, 12, 10, 11, "cloth"), (2, 12, 6.6, 4, 14, 9.4, "hay"), (12, 12, 6.6, 14, 14, 9.4, "hay")],
    {"wood": "oak_planks", "hay": "hay_block", "cloth": "{c}_wool"}, ["hay_block"] * 2 + ["stick"] * 3 + ["cloth"] * 2 + ["pumpkin"], variants="cloth")
piece("hydrant", "decor", [(5, 0, 5, 11, 10, 11, "red"), (4, 10, 4, 12, 13, 12, "red"), (6, 13, 6, 10, 15, 10, "red"), (2, 5, 6, 5, 8, 10, "metal"), (11, 5, 6, 14, 8, 10, "metal")],
      {"red": "red_concrete", "metal": M_METAL}, ["red_concrete"] * 4 + ["iron"] * 2)
piece("traffic_cone", "decor", [(2, 0, 2, 14, 2, 14, "orange"), (4, 2, 4, 12, 8, 12, "orange"), (6, 8, 6, 10, 14, 10, "orange"), (4.2, 4, 4.2, 11.8, 5.2, 11.8, "white")],
      {"orange": "orange_concrete", "white": M_WHITE}, ["orange_concrete"] * 3 + ["white_concrete"])
piece("vending_machine", "storage", [
    (1, 0, 2, 15, 16, 14, "red"), (2, 6, 1.2, 12, 15, 2, "glassy"), (12.5, 6, 1.2, 14.5, 15, 2, "dark"), (3, 1, 1.2, 12, 4, 2, "dark")],
    {"red": "red_concrete", "glassy": "light_blue_concrete", "dark": M_DARK}, ["iron"] * 4 + ["red_concrete"] * 3 + ["glass", "redstone"])
piece("newspaper_box", "storage", [(7, 0, 7, 9, 8, 9, "metal"), (3, 8, 4, 13, 14, 12, "blue"), (4, 10, 3.4, 12, 13, 4, "glassy")],
      {"metal": M_METAL, "blue": "blue_concrete", "glassy": "light_blue_concrete"}, ["iron"] * 3 + ["blue_concrete"] * 3 + ["paper"])
piece("street_bench", "seat", [
    (0, 6, 3, 16, 8, 11, "metal"), (0, 8, 10, 16, 14, 12, "metal"), (1, 0, 3, 3, 6, 11, "metal"), (13, 0, 3, 15, 6, 11, "metal")],
    {"metal": M_METAL}, ["iron"] * 6)
piece("bike_rack", "decor", [(1, 0, 6, 15, 1, 10, "metal"), (2, 1, 7, 3, 8, 9, "metal"), (6, 1, 7, 7, 8, 9, "metal"), (10, 1, 7, 11, 8, 9, "metal"), (13, 1, 7, 14, 8, 9, "metal"), (2, 8, 7, 14, 9, 9, "metal")],
      {"metal": M_METAL}, ["iron"] * 6)
piece("chalkboard", "decor", [(0, 0, 12, 2, 14, 14, "wood"), (14, 0, 12, 16, 14, 14, "wood"), (0, 14, 12, 16, 16, 14, "wood"), (2, 1, 12.4, 14, 14, 13.4, "board"), (2, 0, 11, 14, 1, 12, "wood")],
      {"wood": "{w}_planks", "board": "green_concrete"}, ["wood"] * 4 + ["green_concrete"] * 2 + ["stick"] * 2, variants="wood")
piece("birdhouse", "decor", [(7.5, 0, 7.5, 8.5, 9, 8.5, "wood"), (4, 9, 4, 12, 15, 12, "wood"), (3, 15, 3, 13, 16, 13, "roof"), (7, 11, 3.4, 9, 13, 4, "dark")],
      {"wood": "{w}_planks", "roof": "red_concrete", "dark": M_DARK}, ["wood"] * 4 + ["stick"] * 2 + ["red_concrete"], variants="wood")
piece("lectern_stand", "decor", [(5, 0, 5, 11, 10, 11, "wood"), (2, 10, 4, 14, 12, 12, "wood"), (2, 12, 11, 14, 14, 12, "wood"), (3, 12, 6, 13, 12.6, 10, "books")],
      {"wood": "{w}_planks", "books": "bookshelf"}, ["wood"] * 4 + ["book"] * 2 + ["stick"], variants="wood")
piece("slide", "decor", [
    (0, 0, 0, 2, 16, 2, "metal"), (14, 0, 0, 16, 16, 2, "metal"), (2, 15, 0, 14, 16, 3, "yellow"),
    (2, 12, 3, 14, 14, 6, "yellow"), (2, 9, 6, 14, 11, 9, "yellow"), (2, 6, 9, 14, 8, 12, "yellow"), (2, 3, 12, 14, 5, 15, "yellow"),
    (0, 0, 3, 2, 14, 4, "metal"), (14, 0, 3, 16, 14, 4, "metal")],
    {"metal": M_METAL, "yellow": "yellow_concrete"}, ["iron"] * 4 + ["yellow_concrete"] * 4 + ["stick"])
piece("see_saw", "seat", [(7, 0, 4, 9, 4, 12, "metal"), (0, 4, 6, 16, 5, 10, "wood"), (0, 5, 6, 1, 8, 10, "metal"), (15, 5, 6, 16, 8, 10, "metal")],
      {"metal": M_METAL, "wood": "{w}_planks"}, ["wood"] * 3 + ["iron"] * 3, variants="wood")

# ================================================================== BATCH 3: even more furniture
piece("recliner", "seat", [(1, 0, 3, 15, 6, 16, "cloth"), (0, 0, 3, 2, 10, 16, "cloth"), (14, 0, 3, 16, 10, 16, "cloth"), (2, 6, 12, 14, 16, 16, "cloth2"), (3, 0, 0, 13, 3, 4, "cloth")],
      {"cloth": "{c}_wool", "cloth2": "white_wool"}, ["cloth"] * 6 + ["stick"] * 2, variants="cloth")
piece("side_table", "decor", [(2, 10, 2, 14, 12, 14, "wood"), (3, 0, 3, 5, 10, 5, "wood"), (11, 0, 3, 13, 10, 5, "wood"), (3, 0, 11, 5, 10, 13, "wood"), (11, 0, 11, 13, 10, 13, "wood")],
      {"wood": "{w}_planks"}, ["wood"] * 3 + ["stick"] * 2, variants="wood")
piece("console_table", "storage", [(0, 10, 3, 16, 12, 11, "wood"), (1, 0, 4, 3, 10, 10, "wood"), (13, 0, 4, 15, 10, 10, "wood"), (3, 3, 4, 13, 5, 10, "wood2")],
      {"wood": "{w}_planks", "wood2": "{w}_planks"}, ["wood"] * 6, variants="wood")
piece("shoe_rack", "storage", [(1, 0, 3, 15, 1, 13, "wood"), (1, 5, 3, 15, 6, 13, "wood"), (1, 10, 3, 15, 11, 13, "wood"), (0, 0, 3, 1, 11, 13, "wood"), (15, 0, 3, 16, 11, 13, "wood"), (3, 1, 5, 7, 4, 11, "shoe"), (9, 6, 5, 13, 9, 11, "shoe")],
      {"wood": "{w}_planks", "shoe": "brown_wool"}, ["wood"] * 5 + ["leather"] * 2, variants="wood")
piece("coat_rack", "decor", [(7, 0, 7, 9, 16, 9, "wood"), (4, 0, 4, 12, 1, 12, "wood"), (3, 12, 7.5, 7, 13, 8.5, "wood"), (9, 12, 7.5, 13, 13, 8.5, "wood"), (4, 5, 6.5, 6, 12, 9.5, "cloth")],
      {"wood": "{w}_planks", "cloth": "{c}_wool"}, ["wood"] * 2 + ["stick"] * 3 + ["cloth"] * 2, variants="cloth")
piece("umbrella_stand", "decor", [(5, 0, 5, 11, 1, 11, "metal"), (5, 1, 5, 6, 8, 11, "metal"), (10, 1, 5, 11, 8, 11, "metal"), (6, 1, 5, 10, 8, 6, "metal"), (6, 1, 10, 10, 8, 11, "metal"), (7.5, 2, 7.5, 8.5, 16, 8.5, "red")],
      {"metal": M_METAL, "red": "red_concrete"}, ["iron"] * 5 + ["stick", "red_concrete"])
piece("fish_bowl", "glow", [(4, 0, 4, 12, 1, 12, "dark"), (3, 1, 3, 13, 9, 13, "glass"), (5, 2, 5, 8, 4, 8, "fish"), (4, 1, 4, 12, 3, 12, "sand")],
      {"dark": M_DARK, "glass": "light_blue_concrete", "fish": "orange_concrete", "sand": "sand"}, ["glass"] * 4 + ["sand", "water_bucket", "cod_bucket"], light=5)
piece("record_player", "decor", [(1, 0, 2, 15, 5, 14, "wood"), (2, 5, 3, 12, 6, 13, "dark"), (3, 6, 4, 11, 7, 12, "disc"), (12, 5, 4, 13, 8, 5, "metal"), (9, 5, 11, 15, 8, 12, "metal")],
      {"wood": "{w}_planks", "dark": M_DARK, "disc": M_DARK, "metal": M_METAL}, ["wood"] * 4 + ["iron"] * 2 + ["music_disc_13"], variants="wood")
piece("speaker", "decor", [(3, 0, 4, 13, 16, 12, "dark"), (5, 8, 3.4, 11, 14, 4, "cone"), (6, 2, 3.4, 10, 6, 4, "cone")],
      {"dark": M_DARK, "cone": "gray_concrete"}, ["black_concrete"] * 4 + ["iron"] * 2 + ["redstone", "gray_concrete"])
piece("radiator", "decor", [(1, 1, 11, 15, 11, 14, "white"), (3, 1, 11, 4, 11, 14, "gray"), (7, 1, 11, 8, 11, 14, "gray"), (11, 1, 11, 12, 11, 14, "gray"), (1, 0, 12, 2, 1, 13, "metal"), (14, 0, 12, 15, 1, 13, "metal")],
      {"white": M_WHITE, "gray": "light_gray_concrete", "metal": M_METAL}, ["iron"] * 6 + ["white_concrete"])
piece("ceiling_fan", "decor", [(7.5, 12, 7.5, 8.5, 16, 8.5, "metal"), (6, 10, 6, 10, 12, 10, "metal"), (1, 10, 7, 15, 11, 9, "wood"), (7, 10, 1, 9, 11, 15, "wood")],
      {"metal": M_METAL, "wood": "{w}_planks"}, ["iron"] * 3 + ["wood"] * 4, variants="wood")
piece("window_planter", "decor", [(0, 0, 8, 16, 5, 16, "wood"), (1, 5, 9, 15, 6, 15, "soil"), (2, 6, 10, 14, 9, 14, "leaf"), (3, 9, 11, 5, 11, 13, "bloom"), (8, 9, 11, 10, 11, 13, "bloom"), (12, 9, 11, 14, 10, 13, "bloom")],
      {"wood": "{w}_planks", "soil": "dirt", "leaf": "green_wool", "bloom": "pink_wool"}, ["wood"] * 4 + ["dirt"] * 2 + ["poppy", "green_wool"], variants="wood")
piece("cat_tree", "decor", [(3, 0, 3, 13, 2, 13, "wood"), (6, 2, 6, 10, 10, 10, "rope"), (2, 10, 2, 14, 12, 14, "cloth"), (7, 12, 7, 9, 15, 9, "rope"), (4, 15, 4, 12, 16, 12, "cloth")],
      {"wood": "{w}_planks", "rope": "hay_block", "cloth": "{c}_wool"}, ["wood"] * 2 + ["hay_block"] * 2 + ["cloth"] * 3 + ["string"], variants="cloth")
piece("dog_bed", "decor", [(1, 0, 1, 15, 3, 3, "wood"), (1, 0, 13, 15, 3, 15, "wood"), (1, 0, 3, 3, 3, 13, "wood"), (13, 0, 3, 15, 3, 13, "wood"), (3, 0, 3, 13, 2, 13, "cloth")],
      {"wood": "{w}_planks", "cloth": "{c}_wool"}, ["wood"] * 3 + ["cloth"] * 3, variants="cloth")
piece("pet_bowl", "decor", [(4, 0, 4, 12, 3, 12, "metal"), (5, 3, 5, 11, 3.6, 11, "food")], {"metal": "red_concrete", "food": "brown_concrete"}, ["iron", "red_concrete", "bone"])
# kitchen
piece("toaster", "decor", [(3, 0, 5, 13, 6, 11, "metal"), (5, 6, 6, 7, 7, 10, "dark"), (9, 6, 6, 11, 7, 10, "dark"), (12, 2, 4.4, 13.4, 5, 5, "dark")],
      {"metal": M_METAL, "dark": M_DARK}, ["iron"] * 4 + ["redstone", "black_concrete"])
piece("blender", "decor", [(4, 0, 4, 12, 4, 12, "dark"), (5, 4, 5, 11, 12, 11, "glass"), (4, 12, 4, 12, 14, 12, "dark")],
      {"dark": M_DARK, "glass": "light_blue_concrete"}, ["iron"] * 3 + ["glass"] * 3 + ["black_concrete"])
piece("kettle", "decor", [(4, 0, 4, 12, 2, 12, "dark"), (5, 2, 5, 11, 9, 11, "metal"), (6, 9, 6, 10, 11, 10, "metal"), (11, 3, 7, 15, 4, 9, "metal"), (1, 4, 7, 5, 9, 9, "dark")],
      {"dark": M_DARK, "metal": M_METAL}, ["iron"] * 5 + ["black_concrete"])
piece("cutting_board", "decor", [(2, 0, 3, 14, 1, 13, "wood"), (5, 1, 5, 11, 2, 7, "veg"), (4, 1, 9, 12, 2, 10, "knife")],
      {"wood": "{w}_planks", "veg": "orange_concrete", "knife": M_METAL}, ["wood"] * 2 + ["carrot", "iron"], variants="wood")
piece("kitchen_cart", "storage", [(1, 10, 2, 15, 12, 14, "wood"), (1, 0, 2, 3, 10, 4, "metal"), (13, 0, 2, 15, 10, 4, "metal"), (1, 0, 12, 3, 10, 14, "metal"), (13, 0, 12, 15, 10, 14, "metal"), (3, 4, 3, 13, 5, 13, "wood")],
      {"wood": "{w}_planks", "metal": M_METAL}, ["wood"] * 3 + ["iron"] * 4, variants="wood")
piece("bread_box", "storage", [(2, 0, 4, 14, 8, 12, "wood"), (2, 8, 4, 14, 9, 12, "wood2"), (7, 9, 7, 9, 10, 9, "metal")],
      {"wood": "{w}_planks", "wood2": "{w}_planks", "metal": M_METAL}, ["wood"] * 5 + ["bread"], variants="wood")
piece("spice_rack", "decor", [(1, 5, 12, 15, 6, 16, "wood"), (1, 10, 12, 15, 11, 16, "wood"), (1, 5, 12, 2, 11, 16, "wood"), (14, 5, 12, 15, 11, 16, "wood"),
                              (3, 6, 13, 5, 9, 15, "jar"), (7, 6, 13, 9, 9, 15, "jar2"), (11, 6, 13, 13, 9, 15, "jar")],
      {"wood": "{w}_planks", "jar": "red_concrete", "jar2": "yellow_concrete"}, ["wood"] * 4 + ["glass"] * 2 + ["red_dye", "yellow_dye"], variants="wood")
piece("high_chair", "seat", [(2, 0, 2, 4, 8, 4, "wood"), (12, 0, 2, 14, 8, 4, "wood"), (2, 0, 12, 4, 8, 14, "wood"), (12, 0, 12, 14, 8, 14, "wood"), (2, 8, 2, 14, 10, 14, "wood"), (2, 10, 12, 14, 16, 14, "wood"), (2, 10, 2, 14, 11, 4, "wood")],
      {"wood": "{w}_planks"}, ["wood"] * 5 + ["stick"] * 3, variants="wood")
piece("freezer", "storage", [(0, 0, 1, 16, 12, 15, "white"), (0, 12, 0, 16, 14, 16, "metal"), (6, 14, 7, 10, 15, 9, "dark"), (1, 1, 0.4, 15, 2, 1, "metal")],
      {"white": M_WHITE, "metal": M_METAL, "dark": M_DARK}, ["iron"] * 4 + ["white_concrete"] * 3 + ["ice"])
piece("water_cooler", "decor", [(3, 0, 3, 13, 9, 13, "white"), (4, 9, 4, 12, 16, 12, "water"), (5, 5, 2.4, 6, 6, 3, "blue"), (10, 5, 2.4, 11, 6, 3, "red")],
      {"white": M_WHITE, "water": "light_blue_concrete", "blue": "blue_concrete", "red": "red_concrete"}, ["white_concrete"] * 3 + ["glass"] * 3 + ["water_bucket"])
# bedroom / bath
piece("bunk_bed", "decor", [(0, 0, 0, 2, 16, 2, "wood"), (14, 0, 0, 16, 16, 2, "wood"), (0, 0, 14, 2, 16, 16, "wood"), (14, 0, 14, 16, 16, 16, "wood"),
                            (2, 3, 1, 14, 5, 15, "wood2"), (2, 5, 2, 14, 7, 14, "cloth"), (2, 10, 1, 14, 12, 15, "wood2"), (2, 12, 2, 14, 14, 14, "cloth"), (0, 14, 0, 16, 15, 2, "wood")],
      {"wood": "{w}_planks", "wood2": "{w}_planks", "cloth": "{c}_wool"}, ["wood"] * 7 + ["cloth"] * 2, variants="wood")
piece("cot", "decor", [(0, 0, 0, 2, 5, 2, "wood"), (14, 0, 0, 16, 5, 2, "wood"), (0, 0, 14, 2, 5, 16, "wood"), (14, 0, 14, 16, 5, 16, "wood"), (1, 5, 1, 15, 7, 15, "wood2"), (2, 7, 2, 14, 9, 14, "cloth"), (2, 9, 2, 14, 10, 6, "cloth2")],
      {"wood": "{w}_planks", "wood2": "{w}_planks", "cloth": "{c}_wool", "cloth2": "white_wool"}, ["wood"] * 5 + ["cloth"] * 3, variants="cloth")
piece("folding_screen", "decor", [(0, 0, 7, 5, 16, 8, "cloth"), (5, 0, 5, 10, 16, 6, "cloth"), (10, 0, 7, 16, 16, 8, "cloth"), (0, 15, 7, 16, 16, 8, "wood"), (0, 0, 7, 16, 1, 8, "wood")],
      {"wood": "oak_planks", "cloth": "{c}_wool"}, ["stick"] * 4 + ["cloth"] * 4, variants="cloth")
piece("jewelry_box", "storage", [(3, 0, 5, 13, 5, 11, "wood"), (3, 5, 5, 13, 7, 11, "wood2"), (7, 4, 4.6, 9, 6, 5, "gold")],
      {"wood": "dark_oak_planks", "wood2": "red_wool", "gold": "gold_block"}, ["dark_oak_planks"] * 3 + ["gold_ingot", "red_wool"])
piece("alarm_clock", "decor", [(4, 0, 6, 12, 7, 10, "red"), (5, 1, 5.6, 11, 6, 6, "face"), (3, 6, 7, 5, 8, 9, "metal"), (11, 6, 7, 13, 8, 9, "metal")],
      {"red": "red_concrete", "face": M_WHITE, "metal": M_METAL}, ["red_concrete"] * 2 + ["clock", "iron"])
piece("laundry_basket", "storage", [(3, 0, 3, 13, 1, 13, "wood"), (3, 1, 3, 4, 9, 13, "wood"), (12, 1, 3, 13, 9, 13, "wood"), (4, 1, 3, 12, 9, 4, "wood"), (4, 1, 12, 12, 9, 13, "wood"), (4, 5, 4, 12, 10, 12, "cloth")],
      {"wood": "{w}_planks", "cloth": "{c}_wool"}, ["wood"] * 4 + ["stick"] * 2 + ["cloth"] * 2, variants="cloth")
piece("ironing_board", "decor", [(2, 11, 1, 14, 12, 15, "cloth"), (3, 0, 3, 4, 11, 4, "metal"), (12, 0, 3, 13, 11, 4, "metal"), (3, 0, 12, 4, 11, 13, "metal"), (12, 0, 12, 13, 11, 13, "metal"), (11, 12, 6, 14, 14, 10, "metal")],
      {"cloth": "white_wool", "metal": M_METAL}, ["iron"] * 5 + ["white_wool"] * 2)
piece("sewing_machine", "decor", [(2, 0, 4, 14, 3, 12, "wood"), (3, 3, 6, 13, 6, 10, "dark"), (10, 6, 6, 13, 11, 10, "dark"), (5, 8, 7, 11, 10, 9, "dark"), (11, 3, 5.4, 12, 5, 6, "metal")],
      {"wood": "{w}_planks", "dark": M_DARK, "metal": M_METAL}, ["wood"] * 2 + ["iron"] * 3 + ["black_concrete"] * 2, variants="wood")
piece("bathroom_scale", "decor", [(3, 0, 3, 13, 1, 13, "white"), (5, 1, 4.6, 11, 1.4, 7, "dark")], {"white": M_WHITE, "dark": M_DARK}, ["white_concrete"] * 2 + ["iron", "redstone"])
piece("toilet_paper_holder", "decor", [(2, 8, 14, 14, 9, 16, "metal"), (4, 6, 12, 12, 11, 14, "white"), (3, 7, 13, 4, 10, 16, "metal")], {"metal": M_METAL, "white": M_WHITE}, ["iron"] * 3 + ["paper"] * 2)
piece("bathroom_cabinet", "storage", [(2, 4, 11, 14, 14, 16, "wood"), (3, 5, 10.4, 13, 13, 11, "glass"), (4, 7, 9.6, 5, 11, 10.4, "metal")],
      {"wood": "{w}_planks", "glass": "light_blue_concrete", "metal": M_METAL}, ["wood"] * 5 + ["glass"] * 2 + ["iron"], variants="wood")
# office / school
piece("whiteboard", "decor", [(0, 6, 13, 16, 16, 15, "metal"), (1, 7, 12.6, 15, 15, 13, "white")], {"metal": M_METAL, "white": M_WHITE}, ["iron"] * 4 + ["white_concrete"] * 3 + ["dye"] if False else ["iron"] * 4 + ["white_concrete"] * 3)
piece("safe", "storage", [(1, 0, 1, 15, 14, 15, "metal"), (2, 1, 0.4, 14, 13, 1, "dark"), (11, 6, 0, 13, 8, 0.6, "gold"), (3, 0, 0, 5, 1, 1, "dark")],
      {"metal": "gray_concrete", "dark": M_DARK, "gold": "gold_block"}, ["iron"] * 6 + ["gold_ingot", "redstone", "gray_concrete"])
piece("projector", "decor", [(3, 0, 4, 13, 5, 12, "white"), (13, 1.5, 6, 15, 3.5, 8, "dark"), (5, 5, 6, 11, 6, 10, "metal")], {"white": M_WHITE, "dark": M_DARK, "metal": M_METAL}, ["iron"] * 3 + ["white_concrete"] * 3 + ["glass"] * 2)
piece("plant_stand", "decor", [(5, 0, 5, 11, 1, 11, "wood"), (7, 1, 7, 9, 8, 9, "wood"), (3, 8, 3, 13, 9, 13, "wood"), (4, 9, 4, 12, 13, 12, "pot"), (3, 12, 3, 13, 16, 13, "leaf")],
      {"wood": "{w}_planks", "pot": "terracotta", "leaf": "green_wool"}, ["wood"] * 3 + ["terracotta"] * 2 + ["green_wool", "sapling"], variants="wood")
piece("shredder", "storage", [(2, 0, 3, 14, 10, 13, "dark"), (3, 10, 4, 13, 12, 12, "metal"), (4, 12, 6, 12, 13, 10, "dark")], {"dark": M_DARK, "metal": M_METAL}, ["iron"] * 4 + ["black_concrete"] * 3 + ["shears"])
# garden / outdoor
piece("garden_arch", "decor", [(0, 0, 6, 2, 16, 10, "wood"), (14, 0, 6, 16, 16, 10, "wood"), (0, 14, 6, 16, 16, 10, "wood"), (2, 11, 7, 14, 14, 9, "leaf"), (0, 6, 7, 2, 14, 9, "bloom"), (14, 6, 7, 16, 14, 9, "bloom")],
      {"wood": "{w}_planks", "leaf": "green_wool", "bloom": "pink_wool"}, ["wood"] * 5 + ["green_wool"] * 2 + ["poppy"], variants="wood")
piece("trellis", "decor", [(0, 0, 14, 16, 16, 15, "wood"), (1, 1, 13, 3, 15, 14, "leaf"), (6, 3, 13, 8, 12, 14, "leaf"), (11, 1, 13, 14, 14, 14, "bloom")], {"wood": "{w}_planks", "leaf": "green_wool", "bloom": "red_wool"}, ["wood"] * 4 + ["stick"] * 3 + ["green_wool"], variants="wood")
piece("compost_bin", "storage", [(1, 0, 1, 15, 1, 15, "wood"), (1, 1, 1, 2, 12, 15, "wood"), (14, 1, 1, 15, 12, 15, "wood"), (2, 1, 1, 14, 12, 2, "wood"), (2, 1, 14, 14, 12, 15, "wood"), (2, 1, 2, 14, 9, 14, "soil")],
      {"wood": "{w}_planks", "soil": "coarse_dirt"}, ["wood"] * 6 + ["dirt"], variants="wood")
piece("rain_barrel", "storage", [(3, 0, 3, 13, 14, 13, "wood"), (2, 3, 2, 14, 4, 14, "metal"), (2, 10, 2, 14, 11, 14, "metal"), (4, 14, 4, 12, 15, 12, "water"), (13, 2, 7, 15, 4, 9, "metal")],
      {"wood": "{w}_planks", "metal": M_METAL, "water": "light_blue_concrete"}, ["wood"] * 5 + ["iron"] * 3 + ["bucket"], variants="wood")
piece("watering_can", "decor", [(4, 0, 4, 12, 7, 12, "metal"), (12, 3, 7, 16, 4, 9, "metal"), (15, 3, 6.4, 16, 5, 9.6, "metal"), (6, 7, 4, 10, 10, 5, "metal"), (4, 7, 11, 10, 8, 12, "metal")], {"metal": "green_concrete"}, ["iron"] * 4 + ["green_concrete"] * 2 + ["bucket"])
piece("flagpole", "decor", [(5, 0, 5, 11, 2, 11, "stone"), (7.5, 2, 7.5, 8.5, 16, 8.5, "metal"), (8.5, 9, 7.8, 15, 15, 8.2, "cloth")], {"stone": "stone", "metal": M_METAL, "cloth": "{c}_wool"}, ["iron"] * 2 + ["stone"] * 2 + ["cloth"] * 3 + ["stick"], variants="cloth")
piece("sundial", "decor", [(5, 0, 5, 11, 7, 11, "stone"), (3, 7, 3, 13, 9, 13, "stone2"), (7.5, 9, 4, 8.5, 12, 12, "gold")], {"stone": "stone", "stone2": "smooth_stone", "gold": "gold_block"}, ["stone"] * 4 + ["smooth_stone"] * 2 + ["gold_ingot", "clock"])
piece("stone_lantern", "glow", [(4, 0, 4, 12, 2, 12, "stone"), (6, 2, 6, 10, 6, 10, "stone"), (4, 6, 4, 12, 7, 12, "stone"), (5, 7, 5, 11, 11, 11, "lamp"), (3, 11, 3, 13, 13, 13, "stone"), (6, 13, 6, 10, 15, 10, "stone")],
      {"stone": "stone_bricks", "lamp": "glowstone"}, ["stone_bricks"] * 6 + ["glowstone_dust", "torch"], light=14)
piece("tiki_torch", "glow", [(7.5, 0, 7.5, 8.5, 12, 8.5, "wood"), (6, 12, 6, 10, 14, 10, "metal"), (6.5, 14, 6.5, 9.5, 16, 9.5, "fire")], {"wood": "bamboo_planks", "metal": M_METAL, "fire": "orange_concrete"}, ["stick"] * 4 + ["coal", "iron"], light=14)
piece("porch_swing", "seat", [(0, 0, 4, 2, 16, 6, "wood"), (14, 0, 4, 16, 16, 6, "wood"), (0, 14, 4, 16, 16, 6, "wood"), (2, 4, 6, 14, 6, 12, "wood2"), (2, 6, 11, 14, 11, 12, "wood2"), (3, 6, 5, 3.6, 14, 6, "metal"), (12.4, 6, 5, 13, 14, 6, "metal")],
      {"wood": "{w}_planks", "wood2": "{w}_planks", "metal": M_METAL}, ["wood"] * 6 + ["iron"] * 2, variants="wood")
piece("pizza_oven", "glow", [(1, 0, 1, 15, 3, 15, "stone"), (2, 3, 2, 14, 10, 14, "brick"), (4, 4, 0.4, 12, 8, 2, "dark"), (6, 10, 6, 10, 15, 10, "brick"), (5, 3, 0.2, 11, 4, 1, "fire")],
      {"stone": "stone_bricks", "brick": "bricks", "dark": M_DARK, "fire": "orange_concrete"}, ["bricks"] * 5 + ["stone_bricks"] * 3 + ["coal"], light=10)
piece("picnic_basket", "storage", [(2, 0, 4, 14, 7, 12, "wood"), (2, 7, 4, 14, 8, 8, "cloth"), (2, 7, 9, 14, 8, 12, "cloth2"), (4, 8, 7, 12, 10, 9, "wood")],
      {"wood": "bamboo_planks", "cloth": "red_wool", "cloth2": "white_wool"}, ["stick"] * 4 + ["red_wool", "white_wool", "string"])
piece("sun_lounger", "seat", [(1, 0, 1, 3, 4, 3, "metal"), (13, 0, 1, 15, 4, 3, "metal"), (1, 0, 13, 3, 4, 15, "metal"), (13, 0, 13, 15, 4, 15, "metal"), (2, 4, 1, 14, 6, 11, "cloth"), (2, 6, 11, 14, 14, 13, "cloth")],
      {"metal": M_METAL, "cloth": "{c}_wool"}, ["iron"] * 3 + ["cloth"] * 4, variants="cloth")
piece("surfboard_stand", "decor", [(4, 0, 6, 12, 2, 10, "wood"), (7, 2, 7, 9, 6, 9, "wood"), (4, 6, 6.6, 12, 16, 9.4, "board"), (7.2, 7, 6.2, 8.8, 15, 6.6, "stripe")],
      {"wood": "{w}_planks", "board": "{c}_concrete", "stripe": M_WHITE}, ["wood"] * 3 + ["{c}_dye"] * 2 + ["white_concrete"] * 2, variants="cloth")
piece("life_ring", "decor", [(2, 4, 7.5, 14, 5, 8.5, "red"), (2, 11, 7.5, 14, 12, 8.5, "red"), (2, 5, 7.5, 3, 11, 8.5, "white"), (13, 5, 7.5, 14, 11, 8.5, "white"), (7.5, 11, 7, 8.5, 16, 9, "metal")],
      {"red": "red_concrete", "white": M_WHITE, "metal": M_METAL}, ["red_concrete"] * 3 + ["white_concrete"] * 3 + ["string"])
piece("basketball_hoop", "decor", [(7, 0, 7, 9, 12, 9, "metal"), (2, 12, 8, 14, 16, 9, "board"), (5, 11, 9, 11, 12, 11, "orange"), (5, 6, 9, 6, 11, 10, "net"), (10, 6, 9, 11, 11, 10, "net")],
      {"metal": M_METAL, "board": M_WHITE, "orange": "orange_concrete", "net": "white_wool"}, ["iron"] * 4 + ["white_concrete"] * 2 + ["orange_concrete", "string"])
piece("tennis_net", "decor", [(0, 0, 7, 2, 12, 9, "metal"), (14, 0, 7, 16, 12, 9, "metal"), (2, 8, 7.6, 14, 12, 8.4, "net"), (2, 11.5, 7.4, 14, 12.5, 8.6, "white")],
      {"metal": M_METAL, "net": "white_wool", "white": M_WHITE}, ["iron"] * 3 + ["string"] * 4 + ["white_wool"])
piece("skate_ramp", "decor", [(0, 0, 0, 16, 2, 4, "wood"), (0, 2, 4, 16, 4, 8, "wood"), (0, 4, 8, 16, 6, 12, "wood"), (0, 6, 12, 16, 8, 16, "wood"), (0, 0, 14, 16, 1, 16, "dark")],
      {"wood": "{w}_planks", "dark": M_DARK}, ["wood"] * 7, variants="wood")
piece("gazebo_bench", "seat", [(0, 6, 2, 16, 8, 12, "wood"), (0, 8, 11, 16, 14, 13, "wood"), (1, 0, 3, 3, 6, 11, "wood"), (13, 0, 3, 15, 6, 11, "wood")],
      {"wood": "{w}_planks"}, ["wood"] * 6 + ["stick"], variants="wood")
piece("bell_post", "decor", [(7, 0, 7, 9, 14, 9, "wood"), (2, 12, 7, 14, 14, 9, "wood"), (6, 6, 6, 10, 12, 10, "metal"), (7, 4, 7, 9, 6, 9, "metal")],
      {"wood": "{w}_planks", "metal": "gold_block"}, ["wood"] * 4 + ["gold_ingot"] * 2 + ["stick"], variants="wood")

# ================================================================== BATCH 4: bathroom, kitchen, living room, bedroom, closet, basement, outdoors, pool, yard
# ---------------------------------------------------------------- BATHROOM
piece("bidet", "decor", [(4, 0, 2, 12, 6, 12, "white"), (3, 6, 1, 13, 8, 12, "white"), (7, 8, 12, 9, 10, 13, "metal"), (5, 8.2, 3, 11, 8.6, 10, "water")],
      {"white": M_WHITE, "metal": M_METAL, "water": "light_blue_concrete"}, ["white_concrete"] * 4 + ["iron"] * 2 + ["bucket"])
piece("urinal", "decor", [(4, 3, 13, 12, 12, 16, "white"), (5, 2, 11, 11, 8, 13, "white"), (7, 12, 14, 9, 14, 16, "metal"), (6, 0, 12, 10, 3, 14, "metal")],
      {"white": M_WHITE, "metal": M_METAL}, ["white_concrete"] * 4 + ["iron"] * 3)
piece("double_vanity", "storage", [(0, 0, 2, 16, 12, 14, "wood"), (0, 12, 1, 16, 14, 15, "top"), (1, 12.1, 3, 7, 14.4, 13, "basin"), (9, 12.1, 3, 15, 14.4, 13, "basin"), (3, 14, 12, 5, 17, 13, "metal"), (11, 14, 12, 13, 17, 13, "metal")],
      {"wood": "{w}_planks", "top": M_WHITE, "basin": "light_gray_concrete", "metal": M_METAL}, ["wood"] * 5 + ["white_concrete"] * 2 + ["iron"] * 2, variants="wood")
piece("shower_stall", "decor", [(0, 0, 0, 16, 1, 16, "white"), (0, 1, 0, 1, 16, 16, "glass"), (15, 1, 0, 16, 16, 16, "glass"), (1, 1, 15, 15, 16, 16, "tile"), (7, 12, 12, 9, 16, 14, "metal"), (5, 14, 8, 11, 16, 12, "metal")],
      {"white": M_WHITE, "glass": "light_blue_concrete", "tile": "white_terracotta", "metal": M_METAL}, ["iron"] * 3 + ["glass"] * 4 + ["white_concrete"] * 2)
piece("bath_mat", "decor", [(1, 0, 3, 15, 1, 13, "cloth"), (2, 1, 4, 14, 1.2, 12, "cloth2")], {"cloth": "{c}_wool", "cloth2": "white_wool"}, ["cloth"] * 3, variants="cloth")
piece("towel_stack", "decor", [(3, 0, 4, 13, 3, 12, "cloth"), (4, 3, 5, 12, 6, 11, "cloth2"), (5, 6, 6, 11, 8, 10, "cloth")], {"cloth": "{c}_wool", "cloth2": "white_wool"}, ["cloth"] * 4, variants="cloth")
piece("soap_dispenser", "decor", [(5, 0, 5, 11, 7, 11, "pink"), (6, 7, 6, 10, 9, 10, "metal"), (9, 8, 7, 12, 9, 9, "metal")], {"pink": "pink_concrete", "metal": M_METAL}, ["pink_concrete"] * 3 + ["iron"])
piece("toilet_brush", "decor", [(5, 0, 5, 11, 6, 11, "white"), (7.5, 6, 7.5, 8.5, 14, 8.5, "dark"), (6.5, 12, 6.5, 9.5, 15, 9.5, "dark")], {"white": M_WHITE, "dark": M_DARK}, ["white_concrete"] * 2 + ["stick"] * 2 + ["black_concrete"])
piece("sauna_bench", "seat", [(0, 6, 2, 16, 8, 14, "wood"), (0, 0, 2, 2, 6, 14, "wood"), (14, 0, 2, 16, 6, 14, "wood"), (0, 8, 12, 16, 14, 14, "wood")], {"wood": "spruce_planks"}, ["spruce_planks"] * 6 + ["stick"])
piece("hot_tub", "glow", [(0, 0, 0, 16, 9, 2, "wood"), (0, 0, 14, 16, 9, 16, "wood"), (0, 0, 2, 2, 9, 14, "wood"), (14, 0, 2, 16, 9, 14, "wood"), (2, 0, 2, 14, 7, 14, "water"), (7, 9, 7, 9, 10, 9, "metal")],
      {"wood": "{w}_planks", "water": "light_blue_concrete", "metal": M_METAL}, ["wood"] * 5 + ["water_bucket", "iron", "lantern"], light=6, variants="wood")
piece("linen_closet", "storage", [(1, 0, 2, 15, 16, 14, "wood"), (2, 1, 1.2, 14, 7, 2, "cloth"), (2, 8, 1.2, 14, 15, 2, "cloth2"), (7.5, 6, 0.8, 8.5, 10, 1.4, "metal")],
      {"wood": "{w}_planks", "cloth": "white_wool", "cloth2": "light_blue_wool", "metal": M_METAL}, ["wood"] * 6 + ["white_wool"] * 2, variants="wood")
# ---------------------------------------------------------------- KITCHEN
piece("pantry_cabinet", "storage", [(1, 0, 2, 15, 16, 14, "wood"), (2, 1, 1.2, 8, 15, 2, "wood2"), (8, 1, 1.2, 14, 15, 2, "wood2"), (7, 7, 0.8, 7.8, 10, 1.4, "metal"), (8.2, 7, 0.8, 9, 10, 1.4, "metal")],
      {"wood": "{w}_planks", "wood2": "{w}_planks", "metal": M_METAL}, ["wood"] * 7 + ["iron"], variants="wood")
piece("kitchen_island", "storage", [(0, 0, 0, 16, 13, 16, "wood"), (0, 13, 0, 16, 15, 16, "top"), (1, 2, -0.2, 15, 7, 0.4, "dark"), (3, 7, -0.4, 13, 8, 0.2, "metal")],
      {"wood": "{w}_planks", "top": M_STONE, "dark": M_DARK, "metal": M_METAL}, ["wood"] * 6 + ["smooth_stone"] * 2 + ["iron"], variants="wood")
piece("dish_rack", "decor", [(1, 0, 3, 15, 1, 13, "metal"), (1, 1, 3, 15, 2, 4, "metal"), (1, 1, 12, 15, 2, 13, "metal"), (3, 1, 5, 4, 8, 11, "plate"), (6, 1, 5, 7, 8, 11, "plate"), (9, 1, 5, 10, 8, 11, "plate"), (12, 1, 5, 13, 8, 11, "plate")],
      {"metal": M_METAL, "plate": M_WHITE}, ["iron"] * 4 + ["white_concrete"] * 2 + ["stick"])
piece("pot_rack", "decor", [(1, 13, 7, 15, 14, 9, "metal"), (2, 13, 7.4, 3, 16, 8.6, "metal"), (13, 13, 7.4, 14, 16, 8.6, "metal"), (3, 6, 6, 6, 13, 10, "dark"), (8, 8, 6, 11, 13, 10, "dark"), (12, 7, 7, 13.6, 13, 8.6, "dark")],
      {"metal": M_METAL, "dark": M_DARK}, ["iron"] * 5 + ["black_concrete"])
piece("stand_mixer", "decor", [(3, 0, 3, 13, 2, 13, "red"), (9, 2, 5, 13, 11, 11, "red"), (3, 9, 5, 13, 13, 11, "red"), (4, 2, 6, 8, 7, 10, "metal")],
      {"red": "red_concrete", "metal": M_METAL}, ["red_concrete"] * 4 + ["iron"] * 3)
piece("knife_block", "decor", [(4, 0, 4, 12, 9, 12, "wood"), (5, 9, 5, 6, 12, 11, "metal"), (7, 9, 5, 8, 14, 11, "metal"), (9, 9, 5, 10, 11, 11, "metal")],
      {"wood": "{w}_planks", "metal": M_METAL}, ["wood"] * 3 + ["iron"] * 3, variants="wood")
piece("fruit_bowl", "decor", [(4, 0, 4, 12, 2, 12, "wood"), (3, 2, 3, 13, 4, 13, "wood"), (5, 4, 5, 8, 7, 8, "red"), (8, 4, 7, 11, 7, 10, "yellow"), (6, 4, 9, 9, 6, 12, "green")],
      {"wood": "{w}_planks", "red": "red_wool", "yellow": "yellow_wool", "green": "lime_wool"}, ["wood"] * 3 + ["apple", "carrot"], variants="wood")
piece("pedal_bin", "storage", [(4, 0, 4, 12, 11, 12, "metal"), (3, 11, 3, 13, 13, 13, "dark"), (5, 0, 2, 11, 1, 4, "dark")], {"metal": M_METAL, "dark": M_DARK}, ["iron"] * 5 + ["black_concrete"])
piece("butcher_block", "storage", [(0, 0, 0, 16, 11, 16, "wood"), (0, 11, 0, 16, 15, 16, "top"), (1, 2, -0.2, 15, 9, 0.4, "wood2")],
      {"wood": "{w}_planks", "top": "stripped_oak_log", "wood2": "{w}_planks"}, ["wood"] * 6 + ["iron"] * 2, variants="wood")
piece("cooktop", "decor", [(0, 0, 0, 16, 2, 16, "dark"), (2, 2, 2, 7, 2.6, 7, "ring"), (9, 2, 2, 14, 2.6, 7, "ring"), (2, 2, 9, 7, 2.6, 14, "ring"), (9, 2, 9, 14, 2.6, 14, "ring")],
      {"dark": M_DARK, "ring": "gray_concrete"}, ["black_concrete"] * 3 + ["iron"] * 2)
piece("spice_grinder", "decor", [(5, 0, 5, 11, 2, 11, "wood"), (6, 2, 6, 10, 9, 10, "wood"), (5, 9, 5, 11, 11, 11, "metal"), (7.5, 11, 7.5, 8.5, 13, 8.5, "metal")], {"wood": "{w}_planks", "metal": M_METAL}, ["wood"] * 3 + ["iron"], variants="wood")
# ---------------------------------------------------------------- LIVING ROOM
piece("sectional_corner", "seat", [(0, 0, 0, 16, 8, 16, "cloth"), (0, 8, 12, 16, 16, 16, "cloth"), (0, 8, 0, 4, 16, 12, "cloth"), (4, 8, 4, 12, 10, 12, "cloth2")],
      {"cloth": "{c}_wool", "cloth2": "white_wool"}, ["cloth"] * 6 + ["stick"] * 2, variants="cloth")
piece("chaise_longue", "seat", [(1, 0, 0, 15, 6, 16, "cloth"), (1, 6, 10, 15, 14, 16, "cloth"), (0, 0, 0, 2, 9, 16, "wood"), (14, 0, 0, 16, 9, 16, "wood")],
      {"cloth": "{c}_wool", "wood": "{w}_planks"}, ["cloth"] * 5 + ["wood"] * 2 + ["stick"], variants="cloth")
piece("floor_cushion", "seat", [(2, 0, 2, 14, 3, 14, "cloth"), (3, 3, 3, 13, 4, 13, "cloth2")], {"cloth": "{c}_wool", "cloth2": "white_wool"}, ["cloth"] * 3, variants="cloth")
piece("wall_tv", "decor", [(0.5, 2, 14, 15.5, 14, 16, "dark"), (1.5, 3, 13.6, 14.5, 13, 14, "screen"), (6, 0, 15, 10, 2, 16, "metal")], {"dark": M_DARK, "screen": "cyan_concrete", "metal": M_METAL}, ["black_concrete"] * 2 + ["glass"] * 2 + ["iron", "redstone"])
piece("ladder_shelf", "decor", [(1, 0, 8, 2, 16, 10, "wood"), (14, 0, 8, 15, 16, 10, "wood"), (1, 2, 8, 15, 3, 14, "wood"), (1, 6, 8, 15, 7, 12, "wood"), (1, 10, 8, 15, 11, 11, "wood"), (3, 3, 9, 7, 6, 13, "books"), (9, 7, 9, 13, 10, 11, "pot")],
      {"wood": "{w}_planks", "books": "bookshelf", "pot": "terracotta"}, ["wood"] * 5 + ["stick"] * 3, variants="wood")
piece("display_cabinet", "storage", [(1, 0, 2, 15, 16, 14, "wood"), (2, 2, 1.2, 14, 14, 2, "glass"), (3, 7, 4, 6, 8, 12, "wood"), (8, 3, 5, 11, 6, 11, "gold")],
      {"wood": "{w}_planks", "glass": "light_blue_concrete", "gold": "gold_block"}, ["wood"] * 6 + ["glass"] * 3, variants="wood")
piece("candelabra", "glow", [(5, 0, 5, 11, 1, 11, "gold"), (7.5, 1, 7.5, 8.5, 8, 8.5, "gold"), (2, 8, 7.5, 14, 9, 8.5, "gold"), (3, 9, 7, 4, 13, 9, "candle"), (7.5, 9, 7, 8.5, 14, 9, "candle"), (12, 9, 7, 13, 13, 9, "candle")],
      {"gold": "gold_block", "candle": M_WHITE}, ["gold_ingot"] * 3 + ["white_concrete"] * 3 + ["torch"], light=13)
piece("magazine_rack", "decor", [(2, 0, 4, 14, 1, 12, "wood"), (2, 1, 4, 3, 8, 12, "wood"), (13, 1, 4, 14, 8, 12, "wood"), (4, 1, 5, 7, 7, 11, "mag"), (8, 1, 5, 11, 6, 11, "mag2")],
      {"wood": "{w}_planks", "mag": "red_wool", "mag2": "blue_wool"}, ["wood"] * 3 + ["paper"] * 2, variants="wood")
piece("game_console", "decor", [(2, 0, 4, 14, 3, 12, "dark"), (3, 3, 6, 6, 5, 10, "metal"), (10, 0.5, 2, 13, 2, 3, "pad"), (3, 0.5, 2, 6, 2, 3, "pad")],
      {"dark": M_DARK, "metal": M_METAL, "pad": "gray_concrete"}, ["black_concrete"] * 3 + ["redstone", "iron"])
piece("arcade_machine", "glow", [(2, 0, 2, 14, 16, 14, "body"), (3, 9, 1.2, 13, 15, 2, "screen"), (3, 6, 0.6, 13, 8, 2, "panel"), (4, 6, 0.2, 5, 7.6, 0.8, "stick"), (9, 6, 0.2, 11, 7.2, 0.8, "btn")],
      {"body": "blue_concrete", "screen": "cyan_concrete", "panel": M_DARK, "stick": "red_concrete", "btn": "yellow_concrete"}, ["blue_concrete"] * 4 + ["glass"] * 2 + ["redstone"] * 2 + ["iron"], light=8)
piece("jukebox_stand", "decor", [(2, 0, 2, 14, 6, 14, "wood"), (3, 6, 3, 13, 12, 13, "wood2"), (5, 12, 5, 11, 13, 11, "dark")], {"wood": "{w}_planks", "wood2": "{w}_planks", "dark": M_DARK}, ["wood"] * 6 + ["diamond"], variants="wood")
# ---------------------------------------------------------------- BEDROOM
piece("single_bed", "decor", [(1, 0, 0, 15, 4, 16, "wood"), (2, 4, 1, 14, 7, 16, "cloth"), (3, 7, 1.5, 13, 9, 6, "cloth2"), (1, 4, 0, 15, 12, 1, "wood")],
      {"wood": "{w}_planks", "cloth": "{c}_wool", "cloth2": "white_wool"}, ["wood"] * 3 + ["cloth"] * 3 + ["white_wool"], variants="cloth")
piece("double_bed", "decor", [(0, 0, 0, 16, 4, 16, "wood"), (1, 4, 1, 15, 7, 16, "cloth"), (1.5, 7, 1.5, 7.5, 9, 6.5, "cloth2"), (8.5, 7, 1.5, 14.5, 9, 6.5, "cloth2"), (0, 4, 0, 16, 14, 1, "wood")],
      {"wood": "{w}_planks", "cloth": "{c}_wool", "cloth2": "white_wool"}, ["wood"] * 3 + ["cloth"] * 4 + ["white_wool", "stick"], variants="cloth")
piece("canopy_bed", "decor", [(0, 0, 0, 2, 16, 2, "wood"), (14, 0, 0, 16, 16, 2, "wood"), (0, 0, 14, 2, 16, 16, "wood"), (14, 0, 14, 16, 16, 16, "wood"), (0, 14, 0, 16, 16, 16, "cloth"), (2, 3, 2, 14, 5, 14, "wood"), (2, 5, 3, 14, 7, 14, "cloth2")],
      {"wood": "{w}_planks", "cloth": "white_wool", "cloth2": "{c}_wool"}, ["wood"] * 4 + ["white_wool"] * 3 + ["cloth"], variants="cloth")
piece("headboard", "decor", [(0, 0, 13, 16, 14, 16, "wood"), (1, 1, 12.4, 15, 13, 13, "cloth"), (0, 14, 13, 16, 16, 16, "wood2")],
      {"wood": "{w}_planks", "wood2": "{w}_planks", "cloth": "{c}_wool"}, ["wood"] * 4 + ["cloth"] * 2, variants="cloth")
piece("bed_bench", "seat", [(1, 0, 2, 15, 6, 14, "cloth"), (1, 6, 3, 15, 8, 13, "cloth2"), (1, 0, 2, 3, 4, 4, "wood"), (13, 0, 2, 15, 4, 4, "wood"), (1, 0, 12, 3, 4, 14, "wood"), (13, 0, 12, 15, 4, 14, "wood")],
      {"cloth": "{c}_wool", "cloth2": "white_wool", "wood": "{w}_planks"}, ["cloth"] * 3 + ["wood"] * 3, variants="cloth")
piece("rocking_chair", "seat", [(2, 0, 1, 4, 2, 15, "wood"), (12, 0, 1, 14, 2, 15, "wood"), (3, 2, 4, 5, 8, 6, "wood"), (11, 2, 4, 13, 8, 6, "wood"), (3, 8, 3, 13, 9, 11, "cloth"), (3, 9, 10, 13, 16, 12, "cloth")],
      {"wood": "{w}_planks", "cloth": "{c}_wool"}, ["wood"] * 4 + ["cloth"] * 2 + ["stick"] * 2, variants="wood")
piece("bedside_lamp", "lamp", [(5, 0, 5, 11, 1, 11, "wood"), (7, 1, 7, 9, 7, 9, "metal"), (3, 7, 3, 13, 13, 13, "shade")],
      {"wood": "{w}_planks", "metal": M_METAL, "shade": "pink_wool", "shade_on": "glowstone"}, ["wood", "iron", "pink_wool", "glowstone_dust"], light=11, variants="wood")
piece("standing_mirror", "decor", [(3, 0, 6, 5, 2, 10, "wood"), (11, 0, 6, 13, 2, 10, "wood"), (3, 2, 7, 13, 16, 9, "wood"), (4, 3, 6.4, 12, 15, 7, "glass")],
      {"wood": "{w}_planks", "glass": "light_blue_concrete"}, ["wood"] * 4 + ["glass"] * 3, variants="wood")
piece("baby_mobile", "decor", [(7.5, 10, 7.5, 8.5, 16, 8.5, "metal"), (2, 9, 7.5, 14, 10, 8.5, "metal"), (3, 4, 7.5, 5, 9, 8.5, "toy"), (7, 3, 7.5, 9, 9, 8.5, "toy2"), (11, 5, 7.5, 13, 9, 8.5, "toy")],
      {"metal": M_METAL, "toy": "yellow_wool", "toy2": "pink_wool"}, ["iron"] * 2 + ["yellow_wool", "pink_wool", "string"])
# ---------------------------------------------------------------- CLOSET / WARDROBE
piece("clothes_rail", "decor", [(0, 0, 6, 2, 16, 8, "metal"), (14, 0, 6, 16, 16, 8, "metal"), (0, 14, 6, 16, 16, 8, "metal"), (1, 4, 6.5, 15, 5, 7.5, "metal"), (0, 0, 5, 16, 1, 9, "metal")], {"metal": M_METAL}, ["iron"] * 6)
piece("hanging_clothes", "decor", [(0, 15, 7, 16, 16, 9, "metal"), (2, 4, 7.4, 6, 15, 8.6, "cloth"), (6.5, 6, 7.4, 10, 15, 8.6, "cloth2"), (10.5, 3, 7.4, 14, 15, 8.6, "cloth")],
      {"metal": M_METAL, "cloth": "{c}_wool", "cloth2": "white_wool"}, ["iron", "stick"] + ["cloth"] * 3, variants="cloth")
piece("shoe_cabinet", "storage", [(1, 0, 3, 15, 12, 13, "wood"), (2, 1, 2.2, 14, 4, 3, "wood2"), (2, 5, 2.2, 14, 8, 3, "wood2"), (2, 9, 2.2, 14, 11, 3, "wood2"), (7, 6, 1.8, 9, 7, 2.4, "metal")],
      {"wood": "{w}_planks", "wood2": "{w}_planks", "metal": M_METAL}, ["wood"] * 6 + ["leather"], variants="wood")
piece("hat_stand", "decor", [(7.5, 0, 7.5, 8.5, 12, 8.5, "wood"), (4, 0, 4, 12, 1, 12, "wood"), (4, 8, 7.5, 12, 9, 8.5, "wood"), (4.5, 9, 6.5, 6.5, 12, 9.5, "hat"), (9.5, 9, 6.5, 11.5, 12, 9.5, "hat")], {"wood": "{w}_planks", "hat": "black_wool"}, ["wood"] * 2 + ["stick"] * 2 + ["black_wool"], variants="wood")
piece("closet_shelf", "storage", [(0, 0, 4, 16, 2, 16, "wood"), (0, 7, 4, 16, 9, 16, "wood"), (0, 14, 4, 16, 16, 16, "wood"), (0, 0, 4, 2, 16, 16, "wood"), (14, 0, 4, 16, 16, 16, "wood"), (2, 2, 6, 7, 7, 14, "cloth"), (9, 9, 6, 14, 14, 14, "cloth2")],
      {"wood": "{w}_planks", "cloth": "{c}_wool", "cloth2": "white_wool"}, ["wood"] * 6 + ["cloth"] * 2, variants="wood")
piece("suitcase", "storage", [(2, 0, 4, 14, 9, 12, "case"), (4, 9, 6, 12, 11, 10, "metal"), (2, 3, 3.6, 14, 4, 4, "metal"), (3, 0, 12, 5, 1, 13, "metal"), (11, 0, 12, 13, 1, 13, "metal")],
      {"case": "{c}_concrete", "metal": M_METAL}, ["{c}_concrete"] * 4 + ["iron"] * 2 + ["leather"], variants="cloth")
piece("storage_trunk", "storage", [(1, 0, 3, 15, 8, 13, "wood"), (1, 8, 3, 15, 11, 13, "wood2"), (1, 3, 2.6, 15, 4, 3, "metal"), (1, 9, 2.6, 15, 10, 3, "metal"), (7, 6, 2.4, 9, 9, 3, "gold")],
      {"wood": "{w}_planks", "wood2": "{w}_planks", "metal": M_METAL, "gold": "gold_block"}, ["wood"] * 6 + ["iron"] * 2 + ["gold_ingot"], variants="wood")
piece("armoire", "storage", [(0, 0, 1, 16, 16, 15, "wood"), (1, 2, 0.2, 8, 15, 1, "wood2"), (8, 2, 0.2, 15, 15, 1, "wood2"), (0, 0, 0, 16, 2, 1, "wood"), (7, 7, -0.4, 7.8, 11, 0.4, "gold"), (8.2, 7, -0.4, 9, 11, 0.4, "gold")],
      {"wood": "{w}_planks", "wood2": "{w}_planks", "gold": "gold_block"}, ["wood"] * 8 + ["gold_ingot"], variants="wood")
piece("dressing_bench", "seat", [(1, 5, 3, 15, 7, 13, "cloth"), (1, 7, 4, 15, 8, 12, "cloth2"), (2, 0, 4, 4, 5, 6, "wood"), (12, 0, 4, 14, 5, 6, "wood"), (2, 0, 10, 4, 5, 12, "wood"), (12, 0, 10, 14, 5, 12, "wood")],
      {"cloth": "{c}_wool", "cloth2": "white_wool", "wood": "{w}_planks"}, ["cloth"] * 3 + ["wood"] * 3, variants="cloth")
piece("mannequin", "decor", [(5, 0, 5, 11, 1, 11, "wood"), (7.5, 1, 7.5, 8.5, 6, 8.5, "wood"), (5, 6, 6, 11, 14, 10, "cloth"), (6, 14, 6, 10, 16, 10, "skin")],
      {"wood": "{w}_planks", "cloth": "{c}_wool", "skin": "pink_wool"}, ["wood", "stick"] + ["cloth"] * 3 + ["pink_wool"], variants="cloth")
piece("garment_steamer", "decor", [(5, 0, 5, 11, 1, 11, "metal"), (7.5, 1, 7.5, 8.5, 14, 8.5, "metal"), (6, 8, 6, 10, 12, 10, "white"), (8, 13, 7, 12, 15, 9, "metal")], {"metal": M_METAL, "white": M_WHITE}, ["iron"] * 4 + ["white_concrete"])
# ---------------------------------------------------------------- BASEMENT
piece("workbench", "storage", [(0, 12, 0, 16, 14, 16, "wood"), (0, 0, 1, 2, 12, 15, "wood"), (14, 0, 1, 16, 12, 15, "wood"), (2, 4, 2, 14, 5, 14, "wood2"), (3, 14, 11, 5, 17, 13, "metal"), (10, 14, 4, 13, 15, 10, "tool")],
      {"wood": "{w}_planks", "wood2": "{w}_planks", "metal": M_METAL, "tool": "red_concrete"}, ["wood"] * 7 + ["iron"] * 2, variants="wood")
piece("tool_wall", "decor", [(0, 2, 15, 16, 14, 16, "wood"), (2, 4, 14, 3, 12, 15, "metal"), (5, 6, 14, 7, 12, 15, "tool"), (9, 5, 14, 10, 12, 15, "metal"), (12, 7, 14, 14, 11, 15, "tool")],
      {"wood": "oak_planks", "metal": M_METAL, "tool": "orange_concrete"}, ["oak_planks"] * 3 + ["iron"] * 3 + ["stick"] * 2)
piece("tool_chest", "storage", [(1, 0, 3, 15, 9, 13, "red"), (1, 9, 4, 15, 12, 12, "red"), (6, 5, 2.4, 10, 7, 3, "metal"), (1, 4, 2.6, 15, 5, 3, "metal")], {"red": "red_concrete", "metal": M_METAL}, ["iron"] * 4 + ["red_concrete"] * 4)
piece("water_heater", "decor", [(3, 0, 3, 13, 16, 13, "white"), (5, 2, 2.4, 11, 4, 3, "dark"), (4, 14, 4, 6, 16, 6, "metal"), (10, 14, 4, 12, 16, 6, "metal"), (6, 6, 2.2, 10, 8, 3, "metal")], {"white": M_WHITE, "dark": M_DARK, "metal": M_METAL}, ["iron"] * 5 + ["white_concrete"] * 3)
piece("boiler", "decor", [(2, 0, 2, 14, 12, 14, "metal"), (3, 12, 3, 13, 15, 13, "metal"), (6, 15, 6, 10, 16, 10, "dark"), (1, 3, 5, 2, 9, 11, "pipe"), (14, 3, 5, 15, 9, 11, "pipe"), (5, 4, 1.6, 11, 9, 2, "dark")],
      {"metal": "gray_concrete", "dark": M_DARK, "pipe": "copper_block"}, ["iron"] * 5 + ["copper_ingot"] * 3)
piece("metal_shelving", "storage", [(0, 0, 3, 1.5, 16, 13, "metal"), (14.5, 0, 3, 16, 16, 13, "metal"), (0, 1, 3, 16, 2, 13, "wood"), (0, 6, 3, 16, 7, 13, "wood"), (0, 11, 3, 16, 12, 13, "wood"), (2, 2, 5, 6, 6, 11, "box"), (9, 7, 5, 14, 11, 11, "box")],
      {"metal": M_METAL, "wood": "oak_planks", "box": "brown_wool"}, ["iron"] * 4 + ["oak_planks"] * 3 + ["brown_wool"])
piece("wine_barrel", "storage", [(2, 0, 3, 14, 13, 13, "wood"), (1, 3, 3, 15, 4, 13, "metal"), (1, 9, 3, 15, 10, 13, "metal"), (6, 5, 2.4, 10, 8, 3, "dark"), (2, 13, 4, 14, 14, 12, "wood")],
      {"wood": "{w}_planks", "metal": M_METAL, "dark": M_DARK}, ["wood"] * 6 + ["iron"] * 2, variants="wood")
piece("pool_table", "decor", [(0, 6, 0, 16, 10, 16, "wood"), (1, 10, 1, 15, 11, 15, "felt"), (0, 10, 0, 16, 12, 1, "wood"), (0, 10, 15, 16, 12, 16, "wood"), (0, 10, 1, 1, 12, 15, "wood"), (15, 10, 1, 16, 12, 15, "wood"), (1, 0, 1, 3, 6, 3, "wood"), (13, 0, 1, 15, 6, 3, "wood"), (1, 0, 13, 3, 6, 15, "wood"), (13, 0, 13, 15, 6, 15, "wood")],
      {"wood": "{w}_planks", "felt": "green_wool"}, ["wood"] * 6 + ["green_wool"] * 3, variants="wood")
piece("dartboard", "decor", [(2, 2, 14.4, 14, 14, 16, "wood"), (3, 3, 14, 13, 13, 14.5, "ring"), (6, 6, 13.6, 10, 10, 14, "bull")],
      {"wood": "{w}_planks", "ring": "black_concrete", "bull": "red_concrete"}, ["wood"] * 2 + ["black_concrete", "red_concrete", "stick"], variants="wood")
piece("weight_bench", "seat", [(2, 5, 3, 14, 8, 13, "cloth"), (2, 0, 3, 4, 5, 5, "metal"), (12, 0, 3, 14, 5, 5, "metal"), (2, 0, 11, 4, 5, 13, "metal"), (12, 0, 11, 14, 5, 13, "metal"), (0, 12, 6, 16, 13, 10, "metal"), (0, 10, 5, 2, 15, 11, "dark"), (14, 10, 5, 16, 15, 11, "dark")],
      {"cloth": "{c}_wool", "metal": M_METAL, "dark": M_DARK}, ["iron"] * 5 + ["cloth"] * 2 + ["black_concrete"], variants="cloth")
piece("breaker_panel", "decor", [(2, 3, 14, 14, 14, 16, "metal"), (4, 5, 13.6, 12, 12, 14, "dark"), (5, 6, 13.2, 6, 11, 13.6, "red"), (8, 6, 13.2, 9, 11, 13.6, "red"), (11, 6, 13.2, 12, 11, 13.6, "red")],
      {"metal": "gray_concrete", "dark": M_DARK, "red": "red_concrete"}, ["iron"] * 3 + ["redstone"] * 3 + ["lever", "gray_concrete"])
piece("storage_crate", "storage", [(1, 0, 1, 15, 14, 15, "wood"), (0, 0, 0, 2, 16, 2, "wood2"), (14, 0, 0, 16, 16, 2, "wood2"), (0, 0, 14, 2, 16, 16, "wood2"), (14, 0, 14, 16, 16, 16, "wood2"), (0, 14, 0, 16, 16, 16, "wood2")],
      {"wood": "{w}_planks", "wood2": "stripped_oak_log"}, ["wood"] * 4 + ["stick"] * 4, variants="wood")
piece("cardboard_boxes", "decor", [(1, 0, 1, 9, 8, 9, "box"), (9, 0, 3, 15, 6, 11, "box"), (3, 8, 3, 8, 12, 8, "box"), (2, 7.5, 2, 8, 8.5, 8, "tape")],
      {"box": "brown_concrete", "tape": "yellow_concrete"}, ["paper"] * 4 + ["brown_concrete"])
piece("paint_cans", "decor", [(2, 0, 2, 7, 5, 7, "red"), (9, 0, 3, 14, 4, 8, "blue"), (5, 0, 9, 10, 6, 14, "green"), (3, 5, 3, 6, 6, 6, "metal"), (6, 6, 10, 9, 7, 13, "metal")],
      {"red": "red_concrete", "blue": "blue_concrete", "green": "green_concrete", "metal": M_METAL}, ["iron"] * 3 + ["red_dye", "blue_dye", "green_dye"])
piece("dehumidifier", "decor", [(3, 0, 3, 13, 11, 13, "white"), (4, 8, 2.4, 12, 10, 3, "dark"), (5, 3, 2.4, 6, 6, 3, "metal"), (3, 11, 5, 13, 13, 11, "white")], {"white": M_WHITE, "dark": M_DARK, "metal": M_METAL}, ["iron"] * 3 + ["white_concrete"] * 3 + ["water_bucket"])
# ---------------------------------------------------------------- OUTDOORS
piece("outdoor_sofa", "seat", [(0, 0, 3, 16, 7, 16, "wood"), (1, 7, 4, 15, 9, 14, "cloth"), (0, 7, 12, 16, 15, 16, "wood"), (1, 9, 13, 15, 15, 15, "cloth"), (0, 0, 3, 2, 10, 16, "wood"), (14, 0, 3, 16, 10, 16, "wood")],
      {"wood": "{w}_planks", "cloth": "{c}_wool"}, ["wood"] * 4 + ["cloth"] * 3, variants="cloth")
piece("hanging_chair", "seat", [(7.5, 12, 7.5, 8.5, 16, 8.5, "metal"), (2, 3, 2, 14, 5, 14, "rope"), (2, 5, 12, 14, 12, 14, "rope"), (3, 5, 3, 13, 6, 12, "cloth"), (2, 5, 2, 4, 9, 12, "rope"), (12, 5, 2, 14, 9, 12, "rope")],
      {"metal": M_METAL, "rope": "hay_block", "cloth": "{c}_wool"}, ["hay_block"] * 3 + ["iron"] * 2 + ["cloth"] * 3, variants="cloth")
piece("outdoor_counter", "storage", [(0, 0, 0, 16, 12, 16, "stone"), (0, 12, 0, 16, 14, 16, "top"), (1, 2, -0.2, 15, 8, 0.4, "wood")], {"stone": "stone_bricks", "top": M_STONE, "wood": "spruce_planks"}, ["stone_bricks"] * 5 + ["smooth_stone"] * 2 + ["spruce_planks"] * 2)
piece("smoker_grill", "glow", [(2, 0, 2, 14, 10, 14, "dark"), (1, 10, 1, 15, 12, 15, "dark"), (6, 12, 6, 10, 16, 10, "metal"), (3, 0, 14, 5, 3, 16, "metal"), (11, 0, 14, 13, 3, 16, "metal"), (4, 3, 1.4, 12, 8, 2, "metal")],
      {"dark": M_DARK, "metal": M_METAL}, ["iron"] * 5 + ["black_concrete"] * 3 + ["coal"], light=7)
piece("patio_heater", "glow", [(5, 0, 5, 11, 1, 11, "metal"), (7.5, 1, 7.5, 8.5, 12, 8.5, "metal"), (4, 12, 4, 12, 14, 12, "metal"), (6, 14, 6, 10, 16, 10, "fire")], {"metal": M_METAL, "fire": "orange_concrete"}, ["iron"] * 4 + ["coal", "furnace"], light=12)
piece("string_lights", "glow", [(0, 14, 7, 16, 15, 9, "wire"), (1, 11, 7.4, 3, 14, 8.6, "bulb"), (5, 12, 7.4, 7, 14, 8.6, "bulb"), (9, 11, 7.4, 11, 14, 8.6, "bulb"), (13, 12, 7.4, 15, 14, 8.6, "bulb")],
      {"wire": M_DARK, "bulb": "yellow_concrete"}, ["string"] * 3 + ["glowstone_dust"] * 2 + ["iron"], light=12)
piece("cooler_box", "storage", [(1, 0, 3, 15, 9, 13, "blue"), (0, 9, 2, 16, 11, 14, "white"), (6, 11, 7, 10, 12, 9, "dark"), (1, 3, 2.6, 15, 4, 3, "white")], {"blue": "blue_concrete", "white": M_WHITE, "dark": M_DARK}, ["blue_concrete"] * 3 + ["white_concrete"] * 3 + ["ice"])
piece("camping_tent", "decor", [(1, 0, 1, 15, 2, 15, "floor"), (2, 2, 2, 14, 5, 14, "cloth"), (4, 5, 3, 12, 9, 13, "cloth"), (6, 9, 4, 10, 12, 12, "cloth"), (7.5, 0, -0.4, 8.5, 6, 0.6, "dark")],
      {"floor": "brown_wool", "cloth": "{c}_wool", "dark": M_DARK}, ["{c}_wool"] * 5 + ["stick"] * 2 + ["string"], variants="cloth")
piece("sleeping_bag", "decor", [(2, 0, 1, 14, 3, 15, "cloth"), (3, 3, 2, 13, 4, 14, "cloth2"), (3, 3, 2, 13, 5, 5, "white")], {"cloth": "{c}_wool", "cloth2": "gray_wool", "white": "white_wool"}, ["cloth"] * 3 + ["white_wool"], variants="cloth")
piece("camp_chair", "seat", [(2, 0, 2, 4, 8, 4, "metal"), (12, 0, 2, 14, 8, 4, "metal"), (2, 0, 12, 4, 8, 14, "metal"), (12, 0, 12, 14, 8, 14, "metal"), (2, 8, 2, 14, 9, 14, "cloth"), (2, 9, 12, 14, 15, 13, "cloth")], {"metal": M_METAL, "cloth": "{c}_wool"}, ["iron"] * 3 + ["cloth"] * 3, variants="cloth")
piece("signpost", "decor", [(7, 0, 7, 9, 16, 9, "wood"), (1, 11, 6.4, 15, 15, 9.6, "board"), (2, 5, 6.4, 13, 9, 9.6, "board")], {"wood": "{w}_planks", "board": "oak_planks"}, ["wood"] * 3 + ["oak_planks"] * 2 + ["stick"], variants="wood")
piece("stone_bench", "seat", [(0, 6, 2, 16, 9, 14, "stone"), (1, 0, 3, 5, 6, 13, "stone2"), (11, 0, 3, 15, 6, 13, "stone2")], {"stone": "smooth_stone", "stone2": "stone_bricks"}, ["smooth_stone"] * 3 + ["stone_bricks"] * 3)
piece("pergola_post", "decor", [(6, 0, 6, 10, 16, 10, "wood"), (0, 13, 6, 16, 16, 10, "wood"), (6, 13, 0, 10, 16, 16, "wood"), (3, 10, 6.4, 6, 13, 9.6, "wood")], {"wood": "{w}_planks"}, ["wood"] * 5 + ["stick"] * 2, variants="wood")
piece("outdoor_fridge", "storage", [(1, 0, 1, 15, 16, 15, "steel"), (1, 7.6, 0.4, 15, 8.4, 1, "dark"), (12, 10, 0, 13, 15, 1, "white"), (12, 2, 0, 13, 6, 1, "white")], {"steel": "gray_concrete", "dark": M_DARK, "white": M_WHITE}, ["iron"] * 6 + ["gray_concrete", "redstone", "glass"])
# ---------------------------------------------------------------- POOL
piece("pool_float_ring", "decor", [(2, 0, 2, 14, 3, 4, "float"), (2, 0, 12, 14, 3, 14, "float"), (2, 0, 4, 4, 3, 12, "float"), (12, 0, 4, 14, 3, 12, "float"), (2, 1, 2, 14, 2, 3, "white")], {"float": "{c}_wool", "white": "white_wool"}, ["{c}_wool"] * 4 + ["string"], variants="cloth")
piece("pool_slide", "decor", [(0, 0, 0, 2, 16, 2, "metal"), (14, 0, 0, 16, 16, 2, "metal"), (2, 14, 0, 14, 16, 4, "slide"), (2, 11, 4, 14, 13, 8, "slide"), (2, 8, 8, 14, 10, 12, "slide"), (2, 5, 12, 14, 7, 16, "slide")], {"metal": M_METAL, "slide": "light_blue_concrete"}, ["iron"] * 4 + ["light_blue_concrete"] * 4)
piece("pool_light", "glow", [(3, 5, 12, 13, 11, 16, "metal"), (4, 6, 11.4, 12, 10, 12, "lamp"), (1, 7, 13, 3, 9, 15, "metal"), (13, 7, 13, 15, 9, 15, "metal")], {"metal": M_METAL, "lamp": "cyan_concrete"}, ["iron"] * 3 + ["glowstone_dust"] * 2 + ["glass", "redstone"], light=13)
piece("pool_steps", "decor", [(0, 0, 0, 16, 3, 5, "tile"), (0, 0, 5, 16, 6, 10, "tile"), (0, 0, 10, 16, 9, 16, "tile"), (0, 9, 15, 2, 14, 16, "metal"), (14, 9, 15, 16, 14, 16, "metal")], {"tile": M_WHITE, "metal": M_METAL}, ["white_concrete"] * 6 + ["iron"] * 2)
piece("towel_cart", "storage", [(1, 0, 2, 15, 1, 14, "wood"), (1, 6, 2, 15, 7, 14, "wood"), (1, 12, 2, 15, 13, 14, "wood"), (1, 0, 2, 2, 14, 14, "wood"), (14, 0, 2, 15, 14, 14, "wood"), (3, 7, 4, 13, 11, 12, "cloth"), (3, 1, 4, 13, 5, 12, "cloth2")],
      {"wood": "{w}_planks", "cloth": "{c}_wool", "cloth2": "white_wool"}, ["wood"] * 5 + ["cloth"] * 2, variants="cloth")
piece("pool_bar", "storage", [(0, 0, 2, 16, 12, 14, "wood"), (0, 12, 0, 16, 14, 16, "top"), (2, 14, 2, 4, 16, 4, "glass"), (8, 14, 10, 10, 16, 12, "glass"), (0, 14, 14, 16, 16, 15, "roof")],
      {"wood": "bamboo_planks", "top": "stripped_bamboo_block", "glass": "light_blue_concrete", "roof": "yellow_wool"}, ["bamboo_planks"] * 5 + ["glass"] * 2 + ["yellow_wool"])
piece("inflatable_flamingo", "decor", [(3, 0, 3, 13, 5, 13, "pink"), (6, 5, 6, 10, 12, 10, "pink"), (5, 12, 5, 11, 14, 11, "pink"), (11, 12, 7, 15, 13, 9, "beak"), (7, 14, 7.5, 9, 16, 8.5, "pink")], {"pink": "pink_wool", "beak": "orange_wool"}, ["pink_wool"] * 4 + ["orange_wool", "string"])
piece("lifeguard_chair", "seat", [(1, 0, 1, 3, 16, 3, "wood"), (13, 0, 1, 15, 16, 3, "wood"), (1, 0, 13, 3, 16, 15, 3 and "wood"), (13, 0, 13, 15, 16, 15, "wood"), (1, 8, 1, 15, 10, 15, "wood2"), (2, 10, 12, 14, 16, 14, "wood2"), (0, 15, 0, 16, 16, 16, "roof")] if False else
      [(1, 0, 1, 3, 16, 3, "wood"), (13, 0, 1, 15, 16, 3, "wood"), (1, 0, 13, 3, 16, 15, "wood"), (13, 0, 13, 15, 16, 15, "wood"), (1, 8, 1, 15, 10, 15, "wood2"), (2, 10, 12, 14, 16, 14, "wood2")],
      {"wood": "{w}_planks", "wood2": "{w}_planks"}, ["wood"] * 7 + ["stick"] * 2, variants="wood")
piece("pool_fence", "decor", [(0, 0, 7, 1, 14, 9, "metal"), (15, 0, 7, 16, 14, 9, "metal"), (0, 12, 7, 16, 14, 9, "metal"), (0, 2, 7, 16, 4, 9, "metal"), (4, 2, 7.4, 5, 12, 8.6, "metal"), (8, 2, 7.4, 9, 12, 8.6, "metal"), (12, 2, 7.4, 13, 12, 8.6, "metal")], {"metal": M_METAL}, ["iron"] * 7)
piece("pool_filter", "decor", [(3, 0, 3, 13, 8, 13, "blue"), (4, 8, 4, 12, 10, 12, "white"), (7, 10, 7, 9, 12, 9, "metal"), (13, 3, 6, 16, 5, 10, "metal")], {"blue": "blue_concrete", "white": M_WHITE, "metal": M_METAL}, ["iron"] * 4 + ["blue_concrete"] * 2 + ["bucket"])
piece("kiddie_pool", "decor", [(0, 0, 0, 16, 4, 2, "wall"), (0, 0, 14, 16, 4, 16, "wall"), (0, 0, 2, 2, 4, 14, "wall"), (14, 0, 2, 16, 4, 14, "wall"), (2, 0, 2, 14, 3, 14, "water")], {"wall": "{c}_wool", "water": "light_blue_concrete"}, ["{c}_wool"] * 5 + ["water_bucket"], variants="cloth")
piece("pool_noodles", "decor", [(2, 0, 2, 5, 14, 5, "red"), (6, 0, 6, 9, 14, 9, "blue"), (10, 0, 3, 13, 14, 6, "yellow"), (4, 0, 10, 7, 14, 13, "green")], {"red": "red_wool", "blue": "blue_wool", "yellow": "yellow_wool", "green": "lime_wool"}, ["red_wool", "blue_wool", "yellow_wool", "lime_wool", "string"])
piece("pool_cover_roller", "decor", [(0, 0, 3, 2, 8, 13, "metal"), (14, 0, 3, 16, 8, 13, "metal"), (2, 3, 5, 14, 7, 11, "cover"), (14, 3, 7, 16, 5, 9, "metal")], {"metal": M_METAL, "cover": "blue_wool"}, ["iron"] * 3 + ["blue_wool"] * 3)
piece("pool_shower_post", "decor", [(7, 0, 7, 9, 14, 9, "metal"), (5, 14, 5, 11, 16, 11, "metal"), (3, 0, 3, 13, 1, 13, "stone"), (9, 3, 7.4, 12, 5, 8.6, "metal")], {"metal": M_METAL, "stone": "stone"}, ["iron"] * 4 + ["bucket"])
# ---------------------------------------------------------------- YARD
piece("bird_feeder", "decor", [(7.5, 10, 7.5, 8.5, 16, 8.5, "metal"), (4, 6, 4, 12, 10, 12, "wood"), (3, 5, 3, 13, 6, 13, "wood2"), (5, 7, 5, 11, 9, 11, "seed")], {"metal": M_METAL, "wood": "{w}_planks", "wood2": "{w}_planks", "seed": "yellow_wool"}, ["wood"] * 4 + ["wheat_seeds"] * 2 + ["iron"], variants="wood")
piece("stake_light", "lamp", [(7.5, 0, 7.5, 8.5, 6, 8.5, "metal"), (5, 6, 5, 11, 12, 11, "shade")], {"metal": M_METAL, "shade": "white_concrete", "shade_on": "glowstone"}, ["iron", "glass", "glowstone_dust"], light=10)
piece("raised_bed", "decor", [(0, 0, 0, 16, 8, 2, "wood"), (0, 0, 14, 16, 8, 16, "wood"), (0, 0, 2, 2, 8, 14, "wood"), (14, 0, 2, 16, 8, 14, "wood"), (2, 0, 2, 14, 7, 14, "soil"), (3, 7, 3, 13, 10, 13, "leaf"), (5, 10, 5, 7, 12, 7, "bloom")],
      {"wood": "{w}_planks", "soil": "dirt", "leaf": "green_wool", "bloom": "red_wool"}, ["wood"] * 5 + ["dirt"] * 2 + ["wheat_seeds"], variants="wood")
piece("lawn_mower", "decor", [(2, 2, 2, 14, 7, 14, "red"), (3, 7, 3, 9, 9, 9, "dark"), (2, 8, 12, 14, 9, 13, "metal"), (3, 9, 12, 4, 14, 13, "metal"), (12, 9, 12, 13, 14, 13, "metal"), (0, 0, 1, 3, 4, 5, "dark"), (13, 0, 1, 16, 4, 5, "dark"), (0, 0, 11, 3, 4, 15, "dark"), (13, 0, 11, 16, 4, 15, "dark")],
      {"red": "red_concrete", "dark": M_DARK, "metal": M_METAL}, ["iron"] * 4 + ["red_concrete"] * 3 + ["black_concrete"])
piece("hose_reel", "decor", [(3, 0, 5, 13, 1, 11, "metal"), (4, 1, 6, 6, 8, 10, "metal"), (10, 1, 6, 12, 8, 10, "metal"), (3, 2, 5, 13, 8, 11, "hose")], {"metal": M_METAL, "hose": "green_concrete"}, ["iron"] * 3 + ["green_concrete"] * 3 + ["string"])
piece("fountain", "glow", [(1, 0, 1, 15, 3, 15, "stone"), (2, 3, 2, 14, 5, 14, "water"), (5, 3, 5, 11, 9, 11, "stone"), (3, 9, 3, 13, 11, 13, "stone"), (4, 11, 4, 12, 12, 12, "water"), (7, 11, 7, 9, 16, 9, "stone")], {"stone": "stone_bricks", "water": "light_blue_concrete"}, ["stone_bricks"] * 6 + ["water_bucket"], light=4)
piece("stepping_stones", "decor", [(1, 0, 1, 6, 1, 6, "stone"), (8, 0, 3, 14, 1, 9, "stone"), (2, 0, 9, 8, 1, 15, "stone"), (10, 0, 11, 15, 1, 15, "stone")], {"stone": "stone"}, ["stone"] * 4)
piece("tire_swing", "seat", [(0, 0, 6, 2, 16, 10, "wood"), (14, 0, 6, 16, 16, 10, "wood"), (0, 14, 6, 16, 16, 10, "wood"), (7.5, 7, 7.5, 8.5, 14, 8.5, "rope"), (3, 2, 3, 13, 5, 5, "tire"), (3, 2, 11, 13, 5, 13, "tire"), (3, 2, 5, 5, 5, 11, "tire"), (11, 2, 5, 13, 5, 11, "tire")],
      {"wood": "{w}_planks", "rope": "hay_block", "tire": M_DARK}, ["wood"] * 5 + ["black_concrete"] * 3, variants="wood")
piece("trampoline", "decor", [(1, 0, 1, 3, 6, 3, "metal"), (13, 0, 1, 15, 6, 3, "metal"), (1, 0, 13, 3, 6, 15, "metal"), (13, 0, 13, 15, 6, 15, "metal"), (0, 6, 0, 16, 8, 16, "pad"), (2, 8, 2, 14, 8.6, 14, "mat")], {"metal": M_METAL, "pad": "blue_wool", "mat": M_DARK}, ["iron"] * 4 + ["blue_wool"] * 3 + ["black_wool"])
piece("topiary", "decor", [(5, 0, 5, 11, 4, 11, "pot"), (7.5, 4, 7.5, 8.5, 8, 8.5, "wood"), (4, 8, 4, 12, 12, 12, "leaf"), (6, 12, 6, 10, 15, 10, "leaf")], {"pot": "terracotta", "wood": "oak_planks", "leaf": "green_wool"}, ["terracotta", "green_wool", "green_wool", "sapling", "stick"])
piece("wind_spinner", "decor", [(7.5, 0, 7.5, 8.5, 10, 8.5, "metal"), (2, 10, 7.5, 14, 12, 8.5, "red"), (7, 10, 7.5, 9, 16, 8.5, "blue"), (3, 11, 7, 6, 13, 9, "yellow")], {"metal": M_METAL, "red": "red_concrete", "blue": "blue_concrete", "yellow": "yellow_concrete"}, ["iron", "stick", "red_dye", "blue_dye", "yellow_dye"])
piece("clothesline", "decor", [(0, 0, 7, 2, 16, 9, "wood"), (14, 0, 7, 16, 16, 9, "wood"), (2, 14, 7.6, 14, 14.8, 8.4, "string"), (3, 6, 7.4, 6, 14, 8.6, "cloth"), (7, 8, 7.4, 10, 14, 8.6, "cloth2"), (11, 5, 7.4, 13, 14, 8.6, "cloth")],
      {"wood": "{w}_planks", "string": "white_wool", "cloth": "{c}_wool", "cloth2": "white_wool"}, ["wood"] * 3 + ["string"] * 3 + ["cloth"] * 2, variants="cloth")
piece("playhouse", "decor", [(1, 0, 1, 15, 10, 15, "wall"), (0, 10, 0, 16, 12, 16, "roof"), (3, 12, 2, 13, 14, 14, "roof"), (5, 0, 0.2, 11, 8, 1, "door"), (2, 4, 0.4, 5, 8, 1, "glass"), (11, 4, 0.4, 14, 8, 1, "glass")],
      {"wall": "{w}_planks", "roof": "red_concrete", "door": "dark_oak_planks", "glass": "light_blue_concrete"}, ["wood"] * 6 + ["red_concrete"] * 2 + ["glass"], variants="wood")
piece("swing_set", "seat", [(0, 0, 6, 2, 16, 10, "metal"), (14, 0, 6, 16, 16, 10, "metal"), (0, 14, 6, 16, 16, 10, "metal"), (3, 5, 7.5, 3.6, 14, 8.5, "rope"), (5.4, 5, 7.5, 6, 14, 8.5, "rope"), (3, 4, 6, 6, 5, 10, "seat"), (10, 5, 7.5, 10.6, 14, 8.5, "rope"), (12.4, 5, 7.5, 13, 14, 8.5, "rope"), (10, 4, 6, 13, 5, 10, "seat")],
      {"metal": M_METAL, "rope": M_DARK, "seat": "{c}_wool"}, ["iron"] * 4 + ["string"] * 2 + ["cloth"] * 2, variants="cloth")
piece("wishing_well", "decor", [(2, 0, 2, 14, 7, 14, "stone"), (3, 6.5, 3, 13, 7.2, 13, "water"), (2, 7, 2, 4, 16, 4, "wood"), (12, 7, 2, 14, 16, 4, "wood"), (1, 14, 1, 15, 16, 15, "roof"), (7.5, 8, 7.5, 8.5, 14, 8.5, "rope")],
      {"stone": "cobblestone", "water": "light_blue_concrete", "wood": "{w}_planks", "roof": "{w}_planks", "rope": "hay_block"}, ["cobblestone"] * 4 + ["wood"] * 3 + ["bucket", "string"], variants="wood")
piece("garden_table_set", "decor", [(2, 10, 2, 14, 12, 14, "wood"), (7, 0, 7, 9, 10, 9, "wood"), (0, 0, 7, 3, 7, 9, "chair"), (13, 0, 7, 16, 7, 9, "chair")], {"wood": "{w}_planks", "chair": "{w}_planks"}, ["wood"] * 5 + ["stick"] * 3, variants="wood")
