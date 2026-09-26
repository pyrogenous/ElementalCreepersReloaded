package com.pyro.elementalcreepersreloaded.client;

import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import com.pyro.elementalcreepersreloaded.client.model.CreeperfishModel;
import com.pyro.elementalcreepersreloaded.client.model.SpiderCreeperModel;
import com.pyro.elementalcreepersreloaded.client.render.ECCreeperRenderer;
import com.pyro.elementalcreepersreloaded.client.render.HolidayHatLayer;
import com.pyro.elementalcreepersreloaded.client.screen.CreepapediaScreen;
import com.pyro.elementalcreepersreloaded.entity.ElementalCreeper;
import com.pyro.elementalcreepersreloaded.entity.FriendlyCreeper;
import com.pyro.elementalcreepersreloaded.item.CreepapediaItem;
import com.pyro.elementalcreepersreloaded.registry.ECEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;

/** Client setup: renderers, model layers, and the Creepapedia screen. */
public final class ECClient {
    public static final ModelLayerLocation SPIDER_CREEPER = new ModelLayerLocation(ElementalCreepers.id("spider_creeper"), "main");
    public static final ModelLayerLocation SPIDER_CREEPER_ARMOR = new ModelLayerLocation(ElementalCreepers.id("spider_creeper"), "armor");
    public static final ModelLayerLocation CREEPERFISH = new ModelLayerLocation(ElementalCreepers.id("creeperfish"), "main");
    public static final ModelLayerLocation CREEPERFISH_ARMOR = new ModelLayerLocation(ElementalCreepers.id("creeperfish"), "armor");

    private static final Identifier VANILLA_CREEPER = Identifier.withDefaultNamespace("textures/entity/creeper/creeper.png");
    private static final Identifier FRIENDLY_WILD = texture("friendly_creeper");
    private static final Identifier FRIENDLY_TAME = texture("friendly_creeper_tame");

    private ECClient() {
    }

    public static void init(BusGroup modBus) {
        EntityRenderersEvent.RegisterLayerDefinitions.BUS.addListener(ECClient::registerLayers);
        EntityRenderersEvent.RegisterRenderers.BUS.addListener(ECClient::registerRenderers);
        EntityRenderersEvent.AddLayers.BUS.addListener(ECClient::addLayers);
        CreepapediaItem.openScreen = stack -> Minecraft.getInstance().gui.setScreen(new CreepapediaScreen(stack));
    }

    /** textures/entity/<name>.png of the mod. */
    public static Identifier texture(String name) {
        return ElementalCreepers.id("textures/entity/" + name + ".png");
    }

    private static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SPIDER_CREEPER, () -> SpiderCreeperModel.createBodyLayer(CubeDeformation.NONE));
        event.registerLayerDefinition(SPIDER_CREEPER_ARMOR, () -> SpiderCreeperModel.createBodyLayer(new CubeDeformation(2.0F)));
        event.registerLayerDefinition(CREEPERFISH, () -> CreeperfishModel.createBodyLayer(CubeDeformation.NONE));
        event.registerLayerDefinition(CREEPERFISH_ARMOR, () -> CreeperfishModel.createBodyLayer(new CubeDeformation(2.0F)));
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (var type : ECEntities.ELEMENTAL)
            register(event, type.get());
        event.registerEntityRenderer(ECEntities.FRIENDLY_CREEPER.get(), context -> new ECCreeperRenderer<FriendlyCreeper>(context,
                new CreeperModel(context.bakeLayer(ModelLayers.CREEPER)), new CreeperModel(context.bakeLayer(ModelLayers.CREEPER_ARMOR)),
                creeper -> creeper.isTame() ? FRIENDLY_TAME : FRIENDLY_WILD, 1.0F, false));
    }

    private static <T extends ElementalCreeper> void register(EntityRenderersEvent.RegisterRenderers event, EntityType<T> type) {
        event.registerEntityRenderer(type, rendererFor(type));
    }

    private static <T extends ElementalCreeper> EntityRendererProvider<T> rendererFor(EntityType<T> type) {
        String name = BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath();
        if (type == ECEntities.SPIDER_CREEPER.get())
            return context -> new ECCreeperRenderer<>(context, new SpiderCreeperModel(context.bakeLayer(SPIDER_CREEPER)),
                    new SpiderCreeperModel(context.bakeLayer(SPIDER_CREEPER_ARMOR)), creeper -> texture(name), 1.0F, false);
        if (type == ECEntities.CREEPERFISH.get())
            return context -> new ECCreeperRenderer<>(context, new CreeperfishModel(context.bakeLayer(CREEPERFISH)),
                    new CreeperfishModel(context.bakeLayer(CREEPERFISH_ARMOR)), creeper -> texture(name), 1.0F, false);
        boolean ghost = type == ECEntities.GHOST_CREEPER.get();
        // The Big Bad Creep wears the vanilla skin, six times bigger; the ghost is a see-through vanilla creeper
        Identifier skin = ghost || type == ECEntities.BIG_BAD_CREEP.get() ? VANILLA_CREEPER : texture(name);
        float size = type == ECEntities.BIG_BAD_CREEP.get() ? 6.0F : 1.0F;
        return context -> new ECCreeperRenderer<>(context, new CreeperModel(context.bakeLayer(ModelLayers.CREEPER)),
                new CreeperModel(context.bakeLayer(ModelLayers.CREEPER_ARMOR)), creeper -> skin, size, ghost);
    }

    // Vanilla creepers wear the holiday hats too
    private static void addLayers(EntityRenderersEvent.AddLayers event) {
        CreeperRenderer renderer = event.getEntityRenderer(EntityTypes.CREEPER);
        if (renderer != null)
            renderer.addLayer(new HolidayHatLayer<>(renderer));
    }
}
