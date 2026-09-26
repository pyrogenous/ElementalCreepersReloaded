package com.pyro.elementalcreepersreloaded.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * The Psychic and Wind Creepers' blasts: an explosion's knockback without its damage or broken blocks, scaled
 * separately sideways and upwards (the Psychic Creeper flings up, the Wind Creeper blows away).
 */
final class Knockback {
    private Knockback() {
    }

    static void blast(ServerLevel level, Entity source, float size, double horizontal, double vertical, boolean setFire) {
        Vec3 center = source.position();
        float reach = size * 2.0F;
        AABB area = new AABB(center, center).inflate(reach + 1.0);
        for (Entity entity : level.getEntities(source, area)) {
            if (entity.isSpectator()) continue;
            double dist = Math.sqrt(entity.distanceToSqr(center)) / reach;
            if (dist > 1.0) continue;
            Vec3 direction = entity.getEyePosition().subtract(center);
            if (direction.lengthSqr() < 1.0E-8) continue;
            direction = direction.normalize();
            double resistance = entity instanceof LivingEntity living ? living.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE) : 0.0;
            double strength = (1.0 - dist) * ServerExplosion.getSeenPercent(center, entity) * (1.0 - resistance);
            entity.push(direction.x * strength * horizontal, direction.y * strength * vertical, direction.z * strength * horizontal);
            // Velocity changes of players are only sent to their client when marked
            entity.hurtMarked = true;
            if (setFire)
                entity.igniteForSeconds(4.0F);
        }
    }
}
