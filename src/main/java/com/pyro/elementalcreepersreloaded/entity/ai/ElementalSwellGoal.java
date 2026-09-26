package com.pyro.elementalcreepersreloaded.entity.ai;

import com.pyro.elementalcreepersreloaded.entity.ElementalCreeper;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jspecify.annotations.Nullable;

/** The vanilla creeper's swell goal, with the start and give-up distances as parameters (the Big Bad Creep's are larger). */
public class ElementalSwellGoal extends Goal {
    private final ElementalCreeper creeper;
    private final double startDistanceSqr;
    private final double stopDistanceSqr;
    private @Nullable LivingEntity target;

    public ElementalSwellGoal(ElementalCreeper creeper, double startDistanceSqr, double stopDistanceSqr) {
        this.creeper = creeper;
        this.startDistanceSqr = startDistanceSqr;
        this.stopDistanceSqr = stopDistanceSqr;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.creeper.getTarget();
        return this.creeper.getSwellDir() > 0
                || target != null && !target.isDeadOrDying() && this.creeper.distanceToSqr(target) < this.startDistanceSqr;
    }

    @Override
    public void start() {
        this.creeper.getNavigation().stop();
        this.target = this.creeper.getTarget();
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.target == null || this.target.isDeadOrDying()
                || this.creeper.distanceToSqr(this.target) > this.stopDistanceSqr
                || !this.creeper.getSensing().hasLineOfSight(this.target))
            this.creeper.setSwellDir(-1);
        else
            this.creeper.setSwellDir(1);
    }
}
