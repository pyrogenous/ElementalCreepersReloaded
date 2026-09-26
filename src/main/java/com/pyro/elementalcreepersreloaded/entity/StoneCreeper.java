package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

/** Crumbles the stone around it: stone, cobblestone and stone bricks break in a ball and drop as items. */
public class StoneCreeper extends ElementalCreeper {
    public StoneCreeper(EntityType<? extends StoneCreeper> type, Level level) {
        super(type, level);
    }

    public static boolean isStone(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(Tags.Blocks.STONES) || state.is(Tags.Blocks.COBBLESTONES)
                || state.is(BlockTags.STONE_BRICKS);
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        if (!griefing) return;
        int radius = this.radius(ECConfig.STONE_RADIUS, power);
        BlockPos center = this.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            if (pos.distSqr(center) <= radius * radius && isStone(level.getBlockState(pos)))
                level.destroyBlock(pos.immutable(), true, this);
        }
    }
}
