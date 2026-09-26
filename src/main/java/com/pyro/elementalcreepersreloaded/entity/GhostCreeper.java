package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * The ghost of a creeper killed by a player (ghost_creeper_chance). Translucent, and its explosion hurts but leaves the
 * blocks alone.
 */
public class GhostCreeper extends ElementalCreeper {
    public GhostCreeper(EntityType<? extends GhostCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        level.explode(this, this.getX(), this.getY(), this.getZ(), ECConfig.GHOST_RADIUS.get() * power, Level.ExplosionInteraction.NONE);
    }
}
