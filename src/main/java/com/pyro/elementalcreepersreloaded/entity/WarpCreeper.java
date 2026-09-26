package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Shuffles everyone around it: each creature ends up where another one was. */
public class WarpCreeper extends ElementalCreeper {
    public WarpCreeper(EntityType<? extends WarpCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        List<LivingEntity> entities = this.nearby(LivingEntity.class, this.radius(ECConfig.WARP_RADIUS, power)).stream()
                .filter(e -> !e.isSpectator() && !e.isPassenger()).toList();
        if (entities.size() < 2) return;
        List<Vec3> positions = entities.stream().map(LivingEntity::position).toList();
        // Rotating the positions by a random offset moves every entity, and none lands on its own spot
        int offset = 1 + this.random.nextInt(entities.size() - 1);
        for (int i = 0; i < entities.size(); i++) {
            LivingEntity entity = entities.get(i);
            Vec3 target = positions.get((i + offset) % positions.size());
            entity.teleportTo(target.x, target.y, target.z);
            entity.resetFallDistance();
            level.playSound(null, target.x, target.y, target.z, SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 1.0F, 1.0F);
        }
    }
}
