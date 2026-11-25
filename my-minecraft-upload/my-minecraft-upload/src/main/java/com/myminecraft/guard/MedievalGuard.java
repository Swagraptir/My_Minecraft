package com.myminecraft.guard;

import java.util.EnumSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.Nullable;

/**
 * Village defenders. KNIGHTs fight in melee with sword and shield, ARCHERs keep their distance and shoot arrows.
 * Both attack any hostile monster (and anything that hurts them); they never attack players unless hit first.
 * The role comes from the registered entity name because Mob's constructor runs registerGoals() before fields exist.
 */
public class MedievalGuard extends PathfinderMob implements RangedAttackMob {
    public enum Role { KNIGHT, ARCHER }

    private Role role;

    public MedievalGuard(EntityType<? extends MedievalGuard> type, Level level, Role role) {
        super(type, level);
        this.role = role;
    }

    public Role role() {
        if (role == null) {
            role = BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath().equals("archer_guard") ? Role.ARCHER : Role.KNIGHT;
        }
        return role;
    }

    public static AttributeSupplier.Builder createAttributes(Role role) {
        AttributeSupplier.Builder b = Monster.createMonsterAttributes();
        if (role == Role.KNIGHT) {
            return b.add(Attributes.MAX_HEALTH, 44.0).add(Attributes.MOVEMENT_SPEED, 0.32).add(Attributes.ATTACK_DAMAGE, 7.0)
                    .add(Attributes.KNOCKBACK_RESISTANCE, 0.4).add(Attributes.FOLLOW_RANGE, 32.0);
        }
        return b.add(Attributes.MAX_HEALTH, 26.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 36.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        if (role() == Role.KNIGHT) {
            this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true));
        } else {
            this.goalSelector.addGoal(2, new ArcherGoal(this));
        }
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true));
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason,
                                                  @Nullable SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        if (role() == Role.KNIGHT) {
            setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
            setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
            setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
            setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
        } else {
            setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
            setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            setDropChance(slot, 0.0f);
        }
        setPersistenceRequired();
        return result;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        ItemStack bow = getMainHandItem();
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this, new ItemStack(Items.ARROW), power, bow);
        double dx = target.getX() - getX();
        double dy = target.getY(0.3333333333333333) - arrow.getY();
        double dz = target.getZ() - getZ();
        double flat = Math.sqrt(dx * dx + dz * dz);
        arrow.shoot(dx, dy + flat * 0.2, dz, 1.6f, 14 - level().getDifficulty().getId() * 4);
        playSound(SoundEvents.SKELETON_SHOOT, 1.0f, 1.0f / (getRandom().nextFloat() * 0.4f + 0.8f));
        level().addFreshEntity(arrow);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    /** Keeps 8-20 blocks away from the target and fires every ~1.2 seconds while it can see it. */
    private static final class ArcherGoal extends Goal {
        private final MedievalGuard mob;
        private int cooldown = 20;

        ArcherGoal(MedievalGuard mob) {
            this.mob = mob;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity t = mob.getTarget();
            return t != null && t.isAlive() && mob.isHolding(Items.BOW);
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void tick() {
            LivingEntity t = mob.getTarget();
            if (t == null) return;
            double dist = mob.distanceTo(t);
            boolean sees = mob.getSensing().hasLineOfSight(t);
            mob.getLookControl().setLookAt(t, 30.0f, 30.0f);
            if (dist > 20.0 || !sees) {
                mob.getNavigation().moveTo(t, 1.0);
            } else if (dist < 8.0) {
                mob.getMoveControl().strafe(-0.5f, 0.0f);
            } else {
                mob.getNavigation().stop();
            }
            if (--cooldown <= 0 && sees && dist < 28.0) {
                mob.performRangedAttack(t, 1.0f);
                cooldown = 24;
            }
        }
    }
}
