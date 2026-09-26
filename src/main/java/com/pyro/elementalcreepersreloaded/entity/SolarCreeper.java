package com.pyro.elementalcreepersreloaded.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.core.Direction;

/**
 * Charges up in the sun: the more sky light it stands in, the bigger the boom (up to 8 blocks, 16 charged), and at full
 * power it sets the ground on fire. Weaker at night and in rain.
 */
public class SolarCreeper extends ElementalCreeper {
    public SolarCreeper(EntityType<? extends SolarCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        int size = 3;
        BlockPos above = this.blockPosition().above(3);
        if (level.isBrightOutside()) {
            if (level.canSeeSky(above)) {
                int light = Math.clamp(level.getBrightness(LightLayer.SKY, above) - level.getSkyDarken(), 0, 15);
                size = 3 + light / 3;
            }
        } else {
            size--;
        }
        if (level.isRaining() || level.isThundering())
            size--;
        int radius = Math.max(1, size) * power;
        level.explode(this, this.getX(), this.getY(), this.getZ(), radius,
                griefing ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
        if (griefing && size >= 8) {
            BlockPos center = this.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
                if (isGroundSpot(level, pos) && this.random.nextBoolean() && BaseFireBlock.canBePlacedAt(level, pos, Direction.UP))
                    level.setBlock(pos, BaseFireBlock.getState(level, pos), 11);
            }
        }
    }
}
