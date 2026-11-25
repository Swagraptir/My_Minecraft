package com.myminecraft.worldgen;

import com.myminecraft.registry.ModStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

/**
 * The single piece of a procedural structure. It stores only (kind, groundY, seed) and re-draws the whole blueprint for
 * every chunk it touches; {@link Canvas} clips the output to the chunk being generated.
 */
public class ProceduralPiece extends StructurePiece {
    private final String kind;
    private final int groundY;
    private final long seed;

    public ProceduralPiece(String kind, BoundingBox box, int groundY, long seed) {
        super(ModStructures.PIECE_TYPE, 0, box);
        this.kind = kind;
        this.groundY = groundY;
        this.seed = seed;
    }

    public ProceduralPiece(StructurePieceSerializationContext ctx, CompoundTag tag) {
        super(ModStructures.PIECE_TYPE, tag);
        this.kind = tag.getStringOr("Kind", "medieval_village");
        this.groundY = tag.getIntOr("GroundY", 64);
        this.seed = tag.getLongOr("Seed", 0L);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
        tag.putString("Kind", kind);
        tag.putInt("GroundY", groundY);
        tag.putLong("Seed", seed);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structures, ChunkGenerator generator, RandomSource random,
                            BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        Canvas canvas = new Canvas(level, chunkBox, this.boundingBox, groundY, seed);
        Blueprints.run(kind, canvas);
    }
}
