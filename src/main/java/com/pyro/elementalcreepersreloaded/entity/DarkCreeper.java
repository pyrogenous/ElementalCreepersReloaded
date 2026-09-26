package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Hates light: burns in the sun (the minecraft:burn_in_daylight tag), and its explosion breaks every light source around it (they drop as items). */
public class DarkCreeper extends ElementalCreeper {
    public DarkCreeper(EntityType<? extends DarkCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        if (!griefing) return;
        int radius = this.radius(ECConfig.DARK_RADIUS, power);
        BlockPos center = this.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            BlockState state = level.getBlockState(pos);
            if (state.getLightEmission(level, pos) > 0 && state.getFluidState().isEmpty() && state.getDestroySpeed(level, pos) >= 0)
                level.destroyBlock(pos.immutable(), true, this);
        }
    }
}
