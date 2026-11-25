package com.myminecraft.client;

import com.myminecraft.animal.AnimalSpec;
import com.myminecraft.animal.AnimalSpec.Layout;
import com.myminecraft.animal.AnimalSpec.Mane;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.ModelPart;

/**
 * One model for all animals, built from the numbers in {@link AnimalSpec}.
 * Model space: 16 px = 1 block, y grows DOWNWARD, ground is y = 24, front is -z.
 * The texture slots (texOffs) must stay identical to SLOT in tools/textures.py.
 */
public class AnimalModel extends EntityModel<AnimalRenderState> {
    private final AnimalSpec spec;
    private final ModelPart head, leg0, leg1, leg2, leg3, arm0, arm1, tail, wing0, wing1, orbit;
    private final ModelPart[] tents;

    public AnimalModel(ModelPart root, AnimalSpec spec) {
        super(root);
        this.spec = spec;
        this.head = root.getChild("head");
        this.leg0 = root.getChild("leg0");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.hasChild("leg2") ? root.getChild("leg2") : null;
        this.leg3 = root.hasChild("leg3") ? root.getChild("leg3") : null;
        this.arm0 = root.hasChild("arm0") ? root.getChild("arm0") : null;
        this.arm1 = root.hasChild("arm1") ? root.getChild("arm1") : null;
        this.tail = root.hasChild("tail") ? root.getChild("tail") : null;
        this.wing0 = root.hasChild("wing0") ? root.getChild("wing0") : null;
        this.wing1 = root.hasChild("wing1") ? root.getChild("wing1") : null;
        this.orbit = root.hasChild("orbit") ? root.getChild("orbit") : null;
        this.tents = new ModelPart[spec.tentacleN];
        for (int i = 0; i < tents.length; i++) {
            tents[i] = root.getChild("tent" + i);
        }
    }

    // ------------------------------------------------------------------------------------------- geometry
    public static LayerDefinition createLayer(AnimalSpec s) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        boolean quad = s.layout == Layout.QUAD;
        float bodyTop = 24 - s.legH - s.bh;

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 64)
                .addBox(-s.bw / 2f, 0, -s.bl / 2f, s.bw, s.bh, s.bl), PartPose.offset(0, bodyTop, 0));

        // ---- legs
        CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 128).addBox(-s.legT / 2f, 0, -s.legT / 2f, s.legT, s.legH, s.legT);
        float legY = 24 - s.legH;
        if (quad) {
            float lx = s.bw / 2f - s.legT / 2f;
            float lz = s.bl / 2f - s.legT / 2f;
            root.addOrReplaceChild("leg0", leg, PartPose.offset(-lx, legY, -lz));
            root.addOrReplaceChild("leg1", leg, PartPose.offset(lx, legY, -lz));
            root.addOrReplaceChild("leg2", leg, PartPose.offset(-lx, legY, lz));
            root.addOrReplaceChild("leg3", leg, PartPose.offset(lx, legY, lz));
        } else {
            float lx = Math.max(s.bw / 4f, s.legT / 2f + 0.5f);
            root.addOrReplaceChild("leg0", leg, PartPose.offset(-lx, legY, 0));
            root.addOrReplaceChild("leg1", leg, PartPose.offset(lx, legY, 0));
        }

        // ---- neck + head position
        float headY;
        float headZ;
        if (s.neckLen > 0) {
            root.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(128, 64)
                            .addBox(-s.neckT / 2f, -s.neckLen, -s.neckT / 2f, s.neckT, s.neckLen, s.neckT),
                    PartPose.offset(0, bodyTop + 1, -s.bl / 2f + s.neckT / 2f));
            headY = bodyTop + 1 - s.neckLen + s.hh * 0.3f;
            headZ = -s.bl / 2f + s.neckT / 2f + s.hl * 0.3f;
        } else if (quad) {
            headY = bodyTop + s.hh * 0.25f;
            headZ = -s.bl / 2f;
        } else {
            headY = bodyTop - s.hh * 0.1f;
            headZ = -s.bl / 2f + s.hl * 0.6f;
        }
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-s.hw / 2f, -s.hh / 2f, -s.hl, s.hw, s.hh, s.hl), PartPose.offset(0, headY, headZ));

        // eyes
        CubeListBuilder eye = CubeListBuilder.create().texOffs(248, 0).addBox(-0.5f, -0.5f, -0.6f, 1, 1, 1);
        float ex = Math.max(1.2f, s.hw / 4f);
        head.addOrReplaceChild("eye0", eye, PartPose.offset(-ex, -s.hh * 0.12f, -s.hl));
        head.addOrReplaceChild("eye1", eye, PartPose.offset(ex, -s.hh * 0.12f, -s.hl));
        // snout / beak
        if (s.snoutL > 0) {
            head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(0, 32)
                    .addBox(-s.snoutW / 2f, -s.snoutH / 2f + s.hh * 0.15f, -s.snoutL, s.snoutW, s.snoutH, s.snoutL),
                    PartPose.offset(0, 0, -s.hl));
        }
        // ears
        if (s.ear > 0) {
            CubeListBuilder ear = CubeListBuilder.create().texOffs(64, 0).addBox(-s.ear / 2f, -s.ear, -0.5f, s.ear, s.ear, 1);
            float exx = s.hw / 2f - s.ear / 2f;
            head.addOrReplaceChild("ear0", ear, PartPose.offset(-exx, -s.hh / 2f + 0.5f, -s.hl * 0.25f));
            head.addOrReplaceChild("ear1", ear, PartPose.offset(exx, -s.hh / 2f + 0.5f, -s.hl * 0.25f));
        }
        // horn(s), antlers, tusks, trunk, mane
        if (s.horn) {
            head.addOrReplaceChild("horn", CubeListBuilder.create().texOffs(96, 0).addBox(-1.5f, -6, -1.5f, 3, 6, 3),
                    PartPose.offset(0, -s.hh / 2f + 0.5f, -s.hl + (s.snoutL > 0 ? 1 : 2)));
        }
        if (s.horns) {
            CubeListBuilder horn = CubeListBuilder.create().texOffs(96, 0).addBox(-1, -4, -1, 2, 4, 2);
            head.addOrReplaceChild("horn0", horn, PartPose.offset(-s.hw / 2f + 1, -s.hh / 2f + 0.5f, -s.hl * 0.3f));
            head.addOrReplaceChild("horn1", horn, PartPose.offset(s.hw / 2f - 1, -s.hh / 2f + 0.5f, -s.hl * 0.3f));
        }
        if (s.antlers) {
            int wide = Math.max(3, s.hw / 2 + 2);
            head.addOrReplaceChild("antlerL", CubeListBuilder.create().texOffs(96, 24)
                    .addBox(-0.5f, -7, -0.5f, 1, 7, 1).addBox(-wide, -5, -0.5f, wide, 1, 1).addBox(-wide, -7, -0.5f, 1, 3, 1),
                    PartPose.offset(-s.hw / 2f + 0.5f, -s.hh / 2f + 0.5f, -s.hl * 0.4f));
            head.addOrReplaceChild("antlerR", CubeListBuilder.create().texOffs(96, 24)
                    .addBox(-0.5f, -7, -0.5f, 1, 7, 1).addBox(0, -5, -0.5f, wide, 1, 1).addBox(wide - 1, -7, -0.5f, 1, 3, 1),
                    PartPose.offset(s.hw / 2f - 0.5f, -s.hh / 2f + 0.5f, -s.hl * 0.4f));
        }
        if (s.tusks) {
            CubeListBuilder tusk = CubeListBuilder.create().texOffs(64, 24).addBox(-0.5f, 0, -0.5f, 1, 5, 1);
            float tx = Math.max(1.5f, s.hw / 2f - 2);
            head.addOrReplaceChild("tusk0", tusk, PartPose.offset(-tx, s.hh * 0.15f, -s.hl + 0.5f));
            head.addOrReplaceChild("tusk1", tusk, PartPose.offset(tx, s.hh * 0.15f, -s.hl + 0.5f));
        }
        if (s.trunk) {
            head.addOrReplaceChild("trunk", CubeListBuilder.create().texOffs(160, 96)
                    .addBox(-2.5f, 0, -2.5f, 5, s.hh + 4, 5), PartPose.offset(0, -s.hh * 0.1f, -s.hl - 2));
        }
        if (s.mane == Mane.RING) {
            head.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(64, 48)
                    .addBox(-(s.hw + 4) / 2f, -(s.hh + 4) / 2f, -1, s.hw + 4, s.hh + 4, 4), PartPose.offset(0, 0, -s.hl * 0.2f));
        } else if (s.mane == Mane.STRIP) {
            head.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(64, 48)
                    .addBox(-1, -s.hh / 2f - 2, -s.hl * 0.2f, 2, 3, s.hl * 0.9f), PartPose.ZERO);
            root.addOrReplaceChild("maneBody", CubeListBuilder.create().texOffs(64, 48)
                    .addBox(-1, -2, -s.bl / 2f, 2, 3, s.bl * 0.7f), PartPose.offset(0, bodyTop, 0));
        } else if (s.mane == Mane.SPIKES) {
            root.addOrReplaceChild("maneBody", CubeListBuilder.create().texOffs(64, 48)
                    .addBox(-s.bw / 2f - 0.5f, -3, -s.bl / 2f, s.bw + 1, 4, s.bl), PartPose.offset(0, bodyTop, 0));
        }
        if (s.hump) {
            root.addOrReplaceChild("hump", CubeListBuilder.create().texOffs(64, 48)
                    .addBox(-(s.bw - 2) / 2f, -4, -s.bl / 2f, s.bw - 2, 5, s.bl * 0.4f), PartPose.offset(0, bodyTop, 0));
        }

        // ---- tail
        if (s.tailLen > 0) {
            float ty = quad ? bodyTop + s.tailT : 24 - s.legH - s.tailT;
            root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(128, 96)
                    .addBox(-s.tailT / 2f, -s.tailT / 2f, 0, s.tailT, s.tailT, s.tailLen), PartPose.offset(0, ty, s.bl / 2f - 1));
        }
        // ---- arms (upright animals)
        if (s.armLen > 0) {
            CubeListBuilder arm = CubeListBuilder.create().texOffs(40, 128).addBox(-s.armT / 2f, 0, -s.armT / 2f, s.armT, s.armLen, s.armT);
            root.addOrReplaceChild("arm0", arm, PartPose.offset(-s.bw / 2f - s.armT / 2f, bodyTop + 2, 0));
            root.addOrReplaceChild("arm1", arm, PartPose.offset(s.bw / 2f + s.armT / 2f, bodyTop + 2, 0));
        }
        // ---- wings / flippers
        if (s.wingLen > 0) {
            int wd = Math.max(5, s.bl + 3);
            root.addOrReplaceChild("wing0", CubeListBuilder.create().texOffs(128, 128)
                    .addBox(-s.wingLen, 0, -wd / 2f, s.wingLen, 1, wd), PartPose.offset(-s.bw / 2f, bodyTop + 1, 0));
            root.addOrReplaceChild("wing1", CubeListBuilder.create().texOffs(128, 128)
                    .addBox(0, 0, -wd / 2f, s.wingLen, 1, wd), PartPose.offset(s.bw / 2f, bodyTop + 1, 0));
        }
        // ---- tentacles (barnacle): thin boxes waving around the top of the body
        for (int i = 0; i < s.tentacleN; i++) {
            double a = Math.PI * 2 * i / s.tentacleN;
            root.addOrReplaceChild("tent" + i, CubeListBuilder.create().texOffs(128, 96).addBox(-1, -s.tentacleLen, -1, 2, s.tentacleLen, 2),
                    PartPose.offset((float) Math.cos(a) * s.bw * 0.35f, bodyTop + 2, (float) Math.sin(a) * s.bl * 0.35f));
        }
        // ---- orbiting rods (wildfire): spin around the body
        if (s.orbitN > 0) {
            PartDefinition orbit = root.addOrReplaceChild("orbit", CubeListBuilder.create(), PartPose.offset(0, bodyTop + s.bh / 2f, 0));
            for (int i = 0; i < s.orbitN; i++) {
                double a = Math.PI * 2 * i / s.orbitN;
                orbit.addOrReplaceChild("rod" + i, CubeListBuilder.create().texOffs(64, 48).addBox(-1, -s.orbitSize, -1, 2, s.orbitSize * 2, 2),
                        PartPose.offset((float) Math.cos(a) * (s.bw + 4), 0, (float) Math.sin(a) * (s.bw + 4)));
            }
        }
        return LayerDefinition.create(mesh, 256, 256);
    }

    // ------------------------------------------------------------------------------------------- animation
    @Override
    public void setupAnim(AnimalRenderState state) {
        super.setupAnim(state);
        float pos = state.walkAnimationPos;
        float amp = Math.min(1.0f, state.walkAnimationSpeed) * 1.2f;
        float a = (float) Math.cos(pos * 0.6662f) * amp;
        float b = (float) Math.cos(pos * 0.6662f + Math.PI) * amp;

        head.yRot = state.yRot * ((float) Math.PI / 180f);
        head.xRot = state.xRot * ((float) Math.PI / 180f);

        if (spec.layout == Layout.QUAD) {
            leg0.xRot = a; leg3.xRot = a;
            leg1.xRot = b; leg2.xRot = b;
        } else {
            leg0.xRot = a;
            leg1.xRot = b;
            if (spec.flying && state.airborne) {
                leg0.xRot = 0.8f;
                leg1.xRot = 0.8f;
            }
        }
        if (arm0 != null) {
            arm0.xRot = b * 0.8f;
            arm1.xRot = a * 0.8f;
        }
        for (int i = 0; i < tents.length; i++) {
            tents[i].xRot = (float) Math.sin(state.ageInTicks * 0.08f + i) * 0.35f;
            tents[i].zRot = (float) Math.cos(state.ageInTicks * 0.07f + i * 1.3f) * 0.35f;
        }
        if (orbit != null) {
            orbit.yRot = state.ageInTicks * 0.12f;
        }
        if (tail != null) {
            tail.yRot = (float) Math.sin(state.ageInTicks * 0.12f) * 0.12f + a * 0.1f;
        }
        if (wing0 != null) {
            if (spec.flying && state.airborne) {
                float flap = (float) Math.sin(state.ageInTicks * 0.9f) * 0.9f;
                wing0.zRot = -flap;
                wing1.zRot = flap;
            } else {
                float fold = spec.flying ? 1.25f : 0.35f;
                float wobble = spec.flying ? 0 : (float) Math.sin(pos * 0.6662f) * amp * 0.5f;
                wing0.zRot = -(fold + wobble);
                wing1.zRot = fold + wobble;
            }
        }
    }
}
