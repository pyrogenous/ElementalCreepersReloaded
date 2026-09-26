package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.ForgeEventFactory;

/** Leaves snow where it walks. Its explosion freezes water to ice, lava to obsidian, and snows over the area. */
public class IceCreeper extends ElementalCreeper {
    public IceCreeper(EntityType<? extends IceCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level() instanceof ServerLevel level) {
            BlockPos old = BlockPos.containing(this.xo, this.yo, this.zo);
            BlockState snow = Blocks.SNOW.defaultBlockState();
            if (!old.equals(this.blockPosition()) && level.getBlockState(old).isAir() && snow.canSurvive(level, old)
                    && ForgeEventFactory.getMobGriefingEvent(level, this))
                level.setBlockAndUpdate(old, snow);
        }
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        if (!griefing) return;
        int radius = this.radius(ECConfig.ICE_RADIUS, power);
        BlockPos center = this.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            FluidState fluid = level.getFluidState(pos);
            if (!fluid.isSource()) continue;
            if (fluid.is(FluidTags.WATER) && level.getBlockState(pos).is(Blocks.WATER))
                level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
            else if (fluid.is(FluidTags.LAVA))
                level.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
        }
        if (ECConfig.DOME_EXPLOSIONS.get()) {
            this.domeExplosion(level, radius, Blocks.SNOW_BLOCK.defaultBlockState());
            return;
        }
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            if (isGroundSpot(level, pos)) {
                BlockState state = this.random.nextBoolean() ? Blocks.SNOW.defaultBlockState() : Blocks.SNOW_BLOCK.defaultBlockState();
                if (state.canSurvive(level, pos))
                    level.setBlock(pos, state, 2);
            }
        }
    }
}
