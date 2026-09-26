package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/** Six legs, climbs walls like a spider, immune to poison. Its blast spins cobwebs and poisons everything around it. */
public class SpiderCreeper extends ElementalCreeper {
    private static final EntityDataAccessor<Boolean> DATA_CLIMBING = SynchedEntityData.defineId(SpiderCreeper.class, EntityDataSerializers.BOOLEAN);

    public SpiderCreeper(EntityType<? extends SpiderCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_CLIMBING, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide())
            this.entityData.set(DATA_CLIMBING, this.horizontalCollision);
    }

    @Override
    public boolean onClimbable() {
        return this.entityData.get(DATA_CLIMBING);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return !effect.is(MobEffects.POISON) && super.canBeAffected(effect);
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        int radius = this.radius(ECConfig.SPIDER_RADIUS, power);
        if (griefing) {
            BlockPos center = this.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
                if (this.random.nextInt(100) < 2 && level.getBlockState(pos).isAir())
                    level.setBlock(pos, Blocks.COBWEB.defaultBlockState(), 2);
            }
        }
        int seconds = switch (level.getDifficulty()) {
            case EASY -> 14;
            case NORMAL -> 20;
            case HARD -> 30;
            case PEACEFUL -> 0;
        };
        if (seconds == 0 || level.getDifficulty() == Difficulty.PEACEFUL) return;
        for (LivingEntity entity : this.nearby(LivingEntity.class, radius))
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, seconds * 20, 0), this);
    }
}
