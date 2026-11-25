#!/usr/bin/env python3
"""
Generates every data-driven file of the mod from furniture_data.py and animal_data.py:
  python3 tools/generate.py
Outputs (already generated and shipped in the repo, re-run after editing the data files):
  src/main/resources/assets/myminecraft/{blockstates,models,items,textures,lang}
  src/main/resources/data/myminecraft/{recipe,loot_table,worldgen,tags}
  src/main/java/.../furniture/FurnitureDefs.java, animal/AnimalSpecs.java
"""
import json, os, re, sys
sys.path.insert(0, os.path.dirname(__file__))
from furniture_data import FURNITURE, WOODS, COLORS
from animal_data import ANIMALS
import textures as T
from PIL import Image, ImageDraw

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
RES = os.path.join(ROOT, "src/main/resources")
ASSETS = os.path.join(RES, "assets/myminecraft")
DATA = os.path.join(RES, "data/myminecraft")
JAVA = os.path.join(ROOT, "src/main/java/com/myminecraft")
NS = "myminecraft"
LANG = {}


def write(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        f.write(text)


def wjson(path, obj):
    write(path, json.dumps(obj, separators=(",", ":")))


def title(s):
    return " ".join(w.capitalize() for w in s.split("_"))


TOKENS = {"stick": "stick", "iron": "iron_ingot", "sapling": "oak_sapling", "clock": "clock", "book": "book",
          "glass": "glass", "redstone": "redstone", "furnace": "furnace", "bucket": "bucket"}


def ingredient(tok, subs):
    if tok == "wood":
        return f"minecraft:{subs['w']}_planks"
    if tok == "cloth":
        return f"minecraft:{subs['c']}_wool"
    tok = tok.format(**subs) if "{" in tok else tok
    return "minecraft:" + TOKENS.get(tok, tok)


MACAWS = ("chair", "stool", "table", "coffee_table", "desk", "bookcase", "wardrobe", "nightstand", "dresser",
          "kitchen_counter", "wall_cabinet", "bench", "park_bench")


def cond_not(mods):
    return [{"condition": "fabric:not", "value": {"condition": "fabric:any_mods_loaded", "values": mods}}]


def recipe_file(name, obj):
    """Recipes of duplicated furniture only load when the other mod is NOT installed (Fabric resource conditions)."""
    if any(name == b or name.endswith("_" + b) for b in MACAWS):
        obj = {**obj, "fabric:load_conditions": cond_not(["mcwfurnitures"])}
    if name in ("clam", "underwater_torch"):
        obj = {**obj, "fabric:load_conditions": cond_not(["nekomasfixed", "nekomas_fixed", "nekomas-fixed", "nekomas"])}
    wjson(f"{DATA}/recipe/{name}.json", obj)


def loot_block(n):
    return {"type": "minecraft:block", "pools": [{"rolls": 1.0, "bonus_rolls": 0.0,
            "entries": [{"type": "minecraft:item", "name": f"{NS}:{n}"}],
            "conditions": [{"condition": "minecraft:survives_explosion"}]}]}


# =============================================================================== FURNITURE
def expand():
    out = []
    for p in FURNITURE:
        if p["variants"] == "wood":
            subsets = [({"w": w}, w + "_") for w in WOODS]
        elif p["variants"] == "cloth":
            subsets = [({"c": c}, c + "_") for c in COLORS]
        else:
            subsets = [({}, "")]
        for subs, prefix in subsets:
            subs = {"w": "oak", "c": "red", **subs}   # pieces may use both placeholders; the unused one defaults
            mats = {k: v.format(**subs) for k, v in p["mats"].items()}
            out.append(dict(p, base=p["name"], name=prefix + p["name"], mats=mats, subs=subs,
                            recipe=[ingredient(t, subs) for t in p["recipe"]]))
    return out


def model_json(piece, lit=False):
    mats = dict(piece["mats"])
    if lit and "shade_on" in mats:
        mats["shade"] = mats["shade_on"]
    tex = {r: f"minecraft:block/{t}" for r, t in mats.items() if r != "shade_on"}
    tex["particle"] = next(iter(tex.values()))
    elems = []
    for (x1, y1, z1, x2, y2, z2, role) in piece["boxes"]:
        # faces lying on the block boundary get a cullface, so they are not drawn when a solid neighbour hides them
        cull = {"north": z1 <= 0, "south": z2 >= 16, "west": x1 <= 0, "east": x2 >= 16, "down": y1 <= 0, "up": y2 >= 16}
        faces = {}
        for d in ("north", "east", "south", "west", "up", "down"):
            faces[d] = {"texture": f"#{role}", **({"cullface": d} if cull[d] else {})}
        elems.append({"from": [x1, y1, z1], "to": [x2, y2, z2], "faces": faces})
    return {"parent": "minecraft:block/block", "textures": tex, "elements": elems}


def gen_furniture():
    pieces = expand()
    axe, pick = [], []
    for p in pieces:
        n, kind = p["name"], p["kind"]
        wjson(f"{ASSETS}/models/block/{n}.json", model_json(p))
        facing = {"north": 0, "east": 90, "south": 180, "west": 270}
        variants = {}
        if kind == "lamp":
            wjson(f"{ASSETS}/models/block/{n}_on.json", model_json(p, True))
            for f, y in facing.items():
                for lit in ("false", "true"):
                    m = f"{NS}:block/{n}" + ("_on" if lit == "true" else "")
                    variants[f"facing={f},lit={lit}"] = dict(model=m, **({"y": y} if y else {}))
        else:
            for f, y in facing.items():
                variants[f"facing={f}"] = dict(model=f"{NS}:block/{n}", **({"y": y} if y else {}))
        wjson(f"{ASSETS}/blockstates/{n}.json", {"variants": variants})
        wjson(f"{ASSETS}/items/{n}.json", {"model": {"type": "minecraft:model", "model": f"{NS}:block/{n}"}})
        recipe_file(n, {"type": "minecraft:crafting_shapeless", "category": "misc",
                        "ingredients": p["recipe"], "result": {"id": f"{NS}:{n}", "count": 1}})
        wjson(f"{DATA}/loot_table/blocks/{n}.json", loot_block(n))
        LANG[f"block.{NS}.{n}"] = title(n)
        (pick if all("planks" not in t and "wool" not in t for t in p["mats"].values()) else axe).append(f"{NS}:{n}")
    lines = []
    for p in FURNITURE:
        boxes = []
        for (x1, y1, z1, x2, y2, z2, role) in p["boxes"]:
            cx1, cx2, cz1, cz2, cy1, cy2 = max(0, x1), min(16, x2), max(0, z1), min(16, z2), max(0, y1), min(16, y2)
            if cx2 - cx1 > 0.05 and cz2 - cz1 > 0.05 and cy2 - cy1 > 0.05:
                boxes.append(f"{{{cx1}, {cy1}, {cz1}, {cx2}, {cy2}, {cz2}}}")
        prefixes = ""
        if p["variants"] == "wood":
            prefixes = ", " + ", ".join(f'"{w}"' for w in WOODS)
        elif p["variants"] == "cloth":
            prefixes = ", " + ", ".join(f'"{c}"' for c in COLORS)
        lines.append(f'        add("{p["name"]}", FurnitureType.{p["kind"].upper()}, {p["light"]}, new double[][]{{\n'
                     f'            {", ".join(boxes)}}}{prefixes});')
    nl = "\n"
    write(f"{JAVA}/furniture/FurnitureDefs.java", f"""package com.myminecraft.furniture;

import java.util.ArrayList;
import java.util.List;

/**
 * GENERATED by tools/generate.py from tools/furniture_data.py - edit the Python data, not this file.
 * Every entry is one furniture block. Boxes are {{x1, y1, z1, x2, y2, z2}} in pixels, facing NORTH.
 */
public final class FurnitureDefs {{
    public record Def(String name, FurnitureType type, int light, double[][] boxes) {{}}

    public static final List<Def> ALL = new ArrayList<>();

    private static void add(String base, FurnitureType type, int light, double[][] boxes, String... prefixes) {{
        if (prefixes.length == 0) {{
            ALL.add(new Def(base, type, light, boxes));
        }} else {{
            for (String p : prefixes) {{
                ALL.add(new Def(p + "_" + base, type, light, boxes));
            }}
        }}
    }}

    static {{
{nl.join(lines)}
    }}

    private FurnitureDefs() {{}}
}}
""")
    for tile, base, grout in (("pool_tile", "6cc3ea", "d6f1fb"), ("pool_tile_white", "eef3f5", "c3d3da")):
        T.paint_pool_tile(f"{ASSETS}/textures/block/{tile}.png", base, grout)
        wjson(f"{ASSETS}/models/block/{tile}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"{NS}:block/{tile}"}})
        wjson(f"{ASSETS}/blockstates/{tile}.json", {"variants": {"": {"model": f"{NS}:block/{tile}"}}})
        wjson(f"{ASSETS}/items/{tile}.json", {"model": {"type": "minecraft:model", "model": f"{NS}:block/{tile}"}})
        src = "light_blue_concrete" if tile == "pool_tile" else "white_concrete"
        wjson(f"{DATA}/recipe/{tile}.json", {"type": "minecraft:crafting_shapeless", "category": "building",
              "ingredients": [f"minecraft:{src}"] * 4 + ["minecraft:quartz"], "result": {"id": f"{NS}:{tile}", "count": 4}})
        wjson(f"{DATA}/loot_table/blocks/{tile}.json", loot_block(tile))
        LANG[f"block.{NS}.{tile}"] = title(tile)
        pick.append(f"{NS}:{tile}")
    wjson(f"{RES}/data/minecraft/tags/block/mineable/axe.json", {"replace": False, "values": axe})
    wjson(f"{RES}/data/minecraft/tags/block/mineable/pickaxe.json", {"replace": False, "values": pick})
    print(f"furniture: {len(pieces)} blocks")


# =============================================================================== ANIMALS
def jexpr(e):
    e = re.sub(r"keys\(([^)]*)\)", lambda m: "keys(" + ", ".join("Biomes." + x.strip(" '") for x in m.group(1).split(",")) + ")", e)
    return re.sub(r"tag\('([A-Z_]+)'\)", r"tag(BiomeTags.\1)", e)


def loot_entity(drops):
    pools = []
    for item, mn, mx in drops:
        pools.append({"rolls": 1.0, "entries": [{"type": "minecraft:item", "name": item, "functions": [
            {"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": mn, "max": mx}}]}]})
    return {"type": "minecraft:entity", "pools": pools}


def egg_item(name, colors):
    tints = [{"type": "minecraft:constant", "value": int(c, 16)} for c in colors]
    wjson(f"{ASSETS}/items/{name}_spawn_egg.json", {"model": {"type": "minecraft:model",
          "model": "minecraft:item/template_spawn_egg", "tints": tints}})


def gen_animals():
    lines = []
    for a in ANIMALS:
        n = a["name"]
        c = [f'new AnimalSpec("{n}", Layout.{a["layout"]}, Behavior.{a["behavior"]}, Diet.{a["diet"]})']
        c.append(".body(%d, %d, %d)" % a["body"]); c.append(".head(%d, %d, %d)" % a["head"]); c.append(".legs(%d, %d)" % a["legs"])
        if "neck" in a: c.append(".neck(%d, %d)" % a["neck"])
        if "snout" in a: c.append(".snout(%d, %d, %d)" % a["snout"])
        if "ears" in a: c.append(".ears(%d)" % a["ears"])
        if "tail" in a: c.append(".tail(%d, %d)" % a["tail"])
        if "arms" in a: c.append(".arms(%d, %d)" % a["arms"])
        if "wings" in a: c.append(".wings(%d, %d)" % a["wings"])
        for flag in ("trunk", "tusks", "horns", "horn", "antlers", "hump", "beak", "flying"):
            if a.get(flag): c.append(f".{flag}()")
        if a.get("mane"): c.append(f".mane(Mane.{a['mane'].upper()})")
        if a.get("monster"): c.append(".monster()")
        if a.get("roll"): c.append(".roll()")
        if "tentacles" in a: c.append(".tentacles(%d, %d)" % a["tentacles"])
        if "orbit" in a: c.append(".orbit(%d, %d)" % a["orbit"])
        if a.get("special"): c.append(f".special(Special.{a['special']})")
        if a.get("spawnkind"): c.append(f".spawnKind(SpawnKind.{a['spawnkind']})")
        c.append(".stats(%s, %s, %s)" % (float(a["hp"]), a["speed"], float(a["dmg"])))
        s = a["sounds"]
        c.append(f".sounds(SoundEvents.{s}_AMBIENT, SoundEvents.{s}_HURT, SoundEvents.{s}_DEATH)")
        w, mn, mx, pred = a["spawn"]
        w = max(1, w * 2 // 3); mx = min(mx, 3); mn = min(mn, mx)   # fewer entities = more fps
        c.append(f".spawn({w}, {mn}, {mx}, {jexpr(pred)})")
        lines.append("        add(" + "\n            ".join(c) + ");")
        T.paint_animal(a, f"{ASSETS}/textures/entity/{n}.png")
        LANG[f"entity.{NS}.{n}"] = title(n)
        LANG[f"item.{NS}.{n}_spawn_egg"] = title(n) + " Spawn Egg"
        wjson(f"{DATA}/loot_table/entities/{n}.json", loot_entity(a["drops"]))
        egg_item(n, a["egg"])
    nl = "\n"
    write(f"{JAVA}/animal/AnimalSpecs.java", f"""package com.myminecraft.animal;

import com.myminecraft.animal.AnimalSpec.Behavior;
import com.myminecraft.animal.AnimalSpec.Diet;
import com.myminecraft.animal.AnimalSpec.Layout;
import com.myminecraft.animal.AnimalSpec.Mane;
import com.myminecraft.animal.AnimalSpec.Special;
import com.myminecraft.animal.AnimalSpec.SpawnKind;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

/**
 * GENERATED by tools/generate.py from tools/animal_data.py - edit the Python data, not this file.
 * One entry per animal: model dimensions (pixels), AI, stats, sounds and spawn rules.
 */
public final class AnimalSpecs {{
    public static final List<AnimalSpec> ALL = new ArrayList<>();

    private static void add(AnimalSpec spec) {{
        ALL.add(spec);
    }}

    @SafeVarargs
    private static Predicate<BiomeSelectionContext> keys(ResourceKey<Biome>... k) {{
        return BiomeSelectors.includeByKey(k);
    }}

    private static Predicate<BiomeSelectionContext> tag(TagKey<Biome> t) {{
        return BiomeSelectors.tag(t);
    }}

    private static Predicate<BiomeSelectionContext> overworld() {{
        return BiomeSelectors.foundInOverworld();
    }}

    @SafeVarargs
    private static Predicate<BiomeSelectionContext> any(Predicate<BiomeSelectionContext>... p) {{
        return ctx -> {{
            for (Predicate<BiomeSelectionContext> x : p) {{
                if (x.test(ctx)) return true;
            }}
            return false;
        }};
    }}

    static {{
{nl.join(lines)}
    }}

    private AnimalSpecs() {{}}
}}
""")
    print(f"animals: {len(ANIMALS)}")


# =============================================================================== OTHER ENTITIES / ITEMS / WORLDGEN
def opt(tag):
    return {"id": tag, "required": False}


# Extra biome tags so structures also appear in biomes added by Tectonic / TerraBlender / World Weaver / Incendium.
EXTRA_TAGS = {
    "medieval_village": [opt("#minecraft:is_forest"), opt("#minecraft:is_taiga"), opt("#c:is_plains")],
    "medieval_town": [opt("#minecraft:is_forest"), opt("#minecraft:is_taiga"), opt("#c:is_plains")],
    "ice_castle": [opt("#c:is_snowy"), opt("#c:is_icy")],
    "iceologer_cabin": [opt("#c:is_snowy")],
    "haunted_outpost": [opt("#c:is_desert"), opt("#minecraft:is_savanna"), opt("#minecraft:is_taiga")],
    "illusioner_shack": [opt("#c:is_swamp")],
    "baobab_tree": [opt("#minecraft:is_savanna")],
    "termite_mound": [opt("#minecraft:is_savanna")],
    "meerkat_burrow": [opt("#c:is_desert")],
    "wildfire_castle": [opt("#minecraft:is_nether")],
}
# keep clear of vanilla villages / outposts (and the structure sets of mods that extend them)
EXCLUDE = {"medieval_village": "minecraft:villages", "medieval_town": "minecraft:villages", "haunted_outpost": "minecraft:pillager_outposts"}


def gen_misc():
    T.paint_iceologer(f"{ASSETS}/textures/entity/iceologer.png")
    T.paint_player_skin(f"{ASSETS}/textures/entity/knight.png", "knight", "e0b48c", "4a4a4f", "8c9199", "5b6068", "2d2d30")
    T.paint_player_skin(f"{ASSETS}/textures/entity/archer_guard.png", "archer", "e0b48c", "2f5a2c", "3f7a38", "5a4630", "2d2d30")
    T.paint_vehicle(f"{ASSETS}/textures/entity/bike.png", "bike")
    T.paint_vehicle(f"{ASSETS}/textures/entity/car.png", "car")
    for k, col in (("bike", (42, 111, 214, 255)), ("car", (200, 40, 30, 255))):
        img = Image.new("RGBA", (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
        if k == "car":
            d.rectangle([1, 7, 14, 11], fill=col); d.rectangle([4, 4, 11, 7], fill=col); d.rectangle([5, 5, 10, 7], fill=(169, 214, 245, 255))
            for x in (3, 11): d.ellipse([x, 10, x + 3, 13], fill=(20, 20, 20, 255))
        else:
            for x in (1, 10): d.ellipse([x, 8, x + 5, 13], outline=(30, 30, 30, 255))
            d.line([3, 10, 7, 6], fill=col); d.line([7, 6, 12, 10], fill=col); d.line([7, 6, 9, 5], fill=col)
        T._save(img, f"{ASSETS}/textures/item/{k}.png")
        wjson(f"{ASSETS}/models/item/{k}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/{k}"}})
        wjson(f"{ASSETS}/items/{k}.json", {"model": {"type": "minecraft:model", "model": f"{NS}:item/{k}"}})
    wjson(f"{DATA}/recipe/bike.json", {"type": "minecraft:crafting_shapeless", "category": "misc",
          "ingredients": ["minecraft:iron_ingot"] * 5 + ["minecraft:stick", "minecraft:leather", "minecraft:string"], "result": {"id": f"{NS}:bike", "count": 1}})
    wjson(f"{DATA}/recipe/car.json", {"type": "minecraft:crafting_shapeless", "category": "misc",
          "ingredients": ["minecraft:iron_block"] * 2 + ["minecraft:iron_ingot"] * 4 + ["minecraft:glass_pane"] * 2 + ["minecraft:redstone"], "result": {"id": f"{NS}:car", "count": 1}})
    for n, cols in (("iceologer", ("8fd0ee", "2c5d7a")), ("knight", ("8c9199", "c8282e")), ("archer_guard", ("3f7a38", "5a4630"))):
        egg_item(n, cols)
        LANG[f"item.{NS}.{n}_spawn_egg"] = title(n) + " Spawn Egg"
        LANG[f"entity.{NS}.{n}"] = title(n)
    LANG[f"entity.{NS}.bike"] = "Bicycle"; LANG[f"entity.{NS}.car"] = "Car"
    LANG[f"item.{NS}.bike"] = "Bicycle"; LANG[f"item.{NS}.car"] = "Car"
    LANG[f"entity.{NS}.ice_shard"] = "Ice Shard"; LANG[f"entity.{NS}.seat"] = "Seat"
    LANG[f"entity.{NS}.frost_golem"] = "Frost Golem"
    LANG[f"entity.{NS}.iceologer_boss"] = "Iceologer Warlord"
    LANG[f"itemGroup.{NS}"] = "My Minecraft"
    wjson(f"{DATA}/loot_table/entities/iceologer.json", loot_entity([("minecraft:packed_ice", 1, 3), ("minecraft:snowball", 2, 6), ("minecraft:emerald", 0, 2)]))
    wjson(f"{DATA}/loot_table/entities/knight.json", loot_entity([("minecraft:iron_nugget", 0, 4), ("minecraft:bread", 0, 1)]))
    wjson(f"{DATA}/loot_table/entities/archer_guard.json", loot_entity([("minecraft:arrow", 0, 4), ("minecraft:stick", 0, 2)]))
    # --- buttercup flower (dropped by Moobloom shearing) + crab claw item
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    d.line([8, 15, 8, 7], fill=(60, 140, 50, 255)); d.line([5, 12, 8, 10], fill=(60, 140, 50, 255)); d.line([11, 11, 8, 9], fill=(60, 140, 50, 255))
    for (cx, cy) in ((8, 5), (5, 7), (11, 7), (7, 3), (10, 3)):
        d.ellipse([cx - 2, cy - 2, cx + 2, cy + 2], fill=(255, 214, 40, 255)); d.point((cx, cy), fill=(255, 245, 160, 255))
    T._save(img, f"{ASSETS}/textures/block/buttercup.png")
    wjson(f"{ASSETS}/models/block/buttercup.json", {"parent": "minecraft:block/cross", "textures": {"cross": f"{NS}:block/buttercup"}})
    wjson(f"{ASSETS}/blockstates/buttercup.json", {"variants": {"": {"model": f"{NS}:block/buttercup"}}})
    wjson(f"{ASSETS}/items/buttercup.json", {"model": {"type": "minecraft:model", "model": f"{NS}:item/buttercup"}})
    wjson(f"{ASSETS}/models/item/buttercup.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:block/buttercup"}})
    wjson(f"{DATA}/loot_table/blocks/buttercup.json", loot_block("buttercup"))
    LANG[f"block.{NS}.buttercup"] = "Buttercup"
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    d.polygon([(3, 12), (4, 4), (8, 2), (12, 4), (13, 12), (10, 9), (8, 12), (6, 9)], fill=(200, 70, 46, 255))
    d.line([(8, 2), (8, 9)], fill=(120, 34, 20, 255)); d.point((6, 5), fill=(250, 170, 140, 255))
    T._save(img, f"{ASSETS}/textures/item/crab_claw.png")
    wjson(f"{ASSETS}/models/item/crab_claw.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/crab_claw"}})
    wjson(f"{ASSETS}/items/crab_claw.json", {"model": {"type": "minecraft:model", "model": f"{NS}:item/crab_claw"}})
    LANG[f"item.{NS}.crab_claw"] = "Crab Claw"
    ice = ["minecraft:snowy_plains", "minecraft:ice_spikes", "minecraft:snowy_taiga", "minecraft:snowy_slopes", "minecraft:frozen_peaks"]
    vil = ["minecraft:plains", "minecraft:sunflower_plains", "minecraft:meadow", "minecraft:forest", "minecraft:flower_forest", "minecraft:birch_forest", "minecraft:taiga"]
    town = ["minecraft:plains", "minecraft:meadow", "minecraft:forest", "minecraft:birch_forest", "minecraft:taiga"]
    haunt = ["minecraft:desert", "minecraft:plains", "minecraft:savanna", "minecraft:snowy_plains", "minecraft:taiga", "minecraft:meadow", "minecraft:grove", "minecraft:dark_forest"]
    nether = ["minecraft:nether_wastes", "minecraft:crimson_forest", "minecraft:soul_sand_valley"]
    LANG[f"entity.{NS}.wildfire_boss"] = "Wildfire Warlord"
    for kind, biomes, spacing, sep, salt in (("wildfire_castle", nether, 22, 8, 1741005), ("ice_castle", ice, 44, 18, 1741001), ("medieval_village", vil, 36, 12, 1741002),
                                              ("medieval_town", town, 64, 26, 1741003), ("haunted_outpost", haunt, 38, 14, 1741004)):
        wjson(f"{DATA}/tags/worldgen/biome/has_{kind}.json", {"values": biomes + EXTRA_TAGS.get(kind, [])})
        wjson(f"{DATA}/worldgen/structure/{kind}.json", {"type": f"{NS}:procedural", "biomes": f"#{NS}:has_{kind}",
              "step": "surface_structures", "spawn_overrides": {}, "terrain_adaptation": "none", "kind": kind})
        place = {"type": "minecraft:random_spread", "spacing": spacing, "separation": sep, "salt": salt}
        if kind in EXCLUDE:
            place["exclusion_zone"] = {"other_set": EXCLUDE[kind], "chunk_count": 5}
        wjson(f"{DATA}/worldgen/structure_set/{kind}.json", {"structures": [{"structure": f"{NS}:{kind}", "weight": 1}], "placement": place})


def gen_nekoma():
    """Ideas from Nekoma's Fixed (Clam, Underwater Torch), Friends&Foes (Totem of Freezing, small hideouts)."""
    # ---- clam block (closed / open models)
    img = Image.new("RGBA", (16, 16), (240, 200, 205, 255)); d = ImageDraw.Draw(img)
    for i in range(0, 16, 3):
        d.line([i, 0, 8, 15], fill=(214, 150, 165, 255))
    T.noisy(img, (0, 0, 16, 16), (240, 200, 205, 255), 4, __import__("random").Random("clam")); T._save(img, f"{ASSETS}/textures/block/clam.png")
    tex = {"shell": f"{NS}:block/clam", "particle": f"{NS}:block/clam", "pearl": "minecraft:block/white_concrete"}
    def els(boxes):
        return [{"from": b[:3], "to": b[3:6], "faces": {f: {"texture": "#" + b[6]} for f in ("north", "east", "south", "west", "up", "down")}} for b in boxes]
    wjson(f"{ASSETS}/models/block/clam_closed.json", {"parent": "minecraft:block/block", "textures": tex,
          "elements": els([(3, 0, 3, 13, 3, 13, "shell"), (3, 3, 3, 13, 5, 13, "shell")])})
    wjson(f"{ASSETS}/models/block/clam_open.json", {"parent": "minecraft:block/block", "textures": tex,
          "elements": els([(3, 0, 3, 13, 3, 13, "shell"), (3, 3, 11, 13, 9, 13, "shell"), (6, 3, 5, 10, 4, 9, "pearl")])})
    var = {}
    for f, y in {"north": 0, "east": 90, "south": 180, "west": 270}.items():
        for o in ("false", "true"):
            var[f"facing={f},open={o}"] = dict(model=f"{NS}:block/clam_" + ("open" if o == "true" else "closed"), **({"y": y} if y else {}))
    wjson(f"{ASSETS}/blockstates/clam.json", {"variants": var})
    wjson(f"{ASSETS}/items/clam.json", {"model": {"type": "minecraft:model", "model": f"{NS}:block/clam_closed"}})
    wjson(f"{DATA}/loot_table/blocks/clam.json", loot_block("clam"))
    LANG[f"block.{NS}.clam"] = "Clam"
    # ---- underwater torch
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    d.rectangle([7, 6, 8, 15], fill=(120, 80, 40, 255)); d.rectangle([6, 3, 9, 6], fill=(90, 220, 230, 255)); d.rectangle([7, 1, 8, 3], fill=(200, 255, 255, 255))
    T._save(img, f"{ASSETS}/textures/block/underwater_torch.png")
    wjson(f"{ASSETS}/models/block/underwater_torch.json", {"parent": "minecraft:block/template_torch", "textures": {"torch": f"{NS}:block/underwater_torch"}})
    wjson(f"{ASSETS}/blockstates/underwater_torch.json", {"variants": {"": {"model": f"{NS}:block/underwater_torch"}}})
    wjson(f"{ASSETS}/models/item/underwater_torch.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:block/underwater_torch"}})
    wjson(f"{ASSETS}/items/underwater_torch.json", {"model": {"type": "minecraft:model", "model": f"{NS}:item/underwater_torch"}})
    wjson(f"{DATA}/loot_table/blocks/underwater_torch.json", loot_block("underwater_torch"))
    recipe_file("underwater_torch", {"type": "minecraft:crafting_shapeless", "category": "misc",
          "ingredients": ["minecraft:torch", "minecraft:prismarine_shard"], "result": {"id": f"{NS}:underwater_torch", "count": 2}})
    LANG[f"block.{NS}.underwater_torch"] = "Underwater Torch"
    # ---- clam pearl + totem of freezing items
    for name, col, title_ in (("clam_pearl", (250, 245, 255, 255), "Clam Pearl"), ("totem_of_freezing", (120, 200, 255, 255), "Totem of Freezing")):
        img = Image.new("RGBA", (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
        if name == "clam_pearl":
            d.ellipse([4, 4, 11, 11], fill=col); d.point((6, 6), fill=(255, 255, 255, 255)); d.ellipse([4, 4, 11, 11], outline=(200, 190, 220, 255))
        else:
            d.polygon([(8, 1), (13, 5), (13, 12), (8, 15), (3, 12), (3, 5)], fill=col, outline=(40, 90, 150, 255))
            d.line([8, 3, 8, 13], fill=(230, 250, 255, 255)); d.line([5, 8, 11, 8], fill=(230, 250, 255, 255))
        T._save(img, f"{ASSETS}/textures/item/{name}.png")
        wjson(f"{ASSETS}/models/item/{name}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/{name}"}})
        wjson(f"{ASSETS}/items/{name}.json", {"model": {"type": "minecraft:model", "model": f"{NS}:item/{name}"}})
        LANG[f"item.{NS}.{name}"] = title_
    wjson(f"{DATA}/recipe/totem_of_freezing.json", {"type": "minecraft:crafting_shapeless", "category": "misc",
          "ingredients": ["minecraft:blue_ice"] * 3 + ["minecraft:gold_ingot"] * 2 + ["minecraft:emerald", "minecraft:snowball", "minecraft:packed_ice"],
          "result": {"id": f"{NS}:totem_of_freezing", "count": 1}})
    # ---- clam worldgen feature
    wjson(f"{DATA}/worldgen/configured_feature/clam_patch.json", {"type": "minecraft:simple_block", "config": {"to_place": {
        "type": "minecraft:simple_state_provider", "state": {"Name": f"{NS}:clam", "Properties": {"facing": "north", "open": "false", "waterlogged": "true"}}}}})
    wjson(f"{DATA}/worldgen/placed_feature/clam_patch.json", {"feature": f"{NS}:clam_patch", "placement": [
        {"type": "minecraft:count", "count": 6}, {"type": "minecraft:in_square"},
        {"type": "minecraft:heightmap", "heightmap": "OCEAN_FLOOR_WG"},
        {"type": "minecraft:block_predicate_filter", "predicate": {"type": "minecraft:matching_blocks", "blocks": "minecraft:water"}},
        {"type": "minecraft:biome"}]})
    # ---- small hideouts
    for kind, biomes, spacing, sep, salt in (
            ("iceologer_cabin", ["minecraft:snowy_taiga", "minecraft:snowy_plains", "minecraft:grove"], 28, 10, 1741006),
            ("illusioner_shack", ["minecraft:dark_forest", "minecraft:swamp", "minecraft:mangrove_swamp"], 30, 10, 1741007),
            ("baobab_tree", ["minecraft:savanna", "minecraft:savanna_plateau", "minecraft:windswept_savanna"], 10, 4, 1741008),
            ("termite_mound", ["minecraft:savanna", "minecraft:savanna_plateau"], 18, 6, 1741009),
            ("meerkat_burrow", ["minecraft:desert"], 20, 6, 1741010)):
        wjson(f"{DATA}/tags/worldgen/biome/has_{kind}.json", {"values": biomes})
        wjson(f"{DATA}/worldgen/structure/{kind}.json", {"type": f"{NS}:procedural", "biomes": f"#{NS}:has_{kind}",
              "step": "surface_structures", "spawn_overrides": {}, "terrain_adaptation": "none", "kind": kind})
        wjson(f"{DATA}/worldgen/structure_set/{kind}.json", {"structures": [{"structure": f"{NS}:{kind}", "weight": 1}],
              "placement": {"type": "minecraft:random_spread", "spacing": spacing, "separation": sep, "salt": salt}})


if __name__ == "__main__":
    gen_nekoma()
    gen_furniture()
    gen_animals()
    gen_misc()
    wjson(f"{ASSETS}/lang/en_us.json", dict(sorted(LANG.items())))
    print("lang entries:", len(LANG))
