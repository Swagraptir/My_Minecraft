package com.myminecraft.client;

import com.myminecraft.Ids;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.geom.ModelLayerLocation;

/** One model layer location per animal / vehicle. */
public final class ModModelLayers {
    private static final Map<String, ModelLayerLocation> LAYERS = new HashMap<>();

    public static ModelLayerLocation layer(String name) {
        return LAYERS.computeIfAbsent(name, n -> new ModelLayerLocation(Ids.of(n), "main"));
    }

    private ModModelLayers() {}
}
