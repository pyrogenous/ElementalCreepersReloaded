package com.pyro.elementalcreepersreloaded.registry;

import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import com.pyro.elementalcreepersreloaded.item.Creepapedia;
import com.pyro.elementalcreepersreloaded.item.CreepapediaItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ECItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ElementalCreepers.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ElementalCreepers.MOD_ID);

    public static final RegistryObject<Item> CREEPAPEDIA = ITEMS.register("creepapedia", () -> new CreepapediaItem(new Item.Properties()
            .setId(ITEMS.key("creepapedia")).stacksTo(1)));

    public static final List<RegistryObject<Item>> SPAWN_EGGS = new ArrayList<>();
    public static final List<RegistryObject<BlockItem>> CREEPERFISH_EGGS = new ArrayList<>();

    static {
        for (var type : ECEntities.withSpawnEggs()) {
            String name = type.getId().getPath() + "_spawn_egg";
            SPAWN_EGGS.add(ITEMS.register(name, () -> new SpawnEggItem(new Item.Properties().setId(ITEMS.key(name)).spawnEgg(type.get()))));
        }
        for (var block : ECBlocks.CREEPERFISH_EGGS) {
            String name = block.getId().getPath();
            CREEPERFISH_EGGS.add(ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().setId(ITEMS.key(name)).useBlockDescriptionPrefix())));
        }
    }

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("elemental_creepers", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.elementalcreepersreloaded"))
            .icon(() -> SPAWN_EGGS.getFirst().get().getDefaultInstance())
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .displayItems((params, output) -> {
                output.accept(CREEPAPEDIA.get());
                // The creative copy: every page, ghost exorcised
                ItemStack complete = new ItemStack(CREEPAPEDIA.get());
                complete.set(ECComponents.CREEPAPEDIA.get(), Creepapedia.complete());
                complete.set(DataComponents.RARITY, Rarity.RARE);
                output.accept(complete);
                SPAWN_EGGS.forEach(egg -> output.accept(egg.get()));
                CREEPERFISH_EGGS.forEach(egg -> output.accept(egg.get()));
            })
            .build());

    private ECItems() {
    }
}
