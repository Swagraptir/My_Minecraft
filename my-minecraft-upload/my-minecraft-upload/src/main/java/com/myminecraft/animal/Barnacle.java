package com.myminecraft.animal;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Ocean mob from the early mob votes: a shell-covered creature fixed to the sea floor (or a monument wall) whose long
 * tentacles reel in anything that swims close and squeeze it. It never moves, dies slowly out of water, and also lives
 * inside ocean monuments (see MonumentBarnacles).
 */
public class Barnacle extends GenericMonster {
    private int squeeze;

    public Barnacle(EntityType<? extends GenericMonster> type, Level level, AnimalSpec spec) {
        super(type, level, spec);
    }

    public static AttributeSupplier.Builder createAttributes(AnimalSpec s) {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, s.health)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ATTACK_DAMAGE, s.damage)
                .add(Attributes.FOLLOW_RANGE, 10.0);
    }

    @Override
    protected void registerGoals() {
        // intentionally none: the Barnacle never walks
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!(level() instanceof ServerLevel server)) return;
        setDeltaMovement(0, Math.min(0, getDeltaMovement().y), 0);
        if (!isInWater() && tickCount % 40 == 0) {
            hurtServer(server, server.damageSources().dryOut(), 1.0f);
        }
        if (squeeze > 0) squeeze--;
        if (tickCount % 4 != 0) return;                       // the grab is evaluated 5 times a second
        for (Player p : server.getEntitiesOfClass(Player.class, getBoundingBox().inflate(6.0, 4.0, 6.0))) {
            if (p.isSpectator() || p.isCreative() || !p.isInWater()) continue;
            Vec3 pull = position().add(0, 0.6, 0).subtract(p.position());
            double dist = pull.length();
            if (dist > 1.6) {
                p.setDeltaMovement(p.getDeltaMovement().add(pull.normalize().scale(0.22)));
                p.hurtMarked = true;
            }
            if (dist < 2.2 && squeeze <= 0) {
                squeeze = 6;
                p.hurtServer(server, server.damageSources().mobAttack(this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
                server.sendParticles(ParticleTypes.BUBBLE, p.getX(), p.getY() + 1.0, p.getZ(), 10, 0.3, 0.4, 0.3, 0.05);
            }
        }
    }
}
