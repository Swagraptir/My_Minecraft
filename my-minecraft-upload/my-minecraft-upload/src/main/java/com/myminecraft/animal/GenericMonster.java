package com.myminecraft.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/** Hostile counterpart of {@link GenericAnimal}: same spec-driven model, Monster AI. Base of Wildfire and Barnacle. */
public class GenericMonster extends Monster {
    private AnimalSpec spec; // no initializer: Mob's constructor calls registerGoals() first

    public GenericMonster(EntityType<? extends GenericMonster> type, Level level, AnimalSpec spec) {
        super(type, level);
        this.spec = spec;
    }

    public AnimalSpec spec() {
        if (spec == null) {
            spec = AnimalSpec.byName(BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath());
        }
        return spec;
    }

    public static AttributeSupplier.Builder createAttributes(AnimalSpec s) {
        AttributeSupplier.Builder b = Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, s.health)
                .add(Attributes.MOVEMENT_SPEED, s.speed)
                .add(Attributes.ATTACK_DAMAGE, Math.max(1.0, s.damage))
                .add(Attributes.FOLLOW_RANGE, 32.0);
        if (s.flying) {
            b.add(Attributes.FLYING_SPEED, Math.max(0.1, s.speed * 2.0));
        }
        return b;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        if (spec().flying) {
            this.moveControl = new FlyingMoveControl(this, 10, true);
            FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
            nav.setCanOpenDoors(false);
            nav.setCanFloat(true);
            return nav;
        }
        return super.createNavigation(level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1, true));
        this.goalSelector.addGoal(5, spec().flying ? new WaterAvoidingRandomFlyingGoal(this, 1.0)
                : new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return spec().ambient;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return spec().hurt;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return spec().death;
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float multiplier, DamageSource source) {
        return !spec().flying && super.causeFallDamage(fallDistance, multiplier, source);
    }

    @Override
    protected void checkFallDamage(double heightDiff, boolean onGround, BlockState state, BlockPos pos) {
        if (!spec().flying) {
            super.checkFallDamage(heightDiff, onGround, state, pos);
        }
    }
}
