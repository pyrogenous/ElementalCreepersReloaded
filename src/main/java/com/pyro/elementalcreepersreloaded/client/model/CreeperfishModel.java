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

/** Lomeli12's Creeperfish model (made in Tabula): a creeper with a puffed-up head and silverfish fuzz. Texture 80x48. */
public class CreeperfishModel extends EntityModel<CreeperRenderState> {
    private final ModelPart head;
    private final ModelPart frontRightFoot;
    private final ModelPart frontLeftFoot;
    private final ModelPart backRightFoot;
    private final ModelPart backLeftFoot;

    public CreeperfishModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.frontRightFoot = root.getChild("front_right_foot");
        this.frontLeftFoot = root.getChild("front_left_foot");
        this.backRightFoot = root.getChild("back_right_foot");
        this.backLeftFoot = root.getChild("back_left_foot");
    }

    public static LayerDefinition createBodyLayer(CubeDeformation g) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, g.extend(0.5F)),
                PartPose.offset(0.0F, 6.0F, 0.0F));
        root.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, g),
                PartPose.offset(0.0F, 6.0F, 0.0F));
        root.addOrReplaceChild("fuzz", CubeListBuilder.create().texOffs(40, 0).addBox(0.0F, 0.0F, 0.0F, 16.0F, 27.0F, 0.0F, g),
                PartPose.offset(-8.0F, -6.0F, 0.0F));
        CubeListBuilder foot = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, g);
        root.addOrReplaceChild("front_right_foot", foot, PartPose.offset(-2.0F, 18.0F, -4.0F));
        root.addOrReplaceChild("front_left_foot", foot, PartPose.offset(2.0F, 18.0F, -4.0F));
        root.addOrReplaceChild("back_left_foot", foot, PartPose.offset(2.0F, 18.0F, 4.0F));
        root.addOrReplaceChild("back_right_foot", foot, PartPose.offset(-2.0F, 18.0F, 4.0F));
        return LayerDefinition.create(mesh, 80, 48);
    }

    @Override
    public void setupAnim(CreeperRenderState state) {
        super.setupAnim(state);
        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
        float pos = state.walkAnimationPos;
        float speed = state.walkAnimationSpeed;
        this.frontLeftFoot.xRot = Mth.cos(pos * 0.6662F) * 1.4F * speed;
        this.frontRightFoot.xRot = Mth.cos(pos * 0.6662F + Mth.PI) * 1.4F * speed;
        this.backLeftFoot.xRot = Mth.cos(pos * 0.6662F + Mth.PI) * 1.4F * speed;
        this.backRightFoot.xRot = Mth.cos(pos * 0.6662F) * 1.4F * speed;
    }
}
