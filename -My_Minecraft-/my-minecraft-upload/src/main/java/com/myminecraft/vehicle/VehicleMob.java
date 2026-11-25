package com.myminecraft.vehicle;

import com.myminecraft.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Bicycle and car. They are Mobs without AI that follow the rideable-mob pattern used by pigs and striders:
 * right-click to mount, WASD to drive (the car steers where you look), sneak + right-click to pick it up again.
 */
public class VehicleMob extends Mob {
    public enum Kind { BIKE, CAR }

    private final Kind kind;

    public VehicleMob(EntityType<? extends VehicleMob> type, Level level, Kind kind) {
        super(type, level);
        this.kind = kind;
        setPersistenceRequired();
    }

    public Kind kind() {
        return kind;
    }

    public static AttributeSupplier.Builder createAttributes(Kind kind) {
        AttributeSupplier.Builder b = Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0);
        return kind == Kind.CAR
                ? b.add(Attributes.MOVEMENT_SPEED, 0.34).add(Attributes.STEP_HEIGHT, 1.0)
                : b.add(Attributes.MOVEMENT_SPEED, 0.22).add(Attributes.STEP_HEIGHT, 0.6);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            if (level() instanceof ServerLevel server) {
                ejectPassengers();
                spawnAtLocation(server, new ItemStack(kind == Kind.CAR ? ModItems.CAR : ModItems.BIKE));
                discard();
            }
            return InteractionResult.SUCCESS;
        }
        if (!level().isClientSide()) {
            player.startRiding(this);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengers().size() < (kind == Kind.CAR ? 2 : 1);
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        return getFirstPassenger() instanceof Player p ? p : null;
    }

    @Override
    protected void tickRidden(Player rider, Vec3 input) {
        super.tickRidden(rider, input);
        setRot(rider.getYRot(), rider.getXRot() * 0.4f);
        this.yRotO = this.yBodyRot = this.yHeadRot = getYRot();
    }

    @Override
    protected Vec3 getRiddenInput(Player rider, Vec3 input) {
        float forward = rider.zza;
        if (forward < 0) forward *= 0.4f;
        float strafe = kind == Kind.BIKE ? rider.xxa * 0.5f : 0.0f;
        return new Vec3(strafe, 0.0, forward);
    }

    @Override
    protected float getRiddenSpeed(Player rider) {
        float speed = (float) getAttributeValue(Attributes.MOVEMENT_SPEED);
        return rider.isSprinting() ? speed * 1.35f : speed;
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
        return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }
}
