package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Fast, and floats down like a chicken instead of falling. Its gust blows everything away, and sets it on fire in the
 * Nether and badlands.
 */
public class WindCreeper extends ElementalCreeper {
    public WindCreeper(EntityType<? extends WindCreeper> type, Level level) {
        super(type, level);
        this.explosionSound = false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ElementalCreeper.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.5);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.resetFallDistance();
        Vec3 motion = this.getDeltaMovement();
        if (!this.onGround() && motion.y < 0.0)
            this.setDeltaMovement(motion.multiply(1.0, 0.6, 1.0));
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        var biome = level.getBiome(this.blockPosition());
        boolean hot = biome.is(BiomeTags.IS_NETHER) || biome.is(BiomeTags.IS_BADLANDS);
        Knockback.blast(level, this, ECConfig.WIND_RADIUS.get() * power, ECConfig.WIND_POWER.get(), 1.01, hot);
    }
}
