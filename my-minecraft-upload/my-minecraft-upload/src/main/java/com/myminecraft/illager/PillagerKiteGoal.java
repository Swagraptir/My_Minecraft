package com.myminecraft.illager;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

/** Smarter pillagers: when you get close they back off and keep shooting instead of standing still. */
public class PillagerKiteGoal extends Goal {
    private final Pillager mob;
    private int cooldown;

    public PillagerKiteGoal(Pillager mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = mob.getTarget();
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        return t != null && t.isAlive() && mob.isHolding(Items.CROSSBOW) && mob.distanceToSqr(t) < 49.0;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity t = mob.getTarget();
        return t != null && t.isAlive() && !mob.getNavigation().isDone() && mob.distanceToSqr(t) < 121.0;
    }

    @Override
    public void start() {
        LivingEntity t = mob.getTarget();
        if (t == null) return;
        Vec3 away = DefaultRandomPos.getPosAway(mob, 10, 4, t.position());
        if (away != null) {
            mob.getNavigation().moveTo(away.x, away.y, away.z, 1.2);
        }
        cooldown = 60;
    }
}
