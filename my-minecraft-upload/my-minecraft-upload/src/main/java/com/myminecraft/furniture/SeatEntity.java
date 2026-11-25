package com.myminecraft.furniture;

import com.myminecraft.registry.ModEntities;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

/** Invisible, non-saved entity the player rides to "sit" on a chair. Disappears when nobody is riding it. */
public class SeatEntity extends Entity {
    public SeatEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static boolean sit(Level level, BlockPos pos, Player player, Direction facing, double height) {
        List<SeatEntity> existing = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
        if (!existing.isEmpty()) {
            return false; // somebody is already sitting here
        }
        if (!(level instanceof ServerLevel server)) return false;
        SeatEntity seat = ModEntities.SEAT.create(server, EntitySpawnReason.TRIGGERED);
        if (seat == null) return false;
        seat.setPos(pos.getX() + 0.5, pos.getY() + height - 0.35, pos.getZ() + 0.5);
        seat.setYRot(facing.toYRot());
        server.addFreshEntity(seat);
        player.startRiding(seat, true, true);
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && getPassengers().isEmpty()) {
            discard();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(ValueInput input) {}

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {}

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }
}
