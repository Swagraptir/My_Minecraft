package com.myminecraft.worldgen;

import com.myminecraft.registry.ModEntities;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

/**
 * Hand-written blueprints for the four structures. Every method draws with {@link Canvas} local coordinates
 * (y = 0 is the first block above ground). Randomness comes from the piece's seed, so every chunk draws the same thing.
 */
public final class Blueprints {
    private static final String[] WOODS = {"oak", "spruce", "birch", "dark_oak"};

    public record Mat(Block frame, Block infill, Block floor, Block stair, Block slab, Block roofBlock) {}

    static final Mat TIMBER = new Mat(Blocks.DARK_OAK_LOG, Blocks.WHITE_TERRACOTTA, Blocks.SPRUCE_PLANKS,
            Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_PLANKS);
    static final Mat STONE = new Mat(Blocks.STRIPPED_OAK_LOG, Blocks.STONE_BRICKS, Blocks.OAK_PLANKS,
            Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS);
    static final Mat BRICK = new Mat(Blocks.STRIPPED_SPRUCE_LOG, Blocks.BIRCH_PLANKS, Blocks.OAK_PLANKS,
            Blocks.BRICK_STAIRS, Blocks.BRICK_SLAB, Blocks.BRICKS);
    static final Mat[] HOUSE_MATS = {TIMBER, STONE, BRICK};

    // ------------------------------------------------------------------------------------- dispatcher
    /** {width (x), depth (z), height} of each structure's footprint. */
    public static int[] size(String kind) {
        return switch (kind) {
            case "ice_castle" -> new int[]{49, 49, 42};
            case "medieval_town" -> new int[]{85, 85, 34};
            case "haunted_outpost" -> new int[]{25, 25, 36};
            case "wildfire_castle" -> new int[]{61, 61, 48};
            case "iceologer_cabin" -> new int[]{15, 13, 16};
            case "baobab_tree" -> new int[]{17, 17, 28};
            case "termite_mound" -> new int[]{11, 11, 14};
            case "meerkat_burrow" -> new int[]{11, 11, 8};
            case "illusioner_shack" -> new int[]{13, 11, 14};
            default -> new int[]{65, 65, 28};
        };
    }

    /** Maximum height difference (corner to corner) a site may have. */
    public static int maxSlope(String kind) {
        return switch (kind) {
            case "ice_castle" -> 10;
            case "medieval_town" -> 8;
            case "haunted_outpost" -> 9;
            case "wildfire_castle" -> 12;
            case "iceologer_cabin", "illusioner_shack" -> 4;
            case "baobab_tree", "termite_mound", "meerkat_burrow" -> 3;
            default -> 7;
        };
    }

    public static void run(String kind, Canvas c) {
        switch (kind) {
            case "ice_castle" -> iceCastle(c);
            case "medieval_town" -> medievalTown(c);
            case "haunted_outpost" -> hauntedOutpost(c);
            case "wildfire_castle" -> wildfireCastle(c);
            case "iceologer_cabin" -> iceologerCabin(c);
            case "baobab_tree" -> baobab(c);
            case "termite_mound" -> termiteMound(c);
            case "meerkat_burrow" -> meerkatBurrow(c);
            case "illusioner_shack" -> illusionerShack(c);
            default -> medievalVillage(c);
        }
    }

    public static boolean isNether(String kind) {
        return kind.equals("wildfire_castle");
    }

    private static Block pick(RandomSource r, Block... blocks) {
        return blocks[r.nextInt(blocks.length)];
    }

    // =========================================================================================== shared pieces
    static void path(Canvas c, int x1, int z1, int x2, int z2) {
        for (int x = x1; x <= x2; x++)
            for (int z = z1; z <= z2; z++) {
                c.set(x, -1, z, c.rnd.nextInt(6) == 0 ? Blocks.COARSE_DIRT : Blocks.DIRT_PATH);
                c.clear(x, 0, z, x, 3, z);
            }
    }

    static void lampPost(Canvas c, int x, int z) {
        c.box(x, 0, z, x, 3, z, Blocks.OAK_FENCE);
        c.set(x, 4, z, Blocks.LANTERN);
    }

    static void well(Canvas c, int x, int z) {
        c.clear(x, 0, z, x + 4, 5, z + 4);
        c.walls(x, 0, z, x + 4, 0, z + 4, Blocks.COBBLESTONE);
        c.box(x + 1, -1, z + 1, x + 3, -1, z + 3, Blocks.WATER);
        c.box(x + 1, -2, z + 1, x + 3, -2, z + 3, Blocks.WATER);
        for (int dx : new int[]{0, 4})
            for (int dz : new int[]{0, 4}) c.box(x + dx, 1, z + dz, x + dx, 3, z + dz, Blocks.OAK_FENCE);
        c.box(x, 4, z, x + 4, 4, z + 4, Blocks.SPRUCE_PLANKS);
        c.box(x + 1, 4, z + 1, x + 3, 4, z + 3, Blocks.SPRUCE_SLAB);
    }

    static void farm(Canvas c, int x, int z, int w, int d) {
        c.clear(x, 0, z, x + w - 1, 3, z + d - 1);
        for (int i = 0; i < w; i++)
            for (int j = 0; j < d; j++) {
                boolean water = i == w / 2;
                c.set(x + i, -1, z + j, water ? Blocks.WATER : Blocks.FARMLAND);
                if (!water) {
                    c.set(x + i, 0, z + j, Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, c.rnd.nextInt(8)));
                }
            }
        c.walls(x - 1, 0, z - 1, x + w, 0, z + d, Blocks.OAK_FENCE);
        c.set(x + w / 2, 0, z - 1, Blocks.OAK_FENCE_GATE);
    }

    static void stall(Canvas c, int x, int z) {
        c.clear(x, 0, z, x + 4, 5, z + 3);
        for (int dx : new int[]{0, 4})
            for (int dz : new int[]{0, 3}) c.box(x + dx, 0, z + dz, x + dx, 2, z + dz, Blocks.OAK_FENCE);
        c.box(x, 3, z, x + 4, 3, z + 3, c.rnd.nextBoolean() ? Blocks.RED_WOOL : Blocks.BLUE_WOOL);
        c.box(x + 1, 0, z + 3, x + 3, 0, z + 3, Blocks.SPRUCE_SLAB);
        c.set(x + 2, 1, z + 3, Blocks.BARREL);
        c.set(x + 1, 1, z + 3, Blocks.HAY_BLOCK);
        c.set(x + 3, 1, z + 3, Blocks.MELON);
    }

    /** Gable roof with the ridge along x. Solid mass under the stairs so the attic is closed. */
    static void roof(Canvas c, int x, int z, int w, int d, int h, Mat m) {
        for (int k = 0; ; k++) {
            int zn = z - 1 + k;
            int zs = z + d - k;
            int y = h + 1 + k;
            if (zn > zs) break;
            if (zn == zs) {
                for (int i = x - 1; i <= x + w; i++) c.set(i, y, zn, m.roofBlock());
            } else {
                for (int i = x - 1; i <= x + w; i++) {
                    c.stairs(i, y, zn, m.stair(), Direction.SOUTH, false);
                    c.stairs(i, y, zs, m.stair(), Direction.NORTH, false);
                }
            }
            for (int yy = h + 1; yy < y; yy++)
                for (int i = x; i <= x + w - 1; i++) {
                    if (zn >= z && zn <= z + d - 1) c.set(i, yy, zn, m.roofBlock());
                    if (zs != zn && zs >= z && zs <= z + d - 1) c.set(i, yy, zs, m.roofBlock());
                }
        }
    }

    static void hangLantern(Canvas c, int x, int y, int z) {
        c.set(x, y, z, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
    }

    /** Timber-framed house. frontSouth selects which long wall holds the door. */
    static void house(Canvas c, int x, int z, int w, int d, int h, boolean frontSouth, Mat m) {
        c.clear(x - 1, 0, z - 1, x + w, h + d / 2 + 4, z + d);
        c.foundation(x, z, x + w - 1, z + d - 1, Blocks.COBBLESTONE);
        c.box(x, 0, z, x + w - 1, 0, z + d - 1, m.floor());
        for (int y = 1; y <= h; y++)
            for (int i = 0; i < w; i++)
                for (int j = 0; j < d; j++) {
                    boolean ex = i == 0 || i == w - 1;
                    boolean ez = j == 0 || j == d - 1;
                    if (!ex && !ez) continue;
                    boolean post = (ez && i % 4 == 0) || (ex && j % 4 == 0);
                    c.set(x + i, y, z + j, (ex && ez) || post || y == h ? m.frame() : m.infill());
                }
        for (int i = 2; i < w - 1; i += 4) {
            if (i == w / 2) continue;
            c.set(x + i, 2, z, Blocks.GLASS_PANE);
            c.set(x + i, 2, z + d - 1, Blocks.GLASS_PANE);
        }
        for (int j = 2; j < d - 1; j += 4) {
            c.set(x, 2, z + j, Blocks.GLASS_PANE);
            c.set(x + w - 1, 2, z + j, Blocks.GLASS_PANE);
        }
        int dx = x + w / 2;
        int dz = frontSouth ? z + d - 1 : z;
        c.door(dx, 1, dz, Blocks.OAK_DOOR, frontSouth ? Direction.SOUTH : Direction.NORTH);
        c.set(dx, -1, frontSouth ? dz + 1 : dz - 1, Blocks.DIRT_PATH);
        roof(c, x, z, w, d, h, m);
        furnish(c, x, z, w, d, h, frontSouth);
    }

    /** Fills a house with furniture from this mod. */
    static void furnish(Canvas c, int x, int z, int w, int d, int h, boolean frontSouth) {
        String wood = WOODS[c.rnd.nextInt(WOODS.length)];
        int backZ = frontSouth ? z + 1 : z + d - 2;
        Direction toRoom = frontSouth ? Direction.SOUTH : Direction.NORTH;
        Direction toBack = frontSouth ? Direction.NORTH : Direction.SOUTH;
        c.furn(wood + "_wardrobe", x + 1, 1, backZ, toRoom);
        c.furn(wood + "_nightstand", x + w - 2, 1, backZ, toRoom);
        c.bed(x + w - 4, 1, backZ + (frontSouth ? 1 : -1), toBack);
        c.furn("table_lamp", x + w - 2, 2, backZ, toRoom);
        int tx = x + w / 2;
        int tz = z + d / 2;
        c.furn(wood + "_table", tx, 1, tz, Direction.NORTH);
        c.furn(wood + "_chair", tx - 1, 1, tz, Direction.EAST);
        c.furn(wood + "_chair", tx + 1, 1, tz, Direction.WEST);
        c.furn(wood + "_bookcase", x + 1, 1, tz, Direction.EAST);
        c.furn("red_rug", tx, 0, tz + (frontSouth ? 2 : -2), Direction.NORTH);
        c.furn("plant_pot", x + w - 2, 1, frontSouth ? z + d - 2 : z + 1, toRoom);
        hangLantern(c, tx, h, tz);
    }

    static void tower(Canvas c, int x, int z, int size, int h) {
        c.clear(x - 1, 0, z - 1, x + size, h + 8, z + size);
        c.foundation(x, z, x + size - 1, z + size - 1, Blocks.COBBLESTONE);
        c.hollow(x, 0, z, x + size - 1, h, z + size - 1, Blocks.STONE_BRICKS);
        for (int y = 1; y <= h; y++) {
            for (int[] p : new int[][]{{0, 0}, {size - 1, 0}, {0, size - 1}, {size - 1, size - 1}})
                c.set(x + p[0], y, z + p[1], Blocks.POLISHED_ANDESITE);
            if (y % 6 == 4) {
                c.set(x + size / 2, y, z, Blocks.IRON_BARS);
                c.set(x + size / 2, y, z + size - 1, Blocks.IRON_BARS);
                c.set(x, y, z + size / 2, Blocks.IRON_BARS);
                c.set(x + size - 1, y, z + size / 2, Blocks.IRON_BARS);
            }
        }
        for (int i = 0; i < size; i++) {                         // battlements
            if (i % 2 == 0) {
                for (int[] p : new int[][]{{i, 0}, {i, size - 1}, {0, i}, {size - 1, i}}) c.set(x + p[0], h + 1, z + p[1], Blocks.STONE_BRICKS);
            }
        }
        for (int y = 1; y < h; y++)                              // ladder up the inner west wall
            c.set(x + 1, y, z + size / 2, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.EAST));
        c.clear(x + 1, h, z + size / 2, x + 1, h, z + size / 2);
        c.set(x + size / 2, 1, z + size - 1, Blocks.AIR);       // doorway south
        c.set(x + size / 2, 2, z + size - 1, Blocks.AIR);
        hangLantern(c, x + size / 2, h - 1, z + size / 2);
    }

    static void church(Canvas c, int x, int z, boolean doorNorth) {
        int w = 21, d = 11, h = 8;
        Mat m = new Mat(Blocks.POLISHED_ANDESITE, Blocks.STONE_BRICKS, Blocks.STONE_BRICKS, Blocks.DARK_OAK_STAIRS,
                Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS);
        c.clear(x - 1, 0, z - 1, x + w, h + 14, z + d);
        c.foundation(x, z, x + w - 1, z + d - 1, Blocks.COBBLESTONE);
        c.box(x, 0, z, x + w - 1, 0, z + d - 1, Blocks.STONE_BRICKS);
        for (int y = 1; y <= h; y++)
            for (int i = 0; i < w; i++)
                for (int j = 0; j < d; j++) {
                    boolean ex = i == 0 || i == w - 1;
                    boolean ez = j == 0 || j == d - 1;
                    if (!ex && !ez) continue;
                    boolean buttress = (ez && i % 4 == 0) || (ex && j == d / 2) || (ex && ez);
                    c.set(x + i, y, z + j, buttress ? Blocks.POLISHED_ANDESITE : Blocks.STONE_BRICKS);
                }
        Block[] glass = {Blocks.RED_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS_PANE};
        for (int i = 2, n = 0; i < w - 1; i += 4, n++)
            for (int y = 3; y <= 6; y++) {
                c.set(x + i, y, z, glass[n % 4]);
                c.set(x + i, y, z + d - 1, glass[(n + 1) % 4]);
            }
        for (int y = 3; y <= 6; y++) c.set(x + w - 1, y, z + d / 2, Blocks.PURPLE_STAINED_GLASS_PANE);
        int doorX = x + 12;
        int doorZ = doorNorth ? z : z + d - 1;
        c.door(doorX, 1, doorZ, Blocks.DARK_OAK_DOOR, doorNorth ? Direction.NORTH : Direction.SOUTH);
        roof(c, x, z, w, d, h, m);
        // pews facing the altar (east), aisle in the middle
        for (int i = x + 9; i <= x + 17; i += 2) {
            c.furn("dark_oak_park_bench", i, 1, z + 2, Direction.EAST);
            c.furn("dark_oak_park_bench", i, 1, z + 3, Direction.EAST);
            c.furn("dark_oak_park_bench", i, 1, z + 7, Direction.EAST);
            c.furn("dark_oak_park_bench", i, 1, z + 8, Direction.EAST);
        }
        for (int i = x + 8; i <= x + w - 5; i++) c.set(i, 1, z + 5, Blocks.RED_CARPET);
        c.box(x + w - 3, 1, z + 4, x + w - 3, 1, z + 6, Blocks.QUARTZ_BLOCK);
        c.set(x + w - 3, 2, z + 4, Blocks.WHITE_CANDLE);
        c.set(x + w - 3, 2, z + 6, Blocks.WHITE_CANDLE);
        c.set(x + w - 3, 2, z + 5, Blocks.BOOKSHELF);
        for (int i = x + 6; i <= x + w - 6; i += 5) hangLantern(c, i, h, z + 5);
        // bell tower on the west end
        int tx = x - 7;
        tower(c, tx, z + 2, 7, 18);
        c.set(tx + 3, 12, z + 5, Blocks.BELL);
        for (int k = 0; k < 4; k++) c.box(tx + k, 20 + k, z + 2 + k, tx + 6 - k, 20 + k, z + 8 - k, Blocks.DARK_OAK_PLANKS);
        c.spawn(EntityType.ILLUSIONER, x + w - 2, 1, z + 5);
    }

    // =========================================================================================== MEDIEVAL VILLAGE
    static void medievalVillage(Canvas c) {
        RandomSource r = c.rnd;
        path(c, 0, 14, 64, 15);
        path(c, 0, 45, 64, 46);
        path(c, 31, 14, 33, 46);
        for (int x = 0; x < 65; x++) c.clear(x, 0, 13, x, 4, 13);
        well(c, 30, 30);
        int[][] north = {{3, 7}, {14, 9}, {46, 9}, {57, 7}};
        for (int[] p : north) house(c, p[0], 4, p[1], 9, 4 + r.nextInt(2), true, HOUSE_MATS[r.nextInt(3)]);
        int[][] south = {{3, 7}, {14, 9}, {26, 9}, {38, 7}, {48, 9}};
        for (int[] p : south) house(c, p[0], 48, p[1], 9, 4 + r.nextInt(2), false, HOUSE_MATS[r.nextInt(3)]);
        church(c, 12, 24, false);
        stall(c, 26, 6);
        stall(c, 34, 6);
        farm(c, 44, 24, 8, 9);
        farm(c, 54, 24, 8, 9);
        farm(c, 44, 35, 8, 9);
        for (int x : new int[]{8, 26, 40, 56}) {
            lampPost(c, x, 13);
            lampPost(c, x, 47);
        }
        lampPost(c, 29, 30);
        lampPost(c, 35, 34);
        for (int i = 0; i < 8; i++) c.spawn(EntityType.VILLAGER, 6 + i * 7, 0, i % 2 == 0 ? 14 : 45);
        c.spawn(ModEntities.KNIGHT, 28, 0, 29);
        c.spawn(ModEntities.KNIGHT, 36, 0, 35);
        c.spawn(ModEntities.ARCHER_GUARD, 28, 0, 35);
        c.spawn(ModEntities.ARCHER_GUARD, 36, 0, 29);
    }

    // =========================================================================================== WALLED TOWN
    static void wallSegment(Canvas c, int x1, int z1, int x2, int z2) {
        c.foundation(x1, z1, x2, z2, Blocks.COBBLESTONE);
        for (int x = x1; x <= x2; x++)
            for (int z = z1; z <= z2; z++) {
                for (int y = 0; y <= 8; y++) c.set(x, y, z, c.rnd.nextInt(8) == 0 ? Blocks.MOSSY_STONE_BRICKS : Blocks.STONE_BRICKS);
                c.set(x, 9, z, Blocks.STONE_BRICK_SLAB);
            }
    }

    static void medievalTown(Canvas c) {
        RandomSource r = c.rnd;
        c.clear(0, 0, 0, 84, 12, 84);
        // streets
        path(c, 41, 5, 43, 79);
        path(c, 5, 41, 79, 43);
        // walls (3 thick) with crenellations on the outside
        wallSegment(c, 2, 2, 82, 4);
        wallSegment(c, 2, 80, 82, 82);
        wallSegment(c, 2, 5, 4, 79);
        wallSegment(c, 80, 5, 82, 79);
        for (int i = 2; i <= 82; i += 2) {
            c.set(i, 10, 2, Blocks.STONE_BRICKS);
            c.set(i, 10, 82, Blocks.STONE_BRICKS);
            c.set(2, 10, i, Blocks.STONE_BRICKS);
            c.set(82, 10, i, Blocks.STONE_BRICKS);
        }
        // gate (south) with portcullis
        c.clear(40, 0, 80, 44, 6, 82);
        c.box(40, 7, 80, 44, 8, 82, Blocks.STONE_BRICKS);
        for (int x = 40; x <= 44; x++) for (int y = 4; y <= 6; y++) c.set(x, y, 81, Blocks.IRON_BARS);
        c.set(39, 5, 79, Blocks.LANTERN);
        c.set(45, 5, 79, Blocks.LANTERN);
        // corner towers
        tower(c, 0, 0, 9, 16);
        tower(c, 76, 0, 9, 16);
        tower(c, 0, 76, 9, 16);
        tower(c, 76, 76, 9, 16);
        // houses (north of the east-west street face south, south of it face north)
        int[][] nw = {{8, 8, 9, 9}, {20, 8, 9, 9}, {8, 22, 9, 9}, {20, 22, 7, 9}};
        for (int[] p : nw) house(c, p[0], p[1], p[2], p[3], 4 + r.nextInt(2), true, HOUSE_MATS[r.nextInt(3)]);
        int[][] se = {{50, 48, 9, 9}, {62, 48, 9, 9}, {50, 66, 9, 9}, {62, 66, 9, 9}};
        for (int[] p : se) house(c, p[0], p[1], p[2], p[3], 4 + r.nextInt(2), false, HOUSE_MATS[r.nextInt(3)]);
        int[][] sw = {{8, 68, 9, 9}, {20, 68, 9, 9}};
        for (int[] p : sw) house(c, p[0], p[1], p[2], p[3], 4 + r.nextInt(2), false, HOUSE_MATS[r.nextInt(3)]);
        church(c, 14, 50, true);
        keep(c, 52, 8);
        well(c, 61, 38);
        stall(c, 48, 38);
        stall(c, 68, 44);
        stall(c, 30, 44);
        stall(c, 30, 30);
        farm(c, 12, 34, 8, 6);
        for (int x : new int[]{10, 25, 55, 70}) {
            lampPost(c, x, 40);
            lampPost(c, x, 44);
        }
        for (int z : new int[]{12, 28, 60, 74}) {
            lampPost(c, 40, z);
            lampPost(c, 44, z);
        }
        // people
        for (int i = 0; i < 14; i++) c.spawn(EntityType.VILLAGER, i % 2 == 0 ? 42 : 6 + i * 5, 0, i % 2 == 0 ? 8 + i * 5 : 42);
        for (int[] p : new int[][]{{40, 78}, {44, 78}, {40, 44}, {44, 40}, {30, 42}, {56, 42}, {42, 20}, {42, 62}})
            c.spawn(ModEntities.KNIGHT, p[0], 0, p[1]);
        for (int[] p : new int[][]{{20, 10, 3}, {60, 10, 3}, {20, 10, 81}, {60, 10, 81}, {3, 10, 20}, {3, 10, 60}, {81, 10, 20}, {81, 10, 60}})
            c.spawn(ModEntities.ARCHER_GUARD, p[0], p[1], p[2]);
    }

    static void keep(Canvas c, int x, int z) {
        int s = 21, h = 16;
        c.clear(x - 1, 0, z - 1, x + s, h + 6, z + s);
        c.foundation(x, z, x + s - 1, z + s - 1, Blocks.COBBLESTONE);
        c.hollow(x, 0, z, x + s - 1, h, z + s - 1, Blocks.STONE_BRICKS);
        c.box(x + 1, 0, z + 1, x + s - 2, 0, z + s - 2, Blocks.POLISHED_ANDESITE);
        for (int y = 1; y <= h; y++)
            for (int[] p : new int[][]{{0, 0}, {s - 1, 0}, {0, s - 1}, {s - 1, s - 1}})
                c.set(x + p[0], y, z + p[1], Blocks.POLISHED_DEEPSLATE);
        for (int i = 0; i < s; i += 2) {
            c.set(x + i, h + 1, z, Blocks.STONE_BRICKS);
            c.set(x + i, h + 1, z + s - 1, Blocks.STONE_BRICKS);
            c.set(x, h + 1, z + i, Blocks.STONE_BRICKS);
            c.set(x + s - 1, h + 1, z + i, Blocks.STONE_BRICKS);
        }
        for (int y = 4; y <= 12; y += 4)
            for (int i = 3; i < s - 2; i += 5) {
                c.set(x + i, y, z, Blocks.IRON_BARS);
                c.set(x + i, y, z + s - 1, Blocks.IRON_BARS);
            }
        c.set(x + 10, 1, z + s - 1, Blocks.AIR);
        c.set(x + 10, 2, z + s - 1, Blocks.AIR);
        c.set(x + 10, 3, z + s - 1, Blocks.AIR);
        c.box(x + 1, 7, z + 1, x + s - 2, 7, z + s - 2, Blocks.SPRUCE_PLANKS);   // upper floor
        c.clear(x + 2, 7, z + 2, x + 2, 7, z + 2);
        for (int y = 1; y <= 7; y++) c.set(x + 1, y, z + 2, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.EAST));
        for (int y = 8; y < h; y++) c.set(x + 1, y, z + 2, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.EAST));
        c.clear(x + 1, 7, z + 2, x + 1, 7, z + 2);
        c.clear(x + 1, h, z + 2, x + 1, h, z + 2);
        for (int i = 3; i < s - 3; i += 4) {
            hangLantern(c, x + i, 6, z + s / 2);
            hangLantern(c, x + i, 15, z + s / 2);
        }
        c.furn("dark_oak_table", x + 10, 1, z + 10, Direction.NORTH);
        for (int i = -3; i <= 3; i += 2) {
            c.furn("dark_oak_chair", x + 10 + i, 1, z + 9, Direction.SOUTH);
            c.furn("dark_oak_chair", x + 10 + i, 1, z + 11, Direction.NORTH);
        }
        c.furn("dark_oak_wardrobe", x + s - 2, 1, z + 2, Direction.WEST);
        c.furn("fireplace", x + 10, 1, z + 1, Direction.SOUTH);
        c.chest(x + s - 2, 1, z + s - 3, Direction.WEST, BuiltInLootTables.VILLAGE_WEAPONSMITH);
        c.chest(x + s - 2, 8, z + 3, Direction.WEST, BuiltInLootTables.SIMPLE_DUNGEON);
        c.spawn(ModEntities.KNIGHT, x + 8, 1, z + 14);
        c.spawn(ModEntities.KNIGHT, x + 12, 1, z + 14);
        c.spawn(ModEntities.ARCHER_GUARD, x + 10, h + 1, z + 3);
        c.spawn(ModEntities.ARCHER_GUARD, x + 16, h + 1, z + 16);
    }

    // =========================================================================================== HAUNTED OUTPOST
    static void hauntedOutpost(Canvas c) {
        RandomSource r = c.rnd;
        c.clear(0, 0, 0, 24, 34, 24);
        c.foundation(2, 2, 22, 22, Blocks.COARSE_DIRT);
        for (int x = 2; x <= 22; x++)
            for (int z = 2; z <= 22; z++) {
                if (r.nextInt(3) == 0) c.set(x, -1, z, pick(r, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.SOUL_SOIL, Blocks.GRAVEL));
            }
        // tower
        int tx = 8, tz = 8, s = 9, h = 22;
        c.hollow(tx, 0, tz, tx + s - 1, h, tz + s - 1, Blocks.COBBLESTONE);
        for (int y = 1; y <= h; y++)
            for (int i = 0; i < s; i++)
                for (int j = 0; j < s; j++) {
                    boolean ex = i == 0 || i == s - 1;
                    boolean ez = j == 0 || j == s - 1;
                    if (!ex && !ez) continue;
                    Block b = (ex && ez) ? Blocks.DARK_OAK_LOG : pick(r, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Blocks.COBBLESTONE, Blocks.COBBLED_DEEPSLATE);
                    c.set(tx + i, y, tz + j, b);
                }
        for (int y : new int[]{4, 11, 18}) {
            c.set(tx + 4, y, tz, Blocks.IRON_BARS);
            c.set(tx + 4, y, tz + s - 1, Blocks.IRON_BARS);
            c.set(tx, y, tz + 4, Blocks.IRON_BARS);
            c.set(tx + s - 1, y, tz + 4, Blocks.IRON_BARS);
        }
        c.box(tx, 8, tz, tx + s - 1, 8, tz + s - 1, Blocks.DARK_OAK_PLANKS);       // floors
        c.box(tx, 15, tz, tx + s - 1, 15, tz + s - 1, Blocks.DARK_OAK_PLANKS);
        c.box(tx, h, tz, tx + s - 1, h, tz + s - 1, Blocks.DARK_OAK_PLANKS);
        for (int y = 1; y < h; y++) {
            c.set(tx + 1, y, tz + 4, Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.EAST));
        }
        c.clear(tx + 1, 8, tz + 4, tx + 1, 8, tz + 4);
        c.clear(tx + 1, 15, tz + 4, tx + 1, 15, tz + 4);
        c.clear(tx + 1, h, tz + 4, tx + 1, h, tz + 4);
        c.clear(tx + 4, 1, tz + s - 1, tx + 4, 2, tz + s - 1);                          // doorway south
        for (int i = 0; i < s; i++) {                                                   // fenced roof platform
            for (int[] p : new int[][]{{i, 0}, {i, s - 1}, {0, i}, {s - 1, i}}) c.set(tx + p[0], h + 1, tz + p[1], Blocks.DARK_OAK_FENCE);
        }
        for (int[] p : new int[][]{{0, 0}, {s - 1, 0}, {0, s - 1}, {s - 1, s - 1}}) {
            c.set(tx + p[0], h + 2, tz + p[1], Blocks.SOUL_LANTERN);
            c.set(tx + p[0], h + 3, tz + p[1], Blocks.SKELETON_SKULL);
        }
        for (int y : new int[]{7, 14, 21}) hangSoul(c, tx + 4, y, tz + 4);
        for (int i = 0; i < 26; i++)                                                    // cobwebs everywhere
            c.set(tx + 1 + r.nextInt(s - 2), 1 + r.nextInt(h - 1), tz + 1 + r.nextInt(s - 2), Blocks.COBWEB);
        c.chest(tx + 2, 9, tz + 2, Direction.SOUTH, BuiltInLootTables.PILLAGER_OUTPOST);
        c.chest(tx + s - 3, 1, tz + 2, Direction.SOUTH, BuiltInLootTables.PILLAGER_OUTPOST);
        // palisade
        for (int i = 2; i <= 22; i++) {
            for (int[] p : new int[][]{{i, 2}, {i, 22}, {2, i}, {22, i}}) {
                if (r.nextInt(5) == 0) continue;
                int hh = 2 + r.nextInt(3);
                c.box(p[0], 0, p[1], p[0], hh, p[1], Blocks.DARK_OAK_FENCE);
            }
        }
        c.clear(11, 0, 22, 13, 4, 22);
        // ravager pen
        c.walls(16, 0, 3, 20, 3, 7, Blocks.IRON_BARS);
        c.clear(17, 0, 4, 19, 3, 6);
        c.box(16, 4, 3, 20, 4, 7, Blocks.DARK_OAK_SLAB);
        // camp & graves
        c.set(5, 0, 12, Blocks.SOUL_CAMPFIRE);
        c.set(4, 0, 12, Blocks.HAY_BLOCK);
        c.set(6, 0, 13, Blocks.BARREL);
        for (int i = 0; i < 4; i++) {
            int gx = 4 + i * 2, gz = 18;
            c.set(gx, 0, gz, Blocks.COBBLESTONE_SLAB);
            c.set(gx, 1, gz, Blocks.COBBLESTONE_WALL);
            c.set(gx, -1, gz + 1, Blocks.SOUL_SAND);
        }
        for (int i = 0; i < 6; i++) c.set(3 + r.nextInt(19), 0, 3 + r.nextInt(19), Blocks.DEAD_BUSH);
        // the garrison
        c.spawn(EntityType.RAVAGER, 18, 0, 5);
        c.spawn(EntityType.PILLAGER, tx + 2, h + 1, tz + 2);
        c.spawn(EntityType.PILLAGER, tx + 6, h + 1, tz + 6);
        c.spawn(EntityType.PILLAGER, tx + 2, h + 1, tz + 6);
        c.spawn(EntityType.PILLAGER, 12, 0, 20);
        c.spawn(EntityType.PILLAGER, 6, 0, 8);
        c.spawn(EntityType.VINDICATOR, 12, 0, 5);
        c.spawn(EntityType.VINDICATOR, tx + 4, 1, tz + 4);
        c.spawn(EntityType.EVOKER, tx + 4, 9, tz + 4);
    }

    private static void hangSoul(Canvas c, int x, int y, int z) {
        c.set(x, y, z, Blocks.SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
    }

    // =========================================================================================== ICE CASTLE
    static void iceCastle(Canvas c) {
        RandomSource r = c.rnd;
        c.clear(0, 0, 0, 48, 40, 48);
        c.foundation(0, 0, 48, 48, Blocks.PACKED_ICE);
        for (int x = 3; x <= 45; x++)
            for (int z = 3; z <= 45; z++) c.set(x, -1, z, r.nextInt(5) == 0 ? Blocks.BLUE_ICE : Blocks.PACKED_ICE);
        // outer wall, 2 thick, 10 high, crenellated
        for (int i = 0; i <= 48; i++) {
            for (int t = 0; t < 2; t++) {
                for (int y = 0; y <= 9; y++) {
                    Block b = r.nextInt(7) == 0 ? Blocks.BLUE_ICE : Blocks.PACKED_ICE;
                    c.set(i, y, t, b);
                    c.set(i, y, 48 - t, b);
                    c.set(t, y, i, b);
                    c.set(48 - t, y, i, b);
                }
            }
            if (i % 2 == 0) {
                c.set(i, 10, 0, Blocks.SNOW_BLOCK);
                c.set(i, 10, 48, Blocks.SNOW_BLOCK);
                c.set(0, 10, i, Blocks.SNOW_BLOCK);
                c.set(48, 10, i, Blocks.SNOW_BLOCK);
            }
        }
        // gate (south)
        c.clear(22, 0, 47, 26, 6, 48);
        for (int x = 22; x <= 26; x++) c.set(x, 7, 47, Blocks.BLUE_ICE);
        for (int x = 22; x <= 26; x++) c.set(x, 7, 48, Blocks.BLUE_ICE);
        c.set(22, 5, 46, Blocks.LANTERN);
        c.set(26, 5, 46, Blocks.LANTERN);
        // corner towers with spires
        for (int[] p : new int[][]{{4, 4}, {44, 4}, {4, 44}, {44, 44}}) {
            c.cylinder(p[0], p[1], 4, 0, 24, Blocks.PACKED_ICE, true);
            c.cylinder(p[0], p[1], 4, 12, 12, Blocks.PACKED_ICE, false);
            c.cylinder(p[0], p[1], 5, 25, 25, Blocks.BLUE_ICE, false);
            for (int k = 0; k < 9; k++) c.cylinder(p[0], p[1], Math.max(0, 4 - k / 2), 26 + k, 26 + k, Blocks.BLUE_ICE, false);
            for (int y = 5; y <= 21; y += 8) {
                c.set(p[0] - 4, y, p[1], Blocks.ICE);
                c.set(p[0] + 4, y, p[1], Blocks.ICE);
                c.set(p[0], y, p[1] - 4, Blocks.ICE);
                c.set(p[0], y, p[1] + 4, Blocks.ICE);
            }
            hangLantern(c, p[0], 11, p[1]);
        }
        // central keep with a pyramid roof
        int kx = 14, kz = 14, s = 21, h = 16;
        c.hollow(kx, 0, kz, kx + s - 1, h, kz + s - 1, Blocks.PACKED_ICE);
        c.box(kx + 1, 0, kz + 1, kx + s - 2, 0, kz + s - 2, Blocks.SNOW_BLOCK);
        for (int y = 1; y <= h; y++)
            for (int[] p : new int[][]{{0, 0}, {s - 1, 0}, {0, s - 1}, {s - 1, s - 1}})
                c.set(kx + p[0], y, kz + p[1], Blocks.BLUE_ICE);
        for (int y = 5; y <= 8; y++)
            for (int i = 3; i < s - 2; i += 4) {
                c.set(kx + i, y, kz, Blocks.ICE);
                c.set(kx + i, y, kz + s - 1, Blocks.ICE);
                c.set(kx, y, kz + i, Blocks.ICE);
                c.set(kx + s - 1, y, kz + i, Blocks.ICE);
            }
        for (int k = 0; k <= 10; k++) c.box(kx + k, h + 1 + k, kz + k, kx + s - 1 - k, h + 1 + k, kz + s - 1 - k, Blocks.BLUE_ICE);
        c.clear(kx + 9, 1, kz + s - 1, kx + 11, 4, kz + s - 1);                       // entrance
        // throne room at the north end
        c.box(kx + 6, 1, kz + 1, kx + 14, 1, kz + 4, Blocks.QUARTZ_BLOCK);
        c.box(kx + 8, 2, kz + 1, kx + 12, 2, kz + 2, Blocks.QUARTZ_BLOCK);
        c.stairs(kx + 10, 3, kz + 1, Blocks.QUARTZ_STAIRS, Direction.SOUTH, false);
        c.box(kx + 10, 3, kz + 1, kx + 10, 3, kz + 1, Blocks.QUARTZ_STAIRS);
        c.box(kx + 9, 3, kz + 1, kx + 9, 6, kz + 1, Blocks.BLUE_ICE);
        c.box(kx + 11, 3, kz + 1, kx + 11, 6, kz + 1, Blocks.BLUE_ICE);
        c.box(kx + 9, 7, kz + 1, kx + 11, 7, kz + 1, Blocks.BLUE_ICE);
        c.stairs(kx + 10, 3, kz + 1, Blocks.QUARTZ_STAIRS, Direction.SOUTH, false);
        for (int i = 3; i < s - 3; i += 4)
            for (int j = 6; j < s - 2; j += 6) {
                c.box(kx + i, 1, kz + j, kx + i, h - 1, kz + j, Blocks.BLUE_ICE);
                hangLantern(c, kx + i + 1, h - 1, kz + j);
            }
        for (int i = 0; i < 4; i++) {                                                // snow golem statues
            int sx = kx + 3 + (i % 2) * 14;
            int sz = kz + 8 + (i / 2) * 6;
            c.box(sx, 1, sz, sx, 2, sz, Blocks.SNOW_BLOCK);
            c.set(sx, 3, sz, Blocks.CARVED_PUMPKIN);
        }
        c.chest(kx + 2, 1, kz + 2, Direction.SOUTH, BuiltInLootTables.IGLOO_CHEST);
        c.chest(kx + s - 3, 1, kz + 2, Direction.SOUTH, BuiltInLootTables.SIMPLE_DUNGEON);
        c.chest(kx + 10, 1, kz + 6, Direction.SOUTH, BuiltInLootTables.IGLOO_CHEST);
        // inhabitants
        c.spawn(ModEntities.ICEOLOGER, kx + 10, 3, kz + 4, e -> e.setBoss(true));
        c.spawn(ModEntities.ICEOLOGER, kx + 5, 1, kz + 10);
        c.spawn(ModEntities.ICEOLOGER, kx + 15, 1, kz + 10);
        c.spawn(ModEntities.ICEOLOGER, 24, 0, 40);
        c.spawn(ModEntities.ICEOLOGER, 10, 0, 24);
        c.spawn(ModEntities.ICEOLOGER, 38, 0, 24);
    }


    // =========================================================================================== WILDFIRE NETHER CASTLE
    static Block blackstone(RandomSource r) {
        int k = r.nextInt(14);
        return k == 0 ? Blocks.GILDED_BLACKSTONE : k < 4 ? Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS : Blocks.POLISHED_BLACKSTONE_BRICKS;
    }

    static void wildfireCastle(Canvas c) {
        RandomSource r = c.rnd;
        c.clear(0, 0, 0, 60, 46, 60);
        // lava moat around a blackstone platform
        c.foundation(0, 0, 60, 60, Blocks.BLACKSTONE);
        for (int x = 0; x <= 60; x++)
            for (int z = 0; z <= 60; z++) {
                boolean moat = x < 6 || z < 6 || x > 54 || z > 54;
                if (moat) {
                    if ((x == 0 || z == 0 || x == 60 || z == 60)) c.set(x, -1, z, Blocks.BASALT);
                    else c.set(x, -1, z, Blocks.LAVA);
                } else {
                    c.set(x, -1, z, r.nextInt(9) == 0 ? Blocks.MAGMA_BLOCK : blackstone(r));
                }
            }
        // bridge over the moat (south)
        for (int x = 28; x <= 32; x++) for (int z = 50; z <= 60; z++) c.set(x, -1, z, Blocks.POLISHED_BLACKSTONE_BRICKS);
        for (int z = 52; z <= 60; z += 2) {
            c.box(27, 0, z, 27, 2, z, Blocks.NETHER_BRICK_FENCE);
            c.box(33, 0, z, 33, 2, z, Blocks.NETHER_BRICK_FENCE);
            c.set(27, 3, z, Blocks.SOUL_LANTERN);
            c.set(33, 3, z, Blocks.SOUL_LANTERN);
        }
        // curtain wall
        for (int i = 8; i <= 52; i++) {
            for (int t = 0; t < 2; t++) {
                for (int y = 0; y <= 11; y++) {
                    c.set(i, y, 8 + t, blackstone(r));
                    c.set(i, y, 52 - t, blackstone(r));
                    c.set(8 + t, y, i, blackstone(r));
                    c.set(52 - t, y, i, blackstone(r));
                }
            }
            if (i % 2 == 0) {
                c.set(i, 12, 8, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
                c.set(i, 12, 52, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
                c.set(8, 12, i, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
                c.set(52, 12, i, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
            }
            if (i % 6 == 0) {                                       // lava-falls and braziers on the wall
                c.set(i, 13, 8, Blocks.SOUL_CAMPFIRE);
                c.set(i, 13, 52, Blocks.SOUL_CAMPFIRE);
            }
        }
        // gate with a portcullis of bars and a gilded arch
        c.clear(27, 0, 52, 33, 8, 53);
        c.box(27, 9, 52, 33, 10, 53, Blocks.GILDED_BLACKSTONE);
        for (int x = 28; x <= 32; x++) for (int y = 5; y <= 8; y++) c.set(x, y, 52, Blocks.IRON_BARS);
        // four round towers with spires of red nether bricks
        for (int[] p : new int[][]{{8, 8}, {52, 8}, {8, 52}, {52, 52}}) {
            c.cylinder(p[0], p[1], 5, 0, 30, Blocks.POLISHED_BLACKSTONE_BRICKS, true);
            c.cylinder(p[0], p[1], 5, 14, 14, Blocks.BLACKSTONE, false);
            c.cylinder(p[0], p[1], 5, 24, 24, Blocks.BLACKSTONE, false);
            c.cylinder(p[0], p[1], 6, 31, 31, Blocks.RED_NETHER_BRICKS, false);
            for (int k = 0; k < 11; k++) c.cylinder(p[0], p[1], Math.max(0, 5 - k / 2), 32 + k, 32 + k, Blocks.RED_NETHER_BRICKS, false);
            for (int y = 6; y <= 26; y += 10) {
                c.set(p[0] - 5, y, p[1], Blocks.IRON_BARS);
                c.set(p[0] + 5, y, p[1], Blocks.IRON_BARS);
                c.set(p[0], y, p[1] - 5, Blocks.IRON_BARS);
                c.set(p[0], y, p[1] + 5, Blocks.IRON_BARS);
            }
            c.set(p[0], 25, p[1], Blocks.SHROOMLIGHT);
            c.set(p[0], 15, p[1], Blocks.SHROOMLIGHT);
        }
        // keep
        int kx = 18, kz = 18, s = 25, h = 32;
        c.hollow(kx, 0, kz, kx + s - 1, h, kz + s - 1, Blocks.POLISHED_BLACKSTONE_BRICKS);
        c.box(kx + 1, 0, kz + 1, kx + s - 2, 0, kz + s - 2, Blocks.POLISHED_BLACKSTONE);
        for (int y = 1; y <= h; y++) {
            for (int[] p : new int[][]{{0, 0}, {s - 1, 0}, {0, s - 1}, {s - 1, s - 1}, {12, 0}, {12, s - 1}, {0, 12}, {s - 1, 12}})
                c.set(kx + p[0], y, kz + p[1], y % 6 == 0 ? Blocks.GILDED_BLACKSTONE : Blocks.CHISELED_POLISHED_BLACKSTONE);
        }
        for (int y = 6; y <= 24; y += 9)
            for (int i = 3; i < s - 2; i += 4) {
                c.set(kx + i, y, kz, Blocks.IRON_BARS);
                c.set(kx + i, y, kz + s - 1, Blocks.IRON_BARS);
                c.set(kx, y, kz + i, Blocks.IRON_BARS);
                c.set(kx + s - 1, y, kz + i, Blocks.IRON_BARS);
            }
        for (int k = 0; k <= 12; k++) c.box(kx + k, h + 1 + k, kz + k, kx + s - 1 - k, h + 1 + k, kz + s - 1 - k, Blocks.RED_NETHER_BRICKS);
        c.clear(kx + 11, 1, kz + s - 1, kx + 13, 5, kz + s - 1);                          // entrance
        // throne hall: lava channels along the aisle, soul fire braziers, a dais for the Warlord
        c.box(kx + 2, 0, kz + 2, kx + 3, 0, kz + s - 3, Blocks.LAVA);
        c.box(kx + s - 4, 0, kz + 2, kx + s - 3, 0, kz + s - 3, Blocks.LAVA);
        c.box(kx + 6, 1, kz + 1, kx + 18, 1, kz + 6, Blocks.RED_NETHER_BRICKS);
        c.box(kx + 9, 2, kz + 1, kx + 15, 2, kz + 3, Blocks.RED_NETHER_BRICKS);
        c.stairs(kx + 12, 3, kz + 1, Blocks.RED_NETHER_BRICK_STAIRS, Direction.SOUTH, false);
        c.box(kx + 11, 3, kz + 1, kx + 11, 8, kz + 1, Blocks.GILDED_BLACKSTONE);
        c.box(kx + 13, 3, kz + 1, kx + 13, 8, kz + 1, Blocks.GILDED_BLACKSTONE);
        c.box(kx + 11, 9, kz + 1, kx + 13, 9, kz + 1, Blocks.GILDED_BLACKSTONE);
        for (int i = 5; i < s - 4; i += 5)
            for (int j = 8; j < s - 2; j += 6) {
                c.box(kx + i, 1, kz + j, kx + i, h - 2, kz + j, Blocks.POLISHED_BLACKSTONE_BRICKS);
                c.set(kx + i, 2, kz + j + 1, Blocks.SOUL_CAMPFIRE);
                c.set(kx + i + 1, h - 3, kz + j, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
            }
        for (int i = 0; i < 6; i++) c.set(kx + 4 + r.nextInt(s - 8), h - 1, kz + 4 + r.nextInt(s - 8),
                Blocks.CHAIN.defaultBlockState());
        c.chest(kx + 3, 1, kz + 2, Direction.SOUTH, BuiltInLootTables.BASTION_TREASURE);
        c.chest(kx + s - 4, 1, kz + 2, Direction.SOUTH, BuiltInLootTables.BASTION_TREASURE);
        c.chest(kx + 12, 1, kz + 9, Direction.SOUTH, BuiltInLootTables.BASTION_OTHER);
        // inhabitants
        c.spawn(ModEntities.WILDFIRE, kx + 12, 4, kz + 4, e -> e.setBoss(true));
        c.spawn(ModEntities.WILDFIRE, kx + 6, 6, kz + 12);
        c.spawn(ModEntities.WILDFIRE, kx + 18, 6, kz + 12);
        for (int[] p : new int[][]{{12, 3, 30}, {48, 3, 30}, {30, 3, 12}, {30, 3, 48}}) c.spawn(EntityType.BLAZE, p[0], p[1], p[2]);
        c.spawn(EntityType.WITHER_SKELETON, kx + 6, 1, kz + 14);
        c.spawn(EntityType.WITHER_SKELETON, kx + 18, 1, kz + 14);
        c.spawn(EntityType.WITHER_SKELETON, 30, 0, 46);
    }


    // =========================================================================================== SMALL HIDEOUTS (Friends&Foes idea)
    static void iceologerCabin(Canvas c) {
        Mat spruce = new Mat(Blocks.SPRUCE_LOG, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_PLANKS);
        house(c, 3, 3, 9, 7, 4, true, spruce);
        for (int x = 2; x <= 12; x += 2) c.set(x, 9, 6, Blocks.SNOW_BLOCK);
        c.set(7, 1, 6, Blocks.BREWING_STAND);
        c.chest(4, 1, 4, Direction.SOUTH, BuiltInLootTables.IGLOO_CHEST);
        c.spawn(ModEntities.ICEOLOGER, 7, 1, 5);
    }

    static void illusionerShack(Canvas c) {
        Mat dark = new Mat(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_PLANKS);
        house(c, 2, 2, 9, 7, 4, true, dark);
        for (int i = 0; i < 18; i++) c.set(3 + c.rnd.nextInt(7), 1 + c.rnd.nextInt(3), 3 + c.rnd.nextInt(5), Blocks.COBWEB);
        c.set(6, 1, 4, Blocks.BREWING_STAND);
        c.set(8, 1, 4, Blocks.CAULDRON);
        c.chest(3, 1, 7, Direction.NORTH, BuiltInLootTables.SIMPLE_DUNGEON);
        c.spawn(EntityType.ILLUSIONER, 6, 1, 6);
    }


    // =========================================================================================== BIOME VOTE LOSERS
    /** Savanna (2018 biome vote): a huge baobab with a hollow trunk you can walk into and a flat acacia crown. */
    static void baobab(Canvas c) {
        RandomSource r = c.rnd;
        BlockState leaf = Blocks.ACACIA_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        int cx = 8, cz = 8;
        c.clear(0, 0, 0, 16, 26, 16);
        for (int y = 0; y <= 15; y++) {
            int rad = y < 4 ? 3 : 2;
            c.cylinder(cx, cz, rad, y, y, r.nextInt(5) == 0 ? Blocks.STRIPPED_ACACIA_LOG : Blocks.ACACIA_LOG, false);
        }
        for (int a = 0; a < 8; a++) {                                     // buttress roots
            double ang = Math.PI * 2 * a / 8;
            for (int i = 3; i <= 5; i++) c.set(cx + (int) Math.round(Math.cos(ang) * i), 5 - i > 0 ? 5 - i : 0, cz + (int) Math.round(Math.sin(ang) * i), Blocks.ACACIA_LOG);
        }
        c.clear(cx - 1, 1, cz - 1, cx + 1, 6, cz + 1);                    // hollow trunk
        c.clear(cx, 1, cz + 2, cx, 2, cz + 3);                            // doorway
        c.chest(cx, 1, cz - 1, Direction.SOUTH, BuiltInLootTables.SIMPLE_DUNGEON);
        hangLantern(c, cx, 6, cz);
        for (int b = 0; b < 6; b++) {                                     // branches with leaf pads
            double ang = Math.PI * 2 * b / 6 + 0.3;
            int ex = cx, ey = 15, ez = cz;
            for (int i = 1; i <= 6; i++) {
                ex = cx + (int) Math.round(Math.cos(ang) * i);
                ez = cz + (int) Math.round(Math.sin(ang) * i);
                ey = 15 + i / 3;
                c.set(ex, ey, ez, Blocks.ACACIA_LOG);
            }
            c.cylinder(ex, ez, 3, ey + 1, ey + 1, leaf.getBlock(), false);
            c.cylinder(ex, ez, 2, ey + 2, ey + 2, leaf.getBlock(), false);
        }
        c.cylinder(cx, cz, 4, 17, 17, leaf.getBlock(), false);
        c.cylinder(cx, cz, 3, 18, 18, leaf.getBlock(), false);
    }

    /** Savanna (2018 biome vote): a tapering mound of dried clay with termites crawling around its base. */
    static void termiteMound(Canvas c) {
        RandomSource r = c.rnd;
        c.clear(0, 0, 0, 10, 13, 10);
        c.foundation(1, 1, 9, 9, Blocks.COARSE_DIRT);
        for (int y = 0; y <= 10; y++) {
            int rad = Math.max(0, 5 - (y + 1) / 2);
            Block b = y % 3 == 0 ? Blocks.TERRACOTTA : y % 2 == 0 ? Blocks.BROWN_TERRACOTTA : Blocks.COARSE_DIRT;
            c.cylinder(5, 5, rad, y, y, b, false);
        }
        for (int i = 0; i < 10; i++) c.set(2 + r.nextInt(7), 1 + r.nextInt(5), 2 + r.nextInt(7), Blocks.AIR);
        for (int[] p : new int[][]{{3, 5}, {7, 5}, {5, 3}, {5, 7}}) c.spawn(ModEntities.ANIMALS.get("termite"), p[0], 0, p[1]);
    }

    /** Desert (2018 biome vote): a hidden meerkat burrow - a sand shaft down to a small sandstone chamber. */
    static void meerkatBurrow(Canvas c) {
        c.clear(0, 0, 0, 10, 6, 10);
        c.hollow(0, -8, 0, 10, -4, 10, Blocks.SANDSTONE);
        c.box(1, -8, 1, 9, -8, 9, Blocks.SAND);
        c.clear(4, -7, 4, 6, -1, 6);
        for (int x = 3; x <= 7; x++) for (int z = 3; z <= 7; z++) if (x < 4 || x > 6 || z < 4 || z > 6) c.set(x, -1, z, Blocks.SANDSTONE);
        c.set(2, -7, 2, Blocks.TORCH);
        c.set(8, -7, 2, Blocks.TORCH);
        c.set(1, 0, 1, Blocks.CACTUS);
        c.set(9, 0, 8, Blocks.CACTUS);
        c.chest(8, -7, 8, Direction.WEST, BuiltInLootTables.DESERT_PYRAMID);
        for (int[] p : new int[][]{{3, -7, 3}, {7, -7, 3}, {5, -7, 7}}) c.spawn(ModEntities.ANIMALS.get("meerkat"), p[0], p[1], p[2]);
    }

    private Blueprints() {}
}
