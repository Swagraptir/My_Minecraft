package com.myminecraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.myminecraft.Ids;
import com.myminecraft.animal.AnimalSpec;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class AnimalRenderer<T extends Mob> extends MobRenderer<T, AnimalRenderState, AnimalModel> {
    private final Identifier texture;

    public AnimalRenderer(EntityRendererProvider.Context ctx, AnimalSpec spec) {
        super(ctx, new AnimalModel(ctx.bakeLayer(ModModelLayers.layer(spec.name)), spec), 0.3f + spec.bw / 32f);
        this.texture = Ids.of("textures/entity/" + spec.name + ".png");
    }

    @Override
    public AnimalRenderState createRenderState() {
        return new AnimalRenderState();
    }

    @Override
    public void extractRenderState(T entity, AnimalRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.airborne = !entity.onGround();
    }

    @Override
    public Identifier getTextureLocation(AnimalRenderState state) {
        return texture;
    }

    @Override
    protected void scale(AnimalRenderState state, PoseStack poseStack) {
        if (state.isBaby) {
            poseStack.scale(0.55f, 0.55f, 0.55f);
        }
    }
}
