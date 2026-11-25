package com.myminecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Compatibility with other mods + a tiny config (config/myminecraft.json, created on first start).
 *
 * "Auto compat" (default on): when another mod already provides the same thing, My Minecraft switches its duplicate off
 *  - Naturalist loaded  -> overlapping animals stop spawning naturally (spawn eggs still work)
 *  - Macaw's Furniture  -> overlapping furniture is hidden from the creative tab and its recipes are disabled
 *  - Nekoma's Fixed     -> Clam and Underwater Torch are disabled
 * Nothing is ever removed from the registries, so existing worlds keep working.
 */
public final class Compat {
    private static final List<String> NATURALIST = List.of("deer", "brown_bear", "black_bear", "lion", "zebra", "elephant", "giraffe",
            "rhino", "hippo", "vulture", "duck", "wild_boar", "crocodile", "firefly");
    private static final List<String> MACAWS_BASES = List.of("chair", "stool", "table", "coffee_table", "desk", "bookcase", "wardrobe",
            "nightstand", "dresser", "kitchen_counter", "wall_cabinet", "bench", "park_bench");
    private static final List<String> NEKOMA_IDS = List.of("nekomasfixed", "nekomas_fixed", "nekomas-fixed", "nekomas", "nekoma");

    private static boolean auto = true;
    private static final Set<String> disabledSpawns = new HashSet<>();
    private static final Set<String> hiddenItems = new HashSet<>();
    private static boolean naturalist, macaws, nekoma;

    public static void load() {
        FabricLoader loader = FabricLoader.getInstance();
        naturalist = loader.isModLoaded("naturalist");
        macaws = loader.isModLoaded("mcwfurnitures");
        nekoma = NEKOMA_IDS.stream().anyMatch(loader::isModLoaded);
        Path file = loader.getConfigDir().resolve("myminecraft.json");
        try {
            if (Files.exists(file)) {
                JsonObject o = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
                if (o.has("autoCompat")) auto = o.get("autoCompat").getAsBoolean();
                readList(o, "disabledSpawns", disabledSpawns);
                readList(o, "hiddenItems", hiddenItems);
            } else {
                JsonObject o = new JsonObject();
                o.addProperty("_help", "autoCompat: switch off duplicates when Naturalist / Macaw's Furniture / Nekoma's Fixed are installed. "
                        + "disabledSpawns: mob ids that should not spawn naturally (e.g. \"lion\"). hiddenItems: item ids hidden from the creative tab.");
                o.addProperty("autoCompat", true);
                o.add("disabledSpawns", new JsonArray());
                o.add("hiddenItems", new JsonArray());
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Files.writeString(file, gson.toJson(o));
            }
        } catch (IOException | RuntimeException e) {
            MyMinecraft.LOGGER.warn("Could not read {}: {} (using defaults)", file, e.toString());
        }
        MyMinecraft.LOGGER.info("Compat: naturalist={}, macaws={}, nekoma={}, auto={}", naturalist, macaws, nekoma, auto);
    }

    private static void readList(JsonObject o, String key, Set<String> into) {
        if (o.has(key) && o.get(key).isJsonArray()) {
            for (JsonElement e : o.getAsJsonArray(key)) into.add(e.getAsString());
        }
    }

    /** True if this mob must not spawn naturally. */
    public static boolean spawnDisabled(String mob) {
        return disabledSpawns.contains(mob) || (auto && naturalist && NATURALIST.contains(mob));
    }

    /** True if this item should not appear in the creative tab. */
    public static boolean hidden(String item) {
        if (hiddenItems.contains(item)) return true;
        if (!auto) return false;
        if (nekoma && (item.equals("clam") || item.equals("underwater_torch"))) return true;
        if (macaws) {
            for (String base : MACAWS_BASES) {
                if (item.equals(base) || item.endsWith("_" + base)) return true;
            }
        }
        return false;
    }

    /** Clams generate on ocean floors unless Nekoma's Fixed already does it. */
    public static boolean clamFeatureEnabled() {
        return !(auto && nekoma);
    }

    private Compat() {}
}
