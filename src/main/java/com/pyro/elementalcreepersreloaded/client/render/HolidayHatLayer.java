package com.pyro.elementalcreepersreloaded.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pyro.elementalcreepersreloaded.ECConfig;
import java.time.LocalDate;
import java.time.Month;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * What creepers wear on special days (special_events in the config): a carved pumpkin on Halloween, a sponge on
 * November 12, a snow block on Christmas. Added to the vanilla creeper too.
 */
public class HolidayHatLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
    private final ItemStackRenderState hat = new ItemStackRenderState();

    public HolidayHatLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    /** Today's hat, or empty on an ordinary day. */
    public static ItemStack todaysHat() {
        LocalDate today = LocalDate.now();
        if (today.getMonth() == Month.OCTOBER && today.getDayOfMonth() == 31) return new ItemStack(Items.CARVED_PUMPKIN);
        if (today.getMonth() == Month.NOVEMBER && today.getDayOfMonth() == 12) return new ItemStack(Items.SPONGE);
        if (today.getMonth() == Month.DECEMBER && today.getDayOfMonth() == 25) return new ItemStack(Items.SNOW_BLOCK);
        return ItemStack.EMPTY;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float yRot, float xRot) {
        if (state.isInvisible || !ECConfig.SPECIAL_EVENTS.get() || !state.headItem.isEmpty()) return;
        ItemStack hat = todaysHat();
        if (hat.isEmpty()) return;
        M model = this.getParentModel();
        if (!model.root().hasChild("head")) return;
        ModelPart head = model.root().getChild("head");
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getItemModelResolver().updateForTopItem(this.hat, hat, ItemDisplayContext.HEAD, minecraft.level, null, 0);
        poseStack.pushPose();
        model.root().translateAndRotate(poseStack);
        head.translateAndRotate(poseStack);
        CustomHeadLayer.translateToHead(poseStack, CustomHeadLayer.Transforms.DEFAULT);
        this.hat.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }
}
