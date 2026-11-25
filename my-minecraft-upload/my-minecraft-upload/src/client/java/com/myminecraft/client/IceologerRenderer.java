package com.myminecraft.client;

import com.myminecraft.Ids;
import com.myminecraft.illager.Iceologer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EvokerRenderer;
import net.minecraft.client.renderer.entity.state.EvokerRenderState;
import net.minecraft.resources.Identifier;

/** Evoker model and animations with the icy Iceologer skin. */
public class IceologerRenderer extends EvokerRenderer<Iceologer> {
    private static final Identifier TEXTURE = Ids.of("textures/entity/iceologer.png");

    public IceologerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTextureLocation(EvokerRenderState state) {
        return TEXTURE;
    }
}
