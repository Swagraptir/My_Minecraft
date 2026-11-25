package com.myminecraft.animal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

/**
 * 2021 mob-vote loser: lives in lush caves, flies to spots dark enough for monsters to spawn and glows there to warn
 * you. Feed it glow berries.
 */
public class Glare extends GenericAnimal {
    public Glare(EntityType<? extends GenericAnimal> type, Level level, AnimalSpec spec) {
        super(type, level, spec);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new FindDarkSpotGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.GLOW_BERRIES);
    }

    private static final class FindDarkSpotGoal extends Goal {
        private final Glare mob;
        private BlockPos target;
        private int cooldown = 60;

        FindDarkSpotGoal(Glare mob) {
            this.mob = mob;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (--cooldown > 0) return false;
            cooldown = 80 + mob.getRandom().nextInt(80);
            target = search();
            return target != null;
        }

        private BlockPos search() {
            Level level = mob.level();
            for (int i = 0; i < 24; i++) {
                BlockPos p = mob.blockPosition().offset(mob.getRandom().nextInt(33) - 16, mob.getRandom().nextInt(13) - 6,
                        mob.getRandom().nextInt(33) - 16);
                if (level.getBrightness(LightLayer.BLOCK, p) == 0 && level.getBrightness(LightLayer.SKY, p) == 0
                        && level.getBlockState(p).isAir() && level.getBlockState(p.below()).isSolid()) {
                    return p;
                }
            }
            return null;
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && !mob.getNavigation().isDone();
        }

        @Override
        public void start() {
            mob.getNavigation().moveTo(target.getX() + 0.5, target.getY() + 1.5, target.getZ() + 0.5, 1.0);
        }

        @Override
        public void stop() {
            if (target != null && mob.distanceToSqr(target.getX() + 0.5, target.getY() + 1.5, target.getZ() + 0.5) < 16
                    && mob.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.END_ROD, mob.getX(), mob.getY() + 0.5, mob.getZ(), 16, 0.4, 0.4, 0.4, 0.02);
                server.playSound(null, mob.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.NEUTRAL, 1.0f, 1.2f);
                mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 160, 0, false, false));
            }
            target = null;
        }
    }
}
