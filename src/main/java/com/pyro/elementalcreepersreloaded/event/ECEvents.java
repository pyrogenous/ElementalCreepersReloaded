package com.pyro.elementalcreepersreloaded.event;

import com.pyro.elementalcreepersreloaded.ECConfig;
import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import com.pyro.elementalcreepersreloaded.entity.FriendlyCreeper;
import com.pyro.elementalcreepersreloaded.entity.GhostCreeper;
import com.pyro.elementalcreepersreloaded.entity.IllusionCreeper;
import com.pyro.elementalcreepersreloaded.item.Creepapedia;
import com.pyro.elementalcreepersreloaded.item.CreepapediaData;
import com.pyro.elementalcreepersreloaded.item.CreepapediaItem;
import com.pyro.elementalcreepersreloaded.registry.ECComponents;
import com.pyro.elementalcreepersreloaded.registry.ECEntities;
import com.pyro.elementalcreepersreloaded.registry.ECItems;
import com.pyro.lomlibreloaded.nitea.NiteaSupport;
import com.pyro.lomlibreloaded.util.EntityUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

/** Game events: the Creepapedia filling up, ghosts coming back, the spawn eggs in the vanilla tab. */
public final class ECEvents {
    private ECEvents() {
    }

    public static void register() {
        LivingDeathEvent.BUS.addListener(ECEvents::onDeath);
        BuildCreativeModeTabContentsEvent.BUS.addListener(ECEvents::addToVanillaTabs);
    }

    private static void onDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(victim.level() instanceof ServerLevel level) || !Creepapedia.isEntry(victim.getType())) return;
        if (!EntityUtil.damageFromPlayer(event.getSource()) || !(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (victim instanceof IllusionCreeper illusion && illusion.isIllusion()) return;

        NiteaSupport.guard(ElementalCreepers.nitea(), "creepapedia entry", () -> recordKill(player, victim));

        // Its ghost may come back to finish the job
        if (!(victim instanceof GhostCreeper) && !(victim instanceof FriendlyCreeper)
                && level.getRandom().nextInt(100) < ECConfig.GHOST_CHANCE.get()) {
            GhostCreeper ghost = ECEntities.GHOST_CREEPER.get().create(level, EntitySpawnReason.MOB_SUMMONED);
            if (ghost != null) {
                ghost.snapTo(victim.getX(), victim.getY(), victim.getZ(), victim.getYRot(), victim.getXRot());
                ghost.setTarget(player);
                level.addFreshEntity(ghost);
            }
        }
    }

    /** Adds the creeper to the first Creepapedia in the player's inventory, and says so the first time. */
    private static void recordKill(ServerPlayer player, LivingEntity victim) {
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (!stack.is(ECItems.CREEPAPEDIA.get())) continue;
            CreepapediaData data = CreepapediaItem.data(stack);
            var entry = Creepapedia.id(victim.getType());
            if (data.has(entry)) return;
            CreepapediaData updated = data.with(entry);
            stack.set(ECComponents.CREEPAPEDIA.get(), updated);
            player.sendSystemMessage(Component.translatable("book.elementalcreepersreloaded.new_entry", victim.getType().getDescription())
                    .withStyle(ChatFormatting.GREEN));
            if (updated.entries().size() >= Creepapedia.allEntries().size())
                player.sendSystemMessage(Component.translatable("book.elementalcreepersreloaded.all_entries")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
            return;
        }
    }

    private static void addToVanillaTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS)
            ECItems.SPAWN_EGGS.forEach(event::accept);
        else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(ECItems.CREEPAPEDIA);
    }
}
