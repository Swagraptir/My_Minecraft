package com.myminecraft.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.myminecraft.registry.ModStructures;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

/** One Structure class for all four kinds; the JSON field "kind" picks the blueprint. */
public class ProceduralStructure extends Structure {
    public static final MapCodec<ProceduralStructure> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            settingsCodec(i),
            Codec.STRING.fieldOf("kind").forGetter(s -> s.kind)
    ).apply(i, ProceduralStructure::new));

    private final String kind;

    public ProceduralStructure(StructureSettings settings, String kind) {
        super(settings);
        this.kind = kind;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
        if (Blueprints.isNether(kind)) {
            return findNetherSite(ctx);
        }
        int[] size = Blueprints.size(kind);        // {width, depth, height}
        ChunkPos cp = ctx.chunkPos();
        int cx = cp.getMiddleBlockX();
        int cz = cp.getMiddleBlockZ();
        int x0 = cx - size[0] / 2;
        int z0 = cz - size[1] / 2;

        // sample the four corners + centre: reject steep or watery sites
        int[][] pts = {{x0, z0}, {x0 + size[0], z0}, {x0, z0 + size[1]}, {x0 + size[0], z0 + size[1]}, {cx, cz}};
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, sum = 0;
        for (int[] p : pts) {
            int h = ctx.chunkGenerator().getFirstOccupiedHeight(p[0], p[1], Heightmap.Types.WORLD_SURFACE_WG, ctx.heightAccessor(), ctx.randomState());
            min = Math.min(min, h);
            max = Math.max(max, h);
            sum += h;
        }
        if (max - min > Blueprints.maxSlope(kind)) {
            return Optional.empty();
        }
        int centre = ctx.chunkGenerator().getFirstOccupiedHeight(cx, cz, Heightmap.Types.OCEAN_FLOOR_WG, ctx.heightAccessor(), ctx.randomState());
        int surface = ctx.chunkGenerator().getFirstOccupiedHeight(cx, cz, Heightmap.Types.WORLD_SURFACE_WG, ctx.heightAccessor(), ctx.randomState());
        if (surface > centre + 0) {
            // WORLD_SURFACE sits above OCEAN_FLOOR: there is water or something non-solid on top
            if (surface - centre > 1) return Optional.empty();
        }
        int ground = Math.max(min, sum / pts.length - 1);   // build slightly above the average; foundations fill the rest
        BoundingBox box = new BoundingBox(x0, ground - 12, z0, x0 + size[0] - 1, ground + size[2], z0 + size[1] - 1);
        long seed = ctx.random().nextLong();
        return Optional.of(new GenerationStub(new BlockPos(cx, ground, cz),
                builder -> builder.addPiece(new ProceduralPiece(kind, box, ground, seed))));
    }

    /** The nether has a bedrock roof, so surface heightmaps are useless: scan each column for a floor with two air blocks above. */
    private Optional<GenerationStub> findNetherSite(GenerationContext ctx) {
        int[] size = Blueprints.size(kind);
        ChunkPos cp = ctx.chunkPos();
        int cx = cp.getMiddleBlockX();
        int cz = cp.getMiddleBlockZ();
        int x0 = cx - size[0] / 2;
        int z0 = cz - size[1] / 2;
        int[][] pts = {{x0 + 6, z0 + 6}, {x0 + size[0] - 7, z0 + 6}, {x0 + 6, z0 + size[1] - 7}, {x0 + size[0] - 7, z0 + size[1] - 7}, {cx, cz}};
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, sum = 0;
        for (int[] p : pts) {
            int h = netherFloor(ctx, p[0], p[1]);
            if (h < 0) return Optional.empty();
            min = Math.min(min, h);
            max = Math.max(max, h);
            sum += h;
        }
        if (max - min > Blueprints.maxSlope(kind)) return Optional.empty();
        int ground = sum / pts.length;
        BoundingBox box = new BoundingBox(x0, ground - 14, z0, x0 + size[0] - 1, ground + size[2], z0 + size[1] - 1);
        long seed = ctx.random().nextLong();
        return Optional.of(new GenerationStub(new BlockPos(cx, ground, cz),
                builder -> builder.addPiece(new ProceduralPiece(kind, box, ground, seed))));
    }

    /** First solid, non-lava block (from y=100 down to 34) with air above it, or -1. */
    private static int netherFloor(GenerationContext ctx, int x, int z) {
        NoiseColumn col = ctx.chunkGenerator().getBaseColumn(x, z, ctx.heightAccessor(), ctx.randomState());
        for (int y = 100; y > 34; y--) {
            net.minecraft.world.level.block.state.BlockState s = col.getBlock(y);
            if (!s.isAir() && s.getFluidState().isEmpty() && col.getBlock(y + 1).isAir() && col.getBlock(y + 2).isAir()) {
                return y + 1;
            }
        }
        return -1;
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.PROCEDURAL;
    }
}
