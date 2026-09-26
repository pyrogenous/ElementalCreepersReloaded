package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.registry.ECEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * When a player comes close, it splits into four copies and jumps. Only the real one explodes; the illusions just
 * vanish in a puff of smoke.
 */
public class IllusionCreeper extends ElementalCreeper {
    private boolean split;
    private boolean illusion;

    public IllusionCreeper(EntityType<? extends IllusionCreeper> type, Level level) {
        super(type, level);
    }

    public boolean isIllusion() {
        return this.illusion;
    }

    @Override
    public void tick() {
        if (this.level() instanceof ServerLevel level && !this.illusion && !this.split) {
            Player player = level.getNearestPlayer(this, 8.0);
            if (player != null && !player.isCreative() && !player.isSpectator()) {
                this.split = true;
                this.spawnIllusions(level);
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.5, 0.0));
            }
        }
        super.tick();
    }

    private void spawnIllusions(ServerLevel level) {
        for (int i = 0; i < 4; i++) {
            IllusionCreeper copy = ECEntities.ILLUSION_CREEPER.get().create(level, EntitySpawnReason.MOB_SUMMONED);
            if (copy == null) continue;
            copy.split = true;
            copy.illusion = true;
            copy.explosionSound = false;
            copy.snapTo(this.getX(), this.getY(), this.getZ(), this.random.nextFloat() * 360.0F, 0.0F);
            copy.setDeltaMovement(new Vec3((this.random.nextDouble() - 0.5) * 0.6, 0.5, (this.random.nextDouble() - 0.5) * 0.6));
            copy.setTarget(this.getTarget());
            level.addFreshEntity(copy);
        }
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        if (!this.illusion)
            level.explode(this, this.getX(), this.getY(), this.getZ(), this.explosionRadius * power,
                    griefing ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("split", this.split);
        output.putBoolean("isIllusion", this.illusion);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.split = input.getBooleanOr("split", false);
        this.illusion = input.getBooleanOr("isIllusion", false);
        this.explosionSound = !this.illusion;
    }

    // Illusions leave nothing behind
    @Override
    protected boolean shouldDropLoot(ServerLevel level) {
        return !this.illusion && super.shouldDropLoot(level);
    }
}
