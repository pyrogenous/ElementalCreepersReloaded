package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Turns the world upside down: every block in a ball around it is mirrored top to bottom. Blocks with contents
 * (chests, furnaces, ...) and unbreakable ones stay where they are, so nothing is lost or duplicated.
 */
public class ReverseCreeper extends ElementalCreeper {
    public ReverseCreeper(EntityType<? extends ReverseCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        if (!griefing) return;
        int radius = this.isPowered() ? (int) (ECConfig.REVERSE_RADIUS.get() * 1.5F) : ECConfig.REVERSE_RADIUS.get();
        BlockPos center = this.blockPosition();
        Map<BlockPos, BlockState> movable = new HashMap<>();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            if (pos.distSqr(center) > radius * radius || level.isOutsideBuildHeight(pos)) continue;
            BlockState state = level.getBlockState(pos);
            if (state.hasBlockEntity() || state.getDestroySpeed(level, pos) < 0) continue;
            movable.put(pos.immutable(), state);
        }
        // Without neighbor updates while swapping, so torches and the like don't pop off halfway
        int flags = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;
        for (Map.Entry<BlockPos, BlockState> entry : movable.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockPos mirror = new BlockPos(pos.getX(), 2 * center.getY() - pos.getY(), pos.getZ());
            BlockState mirrored = movable.get(mirror);
            if (mirrored != null && mirrored != entry.getValue())
                level.setBlock(pos, mirrored, flags);
        }
        for (BlockPos pos : movable.keySet())
            level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock(), null);
    }
}
