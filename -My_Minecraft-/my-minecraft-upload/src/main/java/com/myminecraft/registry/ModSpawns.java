package com.myminecraft.registry;

import com.myminecraft.animal.AnimalSpec;
import com.myminecraft.animal.AnimalSpecs;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import com.myminecraft.Compat;
import com.myminecraft.Ids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Natural spawning. Every niche has its own rule so species do not pile into the same spot:
 * SURFACE animals by biome, CAVE (dark caves, ambient), TUFF (only on tuff), SHORE (sand/mud), WATER (sea floor), NETHER.
 */
public final class ModSpawns {
    private static boolean darkCave(LevelAccessor lv, BlockPos p) {
        return p.getY() < 55 && lv.getBrightness(LightLayer.SKY, p) == 0 && lv.getBrightness(LightLayer.BLOCK, p) <= 3;
    }

    private static <T extends Mob> void place(EntityType<T> type, SpawnPlacementType pt, SpawnPlacements.SpawnPredicate<T> rule) {
        SpawnPlacements.register(type, pt, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, rule);
    }

    private static <T extends Mob> void forSpec(EntityType<T> type, AnimalSpec spec) {
        SpawnPlacementType ground = spec.flying ? SpawnPlacementTypes.NO_RESTRICTIONS : SpawnPlacementTypes.ON_GROUND;
        MobCategory category = MobCategory.CREATURE;
        switch (spec.spawnKind) {
            case CAVE -> {
                category = MobCategory.AMBIENT;
                place(type, ground, (t, lv, reason, pos, rnd) -> darkCave(lv, pos) && (spec.flying || lv.getBlockState(pos.below()).isSolid()));
            }
            case TUFF -> {
                category = MobCategory.AMBIENT;
                place(type, ground, (t, lv, reason, pos, rnd) -> darkCave(lv, pos) && lv.getBlockState(pos.below()).is(Blocks.TUFF));
            }
            case SHORE -> place(type, ground, (t, lv, reason, pos, rnd) -> lv.getBlockState(pos.below()).is(BlockTags.SAND)
                    || lv.getBlockState(pos.below()).is(Blocks.MUD) || lv.getBlockState(pos.below()).is(BlockTags.DIRT));
            case WATER -> {
                category = MobCategory.WATER_CREATURE;
                place(type, SpawnPlacementTypes.IN_WATER, (t, lv, reason, pos, rnd) -> pos.getY() < lv.getSeaLevel() - 3
                        && lv.getFluidState(pos).is(FluidTags.WATER) && lv.getBlockState(pos.below()).isSolid());
            }
            case NETHER -> {
                category = MobCategory.MONSTER;
                place(type, SpawnPlacementTypes.NO_RESTRICTIONS, (t, lv, reason, pos, rnd) -> lv.getBlockState(pos).isAir());
            }
            default -> {
                if (spec.flying) {
                    place(type, SpawnPlacementTypes.NO_RESTRICTIONS, (t, lv, reason, pos, rnd) -> true);
                } else {
                    place(type, SpawnPlacementTypes.ON_GROUND, (t, lv, reason, pos, rnd) -> lv.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON)
                            && Animal.isBrightEnoughToSpawn(lv, pos));
                }
            }
        }
        if (!Compat.spawnDisabled(spec.name)) {
            BiomeModifications.addSpawn(spec.biomes, category, type, spec.spawnWeight, spec.spawnMin, spec.spawnMax);
        }
    }

    public static void register() {
        for (AnimalSpec spec : AnimalSpecs.ALL) {
            forSpec(ModEntities.ANIMALS.get(spec.name), spec);
        }
        // Clams on warm-ocean floors (Nekoma's Fixed idea): configured + placed feature live in data/myminecraft/worldgen
        if (Compat.clamFeatureEnabled()) {
            BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN),
                    GenerationStep.Decoration.VEGETAL_DECORATION, Ids.key(Registries.PLACED_FEATURE, "clam_patch"));
        }
        SpawnPlacements.register(ModEntities.ICEOLOGER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA, Biomes.ICE_SPIKES,
                Biomes.SNOWY_SLOPES, Biomes.GROVE, Biomes.FROZEN_PEAKS), MobCategory.MONSTER, ModEntities.ICEOLOGER, 6, 1, 2);
    }

    private ModSpawns() {}
}
