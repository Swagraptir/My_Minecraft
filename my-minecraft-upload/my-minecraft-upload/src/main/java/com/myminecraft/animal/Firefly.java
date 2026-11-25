package com.myminecraft.animal;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Swamp insect from the 2019 biome vote (it was cut from the frogs update because frogs eat it). A tiny hovering
 * glow that drifts over swamps; it twinkles with light particles.
 */
public class Firefly extends GenericAnimal {
    public Firefly(EntityType<? extends GenericAnimal> type, Level level, AnimalSpec spec) {
        super(type, level, spec);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level() instanceof ServerLevel server && tickCount % 10 == 0) {
            server.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 0.1, getZ(), 1, 0.05, 0.05, 0.05, 0.0);
        }
    }
}
