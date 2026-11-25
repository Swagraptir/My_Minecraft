package com.myminecraft.animal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * 2018-era mob-vote loser, built the way it was shown: a floating fire spirit ringed by orbiting fire rods and crowned
 * with horns. It hovers above you and throws fan-shaped volleys of fireballs; every few volleys it sets a ring of fire
 * around you. The "Warlord" (boss) version guards the nether castle: 300 HP, boss bar and summons blazes.
 */
public class Wildfire extends GenericMonster {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.myminecraft.wildfire_boss"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_12);
    private boolean boss;

    public Wildfire(EntityType<? extends GenericMonster> type, Level level, AnimalSpec spec) {
        super(type, level, spec);
        this.xpReward = 20;
    }

    public void setBoss(boolean boss) {
        this.boss = boss;
        if (boss) {
            getAttribute(Attributes.MAX_HEALTH).setBaseValue(300.0);
            getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.8);
            setHealth(getMaxHealth());
            setPersistenceRequired();
            setCustomName(Component.translatable("entity.myminecraft.wildfire_boss"));
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new VolleyGoal());
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level() instanceof ServerLevel server && tickCount % 6 == 0) {
            server.sendParticles(ParticleTypes.FLAME, getX(), getY() + 1.0, getZ(), 4, 0.4, 0.5, 0.4, 0.01);
        }
    }

    private void volley(LivingEntity target, int ring) {
        if (!(level() instanceof ServerLevel server)) return;
        Vec3 from = position().add(0, getEyeHeight() * 0.8, 0);
        int shots = boss ? 5 : 3;
        for (int i = 0; i < shots; i++) {
            Vec3 dir = new Vec3(target.getX() - from.x, target.getEyeY() - from.y, target.getZ() - from.z).normalize();
            double spread = (i - (shots - 1) / 2.0) * 0.18;
            dir = new Vec3(dir.x + dir.z * spread, dir.y, dir.z - dir.x * spread).normalize().scale(0.9);
            SmallFireball ball = new SmallFireball(server, this, dir);
            ball.setPos(from.x, from.y, from.z);
            server.addFreshEntity(ball);
        }
        server.playSound(null, blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.0f, 0.8f);
        if (ring % 4 == 3) {
            BlockPos c = target.blockPosition();
            for (int a = 0; a < 16; a++) {
                double ang = Math.PI * 2 * a / 16;
                BlockPos p = c.offset((int) Math.round(Math.cos(ang) * 3), 0, (int) Math.round(Math.sin(ang) * 3));
                if (server.getBlockState(p).isAir() && server.getBlockState(p.below()).isSolid()) {
                    server.setBlockAndUpdate(p, Blocks.FIRE.defaultBlockState());
                }
            }
            server.sendParticles(ParticleTypes.LAVA, c.getX() + 0.5, c.getY() + 0.5, c.getZ() + 0.5, 20, 2.5, 0.2, 2.5, 0.0);
            if (boss && server.getEntities(EntityType.BLAZE, getBoundingBox().inflate(24), e -> true).size() < 4) {
                for (int i = 0; i < 2; i++) {
                    var b = EntityType.BLAZE.create(server, EntitySpawnReason.MOB_SUMMONED);
                    if (b != null) {
                        b.snapTo(getX() + random.nextInt(5) - 2, getY(), getZ() + random.nextInt(5) - 2, 0, 0);
                        b.setTarget(target);
                        server.addFreshEntity(b);
                    }
                }
            }
        }
    }

    private final class VolleyGoal extends Goal {
        private int cooldown = 30;
        private int volleys;

        VolleyGoal() {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity t = getTarget();
            return t != null && t.isAlive();
        }

        @Override
        public void tick() {
            LivingEntity t = getTarget();
            if (t == null) return;
            getLookControl().setLookAt(t, 30.0f, 30.0f);
            double dist = distanceTo(t);
            if (dist > 16 || !getSensing().hasLineOfSight(t)) {
                getMoveControl().setWantedPosition(t.getX(), t.getY() + 3.0, t.getZ(), 1.0);
            } else if (dist < 7) {
                getMoveControl().setWantedPosition(getX() + (getX() - t.getX()), t.getY() + 4.0, getZ() + (getZ() - t.getZ()), 1.0);
            }
            if (--cooldown <= 0 && getSensing().hasLineOfSight(t) && dist < 24) {
                volley(t, volleys++);
                cooldown = boss ? 28 : 45;
            }
        }
    }

    // ------------------------------------------------------------------------------------- boss bar & persistence
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (boss) bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (boss) bossEvent.setProgress(getHealth() / getMaxHealth());
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Boss", boss);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        if (input.getBooleanOr("Boss", false)) {
            float saved = getHealth();
            setBoss(true);
            setHealth(Math.min(saved, getMaxHealth()));
        }
    }
}
