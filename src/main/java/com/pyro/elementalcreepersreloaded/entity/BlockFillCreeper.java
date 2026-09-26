package com.pyro.elementalcreepersreloaded.entity;

import java.util.function.Supplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Creepers whose explosion leaves a block behind: Water (water), Earth (dirt), Light (glowstone) and Magma (lava).
 * Scattered on the ground, or in a dome with dome_explosions on. Needs mob griefing.
 */
public class BlockFillCreeper extends ElementalCreeper {
    private final ForgeConfigSpec.IntValue radius;
    private final Supplier<BlockState> block;

    public BlockFillCreeper(EntityType<? extends BlockFillCreeper> type, Level level, ForgeConfigSpec.IntValue radius, Supplier<BlockState> block) {
        super(type, level);
        this.radius = radius;
        this.block = block;
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        if (griefing)
            this.fillExplosion(level, this.radius(this.radius, power), this.block.get());
    }
}
