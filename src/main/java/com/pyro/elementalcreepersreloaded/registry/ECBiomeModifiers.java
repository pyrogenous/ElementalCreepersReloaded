package com.pyro.elementalcreepersreloaded.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pyro.elementalcreepersreloaded.ECConfig;
import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Biome modifiers that read the config when a world loads, so spawn weights and the Creeperfish egg veins can be
 * changed without editing a datapack. The JSON files in data/elementalcreepersreloaded/forge/biome_modifier use them.
 */
public final class ECBiomeModifiers {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ElementalCreepers.MOD_ID);

    public static final RegistryObject<MapCodec<ConfigSpawns>> CONFIG_SPAWNS = SERIALIZERS.register("config_spawns", () -> ConfigSpawns.CODEC);
    public static final RegistryObject<MapCodec<CreeperfishEggs>> CREEPERFISH_EGGS = SERIALIZERS.register("creeperfish_eggs", () -> CreeperfishEggs.CODEC);

    private ECBiomeModifiers() {
    }

    /** Adds a creeper to the biomes' spawns with the weight from spawn_weights.<entity name> (none when 0). */
    public record ConfigSpawns(HolderSet<Biome> biomes, EntityType<?> type, int minCount, int maxCount, int weightMultiplier) implements BiomeModifier {
        public static final MapCodec<ConfigSpawns> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Biome.LIST_CODEC.fieldOf("biomes").forGetter(ConfigSpawns::biomes),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(ConfigSpawns::type),
                Codec.INT.fieldOf("min_count").forGetter(ConfigSpawns::minCount),
                Codec.INT.fieldOf("max_count").forGetter(ConfigSpawns::maxCount),
                Codec.INT.optionalFieldOf("weight_multiplier", 1).forGetter(ConfigSpawns::weightMultiplier)
        ).apply(i, ConfigSpawns::new));

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase != Phase.ADD || !this.biomes.contains(biome)) return;
            String name = BuiltInRegistries.ENTITY_TYPE.getKey(this.type).getPath();
            int weight = ECConfig.spawnWeight(name) * this.weightMultiplier;
            if (weight > 0)
                builder.getMobSpawnSettings().addSpawn(this.type.getCategory(), weight,
                        new MobSpawnSettings.SpawnerData(this.type, this.minCount, this.maxCount));
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return CODEC;
        }
    }

    /** Adds the Creeperfish egg veins unless creeperfish_eggs is off. */
    public record CreeperfishEggs(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features) implements BiomeModifier {
        public static final MapCodec<CreeperfishEggs> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Biome.LIST_CODEC.fieldOf("biomes").forGetter(CreeperfishEggs::biomes),
                PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(CreeperfishEggs::features)
        ).apply(i, CreeperfishEggs::new));

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD && this.biomes.contains(biome) && ECConfig.CREEPERFISH_EGGS.get())
                this.features.forEach(feature -> builder.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, feature));
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return CODEC;
        }
    }
}
