package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.level.Level;

/** Strikes every creature around it (not other monsters) with lightning, and turns zombified piglins against its target. */
public class ElectricCreeper extends ElementalCreeper {
    public ElectricCreeper(EntityType<? extends ElectricCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        int radius = this.isPowered() ? (int) (ECConfig.ELECTRIC_RADIUS.get() * 1.5F) : ECConfig.ELECTRIC_RADIUS.get();
        for (LivingEntity entity : this.nearby(LivingEntity.class, radius)) {
            if (entity instanceof Enemy) continue;
            LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.EVENT);
            if (bolt != null) {
                bolt.snapTo(entity.position());
                level.addFreshEntity(bolt);
            }
        }
        LivingEntity target = this.getTarget();
        for (ZombifiedPiglin piglin : this.nearby(ZombifiedPiglin.class, radius)) {
            if (target != null) {
                piglin.setTarget(target);
                piglin.startPersistentAngerTimer();
            }
        }
    }
}
