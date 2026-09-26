package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.ForgeEventFactory;

/** Leaves a trail of fire where it walks, hates water, and floods the area with lava when it explodes. */
public class MagmaCreeper extends BlockFillCreeper {
    public MagmaCreeper(EntityType<? extends MagmaCreeper> type, Level level) {
        super(type, level, ECConfig.MAGMA_RADIUS, Blocks.LAVA::defaultBlockState);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level() instanceof ServerLevel level) {
            BlockPos old = BlockPos.containing(this.xo, this.yo, this.zo);
            if (!old.equals(this.blockPosition()) && level.getBlockState(old).isAir()
                    && BaseFireBlock.canBePlacedAt(level, old, Direction.UP) && ForgeEventFactory.getMobGriefingEvent(level, this))
                level.setBlockAndUpdate(old, BaseFireBlock.getState(level, old));
            if (this.isInWaterOrRain())
                this.hurtServer(level, this.damageSources().drown(), 1.0F);
        }
    }
}
