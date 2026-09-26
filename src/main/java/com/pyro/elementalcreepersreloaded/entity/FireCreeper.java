package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;

/** Sets the ground around it on fire. Without mob griefing, it sets the mobs around it on fire instead. */
public class FireCreeper extends ElementalCreeper {
    public FireCreeper(EntityType<? extends FireCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        int radius = this.radius(ECConfig.FIRE_RADIUS, power);
        if (!griefing) {
            for (LivingEntity entity : this.nearby(LivingEntity.class, radius))
                entity.igniteForSeconds(25.0F);
            return;
        }
        BlockPos center = this.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            if (isGroundSpot(level, pos) && this.random.nextBoolean() && BaseFireBlock.canBePlacedAt(level, pos, net.minecraft.core.Direction.UP))
                level.setBlock(pos, BaseFireBlock.getState(level, pos), 11);
        }
    }
}
