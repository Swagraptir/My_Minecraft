package com.myminecraft.client;

import com.myminecraft.animal.AnimalSpec;
import com.myminecraft.animal.AnimalSpecs;
import com.myminecraft.registry.ModEntities;
import com.myminecraft.vehicle.VehicleMob;
import com.myminecraft.registry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

/** Client entrypoint: model layers and entity renderers. */
public class MyMinecraftClient implements ClientModInitializer {
    private static <T extends Mob> void register(EntityType<T> type, AnimalSpec spec) {
        EntityRendererRegistry.register(type, ctx -> new AnimalRenderer<T>(ctx, spec));
    }

    @Override
    public void onInitializeClient() {
        for (AnimalSpec spec : AnimalSpecs.ALL) {
            EntityModelLayerRegistry.registerModelLayer(ModModelLayers.layer(spec.name), () -> AnimalModel.createLayer(spec));
            register(ModEntities.ANIMALS.get(spec.name), spec);
        }

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.layer("bike"), () -> VehicleModel.createLayer(VehicleMob.Kind.BIKE));
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.layer("car"), () -> VehicleModel.createLayer(VehicleMob.Kind.CAR));
        EntityRendererRegistry.register(ModEntities.BIKE, ctx -> new VehicleRenderer(ctx, VehicleMob.Kind.BIKE));
        EntityRendererRegistry.register(ModEntities.CAR, ctx -> new VehicleRenderer(ctx, VehicleMob.Kind.CAR));

        EntityRendererRegistry.register(ModEntities.ICEOLOGER, IceologerRenderer::new);
        EntityRendererRegistry.register(ModEntities.FROST_GOLEM, SnowGolemRenderer::new);
        EntityRendererRegistry.register(ModEntities.ICE_SHARD, ThrownItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.KNIGHT, GuardRenderer::new);
        EntityRendererRegistry.register(ModEntities.ARCHER_GUARD, GuardRenderer::new);
        EntityRendererRegistry.register(ModEntities.SEAT, NoopRenderer::new);
        BlockRenderLayerMap.putBlock(ModBlocks.BUTTERCUP, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ModBlocks.UNDERWATER_TORCH, ChunkSectionLayer.CUTOUT);
    }
}
