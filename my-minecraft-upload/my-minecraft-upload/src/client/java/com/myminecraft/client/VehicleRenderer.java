package com.myminecraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.myminecraft.Ids;
import com.myminecraft.vehicle.VehicleMob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class VehicleRenderer extends MobRenderer<VehicleMob, LivingEntityRenderState, VehicleModel> {
    private final Identifier texture;
    private final float scale;

    public VehicleRenderer(EntityRendererProvider.Context ctx, VehicleMob.Kind kind) {
        super(ctx, new VehicleModel(ctx.bakeLayer(ModModelLayers.layer(kind == VehicleMob.Kind.CAR ? "car" : "bike")), kind),
                kind == VehicleMob.Kind.CAR ? 0.9f : 0.4f);
        this.texture = Ids.of(kind == VehicleMob.Kind.CAR ? "textures/entity/car.png" : "textures/entity/bike.png");
        this.scale = kind == VehicleMob.Kind.CAR ? 1.5f : 1.1f;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return texture;
    }

    @Override
    protected void scale(LivingEntityRenderState state, PoseStack poseStack) {
        poseStack.scale(scale, scale, scale);
    }
}
