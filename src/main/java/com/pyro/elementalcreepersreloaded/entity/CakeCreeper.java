package com.pyro.elementalcreepersreloaded.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Happy birthday! Leaves a cake with four torches around it. */
public class CakeCreeper extends ElementalCreeper {
    public CakeCreeper(EntityType<? extends CakeCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        BlockPos pos = this.blockPosition();
        BlockState cake = Blocks.CAKE.defaultBlockState();
        if (level.getBlockState(pos).canBeReplaced() && cake.canSurvive(level, pos))
            level.setBlockAndUpdate(pos, cake);
        BlockState torch = Blocks.TORCH.defaultBlockState();
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos spot = pos.relative(side);
            if (level.getBlockState(spot).canBeReplaced() && torch.canSurvive(level, spot))
                level.setBlockAndUpdate(spot, torch);
        }
    }
}
