package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Wanted to be a chef. Bakes the players around it: a stone brick oven with lava at their feet and a barred window. */
public class FurnaceCreeper extends ElementalCreeper {
    public FurnaceCreeper(EntityType<? extends FurnaceCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        if (!griefing) return;
        for (Player player : this.nearby(Player.class, this.radius(ECConfig.FURNACE_RADIUS, power))) {
            if (!player.isSpectator() && !player.isCreative())
                this.buildOven(level, player);
        }
    }

    private void buildOven(ServerLevel level, Player player) {
        BlockState wall = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState window = Blocks.IRON_BARS.defaultBlockState();
        BlockState lava = Blocks.LAVA.defaultBlockState();
        BlockPos feet = player.blockPosition();
        for (int x = -1; x < 2; x++)
            for (int y = -1; y < 3; y++)
                for (int z = -1; z < 2; z++) {
                    BlockPos pos = feet.offset(x, y, z);
                    if (!level.getBlockState(pos).isAir()) continue;
                    if (x == -1 && z == 0 && y == 1)
                        level.setBlockAndUpdate(pos, window);
                    else if (x == 0 && z == 0 && y == 0)
                        level.setBlockAndUpdate(pos, lava);
                    else if (!(x == 0 && z == 0 && y == 1))
                        level.setBlockAndUpdate(pos, wall);
                }
    }
}
