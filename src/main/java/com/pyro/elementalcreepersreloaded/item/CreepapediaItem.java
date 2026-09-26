package com.pyro.elementalcreepersreloaded.item;

import com.pyro.elementalcreepersreloaded.registry.ECComponents;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * By Albert Hissingberg. Records every creeper its owner kills (the book must be in their inventory) and shows what
 * it knows about them. Right-click an enchanting table with it to banish the ghost from its pages.
 */
public class CreepapediaItem extends Item {
    /** Opens the book screen; set by the client (a dedicated server has no screens). */
    public static Consumer<ItemStack> openScreen = stack -> {
    };

    public CreepapediaItem(Item.Properties properties) {
        super(properties);
    }

    public static CreepapediaData data(ItemStack stack) {
        return stack.getOrDefault(ECComponents.CREEPAPEDIA.get(), CreepapediaData.EMPTY);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) return InteractionResult.PASS;
        if (level.isClientSide())
            openScreen.accept(player.getItemInHand(hand));
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        CreepapediaData data = data(stack);
        if (data.exorcised() || !context.getLevel().getBlockState(context.getClickedPos()).is(Blocks.ENCHANTING_TABLE))
            return this.use(context.getLevel(), context.getPlayer(), context.getHand());
        if (!context.getLevel().isClientSide()) {
            stack.set(ECComponents.CREEPAPEDIA.get(), data.exorcise());
            if (context.getPlayer() != null)
                context.getPlayer().sendSystemMessage(Component.translatable("item.elementalcreepersreloaded.creepapedia.exorcised")
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return data(stack).exorcised();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        CreepapediaData data = data(stack);
        tooltip.accept(Component.translatable("item.elementalcreepersreloaded.creepapedia.author").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.accept(Component.translatable("item.elementalcreepersreloaded.creepapedia.entries", data.entries().size(),
                Creepapedia.allEntries().size()).withStyle(ChatFormatting.GRAY));
        if (data.exorcised())
            tooltip.accept(Component.translatable("item.elementalcreepersreloaded.creepapedia.exorcised_tooltip").withStyle(ChatFormatting.GOLD));
        if (data.creative())
            tooltip.accept(Component.translatable("item.elementalcreepersreloaded.creepapedia.creative").withStyle(ChatFormatting.DARK_PURPLE));
    }
}
