package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import com.pyro.elementalcreepersreloaded.entity.ai.ElementalSwellGoal;
import com.pyro.elementalcreepersreloaded.registry.ECEntities;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

/**
 * Six times the size of a creeper, 150 health and a long fuse. Its big bad boom releases more creepers
 * (big_bad_creep_amount), picked at random among the vanilla and elemental ones.
 */
public class BigBadCreep extends ElementalCreeper {
    public BigBadCreep(EntityType<? extends BigBadCreep> type, Level level) {
        super(type, level);
        this.maxSwell = 75;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return ElementalCreeper.createAttributes().add(Attributes.MAX_HEALTH, 150.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ElementalSwellGoal(this, 54.0, 300.0));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        level.explode(this, this.getX(), this.getY(), this.getZ(), 10 * power,
                griefing ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
        List<EntityType<? extends Mob>> pool = ECEntities.bigBadSpawnPool();
        int released = 0;
        for (int i = 0; i < ECConfig.BIG_BAD_AMOUNT.get() * power; i++) {
            EntityType<? extends Mob> type = pool.get(this.random.nextInt(pool.size()));
            Mob creeper = type.create(level, EntitySpawnReason.MOB_SUMMONED);
            if (creeper == null) continue;
            creeper.snapTo(this.getX(), this.getY() + 1.0, this.getZ(), this.random.nextFloat() * 360.0F, 0.0F);
            creeper.setTarget(this.getTarget());
            creeper.setDeltaMovement(0.8 * (this.random.nextBoolean() ? 1 : -1), 0.5, 0.8 * (this.random.nextBoolean() ? 1 : -1));
            level.addFreshEntity(creeper);
            released++;
        }
        ElementalCreepers.nitea().addBreadcrumb("explosion", "Big Bad Creep released " + released + " creepers");
    }
}
