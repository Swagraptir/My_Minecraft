package com.myminecraft.animal;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.sounds.SoundEvent;

/**
 * Describes one animal: model dimensions (in pixels, 16 px = 1 block), AI, stats, sounds, spawn rules.
 * Instances are created in the generated {@link AnimalSpecs}; the same data drives the entity, the model and the texture.
 */
public final class AnimalSpec {
    public enum Layout { QUAD, UPRIGHT }
    public enum Behavior { PREY, CALM, NEUTRAL, PREDATOR }
    public enum Diet { HERBIVORE, CARNIVORE, OMNIVORE, FISH, SEEDS }
    public enum Mane { NONE, RING, STRIP, SPIKES }
    /** Mob-vote losers with their own behaviour class. */
    public enum Special { NONE, MOOBLOOM, GLARE, RASCAL, TUFF_GOLEM, WILDFIRE, BARNACLE, FIREFLY }
    /** Where/how it spawns: SURFACE = normal animals, CAVE = dark caves (ambient), TUFF = on tuff, SHORE = beach/mud/sand. */
    public enum SpawnKind { SURFACE, CAVE, TUFF, SHORE, WATER, NETHER }

    private static final Map<String, AnimalSpec> BY_NAME = new HashMap<>();

    public final String name;
    public final Layout layout;
    public final Behavior behavior;
    public final Diet diet;

    // body parts (pixels)
    public int bw = 8, bh = 8, bl = 14;
    public int hw = 6, hh = 6, hl = 6;
    public int legH = 6, legT = 3;
    public int neckLen, neckT;
    public int snoutW, snoutH, snoutL;
    public int ear;
    public int tailLen, tailT;
    public int armLen, armT;
    public int wingLen, wingT;
    public boolean trunk, tusks, horns, horn, antlers, hump, beak, flying;
    public Mane mane = Mane.NONE;
    public boolean monster;
    public boolean roll;
    public int tentacleN, tentacleLen, orbitN, orbitSize;
    public Special special = Special.NONE;
    public SpawnKind spawnKind = SpawnKind.SURFACE;

    // stats
    public double health = 10, speed = 0.25, damage = 2;
    public SoundEvent ambient, hurt, death;

    // spawning
    public int spawnWeight = 5, spawnMin = 1, spawnMax = 3;
    public Predicate<BiomeSelectionContext> biomes = ctx -> false;

    public AnimalSpec(String name, Layout layout, Behavior behavior, Diet diet) {
        this.name = name;
        this.layout = layout;
        this.behavior = behavior;
        this.diet = diet;
        BY_NAME.put(name, this);
    }

    public static AnimalSpec byName(String name) {
        AnimalSpecs.ALL.size(); // make sure the generated list has been initialised
        return BY_NAME.get(name);
    }

    public AnimalSpec body(int w, int h, int l) { bw = w; bh = h; bl = l; return this; }
    public AnimalSpec head(int w, int h, int l) { hw = w; hh = h; hl = l; return this; }
    public AnimalSpec legs(int h, int t) { legH = h; legT = t; return this; }
    public AnimalSpec neck(int len, int t) { neckLen = len; neckT = t; return this; }
    public AnimalSpec snout(int w, int h, int l) { snoutW = w; snoutH = h; snoutL = l; return this; }
    public AnimalSpec ears(int s) { ear = s; return this; }
    public AnimalSpec tail(int len, int t) { tailLen = len; tailT = t; return this; }
    public AnimalSpec arms(int len, int t) { armLen = len; armT = t; return this; }
    public AnimalSpec wings(int len, int t) { wingLen = len; wingT = t; return this; }
    public AnimalSpec trunk() { trunk = true; return this; }
    public AnimalSpec tusks() { tusks = true; return this; }
    public AnimalSpec horns() { horns = true; return this; }
    public AnimalSpec horn() { horn = true; return this; }
    public AnimalSpec antlers() { antlers = true; return this; }
    public AnimalSpec hump() { hump = true; return this; }
    public AnimalSpec beak() { beak = true; return this; }
    public AnimalSpec flying() { flying = true; return this; }
    public AnimalSpec mane(Mane m) { mane = m; return this; }
    public AnimalSpec monster() { monster = true; return this; }
    public AnimalSpec roll() { roll = true; return this; }
    public AnimalSpec tentacles(int n, int len) { tentacleN = n; tentacleLen = len; return this; }
    public AnimalSpec orbit(int n, int size) { orbitN = n; orbitSize = size; return this; }
    public AnimalSpec special(Special sp) { special = sp; return this; }
    public AnimalSpec spawnKind(SpawnKind k) { spawnKind = k; return this; }
    public AnimalSpec stats(double health, double speed, double damage) { this.health = health; this.speed = speed; this.damage = damage; return this; }
    public AnimalSpec sounds(SoundEvent ambient, SoundEvent hurt, SoundEvent death) { this.ambient = ambient; this.hurt = hurt; this.death = death; return this; }
    public AnimalSpec spawn(int weight, int min, int max, Predicate<BiomeSelectionContext> biomes) {
        this.spawnWeight = weight; this.spawnMin = min; this.spawnMax = max; this.biomes = biomes; return this;
    }

    /** Hitbox width in blocks. */
    public float hitWidth() {
        double w = Math.max(bw, bl * 0.55) / 16.0;
        if (layout == Layout.UPRIGHT) w = Math.max(bw, bl) / 16.0 + 0.1;
        return (float) Math.max(0.35, Math.min(2.4, w));
    }

    /** Hitbox height in blocks. */
    public float hitHeight() {
        double px = layout == Layout.UPRIGHT
                ? legH + bh + neckLen + hh
                : legH + bh + (neckLen > 0 ? neckLen + hh : hh * 0.5);
        return (float) Math.max(0.3, px / 16.0);
    }
}
