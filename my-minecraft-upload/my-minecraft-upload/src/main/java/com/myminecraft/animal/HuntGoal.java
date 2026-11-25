package com.myminecraft.animal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

/**
 * Predator target search that only scans for prey now and then (about once every 1.5 s) and never as a baby.
 * The plain NearestAttackableTargetGoal scans the neighbourhood constantly, which adds up with dozens of predators.
 */
public class HuntGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private final Mob hunter;

    public HuntGoal(Mob hunter, Class<T> prey) {
        super(hunter, prey, true);
        this.hunter = hunter;
    }

    @Override
    public boolean canUse() {
        return hunter.getRandom().nextInt(30) == 0 && !hunter.isBaby() && super.canUse();
    }
}
