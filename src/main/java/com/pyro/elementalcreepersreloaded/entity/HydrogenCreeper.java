package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * A hydrogen bomb: a fuse twice as fast, a huge explosion (hydrogen_creeper_radius), wither close to the blast and
 * poison further out.
 */
public class HydrogenCreeper extends ElementalCreeper {
    public HydrogenCreeper(EntityType<? extends HydrogenCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected int swellSpeed() {
        return 2;
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        int radius = ECConfig.HYDROGEN_RADIUS.get();
        for (LivingEntity entity : this.nearby(LivingEntity.class, radius)) {
            double distance = this.distanceTo(entity);
            if (distance >= radius) continue;
            if (distance < radius / 10.0)
                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, 1), this);
            else
                entity.addEffect(new MobEffectInstance(MobEffects.POISON, 500, 2), this);
        }
        level.explode(this, this.getX(), this.getY(), this.getZ(), radius * power,
                griefing ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
    }
}
