package com.myminecraft.vehicle;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

/** Places a bicycle / car where you click. */
public class VehicleItem extends Item {
    private final Supplier<EntityType<VehicleMob>> type;

    public VehicleItem(Item.Properties props, Supplier<EntityType<VehicleMob>> type) {
        super(props);
        this.type = type;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (!(ctx.getLevel() instanceof ServerLevel server)) {
            return InteractionResult.SUCCESS;
        }
        BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
        VehicleMob vehicle = type.get().create(server, EntitySpawnReason.SPAWN_ITEM_USE);
        if (vehicle == null) {
            return InteractionResult.FAIL;
        }
        vehicle.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, ctx.getRotation(), 0.0f);
        server.addFreshEntity(vehicle);
        ctx.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }
}
