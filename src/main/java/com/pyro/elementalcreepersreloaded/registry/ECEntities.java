package com.pyro.elementalcreepersreloaded.registry;

import com.pyro.elementalcreepersreloaded.ECConfig;
import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import com.pyro.elementalcreepersreloaded.entity.BigBadCreep;
import com.pyro.elementalcreepersreloaded.entity.BlockFillCreeper;
import com.pyro.elementalcreepersreloaded.entity.CakeCreeper;
import com.pyro.elementalcreepersreloaded.entity.CookieCreeper;
import com.pyro.elementalcreepersreloaded.entity.Creeperfish;
import com.pyro.elementalcreepersreloaded.entity.DarkCreeper;
import com.pyro.elementalcreepersreloaded.entity.ElectricCreeper;
import com.pyro.elementalcreepersreloaded.entity.ElementalCreeper;
import com.pyro.elementalcreepersreloaded.entity.EnderCreeper;
import com.pyro.elementalcreepersreloaded.entity.FireCreeper;
import com.pyro.elementalcreepersreloaded.entity.FireworkCreeper;
import com.pyro.elementalcreepersreloaded.entity.FriendlyCreeper;
import com.pyro.elementalcreepersreloaded.entity.FurnaceCreeper;
import com.pyro.elementalcreepersreloaded.entity.GhostCreeper;
import com.pyro.elementalcreepersreloaded.entity.HydrogenCreeper;
import com.pyro.elementalcreepersreloaded.entity.IceCreeper;
import com.pyro.elementalcreepersreloaded.entity.IllusionCreeper;
import com.pyro.elementalcreepersreloaded.entity.MagmaCreeper;
import com.pyro.elementalcreepersreloaded.entity.PsychicCreeper;
import com.pyro.elementalcreepersreloaded.entity.ReverseCreeper;
import com.pyro.elementalcreepersreloaded.entity.SolarCreeper;
import com.pyro.elementalcreepersreloaded.entity.SpiderCreeper;
import com.pyro.elementalcreepersreloaded.entity.SpringCreeper;
import com.pyro.elementalcreepersreloaded.entity.StoneCreeper;
import com.pyro.elementalcreepersreloaded.entity.WarpCreeper;
import com.pyro.elementalcreepersreloaded.entity.WindCreeper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Every creeper, in Creepapedia order. */
public final class ECEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ElementalCreepers.MOD_ID);

    /** The elemental creepers (every hostile one), for attributes, spawn rules, spawn eggs and the renderers. */
    public static final List<RegistryObject<? extends EntityType<? extends ElementalCreeper>>> ELEMENTAL = new ArrayList<>();

    public static final RegistryObject<EntityType<FireCreeper>> FIRE_CREEPER = creeper("fire_creeper", FireCreeper::new, EntityType.Builder::fireImmune);
    public static final RegistryObject<EntityType<BlockFillCreeper>> WATER_CREEPER = creeper("water_creeper",
            (type, level) -> new BlockFillCreeper(type, level, ECConfig.WATER_RADIUS, Blocks.WATER::defaultBlockState), b -> b);
    public static final RegistryObject<EntityType<ElectricCreeper>> ELECTRIC_CREEPER = creeper("electric_creeper", ElectricCreeper::new, b -> b);
    public static final RegistryObject<EntityType<CookieCreeper>> COOKIE_CREEPER = creeper("cookie_creeper", CookieCreeper::new, b -> b);
    public static final RegistryObject<EntityType<DarkCreeper>> DARK_CREEPER = creeper("dark_creeper", DarkCreeper::new, b -> b);
    public static final RegistryObject<EntityType<BlockFillCreeper>> LIGHT_CREEPER = creeper("light_creeper",
            (type, level) -> new BlockFillCreeper(type, level, ECConfig.LIGHT_RADIUS, Blocks.GLOWSTONE::defaultBlockState), b -> b);
    public static final RegistryObject<EntityType<BlockFillCreeper>> EARTH_CREEPER = creeper("earth_creeper",
            (type, level) -> new BlockFillCreeper(type, level, ECConfig.EARTH_RADIUS, Blocks.DIRT::defaultBlockState), b -> b);
    public static final RegistryObject<EntityType<MagmaCreeper>> MAGMA_CREEPER = creeper("magma_creeper", MagmaCreeper::new, EntityType.Builder::fireImmune);
    public static final RegistryObject<EntityType<ReverseCreeper>> REVERSE_CREEPER = creeper("reverse_creeper", ReverseCreeper::new, b -> b);
    public static final RegistryObject<EntityType<IceCreeper>> ICE_CREEPER = creeper("ice_creeper", IceCreeper::new, b -> b);
    public static final RegistryObject<EntityType<FriendlyCreeper>> FRIENDLY_CREEPER = ENTITIES.register("friendly_creeper",
            () -> EntityType.Builder.of(FriendlyCreeper::new, MobCategory.CREATURE).sized(0.6F, 1.7F).clientTrackingRange(8)
                    .build(ENTITIES.key("friendly_creeper")));
    public static final RegistryObject<EntityType<GhostCreeper>> GHOST_CREEPER = creeper("ghost_creeper", GhostCreeper::new, b -> b);
    public static final RegistryObject<EntityType<IllusionCreeper>> ILLUSION_CREEPER = creeper("illusion_creeper", IllusionCreeper::new, b -> b);
    public static final RegistryObject<EntityType<PsychicCreeper>> PSYCHIC_CREEPER = creeper("psychic_creeper", PsychicCreeper::new, b -> b);
    public static final RegistryObject<EntityType<SpiderCreeper>> SPIDER_CREEPER = creeper("spider_creeper", SpiderCreeper::new, b -> b.sized(0.9F, 1.4F));
    public static final RegistryObject<EntityType<WindCreeper>> WIND_CREEPER = creeper("wind_creeper", WindCreeper::new, b -> b);
    public static final RegistryObject<EntityType<HydrogenCreeper>> HYDROGEN_CREEPER = creeper("hydrogen_creeper", HydrogenCreeper::new, b -> b);
    public static final RegistryObject<EntityType<EnderCreeper>> ENDER_CREEPER = creeper("ender_creeper", EnderCreeper::new, b -> b);
    public static final RegistryObject<EntityType<StoneCreeper>> STONE_CREEPER = creeper("stone_creeper", StoneCreeper::new, b -> b);
    public static final RegistryObject<EntityType<SolarCreeper>> SOLAR_CREEPER = creeper("solar_creeper", SolarCreeper::new, b -> b);
    public static final RegistryObject<EntityType<CakeCreeper>> CAKE_CREEPER = creeper("cake_creeper", CakeCreeper::new, b -> b);
    public static final RegistryObject<EntityType<FireworkCreeper>> FIREWORK_CREEPER = creeper("firework_creeper", FireworkCreeper::new, b -> b);
    public static final RegistryObject<EntityType<BigBadCreep>> BIG_BAD_CREEP = creeper("big_bad_creep", BigBadCreep::new,
            b -> b.sized(0.6F * 6.0F, 1.7F * 6.0F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SpringCreeper>> SPRING_CREEPER = creeper("spring_creeper", SpringCreeper::new, b -> b);
    public static final RegistryObject<EntityType<Creeperfish>> CREEPERFISH = creeper("creeperfish", Creeperfish::new, b -> b);
    public static final RegistryObject<EntityType<FurnaceCreeper>> FURNACE_CREEPER = creeper("furnace_creeper", FurnaceCreeper::new, EntityType.Builder::fireImmune);
    public static final RegistryObject<EntityType<WarpCreeper>> WARP_CREEPER = creeper("warp_creeper", WarpCreeper::new, b -> b);

    private ECEntities() {
    }

    private static <T extends ElementalCreeper> RegistryObject<EntityType<T>> creeper(String name, EntityType.EntityFactory<T> factory,
                                                                                    UnaryOperator<EntityType.Builder<T>> extra) {
        RegistryObject<EntityType<T>> type = ENTITIES.register(name, () -> extra.apply(EntityType.Builder.of(factory, MobCategory.MONSTER)
                        .sized(0.6F, 1.7F).clientTrackingRange(8).notInPeaceful())
                .build(ENTITIES.key(name)));
        ELEMENTAL.add(type);
        return type;
    }

    /** Every creeper with a spawn egg: all of them but the Ghost Creeper, which only comes back from the dead. */
    public static List<RegistryObject<? extends EntityType<? extends Mob>>> withSpawnEggs() {
        List<RegistryObject<? extends EntityType<? extends Mob>>> list = new ArrayList<>();
        for (var type : ELEMENTAL)
            if (type != GHOST_CREEPER)
                list.add(type);
        list.add(list.indexOf(ICE_CREEPER) + 1, FRIENDLY_CREEPER);
        return list;
    }

    /** What a Big Bad Creep releases: the vanilla creeper and every elemental creeper that spawns on its own. */
    public static List<EntityType<? extends Mob>> bigBadSpawnPool() {
        List<EntityType<? extends Mob>> pool = new ArrayList<>();
        pool.add(EntityTypes.CREEPER);
        for (var type : ELEMENTAL)
            if (type != GHOST_CREEPER && type != BIG_BAD_CREEP)
                pool.add(type.get());
        return pool;
    }

    public static void createAttributes(EntityAttributeCreationEvent event) {
        for (var type : ELEMENTAL)
            if (type != WIND_CREEPER && type != BIG_BAD_CREEP)
                event.put(type.get(), ElementalCreeper.createAttributes().build());
        event.put(WIND_CREEPER.get(), WindCreeper.createAttributes().build());
        event.put(BIG_BAD_CREEP.get(), BigBadCreep.createAttributes().build());
        event.put(FRIENDLY_CREEPER.get(), FriendlyCreeper.createAttributes().build());
    }

    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        for (var type : ELEMENTAL)
            event.register(type.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(FRIENDLY_CREEPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
