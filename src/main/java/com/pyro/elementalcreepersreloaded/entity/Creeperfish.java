package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import com.pyro.elementalcreepersreloaded.block.CreeperfishEggBlock;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;
import org.jspecify.annotations.Nullable;

/**
 * The Creeperfish (Silver Creeper in the original mod): a copycat of the silverfish. Hides in stone when it has nothing
 * to do, and its small blast pushes silverfish into the stone around it.
 */
public class Creeperfish extends ElementalCreeper {
    public Creeperfish(EntityType<? extends Creeperfish> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(7, new HideInStoneGoal(this));
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        int radius = this.radius(ECConfig.SILVER_RADIUS, power);
        level.explode(this, this.getX(), this.getY(), this.getZ(), radius, Level.ExplosionInteraction.NONE);
        if (!griefing) return;
        BlockPos center = this.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            BlockState state = level.getBlockState(pos);
            if (InfestedBlock.isCompatibleHostBlock(state) && this.random.nextInt(100) < 25)
                level.setBlock(pos, InfestedBlock.infestedStateByHost(state), 3);
        }
    }

    /** The silverfish's "merge with stone" goal, turning the block into a Creeperfish egg. */
    private static class HideInStoneGoal extends RandomStrollGoal {
        private @Nullable Direction selectedDirection;
        private boolean doMerge;

        HideInStoneGoal(Creeperfish creeper) {
            super(creeper, 1.0, 10);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.mob.getTarget() != null || !this.mob.getNavigation().isDone()) return false;
            RandomSource random = this.mob.getRandom();
            if (ForgeEventFactory.getMobGriefingEvent(getServerLevel(this.mob.level()), this.mob) && random.nextInt(reducedTickDelay(10)) == 0) {
                this.selectedDirection = Direction.getRandom(random);
                if (CreeperfishEggBlock.eggFor(this.mob.level().getBlockState(this.target())) != null) {
                    this.doMerge = true;
                    return true;
                }
            }
            this.doMerge = false;
            return super.canUse();
        }

        private BlockPos target() {
            return BlockPos.containing(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()).relative(this.selectedDirection);
        }

        @Override
        public boolean canContinueToUse() {
            return !this.doMerge && super.canContinueToUse();
        }

        @Override
        public void start() {
            if (!this.doMerge) {
                super.start();
                return;
            }
            BlockPos pos = this.target();
            BlockState egg = CreeperfishEggBlock.eggFor(this.mob.level().getBlockState(pos));
            if (egg != null) {
                this.mob.level().setBlock(pos, egg, 3);
                this.mob.spawnAnim();
                this.mob.discard();
            }
        }
    }
}
