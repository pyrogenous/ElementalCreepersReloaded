package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;

/** Goes off like a firework, and sends half of the creatures around it up on a rocket of their own. */
public class FireworkCreeper extends ElementalCreeper {
    public FireworkCreeper(EntityType<? extends FireworkCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        int radius = this.radius(ECConfig.FIREWORK_RADIUS, power);
        level.addFreshEntity(new FireworkRocketEntity(level, this.getX(), this.getY(), this.getZ(), this.randomRocket()));
        for (LivingEntity entity : this.nearby(LivingEntity.class, radius)) {
            if (this.random.nextBoolean()) continue;
            FireworkRocketEntity rocket = new FireworkRocketEntity(level, entity.getX(), entity.getY(), entity.getZ(), this.randomRocket());
            level.addFreshEntity(rocket);
            entity.startRiding(rocket, true, true);
        }
    }

    /** A creeper-shaped firework with a trail, in a random color. */
    private ItemStack randomRocket() {
        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
        int color = this.random.nextInt(0x1000000);
        stack.set(DataComponents.FIREWORKS, new Fireworks(1,
                List.of(new FireworkExplosion(FireworkExplosion.Shape.CREEPER, IntList.of(color), IntList.of(), true, false))));
        return stack;
    }
}
