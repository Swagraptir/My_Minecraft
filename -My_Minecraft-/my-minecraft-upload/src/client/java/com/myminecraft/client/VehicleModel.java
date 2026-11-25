package com.myminecraft.client;

import com.myminecraft.vehicle.VehicleMob;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/** Box models for the bicycle and the car. Wheels spin with the walk animation. */
public class VehicleModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart[] wheels;
    private final ModelPart steering;

    public VehicleModel(ModelPart root, VehicleMob.Kind kind) {
        super(root);
        if (kind == VehicleMob.Kind.CAR) {
            wheels = new ModelPart[]{root.getChild("w0"), root.getChild("w1"), root.getChild("w2"), root.getChild("w3")};
            steering = null;
        } else {
            wheels = new ModelPart[]{root.getChild("w0"), root.getChild("w1")};
            steering = root.getChild("bars");
        }
    }

    public static LayerDefinition createLayer(VehicleMob.Kind kind) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        if (kind == VehicleMob.Kind.CAR) {
            root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5, -2, -10, 10, 4, 20), PartPose.offset(0, 17, 0));
            root.addOrReplaceChild("cabin", CubeListBuilder.create().texOffs(0, 22).addBox(-4, -4, -4, 8, 4, 10), PartPose.offset(0, 15, 1));
            root.addOrReplaceChild("hood", CubeListBuilder.create().texOffs(0, 0).addBox(-4, -1, -4, 8, 1, 5), PartPose.offset(0, 15, -8));
            CubeListBuilder wheel = CubeListBuilder.create().texOffs(0, 40).addBox(-1, -3, -3, 2, 6, 6);
            root.addOrReplaceChild("w0", wheel, PartPose.offset(-5.5f, 21, -6.5f));
            root.addOrReplaceChild("w1", wheel, PartPose.offset(5.5f, 21, -6.5f));
            root.addOrReplaceChild("w2", wheel, PartPose.offset(-5.5f, 21, 6.5f));
            root.addOrReplaceChild("w3", wheel, PartPose.offset(5.5f, 21, 6.5f));
        } else {
            CubeListBuilder wheel = CubeListBuilder.create().texOffs(0, 40).addBox(-0.5f, -4, -4, 1, 8, 8);
            root.addOrReplaceChild("w0", wheel, PartPose.offset(0, 20, -7));
            root.addOrReplaceChild("w1", wheel, PartPose.offset(0, 20, 7));
            root.addOrReplaceChild("frame", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -0.5f, -7, 1, 1, 14), PartPose.offset(0, 14, 0));
            root.addOrReplaceChild("tube", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0, -0.5f, 1, 7, 1), PartPose.offset(0, 14, 5));
            root.addOrReplaceChild("seat", CubeListBuilder.create().texOffs(0, 22).addBox(-2, -1, -3, 4, 1, 6), PartPose.offset(0, 13, 5));
            root.addOrReplaceChild("fork", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, 0, -0.5f, 1, 10, 1), PartPose.offset(0, 11, -7));
            root.addOrReplaceChild("bars", CubeListBuilder.create().texOffs(0, 22).addBox(-4, -1, -0.5f, 8, 1, 1), PartPose.offset(0, 11, -7));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        super.setupAnim(state);
        float spin = state.walkAnimationPos * 1.6f;
        for (ModelPart w : wheels) {
            w.xRot = spin;
        }
        if (steering != null) {
            steering.yRot = state.yRot * ((float) Math.PI / 180f) * 0.3f;
        }
    }
}
