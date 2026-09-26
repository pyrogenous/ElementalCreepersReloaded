package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.entity.ai.ElementalSwellGoal;
import com.pyro.lomlibreloaded.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

/**
 * Part enderman: only goes after players who look at it (a carved pumpkin still works), teleports around when hurt,
 * in daylight and in water, and its explosion scatters whoever survives it.
 */
public class EnderCreeper extends ElementalCreeper {
    private int teleportDelay;

    public EnderCreeper(EntityType<? extends EnderCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ElementalSwellGoal(this, 9.0, 49.0));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false,
                (target, level) -> target instanceof Player player && this.isStaredAtBy(player)));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    private boolean isStaredAtBy(Player player) {
        return ForgeHooks.isNotDisguised(this).test(player) && this.isLookingAtMe(player, 0.025, true, false, this.getEyeY());
    }

    @Override
    public void aiStep() {
        if (this.level() instanceof ServerLevel level && this.isAlive()) {
            if (this.teleportDelay > 0)
                this.teleportDelay--;
            if (level.isBrightOutside() && this.getLightLevelDependentMagicValue() > 0.5F
                    && level.canSeeSky(BlockPos.containing(this.getX(), this.getEyeY(), this.getZ()))
                    && this.random.nextFloat() * 30.0F < (this.getLightLevelDependentMagicValue() - 0.4F) * 2.0F) {
                this.setTarget(null);
                EntityUtil.teleportRandomly(this);
                this.teleportDelay = 50;
            }
            if (this.isInWaterOrRain()) {
                this.hurtServer(level, this.damageSources().drown(), 1.0F);
                EntityUtil.teleportRandomly(this);
            }
            LivingEntity target = this.getTarget();
            if (target instanceof Player && this.distanceTo(target) > 6.0F && this.random.nextInt(100) < 5 && this.teleportDelay <= 0) {
                EntityUtil.teleportToEntity(this, target);
                this.teleportDelay = 70;
            }
        }
        super.aiStep();
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        boolean hurt = super.hurtServer(level, source, damage);
        if (hurt && this.isAlive() && this.random.nextInt(100) < 15) {
            double x = this.getX() + this.random.nextInt(5) * (this.random.nextBoolean() ? 1 : -1);
            double y = this.getY() + this.random.nextInt(5) * (this.random.nextBoolean() ? 1 : -1);
            double z = this.getZ() + this.random.nextInt(5) * (this.random.nextBoolean() ? 1 : -1);
            EntityUtil.teleportTo(this, x, y, z);
        }
        return hurt;
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        float radius = this.isPowered() ? this.explosionRadius * 1.5F : this.explosionRadius;
        level.explode(this, this.getX(), this.getY(), this.getZ(), radius,
                griefing ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
        for (LivingEntity entity : this.nearby(LivingEntity.class, radius)) {
            if (this.random.nextInt(100) <= 25)
                EntityUtil.teleportRandomly(entity);
        }
        level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
    }
}
