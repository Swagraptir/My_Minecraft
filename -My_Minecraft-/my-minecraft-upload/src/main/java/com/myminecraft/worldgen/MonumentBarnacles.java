package com.myminecraft.worldgen;

import com.myminecraft.animal.Barnacle;
import com.myminecraft.registry.ModEntities;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;

/**
 * Fills ocean monuments with Barnacles as you explore them. (Spawning at world generation is not possible because the
 * monument's walls are placed after the chunk loads, so the check runs near players instead.)
 */
public final class MonumentBarnacles {
    private static final int CAP = 10;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(MonumentBarnacles::tick);
    }

    private static void tick(ServerLevel level) {
        if (level.getGameTime() % 100 != 0) return;
        RandomSource random = level.getRandom();
        Structure monument = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.OCEAN_MONUMENT).value();
        for (ServerPlayer p : level.players()) {
            StructureStart start = level.structureManager().getStructureAt(p.blockPosition(), monument);
            if (!start.isValid()) continue;
            BoundingBox box = start.getBoundingBox();
            int count = level.getEntitiesOfClass(Barnacle.class, AABB.of(box)).size();
            for (int i = 0; i < 16 && count < CAP; i++) {
                BlockPos q = p.blockPosition().offset(random.nextInt(37) - 18, random.nextInt(13) - 6, random.nextInt(37) - 18);
                if (!box.isInside(q) || q.distSqr(p.blockPosition()) < 36) continue;
                if (level.getFluidState(q).is(FluidTags.WATER) && level.getFluidState(q.above()).is(FluidTags.WATER)
                        && level.getBlockState(q.below()).isSolid()) {
                    Barnacle b = ModEntities.BARNACLE.create(level, EntitySpawnReason.STRUCTURE);
                    if (b == null) continue;
                    b.snapTo(q.getX() + 0.5, q.getY(), q.getZ() + 0.5, random.nextFloat() * 360f, 0);
                    b.setPersistenceRequired();
                    level.addFreshEntity(b);
                    count++;
                }
            }
        }
    }

    private MonumentBarnacles() {}
}
