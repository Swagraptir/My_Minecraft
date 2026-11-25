package com.myminecraft.worldgen;

import com.myminecraft.furniture.FurnitureBlock;
import com.myminecraft.registry.ModBlocks;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.RandomizableContainer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * Drawing surface for procedural structures. Coordinates are LOCAL: x/z start at the structure's bounding box corner,
 * y = 0 is the first block above the ground, y = -1 is the ground surface itself. Everything is clipped to the chunk
 * currently being generated, so the same blueprint can run once per chunk and stitch together seamlessly.
 */
public final class Canvas {
    public final WorldGenLevel level;
    public final BoundingBox chunkBox;
    public final BoundingBox bb;
    public final int groundY;
    public final RandomSource rnd;
    /** The current chunk's box in local coordinates (loops are clipped to it, so a 85x85 town costs one chunk of work per chunk). */
    private final int lx1, lx2, lz1, lz2, ly1, ly2;

    public Canvas(WorldGenLevel level, BoundingBox chunkBox, BoundingBox bb, int groundY, long seed) {
        this.level = level;
        this.chunkBox = chunkBox;
        this.bb = bb;
        this.groundY = groundY;
        this.rnd = RandomSource.create(seed);
        this.lx1 = chunkBox.minX() - bb.minX();
        this.lx2 = chunkBox.maxX() - bb.minX();
        this.lz1 = chunkBox.minZ() - bb.minZ();
        this.lz2 = chunkBox.maxZ() - bb.minZ();
        this.ly1 = chunkBox.minY() - groundY;
        this.ly2 = chunkBox.maxY() - groundY;
    }

    // ------------------------------------------------------------------------------------- primitives
    public BlockPos wp(int x, int y, int z) {
        return new BlockPos(bb.minX() + x, groundY + y, bb.minZ() + z);
    }

    private final BlockPos.MutableBlockPos mp = new BlockPos.MutableBlockPos();

    /** Hot path of every blueprint: reuses one mutable position instead of allocating a BlockPos per block. */
    public void set(int x, int y, int z, BlockState s) {
        mp.set(bb.minX() + x, groundY + y, bb.minZ() + z);
        if (chunkBox.isInside(mp)) {
            level.setBlock(mp, s, 2);
        }
    }

    public void set(int x, int y, int z, Block b) {
        set(x, y, z, b.defaultBlockState());
    }

    public void box(int x1, int y1, int z1, int x2, int y2, int z2, Block b) {
        BlockState s = b.defaultBlockState();
        int xa = Math.max(Math.min(x1, x2), lx1), xb = Math.min(Math.max(x1, x2), lx2);
        int ya = Math.max(Math.min(y1, y2), ly1), yb = Math.min(Math.max(y1, y2), ly2);
        int za = Math.max(Math.min(z1, z2), lz1), zb = Math.min(Math.max(z1, z2), lz2);
        for (int x = xa; x <= xb; x++)
            for (int y = ya; y <= yb; y++)
                for (int z = za; z <= zb; z++)
                    set(x, y, z, s);
    }

    public void clear(int x1, int y1, int z1, int x2, int y2, int z2) {
        box(x1, y1, z1, x2, y2, z2, Blocks.AIR);
    }

    /** Hollow box: shell of {@code wall}, air inside. */
    public void hollow(int x1, int y1, int z1, int x2, int y2, int z2, Block wall) {
        box(x1, y1, z1, x2, y2, z2, wall);
        if (x2 - x1 > 1 && y2 - y1 > 1 && z2 - z1 > 1) {
            clear(x1 + 1, y1 + 1, z1 + 1, x2 - 1, y2 - 1, z2 - 1);
        }
    }

    /** Four walls only (no floor, no roof). */
    public void walls(int x1, int y1, int z1, int x2, int y2, int z2, Block b) {
        box(x1, y1, z1, x2, y2, z1, b);
        box(x1, y1, z2, x2, y2, z2, b);
        box(x1, y1, z1, x1, y2, z2, b);
        box(x2, y1, z1, x2, y2, z2, b);
    }

    /** Vertical cylinder of radius r around (cx, cz); hollow keeps only the outer shell. */
    public void cylinder(int cx, int cz, int r, int y1, int y2, Block b, boolean hollow) {
        for (int x = Math.max(cx - r, lx1); x <= Math.min(cx + r, lx2); x++)
            for (int z = Math.max(cz - r, lz1); z <= Math.min(cz + r, lz2); z++) {
                double d = Math.sqrt((x - cx) * (x - cx) + (z - cz) * (z - cz));
                if (d <= r + 0.3 && (!hollow || d >= r - 0.9)) {
                    for (int y = y1; y <= y2; y++) set(x, y, z, b);
                }
            }
    }

    /** Fills downward from the ground line so buildings never float. */
    public void foundation(int x1, int z1, int x2, int z2, Block b) {
        for (int x = Math.max(x1, lx1); x <= Math.min(x2, lx2); x++)
            for (int z = Math.max(z1, lz1); z <= Math.min(z2, lz2); z++)
                for (int y = -1; y >= -10; y--) {
                    BlockPos p = wp(x, y, z);
                    if (!chunkBox.isInside(p)) break;
                    BlockState s = level.getBlockState(p);
                    if (s.isAir() || !s.getFluidState().isEmpty() || s.canBeReplaced() || s.is(BlockTags.LEAVES) || s.is(BlockTags.LOGS)) {
                        level.setBlock(p, b.defaultBlockState(), 2);
                    } else {
                        break;
                    }
                }
    }

    // ------------------------------------------------------------------------------------- shaped blocks
    public void stairs(int x, int y, int z, Block b, Direction facing, boolean top) {
        BlockState s = b.defaultBlockState().setValue(StairBlock.FACING, facing);
        set(x, y, z, top ? s.setValue(StairBlock.HALF, Half.TOP) : s);
    }

    public void slab(int x, int y, int z, Block b, boolean top) {
        set(x, y, z, b.defaultBlockState().setValue(SlabBlock.TYPE, top ? SlabType.TOP : SlabType.BOTTOM));
    }

    public void door(int x, int y, int z, Block door, Direction facing) {
        BlockState s = door.defaultBlockState().setValue(DoorBlock.FACING, facing);
        set(x, y, z, s.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        set(x, y + 1, z, s.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
    }

    public void bed(int x, int y, int z, Direction dir) {
        BlockState s = Blocks.RED_BED.defaultBlockState().setValue(BedBlock.FACING, dir);
        set(x, y, z, s.setValue(BedBlock.PART, BedPart.FOOT));
        set(x + dir.getStepX(), y, z + dir.getStepZ(), s.setValue(BedBlock.PART, BedPart.HEAD));
    }

    /** Places one of this mod's furniture blocks (ignored silently if the name does not exist). */
    public void furn(String name, int x, int y, int z, Direction facing) {
        Block b = ModBlocks.FURNITURE.get(name);
        if (b != null) {
            set(x, y, z, b.defaultBlockState().setValue(FurnitureBlock.FACING, facing));
        }
    }

    public void chest(int x, int y, int z, Direction facing, ResourceKey<LootTable> loot) {
        set(x, y, z, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing));
        BlockPos p = wp(x, y, z);
        if (chunkBox.isInside(p)) {
            BlockEntity be = level.getBlockEntity(p);
            if (be instanceof RandomizableContainer rc) {
                rc.setLootTable(loot, rnd.nextLong());
            }
        }
    }

    // ------------------------------------------------------------------------------------- entities
    /** Spawns a persistent mob once (only the chunk that contains the position actually creates it). */
    public <T extends Mob> T spawn(EntityType<T> type, int x, int y, int z, Consumer<T> before) {
        BlockPos p = wp(x, y, z);
        if (!chunkBox.isInside(p)) return null;
        T e = type.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
        if (e == null) return null;
        e.snapTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, rnd.nextFloat() * 360.0f, 0.0f);
        if (before != null) before.accept(e);
        e.finalizeSpawn(level, level.getCurrentDifficultyAt(p), EntitySpawnReason.STRUCTURE, null);
        e.setPersistenceRequired();
        level.addFreshEntityWithPassengers(e);
        return e;
    }

    public <T extends Mob> T spawn(EntityType<T> type, int x, int y, int z) {
        return spawn(type, x, y, z, null);
    }
}
