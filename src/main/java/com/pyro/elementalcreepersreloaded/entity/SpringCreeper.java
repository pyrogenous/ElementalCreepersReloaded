package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Doesn't explode when its fuse runs out: it jumps high instead, and explodes where it lands. The higher the fall, the bigger. */
public class SpringCreeper extends ElementalCreeper {
    private boolean sprung;

    public SpringCreeper(EntityType<? extends SpringCreeper> type, Level level) {
        super(type, level);
        this.explosionSound = false;
    }

    public boolean isSprung() {
        return this.sprung;
    }

    @Override
    public boolean diesAfterExplosion() {
        return false;
    }

    @Override
    protected int swellSpeed() {
        // In the air: the fuse already did its job
        return this.sprung ? 0 : 1;
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        if (this.sprung) return;
        this.sprung = true;
        this.setDeltaMovement(this.getDeltaMovement().x, 1.5, this.getDeltaMovement().z);
        this.hurtMarked = true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.sprung && this.level() instanceof ServerLevel level)
            level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 1, 0.1, 0.1, 0.1, 0.0);
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damageModifier, DamageSource damageSource) {
        if (this.sprung && this.level() instanceof ServerLevel level) {
            float radius = (this.isPowered() ? ECConfig.SPRING_POWER.get() * 1.5F : ECConfig.SPRING_POWER.get())
                    * Math.max(1.0F, (float) (fallDistance - 3.0) / 6.0F);
            level.explode(this, this.getX(), this.getY() - 1.0, this.getZ(), radius, Level.ExplosionInteraction.MOB);
            this.discard();
            return true;
        }
        return super.causeFallDamage(fallDistance, damageModifier, damageSource);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("isSprung", this.sprung);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.sprung = input.getBooleanOr("isSprung", false);
    }
}
