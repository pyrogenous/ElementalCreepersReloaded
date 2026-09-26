package com.pyro.elementalcreepersreloaded.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.util.Mth;

/** The creeper model with a third pair of legs on its sides. Uses the creeper texture layout (64x32). */
public class SpiderCreeperModel extends EntityModel<CreeperRenderState> {
    private final ModelPart head;
    private final ModelPart[] legs = new ModelPart[6];

    public SpiderCreeperModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        for (int i = 0; i < 6; i++)
            this.legs[i] = root.getChild("leg" + i);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation g) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, g),
                PartPose.offset(0.0F, 6.0F, 0.0F));
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, g),
                PartPose.offset(0.0F, 6.0F, 0.0F));
        CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, g);
        float[][] offsets = {{-2.0F, 4.0F}, {2.0F, 4.0F}, {-2.0F, -4.0F}, {2.0F, -4.0F}, {-6.0F, 0.0F}, {6.0F, 0.0F}};
        for (int i = 0; i < 6; i++)
            root.addOrReplaceChild("leg" + i, leg, PartPose.offset(offsets[i][0], 18.0F, offsets[i][1]));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(CreeperRenderState state) {
        super.setupAnim(state);
        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
        float pos = state.walkAnimationPos;
        float speed = state.walkAnimationSpeed;
        // Diagonal pairs move together, like the creeper's four legs; the side legs follow the front pair
        float[] phases = {0.0F, Mth.PI, Mth.PI, 0.0F, 0.0F, Mth.PI};
        for (int i = 0; i < 6; i++)
            this.legs[i].xRot = Mth.cos(pos * 0.6662F + phases[i]) * 1.4F * speed;
    }
}
