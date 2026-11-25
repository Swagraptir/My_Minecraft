package com.myminecraft.furniture;

import com.mojang.serialization.MapCodec;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * One class for every furniture block. Behaviour comes from {@link FurnitureType}, collision/selection shape from
 * the generated box list in {@link FurnitureDefs}.
 */
public class FurnitureBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<FurnitureBlock> CODEC = simpleCodec(p -> {
        throw new UnsupportedOperationException("FurnitureBlock is registered in code only");
    });
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private final FurnitureDefs.Def def;
    /** Wood/colour variants share one box array, so their shapes are built once (startup cost / 4). */
    private static final Map<double[][], VoxelShape[]> SHAPE_CACHE = new java.util.IdentityHashMap<>();
    private final Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);

    public FurnitureBlock(BlockBehaviour.Properties props, FurnitureDefs.Def def) {
        super(props);
        this.def = def;
        VoxelShape[] built = SHAPE_CACHE.computeIfAbsent(def.boxes(), boxes -> {
            VoxelShape[] a = new VoxelShape[4];
            for (int q = 0; q < 4; q++) a[q] = buildShape(boxes, q).optimize();
            return a;
        });
        for (Direction d : Direction.Plane.HORIZONTAL) {
            shapes.put(d, built[quarterTurns(d)]);
        }
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    public FurnitureDefs.Def def() {
        return def;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    // ------------------------------------------------------------------------------------- shapes
    private static int quarterTurns(Direction d) {
        return switch (d) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };
    }

    /** Boxes are designed facing north. A model rotated "y: 90" turns clockwise: (x, z) -> (16 - z, x). */
    private static VoxelShape buildShape(double[][] boxes, int quarters) {
        VoxelShape shape = Shapes.empty();
        for (double[] b : boxes) {
            double x1 = b[0], y1 = b[1], z1 = b[2], x2 = b[3], y2 = b[4], z2 = b[5];
            for (int i = 0; i < quarters; i++) {
                double nx1 = 16 - z2, nz1 = x1, nx2 = 16 - z1, nz2 = x2;
                x1 = nx1; z1 = nz1; x2 = nx2; z2 = nz2;
            }
            shape = Shapes.or(shape, Block.box(x1, y1, z1, x2, y2, z2));
        }
        return shape;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return shapes.get(state.getValue(FACING));
    }

    // ------------------------------------------------------------------------------------- state
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // Called from the super constructor before our fields exist, so every furniture block declares LIT
        // (only lamps ever use it).
        builder.add(FACING, LIT);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    // ------------------------------------------------------------------------------------- interaction
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        switch (def.type()) {
            case SEAT -> {
                if (level.isClientSide()) return InteractionResult.SUCCESS;
                if (player.isShiftKeyDown() || player.isPassenger()) return InteractionResult.PASS;
                return SeatEntity.sit(level, pos, player, state.getValue(FACING), seatHeight())
                        ? InteractionResult.CONSUME : InteractionResult.PASS;
            }
            case STORAGE -> {
                if (!level.isClientSide() && level.getBlockEntity(pos) instanceof StorageBlockEntity be) {
                    player.openMenu(be);
                }
                return InteractionResult.SUCCESS;
            }
            case LAMP -> {
                if (!level.isClientSide()) {
                    boolean on = !state.getValue(LIT);
                    level.setBlock(pos, state.setValue(LIT, on), Block.UPDATE_ALL);
                    level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.6f, on ? 0.7f : 0.5f);
                }
                return InteractionResult.SUCCESS;
            }
            default -> {
                return InteractionResult.PASS;
            }
        }
    }

    /** Height (in blocks) of the sitting surface for this piece. */
    private double seatHeight() {
        double top = 0;
        for (double[] b : def.boxes()) {
            if ((b[3] - b[0]) >= 6 && (b[5] - b[2]) >= 6 && b[4] <= 12) {
                top = Math.max(top, b[4]);
            }
        }
        if (top <= 0) top = 8;
        return top / 16.0;
    }

    // ------------------------------------------------------------------------------------- block entity (storage)
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return def.type() == FurnitureType.STORAGE ? new StorageBlockEntity(pos, state) : null;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        Containers.updateNeighboursAfterDestroy(state, level, pos);
    }
}
