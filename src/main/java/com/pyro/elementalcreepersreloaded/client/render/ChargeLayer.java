package com.pyro.elementalcreepersreloaded.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/** The charged creeper's swirl, drawn with a slightly inflated copy of the creeper's own model. */
public class ChargeLayer extends RenderLayer<ECCreeperRenderState, EntityModel<CreeperRenderState>> {
    private static final Identifier POWER_LOCATION = Identifier.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
    private final EntityModel<CreeperRenderState> model;

    public ChargeLayer(RenderLayerParent<ECCreeperRenderState, EntityModel<CreeperRenderState>> renderer, EntityModel<CreeperRenderState> model) {
        super(renderer);
        this.model = model;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, ECCreeperRenderState state, float yRot, float xRot) {
        if (!state.isPowered) return;
        float t = state.ageInTicks;
        submitNodeCollector.order(1).submitModel(this.model, state, poseStack,
                RenderTypes.energySwirl(POWER_LOCATION, t * 0.01F % 1.0F, t * 0.01F % 1.0F),
                lightCoords, OverlayTexture.NO_OVERLAY, -8355712, null, state.outlineColor, null);
    }
}
