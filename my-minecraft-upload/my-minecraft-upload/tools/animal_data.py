"""
Animal definitions for the "My Minecraft" mod.  generate.py turns these into:
  * AnimalSpecs.java  (model dimensions, AI, stats, spawn rules)
  * a procedural 256x256 texture per animal, lang entries, loot tables, spawn-egg item definitions.

Model units are pixels (16 px = 1 block). UPRIGHT animals (birds, kangaroo, gorilla, penguin) stand on
two legs with a tall body; QUAD animals walk on four legs.
Behaviour: PREY (panics) | CALM (peaceful) | NEUTRAL (fights back) | PREDATOR (fights back + hunts small animals)
Diet:      HERBIVORE | CARNIVORE | OMNIVORE | FISH | SEEDS
"""

ANIMALS = []


def animal(name, layout, behavior, diet, body, head, legs, hp, speed, dmg, sounds, spawn, pal, drops, egg, **kw):
    d = dict(name=name, layout=layout, behavior=behavior, diet=diet, body=body, head=head, legs=legs,
             hp=hp, speed=speed, dmg=dmg, sounds=sounds, spawn=spawn, pal=pal, drops=drops, egg=egg)
    d.update(kw)
    ANIMALS.append(d)


def pal(base, dark, light, pattern="none", accent=None):
    return dict(base=base, dark=dark, light=light, pattern=pattern, accent=accent or dark)


animal("capybara", "QUAD", "CALM", "HERBIVORE", (10, 9, 16), (7, 7, 8), (5, 3), 14, 0.22, 0, "PIG",
       (8, 2, 5, "keys('SWAMP','MANGROVE_SWAMP','JUNGLE')"), pal("8a6a47", "5e4630", "a58863"),
       [("minecraft:beef", 1, 2), ("minecraft:leather", 0, 1)], ("8a6a47", "3b2b1c"),
       snout=(6, 4, 3), ears=2, tail=(1, 2))
animal("brown_bear", "QUAD", "NEUTRAL", "OMNIVORE", (14, 13, 20), (9, 8, 9), (8, 5), 34, 0.26, 7, "POLAR_BEAR",
       (6, 1, 2, "tag('IS_TAIGA')"), pal("6b4423", "4a2e15", "8a5d33", accent="a07a4f"),
       [("minecraft:beef", 2, 4), ("minecraft:leather", 1, 2)], ("6b4423", "3d2411"),
       snout=(5, 4, 4), ears=3, tail=(2, 3))
animal("black_bear", "QUAD", "NEUTRAL", "OMNIVORE", (12, 11, 18), (8, 7, 8), (7, 4), 28, 0.27, 6, "POLAR_BEAR",
       (6, 1, 2, "tag('IS_FOREST')"), pal("1f1c1a", "0e0c0b", "33302d", accent="a88b62"),
       [("minecraft:beef", 2, 3), ("minecraft:leather", 1, 1)], ("1f1c1a", "a88b62"),
       snout=(5, 3, 4), ears=3, tail=(2, 3))
animal("lion", "QUAD", "PREDATOR", "CARNIVORE", (11, 11, 20), (9, 9, 9), (9, 4), 28, 0.3, 6, "CAT",
       (6, 1, 3, "tag('IS_SAVANNA')"), pal("c9a15b", "8a6a30", "e4cf9d", accent="5b3a17"),
       [("minecraft:beef", 2, 3), ("minecraft:leather", 0, 1)], ("c9a15b", "5b3a17"),
       snout=(5, 4, 3), ears=2, tail=(14, 2), mane='ring')
animal("tiger", "QUAD", "PREDATOR", "CARNIVORE", (10, 10, 20), (8, 8, 8), (8, 4), 26, 0.32, 6, "CAT",
       (5, 1, 2, "tag('IS_JUNGLE')"), pal("d9822b", "1a1410", "f3e6cf", "stripes"),
       [("minecraft:beef", 2, 3), ("minecraft:leather", 0, 1)], ("d9822b", "1a1410"),
       snout=(5, 3, 3), ears=2, tail=(14, 2))
animal("gorilla", "UPRIGHT", "NEUTRAL", "OMNIVORE", (12, 12, 8), (7, 7, 7), (8, 5), 40, 0.25, 7, "HOGLIN",
       (5, 1, 2, "tag('IS_JUNGLE')"), pal("2e2b2b", "181616", "47433f", accent="6b6460"),
       [("minecraft:beef", 2, 3)], ("2e2b2b", "6b6460"),
       arms=(14, 4), snout=(5, 3, 3), ears=1)
animal("red_panda", "QUAD", "PREY", "OMNIVORE", (6, 6, 12), (5, 5, 5), (4, 2), 12, 0.24, 0, "FOX",
       (6, 2, 4, "tag('IS_JUNGLE')"), pal("b5481f", "2a1a14", "f1ddc6", "rings"),
       [("minecraft:rabbit_hide", 0, 1)], ("b5481f", "f1ddc6"),
       snout=(3, 2, 2), ears=2, tail=(12, 3))
animal("raccoon", "QUAD", "NEUTRAL", "OMNIVORE", (6, 6, 10), (5, 5, 5), (4, 2), 14, 0.26, 2, "FOX",
       (8, 2, 4, "tag('IS_FOREST')"), pal("7a7774", "34322f", "b8b4ae", "rings"),
       [("minecraft:rabbit_hide", 0, 1)], ("7a7774", "34322f"),
       snout=(3, 2, 3), ears=2, tail=(9, 3))
animal("hedgehog", "QUAD", "PREY", "OMNIVORE", (6, 5, 8), (4, 4, 5), (2, 2), 8, 0.2, 0, "RABBIT",
       (8, 2, 3, "any(tag('IS_FOREST'), keys('PLAINS'))"), pal("7c6a52", "3d3228", "d3bfa0", "spots"),
       [("minecraft:rabbit_hide", 0, 1)], ("7c6a52", "d3bfa0"),
       snout=(2, 2, 3), ears=1, mane='spikes')
animal("squirrel", "QUAD", "PREY", "SEEDS", (4, 4, 6), (4, 4, 4), (3, 2), 6, 0.3, 0, "RABBIT",
       (10, 2, 4, "tag('IS_FOREST')"), pal("a8532a", "6a3016", "e7cfae"),
       [("minecraft:rabbit_hide", 0, 1)], ("a8532a", "e7cfae"),
       snout=(2, 2, 2), ears=1, tail=(8, 3))
animal("kangaroo", "UPRIGHT", "PREY", "HERBIVORE", (8, 12, 9), (5, 6, 7), (10, 4), 20, 0.3, 0, "LLAMA",
       (6, 2, 3, "any(tag('IS_SAVANNA'), tag('IS_BADLANDS'))"), pal("b0804e", "7a5230", "e1c79b"),
       [("minecraft:beef", 1, 2), ("minecraft:leather", 0, 1)], ("b0804e", "e1c79b"),
       arms=(6, 2), snout=(3, 3, 3), ears=3, tail=(14, 4))
animal("elephant", "QUAD", "CALM", "HERBIVORE", (22, 22, 30), (14, 16, 12), (14, 8), 70, 0.2, 10, "RAVAGER",
       (3, 1, 2, "tag('IS_SAVANNA')"), pal("8f8d8a", "5f5d5a", "b3b1ae"),
       [("minecraft:leather", 2, 4), ("minecraft:beef", 3, 5)], ("8f8d8a", "5f5d5a"),
       ears=8, tail=(10, 3), trunk=True, tusks=True)
animal("giraffe", "QUAD", "PREY", "HERBIVORE", (10, 12, 16), (5, 5, 8), (22, 3), 30, 0.26, 0, "HORSE",
       (4, 1, 3, "tag('IS_SAVANNA')"), pal("d8a94a", "6d4217", "f0e0b0", "patches"),
       [("minecraft:leather", 1, 2), ("minecraft:beef", 2, 3)], ("d8a94a", "6d4217"),
       neck=(22, 4), snout=(4, 3, 3), ears=2, tail=(10, 2), horns=True)
animal("zebra", "QUAD", "PREY", "HERBIVORE", (10, 12, 18), (6, 7, 9), (12, 3), 22, 0.3, 0, "HORSE",
       (6, 2, 4, "tag('IS_SAVANNA')"), pal("f2f2f2", "151515", "ffffff", "stripes"),
       [("minecraft:leather", 1, 2), ("minecraft:beef", 1, 3)], ("f2f2f2", "151515"),
       neck=(8, 5), snout=(5, 4, 4), ears=2, tail=(9, 2), mane='strip')
animal("rhino", "QUAD", "NEUTRAL", "HERBIVORE", (16, 16, 26), (9, 11, 14), (10, 6), 50, 0.24, 9, "RAVAGER",
       (3, 1, 2, "tag('IS_SAVANNA')"), pal("7d7b78", "55534f", "a19f9b"),
       [("minecraft:leather", 1, 3), ("minecraft:beef", 2, 4)], ("7d7b78", "e3dccb"),
       ears=2, tail=(5, 2), horn=True)
animal("hippo", "QUAD", "NEUTRAL", "HERBIVORE", (18, 16, 28), (14, 12, 12), (6, 7), 55, 0.2, 9, "HOGLIN",
       (4, 1, 2, "any(tag('IS_SAVANNA'), keys('SWAMP'))"), pal("8c7a82", "5d4d55", "c2aab1"),
       [("minecraft:leather", 1, 2), ("minecraft:beef", 3, 5)], ("8c7a82", "c2aab1"),
       snout=(12, 8, 6), ears=2, tail=(3, 2), tusks=True)
animal("bison", "QUAD", "NEUTRAL", "HERBIVORE", (14, 16, 22), (9, 10, 10), (10, 4), 36, 0.23, 6, "COW",
       (6, 2, 4, "keys('PLAINS','SUNFLOWER_PLAINS','MEADOW')"), pal("4a3123", "2b1b12", "6e4c37"),
       [("minecraft:beef", 3, 5), ("minecraft:leather", 1, 2)], ("4a3123", "6e4c37"),
       snout=(6, 5, 3), ears=2, tail=(6, 2), horns=True, hump=True)
animal("deer", "QUAD", "PREY", "HERBIVORE", (8, 9, 16), (5, 6, 7), (12, 2), 18, 0.3, 0, "GOAT",
       (8, 2, 4, "tag('IS_FOREST')"), pal("a1703f", "5c3d1e", "e9d9bd", "spots"),
       [("minecraft:beef", 1, 2), ("minecraft:leather", 0, 1)], ("a1703f", "e9d9bd"),
       neck=(5, 4), snout=(3, 3, 3), ears=3, tail=(3, 2), antlers=True)
animal("moose", "QUAD", "NEUTRAL", "HERBIVORE", (14, 16, 22), (8, 10, 14), (18, 4), 40, 0.25, 8, "HORSE",
       (4, 1, 2, "tag('IS_TAIGA')"), pal("4f3a2b", "2e2117", "7c6350"),
       [("minecraft:beef", 3, 5), ("minecraft:leather", 1, 2)], ("4f3a2b", "7c6350"),
       neck=(6, 6), snout=(6, 6, 4), ears=3, tail=(3, 2), antlers=True)
animal("crocodile", "QUAD", "PREDATOR", "CARNIVORE", (8, 5, 22), (6, 4, 10), (3, 3), 26, 0.22, 7, "RAVAGER",
       (4, 1, 2, "keys('SWAMP','MANGROVE_SWAMP')"), pal("4d6b3a", "2c4120", "b5c58f", "stripes"),
       [("minecraft:leather", 1, 2), ("minecraft:beef", 1, 2)], ("4d6b3a", "b5c58f"),
       snout=(5, 3, 8), tail=(20, 5))
animal("eagle", "UPRIGHT", "PREDATOR", "CARNIVORE", (6, 8, 5), (5, 5, 5), (4, 2), 12, 0.2, 3, "PARROT",
       (6, 2, 3, "any(tag('IS_MOUNTAIN'), tag('IS_HILL'))"), pal("5c3d24", "3a2414", "f3f0e8", accent="e0b030"),
       [("minecraft:feather", 1, 3), ("minecraft:chicken", 0, 1)], ("5c3d24", "f3f0e8"),
       flying=True, snout=(2, 2, 3), tail=(5, 3), wings=(14, 2), beak=True)
animal("owl", "UPRIGHT", "PREDATOR", "CARNIVORE", (6, 8, 5), (7, 6, 5), (3, 2), 8, 0.2, 2, "PARROT",
       (6, 2, 3, "tag('IS_FOREST')"), pal("8d6e4c", "4d3a28", "e8dcc3", "spots", accent="f0c020"),
       [("minecraft:feather", 1, 2)], ("8d6e4c", "e8dcc3"),
       flying=True, snout=(2, 2, 2), tail=(4, 3), wings=(10, 2), beak=True, ears=2)
animal("flamingo", "UPRIGHT", "PREY", "FISH", (6, 8, 6), (3, 3, 4), (16, 2), 8, 0.22, 0, "PARROT",
       (6, 2, 4, "keys('BEACH','SWAMP','MANGROVE_SWAMP')"), pal("f08aa5", "c9506f", "ffc2d1", accent="1a1a1a"),
       [("minecraft:feather", 1, 2), ("minecraft:chicken", 0, 1)], ("f08aa5", "ffc2d1"),
       neck=(12, 2), snout=(2, 2, 3), tail=(3, 3), wings=(8, 1), beak=True)
animal("penguin", "UPRIGHT", "PREY", "FISH", (8, 12, 6), (6, 6, 6), (2, 3), 10, 0.2, 0, "PARROT",
       (8, 2, 5, "keys('SNOWY_BEACH','SNOWY_PLAINS')"), pal("161a24", "0b0d14", "f4f4f4", accent="f0a020"),
       [("minecraft:feather", 1, 2), ("minecraft:cod", 0, 1)], ("161a24", "f4f4f4"),
       snout=(2, 1, 3), wings=(7, 2), beak=True)

# ================================================================== BATCH 2: more real-life animals
animal("cheetah", "QUAD", "PREDATOR", "CARNIVORE", (8, 8, 16), (6, 6, 7), (10, 3), 20, 0.4, 5, "CAT",
       (5, 1, 2, "tag('IS_SAVANNA')"), pal("e0b45a", "2a1a10", "f4e6c4", "dots"),
       [("minecraft:beef", 1, 2), ("minecraft:leather", 0, 1)], ("e0b45a", "2a1a10"), snout=(4, 3, 2), ears=2, tail=(14, 2))
animal("leopard", "QUAD", "PREDATOR", "CARNIVORE", (9, 9, 17), (7, 7, 7), (8, 3), 24, 0.34, 6, "CAT",
       (4, 1, 2, "any(tag('IS_JUNGLE'), tag('IS_SAVANNA'))"), pal("d5a24a", "1b130c", "f1dfb2", "dots"),
       [("minecraft:beef", 1, 3), ("minecraft:leather", 0, 1)], ("d5a24a", "1b130c"), snout=(4, 3, 3), ears=2, tail=(15, 2))
animal("hyena", "QUAD", "PREDATOR", "OMNIVORE", (9, 9, 16), (7, 7, 8), (8, 3), 24, 0.3, 5, "FOX",
       (5, 2, 4, "tag('IS_SAVANNA')"), pal("a08a68", "3a2e22", "d8c8a8", "dots"),
       [("minecraft:beef", 1, 2), ("minecraft:rotten_flesh", 0, 1)], ("a08a68", "3a2e22"), snout=(5, 4, 4), ears=3, tail=(5, 3))
animal("wild_boar", "QUAD", "NEUTRAL", "OMNIVORE", (10, 9, 15), (7, 7, 7), (6, 3), 22, 0.26, 5, "PIG",
       (6, 2, 4, "any(tag('IS_FOREST'), tag('IS_TAIGA'))"), pal("5a4636", "2c221a", "8a7660"),
       [("minecraft:porkchop", 1, 3)], ("5a4636", "8a7660"), snout=(5, 4, 3), ears=2, tail=(3, 2), tusks=True, mane='strip')
animal("badger", "QUAD", "NEUTRAL", "OMNIVORE", (7, 6, 11), (5, 5, 6), (3, 3), 14, 0.24, 3, "FOX",
       (6, 2, 3, "any(tag('IS_FOREST'), keys('PLAINS'))"), pal("4a4743", "1a1918", "e6e4de", "stripes"),
       [("minecraft:rabbit_hide", 0, 1)], ("4a4743", "e6e4de"), snout=(3, 3, 3), ears=2, tail=(3, 3))
animal("koala", "QUAD", "CALM", "HERBIVORE", (7, 8, 7), (7, 6, 6), (4, 3), 14, 0.18, 0, "FOX",
       (5, 2, 3, "tag('IS_FOREST')"), pal("9a9a98", "5c5c5a", "d9d9d6"),
       [("minecraft:leather", 0, 1)], ("9a9a98", "262626"), snout=(3, 4, 2), ears=4)
animal("sloth", "QUAD", "CALM", "HERBIVORE", (7, 7, 10), (5, 5, 5), (4, 2), 14, 0.1, 0, "RABBIT",
       (5, 1, 2, "tag('IS_JUNGLE')"), pal("8f7e63", "4d4130", "cdbf9f"),
       [("minecraft:leather", 0, 1)], ("8f7e63", "cdbf9f"), snout=(3, 2, 1), ears=1, arms=(7, 2))
animal("orangutan", "UPRIGHT", "CALM", "OMNIVORE", (9, 10, 6), (7, 7, 6), (6, 3), 28, 0.22, 4, "HOGLIN",
       (4, 1, 2, "tag('IS_JUNGLE')"), pal("b5612a", "6b3512", "d99455"),
       [("minecraft:beef", 1, 2)], ("b5612a", "6b3512"), arms=(14, 3), snout=(4, 3, 2), ears=1)
animal("antelope", "QUAD", "PREY", "HERBIVORE", (7, 8, 14), (4, 5, 7), (12, 2), 16, 0.34, 0, "GOAT",
       (7, 2, 5, "tag('IS_SAVANNA')"), pal("b8864e", "5c3d20", "f0e2c8", "stripes"),
       [("minecraft:beef", 1, 2), ("minecraft:leather", 0, 1)], ("b8864e", "f0e2c8"), neck=(5, 3), snout=(3, 3, 3), ears=3, tail=(4, 2), horns=True)
animal("warthog", "QUAD", "NEUTRAL", "OMNIVORE", (9, 8, 13), (8, 7, 8), (6, 3), 22, 0.28, 5, "PIG",
       (5, 1, 3, "tag('IS_SAVANNA')"), pal("6e5a48", "3b2e22", "a38d74"),
       [("minecraft:porkchop", 1, 2)], ("6e5a48", "a38d74"), snout=(5, 4, 3), ears=3, tail=(8, 1), tusks=True, mane='strip')
animal("meerkat", "UPRIGHT", "PREY", "OMNIVORE", (5, 8, 4), (4, 4, 4), (3, 2), 6, 0.26, 0, "RABBIT",
       (8, 2, 5, "any(keys('DESERT'), tag('IS_SAVANNA'))"), pal("c8a878", "5a4328", "ecdcc0"),
       [("minecraft:rabbit_hide", 0, 1)], ("c8a878", "5a4328"), arms=(3, 1), snout=(2, 2, 2), ears=1, tail=(7, 2))
animal("ostrich", "UPRIGHT", "PREY", "HERBIVORE", (9, 9, 12), (3, 3, 5), (16, 2), 20, 0.36, 4, "PARROT",
       (6, 2, 4, "tag('IS_SAVANNA')"), pal("2c2a2a", "151414", "f4f2ee", accent="d49a8a"),
       [("minecraft:feather", 1, 3), ("minecraft:chicken", 1, 2)], ("2c2a2a", "f4f2ee"),
       neck=(16, 2), snout=(2, 1, 3), tail=(7, 5), wings=(7, 1), beak=True)
animal("vulture", "UPRIGHT", "PREDATOR", "CARNIVORE", (7, 9, 5), (4, 4, 5), (4, 2), 14, 0.2, 3, "PARROT",
       (5, 2, 3, "any(tag('IS_BADLANDS'), keys('DESERT'), tag('IS_SAVANNA'))"), pal("3a2f28", "1c1612", "d9a89a", accent="e8d8c0"),
       [("minecraft:feather", 1, 2), ("minecraft:rotten_flesh", 0, 2)], ("3a2f28", "d9a89a"),
       flying=True, neck=(3, 2), snout=(2, 2, 3), tail=(6, 4), wings=(16, 2), beak=True)
animal("peacock", "UPRIGHT", "PREY", "SEEDS", (5, 8, 8), (3, 3, 4), (7, 1), 8, 0.22, 0, "PARROT",
       (6, 2, 4, "any(keys('PLAINS','JUNGLE'), tag('IS_JUNGLE'))"), pal("1f7a8c", "0e3f55", "5fc4c4", "spots", accent="e8c030"),
       [("minecraft:feather", 1, 3)], ("1f7a8c", "e8c030"), neck=(8, 2), snout=(2, 1, 2), tail=(14, 7), wings=(6, 1), beak=True)
animal("toucan", "UPRIGHT", "PREY", "SEEDS", (4, 6, 4), (4, 4, 4), (3, 1), 6, 0.2, 0, "PARROT",
       (8, 2, 4, "tag('IS_JUNGLE')"), pal("141414", "050505", "f4f0e0", accent="f08a1a"),
       [("minecraft:feather", 1, 2)], ("141414", "f08a1a"), flying=True, snout=(3, 3, 6), tail=(5, 3), wings=(8, 1), beak=True)
animal("duck", "UPRIGHT", "PREY", "SEEDS", (5, 6, 7), (3, 3, 3), (3, 1), 6, 0.2, 0, "CHICKEN",
       (10, 2, 5, "keys('SWAMP','PLAINS','MANGROVE_SWAMP','RIVER')"), pal("7a5a3a", "3e2c1c", "e8e0d0", accent="f0b020"),
       [("minecraft:feather", 1, 2), ("minecraft:chicken", 0, 1)], ("3a7a3a", "f0b020"), snout=(2, 1, 3), tail=(3, 3), wings=(5, 1), beak=True)

# ================================================================== MOB VOTE LOSERS (2017-2023)
# (Iceologer = illager/Ice Castle, Penguin = above, Copper Golem is vanilla since the 2025 Fall Drop.)
animal("moobloom", "QUAD", "CALM", "HERBIVORE", (10, 10, 16), (8, 8, 6), (7, 4), 14, 0.2, 0, "COW",
       (8, 3, 6, "keys('FLOWER_FOREST','MEADOW')"), pal("f6e05a", "d9a400", "fff6b8", "patches"),
       [("minecraft:beef", 1, 3), ("minecraft:leather", 0, 2)], ("f6e05a", "d9a400"),
       snout=(6, 3, 1), ears=2, tail=(8, 2), horns=True, special="MOOBLOOM")
animal("glare", "UPRIGHT", "CALM", "HERBIVORE", (7, 6, 6), (6, 5, 5), (1, 1), 8, 0.18, 0, "PARROT",
       (6, 2, 3, "keys('LUSH_CAVES')"), pal("78a83c", "3f6a1f", "c8e07a", accent="f8e040"),
       [("minecraft:glow_berries", 1, 2)], ("78a83c", "f8e040"),
       flying=True, wings=(7, 1), ears=2, special="GLARE", spawnkind="CAVE")
animal("rascal", "QUAD", "PREY", "OMNIVORE", (6, 6, 8), (6, 6, 6), (4, 2), 10, 0.3, 0, "FOX",
       (6, 2, 3, "keys('DRIPSTONE_CAVES')"), pal("8a5a3a", "4a2c1a", "d8b48a"),
       [("minecraft:coal", 0, 1)], ("8a5a3a", "d8b48a"), snout=(3, 2, 2), ears=3, tail=(6, 2), special="RASCAL", spawnkind="CAVE")
animal("tuff_golem", "UPRIGHT", "CALM", "HERBIVORE", (8, 10, 6), (6, 6, 6), (6, 3), 30, 0.18, 0, "HOGLIN",
       (5, 1, 2, "overworld()"), pal("6c6c62", "3e3e38", "9a9a8e"),
       [("minecraft:tuff", 2, 5)], ("6c6c62", "9a9a8e"), arms=(8, 3), ears=0, special="TUFF_GOLEM", spawnkind="TUFF")
animal("crab", "QUAD", "NEUTRAL", "FISH", (12, 5, 10), (5, 3, 3), (3, 2), 12, 0.2, 3, "RABBIT",
       (8, 3, 6, "keys('BEACH','MANGROVE_SWAMP','STONY_SHORE')"), pal("c8462e", "7a2214", "f0a888"),
       [("minecraft:cod", 0, 1), ("myminecraft:crab_claw", 0, 1)], ("c8462e", "f0a888"),
       arms=(5, 3), horns=True, spawnkind="SHORE")

# ================================================================== BATCH 3: way more animals (no overlap with vanilla or earlier species)
def q(name, behavior, diet, body, head, legs, hp, speed, dmg, sounds, spawn, pal_, drops, egg, **kw):
    animal(name, "QUAD", behavior, diet, body, head, legs, hp, speed, dmg, sounds, spawn, pal_, drops, egg, **kw)


def u(name, behavior, diet, body, head, legs, hp, speed, dmg, sounds, spawn, pal_, drops, egg, **kw):
    animal(name, "UPRIGHT", behavior, diet, body, head, legs, hp, speed, dmg, sounds, spawn, pal_, drops, egg, **kw)


MEAT = [("minecraft:beef", 1, 2)]
SMALL = [("minecraft:rabbit_hide", 0, 1)]
FEATHER = [("minecraft:feather", 1, 2)]
q("skunk", "NEUTRAL", "OMNIVORE", (5, 5, 9), (4, 4, 5), (3, 2), 8, 0.22, 1, "FOX", (6, 2, 3, "tag('IS_FOREST')"),
  pal("1c1c1c", "0a0a0a", "f4f4f4", "stripes"), SMALL, ("1c1c1c", "f4f4f4"), snout=(2, 2, 2), ears=1, tail=(9, 4))
q("beaver", "CALM", "HERBIVORE", (6, 6, 11), (5, 5, 5), (3, 2), 10, 0.2, 0, "RABBIT", (6, 2, 3, "any(tag('IS_RIVER'), keys('SWAMP','TAIGA'))"),
  pal("7a5230", "4a2f18", "a97c50"), SMALL, ("7a5230", "2a1a0c"), snout=(3, 2, 2), ears=1, tail=(7, 3))
q("porcupine", "PREY", "HERBIVORE", (7, 6, 10), (4, 4, 5), (3, 2), 10, 0.18, 2, "RABBIT", (6, 2, 3, "any(tag('IS_FOREST'), tag('IS_TAIGA'))"),
  pal("5c4a38", "2a2018", "c8b898", "stripes"), SMALL, ("5c4a38", "e8d8b0"), snout=(2, 2, 2), ears=1, tail=(3, 2), mane='spikes')
q("anteater", "CALM", "OMNIVORE", (7, 8, 16), (3, 3, 10), (7, 3), 18, 0.18, 2, "RABBIT", (5, 1, 3, "tag('IS_JUNGLE')"),
  pal("7f7b72", "211e1a", "c9c4b8", "stripes"), MEAT, ("7f7b72", "211e1a"), snout=(2, 2, 8), ears=1, tail=(16, 6))
q("lynx", "PREDATOR", "CARNIVORE", (7, 8, 14), (6, 6, 6), (8, 3), 18, 0.34, 4, "CAT", (5, 1, 2, "tag('IS_TAIGA')"),
  pal("c4a47c", "5a4430", "ece0cc", "dots"), MEAT, ("c4a47c", "3a2c1c"), snout=(3, 3, 2), ears=3, tail=(4, 3))
q("bobcat", "PREDATOR", "CARNIVORE", (7, 7, 13), (6, 6, 6), (7, 3), 16, 0.32, 4, "CAT", (5, 1, 2, "any(tag('IS_FOREST'), tag('IS_BADLANDS'))"),
  pal("a9835a", "4a3822", "e4d6bc", "dots"), MEAT, ("a9835a", "4a3822"), snout=(3, 3, 2), ears=3, tail=(4, 3))
q("puma", "PREDATOR", "CARNIVORE", (9, 9, 18), (7, 7, 7), (9, 4), 26, 0.34, 6, "CAT", (4, 1, 2, "any(tag('IS_MOUNTAIN'), tag('IS_BADLANDS'))"),
  pal("c8a070", "7a5a3a", "e8d8bc"), MEAT + [("minecraft:leather", 0, 1)], ("c8a070", "7a5a3a"), snout=(4, 3, 3), ears=2, tail=(16, 2))
q("snow_leopard", "PREDATOR", "CARNIVORE", (9, 9, 17), (7, 7, 7), (8, 4), 24, 0.33, 6, "CAT", (4, 1, 2, "any(keys('SNOWY_SLOPES','FROZEN_PEAKS','GROVE'))"),
  pal("d8d4cc", "2a2a2a", "f4f2ee", "dots"), MEAT, ("d8d4cc", "2a2a2a"), snout=(4, 3, 3), ears=2, tail=(16, 4))
q("jaguar", "PREDATOR", "CARNIVORE", (10, 10, 18), (8, 8, 8), (8, 4), 28, 0.32, 7, "CAT", (4, 1, 2, "tag('IS_JUNGLE')"),
  pal("d4962c", "1a1008", "f1d9a0", "dots"), MEAT + [("minecraft:leather", 0, 1)], ("d4962c", "1a1008"), snout=(5, 4, 3), ears=2, tail=(15, 3))
q("reindeer", "PREY", "HERBIVORE", (8, 9, 16), (5, 6, 8), (11, 2), 22, 0.28, 0, "GOAT", (7, 2, 4, "any(keys('SNOWY_PLAINS','SNOWY_TAIGA','GROVE'))"),
  pal("8a7a6a", "4a3e32", "f0ece4"), MEAT + [("minecraft:leather", 0, 1)], ("8a7a6a", "f0ece4"), neck=(5, 4), snout=(3, 3, 3), ears=3, tail=(3, 2), antlers=True)
q("yak", "NEUTRAL", "HERBIVORE", (12, 13, 20), (8, 9, 9), (8, 4), 34, 0.22, 6, "COW", (5, 1, 3, "any(keys('SNOWY_SLOPES','STONY_PEAKS','JAGGED_PEAKS','GROVE'))"),
  pal("2e2420", "140e0c", "4d3e36"), MEAT + [("minecraft:leather", 1, 2)], ("2e2420", "4d3e36"), snout=(5, 4, 2), ears=2, tail=(8, 3), horns=True, hump=True)
q("musk_ox", "NEUTRAL", "HERBIVORE", (11, 12, 17), (7, 8, 8), (7, 4), 30, 0.22, 6, "COW", (5, 1, 3, "any(keys('SNOWY_PLAINS','ICE_SPIKES'))"),
  pal("3a2e26", "1c1410", "6e5a48"), MEAT + [("minecraft:leather", 1, 2)], ("3a2e26", "6e5a48"), snout=(5, 4, 2), ears=2, tail=(4, 2), horns=True, hump=True)
q("wolverine", "PREDATOR", "CARNIVORE", (7, 7, 13), (5, 5, 6), (4, 3), 20, 0.28, 5, "FOX", (4, 1, 2, "any(keys('SNOWY_TAIGA','TAIGA','GROVE'))"),
  pal("3e3028", "1a120c", "b89a6a", "stripes"), MEAT, ("3e3028", "b89a6a"), snout=(3, 3, 2), ears=2, tail=(6, 3))
q("walrus", "NEUTRAL", "FISH", (14, 11, 20), (8, 8, 8), (3, 4), 38, 0.16, 6, "POLAR_BEAR", (5, 1, 2, "keys('SNOWY_BEACH','FROZEN_RIVER')"),
  pal("8a6a52", "4a3626", "b89478"), [("minecraft:cod", 1, 3), ("minecraft:leather", 1, 2)], ("8a6a52", "f0e8d0"), snout=(6, 4, 3), ears=0, tail=(4, 3), tusks=True)
q("tapir", "CALM", "HERBIVORE", (9, 10, 16), (6, 7, 8), (7, 3), 24, 0.2, 0, "PIG", (5, 1, 2, "tag('IS_JUNGLE')"),
  pal("2a2826", "0e0d0c", "e8e4dc", "patches"), MEAT, ("2a2826", "e8e4dc"), snout=(4, 4, 5), ears=2, tail=(2, 2))
q("capuchin", "PREY", "OMNIVORE", (5, 6, 8), (5, 5, 5), (4, 2), 8, 0.28, 1, "FOX", (8, 2, 5, "tag('IS_JUNGLE')"),
  pal("6a4a30", "2a1c10", "e8d8b8"), SMALL, ("6a4a30", "e8d8b8"), snout=(3, 2, 2), ears=2, tail=(12, 2))
q("komodo_dragon", "PREDATOR", "CARNIVORE", (7, 5, 18), (5, 4, 8), (3, 3), 30, 0.2, 7, "RAVAGER", (4, 1, 2, "any(keys('DESERT','SAVANNA','SAVANNA_PLATEAU'))"),
  pal("6a6048", "3a3226", "9a8e6c", "dots"), [("minecraft:leather", 1, 2)], ("6a6048", "9a8e6c"), snout=(4, 3, 4), tail=(16, 4))
q("iguana", "PREY", "HERBIVORE", (4, 4, 12), (4, 4, 5), (3, 2), 6, 0.22, 0, "RABBIT", (6, 2, 4, "any(tag('IS_JUNGLE'), keys('BEACH','DESERT'))"),
  pal("5aa04a", "2f6a28", "b0d890", "stripes"), SMALL, ("5aa04a", "b0d890"), snout=(3, 3, 2), tail=(14, 3), mane='spikes')
q("chameleon", "PREY", "OMNIVORE", (4, 6, 8), (5, 5, 5), (3, 2), 6, 0.14, 0, "RABBIT", (6, 2, 3, "any(tag('IS_JUNGLE'), keys('SWAMP'))"),
  pal("78c850", "e8a030", "c8f090", "dots"), SMALL, ("78c850", "e8a030"), snout=(2, 2, 2), tail=(8, 3), horns=True)
q("cobra", "NEUTRAL", "CARNIVORE", (3, 3, 22), (4, 3, 5), (1, 1), 10, 0.22, 4, "RAVAGER", (5, 1, 2, "any(keys('DESERT'), tag('IS_BADLANDS'))"),
  pal("8a7a3c", "3c3418", "d8cc88", "stripes"), SMALL, ("8a7a3c", "3c3418"), tail=(14, 2))
q("wombat", "CALM", "HERBIVORE", (8, 7, 12), (6, 5, 5), (3, 3), 18, 0.16, 2, "PIG", (5, 1, 2, "any(keys('PLAINS','FOREST','MEADOW'))"),
  pal("6a5a4a", "2e261e", "a49880"), MEAT, ("6a5a4a", "a49880"), snout=(3, 2, 2), ears=2, tail=(1, 2))
q("tasmanian_devil", "NEUTRAL", "CARNIVORE", (6, 6, 11), (6, 5, 6), (4, 2), 14, 0.28, 4, "FOX", (4, 1, 2, "any(keys('TAIGA','FOREST','SNOWY_TAIGA'))"),
  pal("2a2624", "100d0c", "e8e4dc", "patches"), SMALL, ("2a2624", "e8e4dc"), snout=(3, 3, 3), ears=2, tail=(7, 3))
q("dingo", "NEUTRAL", "CARNIVORE", (7, 8, 14), (6, 6, 7), (9, 3), 18, 0.32, 4, "FOX", (5, 1, 3, "any(keys('DESERT'), tag('IS_BADLANDS'), tag('IS_SAVANNA'))"),
  pal("c8965a", "7a5a38", "f0dcba"), MEAT, ("c8965a", "f0dcba"), snout=(4, 3, 4), ears=3, tail=(10, 3))
u("emu", "PREY", "HERBIVORE", (8, 9, 11), (3, 3, 5), (14, 2), 18, 0.34, 3, "PARROT", (6, 2, 4, "any(tag('IS_SAVANNA'), keys('PLAINS'))"),
  pal("6a5a4a", "3a3026", "a49a8a"), FEATHER + [("minecraft:chicken", 1, 2)], ("6a5a4a", "a49a8a"), neck=(12, 2), snout=(2, 1, 3), tail=(4, 4), wings=(3, 1), beak=True)
u("swan", "NEUTRAL", "SEEDS", (6, 7, 10), (3, 3, 4), (3, 1), 10, 0.2, 3, "CHICKEN", (8, 2, 4, "any(keys('RIVER','SWAMP','PLAINS','MEADOW'))"),
  pal("f4f4f4", "d0d0d0", "ffffff", accent="f08a1a"), FEATHER, ("f4f4f4", "f08a1a"), neck=(10, 2), snout=(2, 1, 3), tail=(4, 3), wings=(8, 1), beak=True)
u("stork", "PREY", "FISH", (5, 7, 8), (3, 3, 4), (13, 1), 10, 0.22, 0, "PARROT", (6, 2, 3, "any(keys('PLAINS','SWAMP','RIVER'))"),
  pal("f2f2f0", "1c1c1c", "ffffff", accent="e0402a"), FEATHER, ("f2f2f0", "e0402a"), neck=(9, 2), snout=(2, 1, 6), tail=(3, 3), wings=(10, 1), beak=True, flying=True)
u("crane", "PREY", "FISH", (5, 8, 8), (3, 3, 4), (15, 1), 10, 0.22, 0, "PARROT", (5, 2, 3, "any(keys('SWAMP','MANGROVE_SWAMP','RIVER','MEADOW'))"),
  pal("d8d8d4", "2a2a2a", "f4f4f2", accent="e03a2a"), FEATHER, ("d8d8d4", "e03a2a"), neck=(11, 2), snout=(2, 1, 5), tail=(3, 3), wings=(10, 1), beak=True, flying=True)
u("roadrunner", "PREY", "OMNIVORE", (4, 6, 9), (3, 4, 5), (7, 1), 6, 0.4, 0, "CHICKEN", (8, 2, 4, "any(keys('DESERT'), tag('IS_BADLANDS'))"),
  pal("8a7050", "3a2c1c", "e8dcc4", "stripes", accent="4a8ab8"), FEATHER, ("8a7050", "4a8ab8"), neck=(3, 2), snout=(2, 1, 4), tail=(11, 2), wings=(4, 1), beak=True)
u("woodpecker", "PREY", "SEEDS", (3, 5, 4), (3, 3, 3), (2, 1), 4, 0.2, 0, "PARROT", (8, 2, 4, "tag('IS_FOREST')"),
  pal("141414", "050505", "f4f0e8", "stripes", accent="d8201a"), FEATHER, ("141414", "d8201a"), snout=(2, 1, 4), tail=(4, 2), wings=(6, 1), beak=True, flying=True)
u("pheasant", "PREY", "SEEDS", (5, 6, 8), (3, 3, 4), (6, 1), 6, 0.22, 0, "CHICKEN", (8, 2, 4, "any(keys('PLAINS','MEADOW','FOREST'))"),
  pal("a8602a", "3a6a4a", "e8d0a0", "dots", accent="d8201a"), FEATHER + [("minecraft:chicken", 0, 1)], ("a8602a", "3a6a4a"), neck=(3, 2), snout=(2, 1, 2), tail=(12, 3), wings=(5, 1), beak=True)
u("turkey", "PREY", "SEEDS", (7, 8, 10), (3, 4, 4), (6, 2), 10, 0.2, 0, "CHICKEN", (8, 2, 4, "any(keys('FOREST','BIRCH_FOREST','PLAINS'))"),
  pal("5a3c24", "2a1a0c", "a0784a", accent="d8201a"), FEATHER + [("minecraft:chicken", 1, 2)], ("5a3c24", "d8201a"), neck=(5, 2), snout=(2, 1, 2), tail=(10, 7), wings=(6, 1), beak=True)
u("goose", "NEUTRAL", "SEEDS", (6, 7, 10), (3, 3, 4), (4, 1), 10, 0.22, 2, "CHICKEN", (8, 2, 4, "any(keys('PLAINS','RIVER','MEADOW','SWAMP'))"),
  pal("d8d4cc", "5a5248", "f4f2ee", accent="f08a1a"), FEATHER, ("d8d4cc", "f08a1a"), neck=(7, 2), snout=(2, 1, 3), tail=(4, 3), wings=(8, 1), beak=True)

# ================================================================== Wildfire (nether castle boss mob) and Barnacle (ocean + monument)
animal("wildfire", "UPRIGHT", "PREDATOR", "CARNIVORE", (6, 10, 5), (7, 7, 7), (1, 1), 60, 0.28, 6, "BLAZE",
       (6, 2, 3, "tag('IS_NETHER')"), pal("e8661a", "7a1e08", "ffd060", accent="ffe880"),
       [("minecraft:blaze_rod", 1, 3), ("minecraft:gold_nugget", 2, 6)], ("e8661a", "ffd060"),
       flying=True, monster=True, special="WILDFIRE", spawnkind="NETHER", horns=True, ears=2, orbit=(5, 3), arms=(7, 2))
animal("barnacle", "UPRIGHT", "PREDATOR", "CARNIVORE", (8, 7, 8), (6, 4, 6), (1, 1), 30, 0.0, 4, "GUARDIAN",
       (6, 1, 2, "tag('IS_OCEAN')"), pal("5a5048", "2e2822", "9a8e7c", "dots", accent="d85a3a"),
       [("minecraft:prismarine_shard", 1, 3), ("minecraft:ink_sac", 0, 2)], ("5a5048", "d85a3a"),
       monster=True, special="BARNACLE", spawnkind="WATER", tentacles=(8, 14))

# ================================================================== BIOME VOTE LOSERS (2018 desert/savanna, 2019 swamp/badlands)
# Meerkat (desert), Ostrich + Termites + Baobab (savanna), Vulture + Tumbleweed (badlands), Firefly (swamp; cut from vanilla frogs update)
animal("tumbleweed", "QUAD", "CALM", "HERBIVORE", (8, 8, 8), (2, 2, 2), (1, 1), 6, 0.28, 0, "RABBIT",
       (8, 2, 4, "any(tag('IS_BADLANDS'), keys('DESERT'))"), pal("b8935a", "6e5130", "d8c090", "dots"),
       [("minecraft:dead_bush", 0, 1), ("minecraft:stick", 1, 3)], ("b8935a", "6e5130"), roll=True)
animal("termite", "QUAD", "NEUTRAL", "OMNIVORE", (3, 3, 6), (3, 3, 3), (2, 1), 6, 0.3, 1, "RABBIT",
       (4, 2, 4, "keys('SAVANNA','SAVANNA_PLATEAU')"), pal("c8a070", "7a5230", "e8d0a0"),
       [("minecraft:stick", 0, 1)], ("c8a070", "7a5230"), monster=True, ears=0, snout=(1, 1, 2))
animal("firefly", "UPRIGHT", "CALM", "HERBIVORE", (2, 2, 3), (2, 2, 2), (1, 1), 2, 0.1, 0, "PARROT",
       (8, 3, 6, "keys('SWAMP','MANGROVE_SWAMP')"), pal("3a3a2a", "1a1a12", "fff060", accent="ffe840"),
       [], ("3a3a2a", "ffe840"), flying=True, wings=(3, 1), special="FIREFLY")
