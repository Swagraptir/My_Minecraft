package com.myminecraft;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

/**
 * Every Identifier / ResourceKey in the mod is created here.
 * If a future Minecraft version renames Identifier again, this is the only file to touch.
 */
public final class Ids {
    public static final String MOD_ID = "myminecraft";

    public static Identifier of(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registry, String path) {
        return ResourceKey.create(registry, of(path));
    }

    public static ResourceKey<net.minecraft.world.level.block.Block> blockKey(String path) {
        return key(Registries.BLOCK, path);
    }

    public static ResourceKey<net.minecraft.world.item.Item> itemKey(String path) {
        return key(Registries.ITEM, path);
    }

    public static ResourceKey<net.minecraft.world.entity.EntityType<?>> entityKey(String path) {
        return key(Registries.ENTITY_TYPE, path);
    }

    private Ids() {}
}
