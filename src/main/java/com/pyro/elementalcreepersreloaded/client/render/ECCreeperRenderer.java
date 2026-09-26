package com.pyro.elementalcreepersreloaded.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pyro.elementalcreepersreloaded.entity.SwellingCreeper;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import org.jspecify.annotations.Nullable;

/**
 * Renders every creeper of the mod: the model and texture it's given, the vanilla swelling and white flashing, the
 * charged swirl, a hat on special days, and a see-through body for ghosts.
 */
public class ECCreeperRenderer<T extends Mob & SwellingCreeper> extends MobRenderer<T, ECCreeperRenderState, EntityModel<CreeperRenderState>> {
    private final Function<T, Identifier> texture;
    private final float size;
    private final boolean ghost;

    public ECCreeperRenderer(EntityRendererProvider.Context context, EntityModel<CreeperRenderState> model, EntityModel<CreeperRenderState> powerModel,
                             Function<T, Identifier> texture, float size, boolean ghost) {
        super(context, model, 0.5F * size);
        this.texture = texture;
        this.size = size;
        this.ghost = ghost;
        this.addLayer(new ChargeLayer(this, powerModel));
        this.addLayer(new HolidayHatLayer<>(this));
    }

    @Override
    protected void scale(ECCreeperRenderState state, PoseStack poseStack) {
        float g = state.swelling;
        float wobble = 1.0F + Mth.sin(g * 100.0F) * g * 0.01F;
        g = Mth.clamp(g, 0.0F, 1.0F);
        g *= g;
        g *= g;
        float s = (1.0F + g * 0.4F) * wobble * this.size;
        float hs = (1.0F + g * 0.1F) / wobble * this.size;
        if (state.isBaby) {
            s *= 0.5F;
            hs *= 0.5F;
        }
        poseStack.scale(s, hs, s);
    }

    @Override
    protected float getWhiteOverlayProgress(ECCreeperRenderState state) {
        float step = state.swelling;
        return (int) (step * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(step, 0.5F, 1.0F);
    }

    @Override
    public Identifier getTextureLocation(ECCreeperRenderState state) {
        return state.texture;
    }

    @Override
    protected @Nullable RenderType getRenderType(ECCreeperRenderState state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing) {
        if (this.ghost && isBodyVisible)
            return RenderTypes.entityTranslucent(this.getTextureLocation(state));
        return super.getRenderType(state, isBodyVisible, forceTransparent, appearGlowing);
    }

    @Override
    protected int getModelTint(ECCreeperRenderState state) {
        // Ghosts: 30% opaque
        return this.ghost ? 0x4DFFFFFF : super.getModelTint(state);
    }

    @Override
    public ECCreeperRenderState createRenderState() {
        return new ECCreeperRenderState();
    }

    @Override
    public void extractRenderState(T entity, ECCreeperRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.swelling = entity.getSwelling(partialTicks);
        state.isPowered = entity.isPowered();
        state.texture = this.texture.apply(entity);
    }
}
