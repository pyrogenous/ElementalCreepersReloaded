package com.pyro.elementalcreepersreloaded.client.render;

import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

/** A creeper's render state, plus the texture picked for this creeper (the Friendly Creeper's depends on taming). */
public class ECCreeperRenderState extends CreeperRenderState {
    public @Nullable Identifier texture;
}
