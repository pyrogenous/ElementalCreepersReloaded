package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Harmless and delicious: bursts into cookies. */
public class CookieCreeper extends ElementalCreeper {
    public CookieCreeper(EntityType<? extends CookieCreeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void explosion(ServerLevel level, int power, boolean griefing) {
        for (int i = 0; i < ECConfig.COOKIE_AMOUNT.get() * power; i++) {
            ItemEntity cookie = new ItemEntity(level, this.getX(), this.getY(), this.getZ(), new ItemStack(Items.COOKIE));
            cookie.setDeltaMovement((this.random.nextDouble() - 0.5) * 0.3, 0.5, (this.random.nextDouble() - 0.5) * 0.3);
            level.addFreshEntity(cookie);
        }
    }
}
