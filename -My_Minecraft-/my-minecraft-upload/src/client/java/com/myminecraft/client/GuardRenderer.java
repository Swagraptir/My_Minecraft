package com.myminecraft.client;

import com.myminecraft.Ids;
import com.myminecraft.guard.MedievalGuard;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.model.geom.ArmorModelSet;
import net.minecraft.resources.Identifier;

/** Knights and archer guards use a player-style skin plus the normal armour layer. */
public class GuardRenderer extends HumanoidMobRenderer<MedievalGuard, GuardRenderer.GuardState, HumanoidModel<GuardRenderer.GuardState>> {
    public static class GuardState extends HumanoidRenderState {
        public boolean archer;
    }

    private static final Identifier KNIGHT = Ids.of("textures/entity/knight.png");
    private static final Identifier ARCHER = Ids.of("textures/entity/archer_guard.png");

    public GuardRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this,
                ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, ctx.getModelSet(), HumanoidModel::new),
                ctx.getEquipmentRenderer()));
    }

    @Override
    public GuardState createRenderState() {
        return new GuardState();
    }

    @Override
    public void extractRenderState(MedievalGuard entity, GuardState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.archer = entity.role() == MedievalGuard.Role.ARCHER;
    }

    @Override
    public Identifier getTextureLocation(GuardState state) {
        return state.archer ? ARCHER : KNIGHT;
    }
}
