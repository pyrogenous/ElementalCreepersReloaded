package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Flings everything around it into the air with telekinesis. Silent, and it breaks nothing. */
public class PsychicCreeper extends ElementalCreeper {
    public PsychicCreeper(EntityType<? extends PsychicCreeper> type, Level level) {
        super(type, level);
        this.explosionSound = false;
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        Knockback.blast(level, this, ECConfig.PSYCHIC_RADIUS.get() * power, 1.0, ECConfig.PSYCHIC_POWER.get(), false);
    }
}
