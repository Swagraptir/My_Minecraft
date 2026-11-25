package com.myminecraft.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class AnimalRenderState extends LivingEntityRenderState {
    /** True while the animal is off the ground (flapping wings for birds). */
    public boolean airborne;
}
