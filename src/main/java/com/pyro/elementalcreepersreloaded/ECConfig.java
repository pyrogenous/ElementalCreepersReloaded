package com.pyro.elementalcreepersreloaded;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * config/elementalcreepersreloaded-common.toml. The same settings as the original mod: spawn weights, explosion sizes
 * and the dome / special event switches. Spawn weights are read when a world loads (see ConfigSpawnsBiomeModifier).
 */
public final class ECConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    /** Spawn weight per creeper, keyed by entity name (fire_creeper, ...). 0 turns natural spawning off. */
    public static final Map<String, ForgeConfigSpec.IntValue> SPAWN_WEIGHTS = new LinkedHashMap<>();

    public static final ForgeConfigSpec.IntValue WATER_RADIUS, FIRE_RADIUS, ICE_RADIUS, ELECTRIC_RADIUS, EARTH_RADIUS,
            PSYCHIC_POWER, PSYCHIC_RADIUS, COOKIE_AMOUNT, MAGMA_RADIUS, GHOST_RADIUS, GHOST_CHANCE, LIGHT_RADIUS,
            DARK_RADIUS, REVERSE_RADIUS, SPIDER_RADIUS, WIND_POWER, WIND_RADIUS, STONE_RADIUS, HYDROGEN_RADIUS,
            FIREWORK_RADIUS, BIG_BAD_AMOUNT, SPRING_POWER, SILVER_RADIUS, FURNACE_RADIUS, WARP_RADIUS;
    public static final ForgeConfigSpec.BooleanValue DOME_EXPLOSIONS, SPECIAL_EVENTS, CREEPERFISH_EGGS;

    static {
        BUILDER.comment("How often each creeper spawns, like a vanilla spawn weight (the vanilla creeper has 100). 0 = never")
                .push("spawn_weights");
        spawn("water_creeper", 10);
        spawn("fire_creeper", 10);
        spawn("ice_creeper", 10);
        spawn("electric_creeper", 10);
        spawn("earth_creeper", 10);
        spawn("psychic_creeper", 10);
        spawn("cookie_creeper", 10);
        spawn("magma_creeper", 6);
        spawn("friendly_creeper", 1);
        spawn("illusion_creeper", 6);
        spawn("light_creeper", 4);
        spawn("dark_creeper", 2);
        spawn("reverse_creeper", 6);
        spawn("spider_creeper", 8);
        spawn("wind_creeper", 10);
        spawn("stone_creeper", 10);
        spawn("ender_creeper", 10);
        spawn("hydrogen_creeper", 10);
        spawn("solar_creeper", 10);
        spawn("cake_creeper", 10);
        spawn("firework_creeper", 10);
        spawn("big_bad_creep", 10);
        spawn("spring_creeper", 10);
        spawn("furnace_creeper", 10);
        spawn("warp_creeper", 1);
        BUILDER.pop();

        BUILDER.comment("Explosion sizes, in blocks. A charged creeper multiplies most of them").push("explosions");
        WATER_RADIUS = radius("water_creeper_radius", 4);
        FIRE_RADIUS = radius("fire_creeper_radius", 6);
        ICE_RADIUS = radius("ice_creeper_radius", 8);
        ELECTRIC_RADIUS = radius("electric_creeper_radius", 5);
        EARTH_RADIUS = radius("earth_creeper_radius", 8);
        PSYCHIC_POWER = radius("psychic_creeper_power", 8);
        PSYCHIC_RADIUS = radius("psychic_creeper_radius", 5);
        COOKIE_AMOUNT = BUILDER.comment("Cookies a Cookie Creeper drops").defineInRange("cookies_dropped", 5, 1, 64);
        MAGMA_RADIUS = radius("magma_creeper_radius", 3);
        GHOST_RADIUS = radius("ghost_creeper_radius", 5);
        GHOST_CHANCE = BUILDER.comment("Chance (percent) that a creeper killed by a player comes back as a Ghost Creeper")
                .defineInRange("ghost_creeper_chance", 35, 0, 100);
        LIGHT_RADIUS = radius("light_creeper_radius", 4);
        DARK_RADIUS = radius("dark_creeper_radius", 12);
        REVERSE_RADIUS = radius("reverse_creeper_radius", 8);
        SPIDER_RADIUS = radius("spider_creeper_radius", 12);
        WIND_POWER = radius("wind_creeper_power", 3);
        WIND_RADIUS = radius("wind_creeper_radius", 5);
        STONE_RADIUS = radius("stone_creeper_radius", 8);
        HYDROGEN_RADIUS = radius("hydrogen_creeper_radius", 64);
        FIREWORK_RADIUS = radius("firework_creeper_radius", 5);
        BIG_BAD_AMOUNT = BUILDER.comment("Creepers a Big Bad Creep releases when it explodes")
                .defineInRange("big_bad_creep_amount", 7, 0, 64);
        SPRING_POWER = radius("spring_creeper_power", 2);
        SILVER_RADIUS = radius("creeperfish_radius", 5);
        FURNACE_RADIUS = radius("furnace_creeper_radius", 3);
        WARP_RADIUS = radius("warp_creeper_radius", 7);
        BUILDER.pop();

        BUILDER.push("general");
        DOME_EXPLOSIONS = BUILDER.comment("Water, Earth, Light, Magma and Ice Creepers fill a dome instead of scattering blocks")
                .define("dome_explosions", false);
        SPECIAL_EVENTS = BUILDER.comment("Creepers wear something special on Halloween, November 12 and Christmas")
                .define("special_events", true);
        CREEPERFISH_EGGS = BUILDER.comment("Generate veins of Creeperfish eggs (stone hiding a creeper) in windswept hills")
                .define("creeperfish_eggs", true);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private ECConfig() {
    }

    private static void spawn(String creeper, int weight) {
        SPAWN_WEIGHTS.put(creeper, BUILDER.defineInRange(creeper, weight, 0, 1000));
    }

    private static ForgeConfigSpec.IntValue radius(String name, int value) {
        return BUILDER.defineInRange(name, value, 0, 128);
    }

    public static int spawnWeight(String creeper) {
        ForgeConfigSpec.IntValue value = SPAWN_WEIGHTS.get(creeper);
        return value == null ? 0 : value.get();
    }
}
