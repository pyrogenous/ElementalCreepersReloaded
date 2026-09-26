package com.pyro.elementalcreepersreloaded.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import com.pyro.elementalcreepersreloaded.entity.Creeperfish;
import com.pyro.elementalcreepersreloaded.registry.ECEntities;
import com.pyro.lomlibreloaded.nitea.NiteaSupport;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.Nullable;

/**
 * A Creeperfish hiding in a stone block, like silverfish in infested stone. Looks like its host block, breaks faster,
 * and lets the Creeperfish out when mined (silk touch keeps it in and drops the host block instead).
 */
public class CreeperfishEggBlock extends Block {
    public static final MapCodec<CreeperfishEggBlock> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("host").forGetter(CreeperfishEggBlock::getHostBlock), propertiesCodec())
                    .apply(i, CreeperfishEggBlock::new));
    private static final Map<Block, CreeperfishEggBlock> BY_HOST = new IdentityHashMap<>();

    private final Block hostBlock;

    public CreeperfishEggBlock(Block hostBlock, BlockBehaviour.Properties properties) {
        super(properties.destroyTime(hostBlock.defaultDestroyTime() / 2.0F).explosionResistance(0.75F));
        this.hostBlock = hostBlock;
        BY_HOST.put(hostBlock, this);
    }

    @Override
    public MapCodec<? extends CreeperfishEggBlock> codec() {
        return CODEC;
    }

    public Block getHostBlock() {
        return this.hostBlock;
    }

    /** The egg block hiding in {@code host}, or null if a Creeperfish can't hide in it. */
    public static @Nullable BlockState eggFor(BlockState host) {
        CreeperfishEggBlock egg = BY_HOST.get(host.getBlock());
        return egg == null ? null : egg.defaultBlockState();
    }

    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.spawnAfterBreak(state, level, pos, tool, dropExperience);
        if (level.getGameRules().get(GameRules.BLOCK_DROPS) && !EnchantmentHelper.hasTag(tool, EnchantmentTags.PREVENTS_INFESTED_SPAWNS))
            NiteaSupport.guard(ElementalCreepers.nitea(), "creeperfish egg hatching", () -> hatch(level, pos));
    }

    private static void hatch(ServerLevel level, BlockPos pos) {
        Creeperfish creeper = ECEntities.CREEPERFISH.get().create(level, EntitySpawnReason.TRIGGERED);
        if (creeper != null) {
            creeper.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
            level.addFreshEntity(creeper);
            creeper.spawnAnim();
        }
    }
}
