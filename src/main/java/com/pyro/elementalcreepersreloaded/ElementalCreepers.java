package com.pyro.elementalcreepersreloaded;

import cc.nitea.Nitea;
import cc.nitea.NiteaClient;
import cc.nitea.NiteaOptions;
import com.mojang.logging.LogUtils;
import com.pyro.elementalcreepersreloaded.client.ECClient;
import com.pyro.elementalcreepersreloaded.command.ECCommands;
import com.pyro.elementalcreepersreloaded.event.ECEvents;
import com.pyro.elementalcreepersreloaded.registry.ECBiomeModifiers;
import com.pyro.elementalcreepersreloaded.registry.ECBlocks;
import com.pyro.elementalcreepersreloaded.registry.ECComponents;
import com.pyro.elementalcreepersreloaded.registry.ECEntities;
import com.pyro.elementalcreepersreloaded.registry.ECItems;
import com.pyro.lomlibreloaded.LomLibReloaded;
import net.minecraft.resources.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

@Mod(ElementalCreepers.MOD_ID)
public final class ElementalCreepers {
    public static final String MOD_ID = "elementalcreepersreloaded";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static NiteaClient nitea;

    public ElementalCreepers(FMLJavaModLoadingContext context) {
        // First thing, so errors thrown while the mod loads are reported too
        nitea = Nitea.init(NiteaOptions.builder(MOD_ID)
                .owner(ElementalCreepers.class)
                .release(context.getContainer().getModInfo().getVersion().toString())
                .gameDir(FMLPaths.GAMEDIR.get())
                .tag("lomlib", ModList.getModContainerById(LomLibReloaded.MOD_ID)
                        .map(mod -> mod.getModInfo().getVersion().toString()).orElse("missing"))
                .build());

        var modBus = context.getModBusGroup();
        ECBlocks.BLOCKS.register(modBus);
        ECItems.ITEMS.register(modBus);
        ECItems.TABS.register(modBus);
        ECEntities.ENTITIES.register(modBus);
        ECComponents.COMPONENTS.register(modBus);
        ECBiomeModifiers.SERIALIZERS.register(modBus);

        context.registerConfig(ModConfig.Type.COMMON, ECConfig.SPEC);
        ModConfigEvent.Loading.getBus(modBus).addListener(ElementalCreepers::onConfig);
        ModConfigEvent.Reloading.getBus(modBus).addListener(ElementalCreepers::onConfig);

        EntityAttributeCreationEvent.BUS.addListener(ECEntities::createAttributes);
        SpawnPlacementRegisterEvent.BUS.addListener(ECEntities::registerSpawnPlacements);
        RegisterCommandsEvent.BUS.addListener(ECCommands::register);
        ECEvents.register();

        if (FMLEnvironment.dist == Dist.CLIENT)
            ECClient.init(modBus);
        nitea.addBreadcrumb("lifecycle", "Elemental Creepers Reloaded constructed");
    }

    /** The mod's Nitea client: errors, crashes and player reports go to the Elemental Creepers dashboard. */
    public static NiteaClient nitea() {
        return nitea;
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    // The settings that change how explosions behave go on every report, so a bug can be matched to them
    private static void onConfig(ModConfigEvent event) {
        if (event.getConfig().getSpec() != ECConfig.SPEC) return;
        nitea.setTag("dome_explosions", String.valueOf(ECConfig.DOME_EXPLOSIONS.get()));
        nitea.setTag("special_events", String.valueOf(ECConfig.SPECIAL_EVENTS.get()));
        nitea.addBreadcrumb("config", event instanceof ModConfigEvent.Reloading ? "Config reloaded" : "Config loaded");
    }
}
