package com.myminecraft.registry;

import com.myminecraft.Ids;
import com.myminecraft.furniture.FurnitureDefs;
import com.myminecraft.furniture.FurnitureType;
import com.myminecraft.furniture.StorageBlockEntity;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
    public static BlockEntityType<StorageBlockEntity> STORAGE;

    public static void register() {
        List<Block> storage = new ArrayList<>();
        for (FurnitureDefs.Def def : FurnitureDefs.ALL) {
            if (def.type() == FurnitureType.STORAGE) {
                storage.add(ModBlocks.FURNITURE.get(def.name()));
            }
        }
        // If this line does not compile on your exact 26.x build, use the vanilla constructor instead:
        //   new BlockEntityType<>(StorageBlockEntity::new, storage.toArray(new Block[0]))
        STORAGE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Ids.of("storage"),
                FabricBlockEntityTypeBuilder.create(StorageBlockEntity::new, storage.toArray(new Block[0])).build());
    }

    private ModBlockEntities() {}
}
