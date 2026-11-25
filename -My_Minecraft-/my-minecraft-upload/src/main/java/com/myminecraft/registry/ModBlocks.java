package com.myminecraft.registry;

import com.myminecraft.Ids;
import com.myminecraft.furniture.ButtercupBlock;
import com.myminecraft.furniture.ClamBlock;
import com.myminecraft.furniture.UnderwaterTorchBlock;
import com.myminecraft.furniture.FurnitureBlock;
import com.myminecraft.furniture.FurnitureDefs;
import com.myminecraft.furniture.FurnitureType;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Registers every furniture block (generated list) plus the two pool tile blocks. */
public final class ModBlocks {
    public static final Map<String, Block> FURNITURE = new LinkedHashMap<>();
    public static Block POOL_TILE;
    public static Block POOL_TILE_WHITE;
    public static Block BUTTERCUP;
    public static Block CLAM;
    public static Block UNDERWATER_TORCH;

    private static final String[] SOFT = {"sofa", "armchair", "rug", "pillow", "curtain", "hammock", "deck_chair", "office_chair", "bar_stool", "towel"};
    private static final String[] METAL = {"fridge", "stove", "microwave", "washing", "bath", "toilet", "sink", "shower", "trash", "file_cabinet",
            "computer", "tv", "printer", "bbq", "pool_ladder", "diving", "lamp", "chandelier", "patio", "aquarium", "mailbox", "swing", "park_bench", "garden_chair"};

    private static SoundType soundFor(String name) {
        for (String s : SOFT) if (name.contains(s)) return SoundType.WOOL;
        for (String s : METAL) if (name.contains(s)) return SoundType.METAL;
        if (name.contains("bird_bath") || name.contains("fireplace")) return SoundType.STONE;
        return SoundType.WOOD;
    }

    public static void register() {
        for (FurnitureDefs.Def def : FurnitureDefs.ALL) {
            ResourceKey<Block> key = Ids.blockKey(def.name());
            BlockBehaviour.Properties props = BlockBehaviour.Properties.of()
                    .setId(key)
                    .strength(1.2f, 3.0f)
                    .sound(soundFor(def.name()))
                    .noOcclusion()
                    .isRedstoneConductor((st, lv, ps) -> false)
                    .isSuffocating((st, lv, ps) -> false)
                    .isViewBlocking((st, lv, ps) -> false);
            if (def.type() == FurnitureType.LAMP) {
                props = props.lightLevel(state -> state.getValue(FurnitureBlock.LIT) ? def.light() : 0);
            } else if (def.type() == FurnitureType.GLOW) {
                props = props.lightLevel(state -> def.light());
            }
            Block block = Registry.register(BuiltInRegistries.BLOCK, key, new FurnitureBlock(props, def));
            FURNITURE.put(def.name(), block);
        }
        POOL_TILE = simple("pool_tile");
        POOL_TILE_WHITE = simple("pool_tile_white");
        ResourceKey<Block> ck = Ids.blockKey("clam");
        CLAM = Registry.register(BuiltInRegistries.BLOCK, ck, new ClamBlock(BlockBehaviour.Properties.of()
                .setId(ck).strength(1.0f).sound(SoundType.STONE).noOcclusion()));
        ResourceKey<Block> tk = Ids.blockKey("underwater_torch");
        UNDERWATER_TORCH = Registry.register(BuiltInRegistries.BLOCK, tk, new UnderwaterTorchBlock(BlockBehaviour.Properties.of()
                .setId(tk).instabreak().sound(SoundType.WOOD).noOcclusion().noCollision()
                .lightLevel(st -> st.getValue(UnderwaterTorchBlock.WATERLOGGED) ? 14 : 0)));
        ResourceKey<Block> bk = Ids.blockKey("buttercup");
        BUTTERCUP = Registry.register(BuiltInRegistries.BLOCK, bk, new ButtercupBlock(BlockBehaviour.Properties.of()
                .setId(bk).noCollision().instabreak().sound(SoundType.GRASS).noOcclusion().offsetType(BlockBehaviour.OffsetType.XZ)));
    }

    private static Block simple(String name) {
        ResourceKey<Block> key = Ids.blockKey(name);
        return Registry.register(BuiltInRegistries.BLOCK, key,
                new Block(BlockBehaviour.Properties.of().setId(key).strength(1.5f, 6.0f).sound(SoundType.STONE)));
    }

    private ModBlocks() {}
}
