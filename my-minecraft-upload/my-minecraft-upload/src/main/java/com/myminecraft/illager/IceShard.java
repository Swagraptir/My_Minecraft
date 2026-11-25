package com.myminecraft.illager;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** A chunk of ice thrown by the Iceologer: damages, freezes and slows whoever it hits. */
public class IceShard extends ThrowableItemProjectile {
    public IceShard(EntityType<? extends IceShard> type, Level level) {
        super(type, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.PACKED_ICE;
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        if (level() instanceof ServerLevel server && hit.getEntity() instanceof LivingEntity target) {
            if (getOwner() instanceof LivingEntity owner && owner.isAlliedTo(target)) {
                return;
            }
            target.hurtServer(server, server.damageSources().thrown(this, getOwner()), 5.0f);
            target.setTicksFrozen(Math.min(target.getTicksRequiredToFreeze() + 60, target.getTicksFrozen() + 140));
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 1), getOwner());
        }
    }

    @Override
    protected void onHit(HitResult hit) {
        super.onHit(hit);
        if (level() instanceof ServerLevel server) {
            server.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.PACKED_ICE)),
                    getX(), getY(), getZ(), 12, 0.15, 0.15, 0.15, 0.05);
            server.playSound(null, blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 0.7f, 1.4f);
            discard();
        }
    }
}
