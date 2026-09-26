package com.pyro.elementalcreepersreloaded.registry;

import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import com.pyro.elementalcreepersreloaded.block.CreeperfishEggBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ECBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ElementalCreepers.MOD_ID);

    /** The Creeperfish eggs, one per block it can hide in. */
    public static final List<RegistryObject<CreeperfishEggBlock>> CREEPERFISH_EGGS = new ArrayList<>();

    public static final RegistryObject<CreeperfishEggBlock> CREEPERFISH_STONE = egg("creeperfish_stone", () -> Blocks.STONE);
    public static final RegistryObject<CreeperfishEggBlock> CREEPERFISH_COBBLESTONE = egg("creeperfish_cobblestone", () -> Blocks.COBBLESTONE);
    public static final RegistryObject<CreeperfishEggBlock> CREEPERFISH_STONE_BRICKS = egg("creeperfish_stone_bricks", () -> Blocks.STONE_BRICKS);
    public static final RegistryObject<CreeperfishEggBlock> CREEPERFISH_MOSSY_STONE_BRICKS = egg("creeperfish_mossy_stone_bricks", () -> Blocks.MOSSY_STONE_BRICKS);
    public static final RegistryObject<CreeperfishEggBlock> CREEPERFISH_CRACKED_STONE_BRICKS = egg("creeperfish_cracked_stone_bricks", () -> Blocks.CRACKED_STONE_BRICKS);
    public static final RegistryObject<CreeperfishEggBlock> CREEPERFISH_CHISELED_STONE_BRICKS = egg("creeperfish_chiseled_stone_bricks", () -> Blocks.CHISELED_STONE_BRICKS);

    private ECBlocks() {
    }

    private static RegistryObject<CreeperfishEggBlock> egg(String name, Supplier<Block> host) {
        RegistryObject<CreeperfishEggBlock> block = BLOCKS.register(name, () -> new CreeperfishEggBlock(host.get(),
                BlockBehaviour.Properties.of().mapColor(MapColor.CLAY).setId(BLOCKS.key(name))));
        CREEPERFISH_EGGS.add(block);
        return block;
    }
}
