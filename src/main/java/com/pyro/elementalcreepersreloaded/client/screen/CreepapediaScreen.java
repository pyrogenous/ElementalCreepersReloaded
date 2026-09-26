package com.pyro.elementalcreepersreloaded.client.screen;

import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import com.pyro.elementalcreepersreloaded.entity.GhostCreeper;
import com.pyro.elementalcreepersreloaded.item.Creepapedia;
import com.pyro.elementalcreepersreloaded.item.CreepapediaData;
import com.pyro.elementalcreepersreloaded.item.CreepapediaItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * The Creepapedia: a list of the creepers found so far, 12 per page; click one for its page, with the creeper itself
 * turning to follow the mouse. Below the book, a button to report a bug from the chat.
 */
public class CreepapediaScreen extends Screen {
    private static final int BOOK_SIZE = 192;
    private static final int PER_PAGE = 12;
    private static final int TEXT_COLOR = 0xFF000000;
    private static final int TITLE_COLOR = 0xFF0D8A0B;

    private final List<EntityType<?>> entries = new ArrayList<>();
    private final boolean exorcised;
    private final Map<EntityType<?>, @Nullable LivingEntity> models = new HashMap<>();
    private int page;
    private @Nullable EntityType<?> open;

    public CreepapediaScreen(ItemStack book) {
        super(Component.translatable("item.elementalcreepersreloaded.creepapedia"));
        CreepapediaData data = CreepapediaItem.data(book);
        for (EntityType<?> type : Creepapedia.allEntries())
            if (data.has(Creepapedia.id(type)))
                this.entries.add(type);
        this.exorcised = data.exorcised();
    }

    private int left() {
        return (this.width - BOOK_SIZE) / 2;
    }

    private int top() {
        return (this.height - BOOK_SIZE) / 2;
    }

    private int pageCount() {
        return Math.max(1, (this.entries.size() + PER_PAGE - 1) / PER_PAGE);
    }

    @Override
    protected void init() {
        this.clearWidgets();
        int left = this.left();
        int top = this.top();
        if (this.open == null) {
            int first = this.page * PER_PAGE;
            for (int i = first; i < Math.min(first + PER_PAGE, this.entries.size()); i++) {
                EntityType<?> type = this.entries.get(i);
                this.addRenderableWidget(new EntryLink(left + 40, top + 16 + 11 * (i - first), 110, type.getDescription(), b -> {
                    this.open = type;
                    this.init();
                }));
            }
            if (this.page > 0)
                this.addRenderableWidget(new PageButton(left + 43, top + 157, false, b -> {
                    this.page--;
                    this.init();
                }, true));
            if (this.page < this.pageCount() - 1)
                this.addRenderableWidget(new PageButton(left + 116, top + 157, true, b -> {
                    this.page++;
                    this.init();
                }, true));
        } else {
            this.addRenderableWidget(new PageButton(left + 43, top + 157, false, b -> {
                this.open = null;
                this.init();
            }, true));
        }
        int buttonsTop = Math.min(this.height - 24, top + BOOK_SIZE + 4);
        this.addRenderableWidget(Button.builder(Component.translatable("gui.elementalcreepersreloaded.report_bug"), b ->
                        this.minecraft.gui.setScreen(new ChatScreen("/elementalcreepers report bug ", false)))
                .bounds(this.width / 2 - 100, buttonsTop, 98, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
                .bounds(this.width / 2 + 2, buttonsTop, 98, 20).build());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BookViewScreen.BOOK_LOCATION, this.left(), this.top(), 0.0F, 0.0F, BOOK_SIZE, BOOK_SIZE, 256, 256);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        int left = this.left();
        int top = this.top();
        if (this.open == null) {
            Component indicator = Component.translatable("book.pageIndicator", this.page + 1, this.pageCount());
            graphics.text(this.font, indicator, left + 148 - this.font.width(indicator), top + 16 - 12, TEXT_COLOR, false);
            if (this.entries.isEmpty())
                graphics.textWithWordWrap(this.font, Component.translatable("book.elementalcreepersreloaded.empty"), left + 36, top + 30, 114, TEXT_COLOR, false);
            return;
        }
        EntityType<?> type = this.open;
        Component name = type.getDescription();
        graphics.text(this.font, name, left + 36 + (114 - this.font.width(name)) / 2, top + 16, TITLE_COLOR, false);
        LivingEntity model = this.model(type);
        if (model != null)
            InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, left + 66, top + 28, left + 126, top + 92, 22, 0.0625F, mouseX, mouseY, model);

        // The ghost's page is unreadable until an enchanting table banishes the spook
        Style style = Style.EMPTY.withoutShadow();
        if (model instanceof GhostCreeper && !this.exorcised)
            style = style.applyFormat(ChatFormatting.OBFUSCATED);
        String key = Creepapedia.textKey(type);
        Component text = Component.translatable(key).withStyle(style);
        float scale = 0.75F;
        List<FormattedCharSequence> lines = this.font.split(text, (int) (114 / scale));
        graphics.pose().pushMatrix();
        graphics.pose().translate(left + 36, top + 96);
        graphics.pose().scale(scale, scale);
        int maxLines = (int) ((150 - 96) / (9 * scale));
        for (int i = 0; i < Math.min(lines.size(), maxLines); i++)
            graphics.text(this.font, lines.get(i), 0, i * 9, TEXT_COLOR, false);
        graphics.pose().popMatrix();
    }

    private @Nullable LivingEntity model(EntityType<?> type) {
        return this.models.computeIfAbsent(type, t -> {
            if (this.minecraft.level == null) return null;
            try {
                if (!(t.create(this.minecraft.level, EntitySpawnReason.LOAD) instanceof LivingEntity living)) return null;
                // Never added to the level, so it has no ID; the renderer reads one. Negative: no clash with real entities
                living.setId(-1 - this.models.size());
                return living;
            } catch (RuntimeException e) {
                ElementalCreepers.nitea().captureException(e);
                ElementalCreepers.LOGGER.error("Couldn't create the Creepapedia model of {}", BuiltInRegistries.ENTITY_TYPE.getKey(t), e);
                return null;
            }
        });
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /** A creeper's name in the list: black text, underlined under the mouse. */
    private static class EntryLink extends Button {
        EntryLink(int x, int y, int width, Component name, Button.OnPress onPress) {
            super(x, y, width, 10, name, onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            Component text = this.isHoveredOrFocused() ? this.getMessage().copy().withStyle(ChatFormatting.UNDERLINE) : this.getMessage();
            graphics.text(net.minecraft.client.Minecraft.getInstance().font, text, this.getX(), this.getY() + 1, TEXT_COLOR, false);
        }
    }
}
