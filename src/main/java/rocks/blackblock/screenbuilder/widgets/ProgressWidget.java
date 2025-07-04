package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.NotNull;
import rocks.blackblock.bib.util.BibLog;
import rocks.blackblock.chunker.chunk.Lump;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.screen.ScreenInfo;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.slots.ClickType;
import rocks.blackblock.screenbuilder.slots.ListenerWidgetSlot;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.textures.WidgetTexture;

/**
 * Show some kind of progress bar
 *
 * @since   0.1.1
 */
@SuppressWarnings("unused")
public class ProgressWidget extends TextureWidget<Integer> {

    // The max value
    public final static int MAX_VALUE = 100;

    // The max amount to use
    protected int max_amount = 100;

    /**
     * The optional slot event listener to use
     */
    protected SlotEventListener slot_event_listener = null;

    public ProgressWidget(WidgetTexture widget_texture) {
        super(widget_texture);
    }

    public ProgressWidget(Identifier texture_identifier) {
        super(texture_identifier);
    }

    /**
     * Set the slot click listener
     */
    public void setSlotClickListener(SlotEventListener listener) {
        this.slot_event_listener = listener;
    }

    /**
     * Get the minimum amount of texture pieces
     *
     * @since   0.1.1
     */
    public int getWantedAmountOfTexturePieces() {
        return this.max_amount;
    }

    /**
     * Add this widget to the textbuilder with the given value
     *
     * @param   builder
     * @param   value
     *
     * @since   0.1.1
     */
    @Override
    public void addWithValue(TextBuilder builder, Integer value) {

        if (value instanceof Integer int_value) {
            float percentage = this.calculatePercentageValue(int_value);
            int total_piece_count = this.widget_texture.getAmountOfPieces();
            int wanted_piece_count = Math.round(((percentage / 100) * total_piece_count));

            this.widget_texture.addToBuilder(builder, this.x, this.y, wanted_piece_count);
        } else {
            this.widget_texture.addToBuilder(builder, this.x, this.y);
        }
    }

    /**
     * Get the current percentage value
     *
     * @since   0.1.1
     */
    public float calculatePercentageValue(int value) {
        return (((float) value / (float) this.max_amount) * 100);
    }

    /**
     * Set the max amount
     *
     * @since   0.1.1
     */
    public void setMaxAmount(int max_amount) {
        this.max_amount = max_amount;
    }

    /**
     * Register this widget
     *
     * @since   0.2.1
     */
    @Override
    public void register() {

        if (this.screen_builder == null) {
            return;
        }

        int container_y = this.screen_builder.getContainerY(this.y);
        int title_y = this.screen_builder.convertToUnderlyingTitleY(this.y);

        // This is the correct registration
        this.widget_texture.registerYOffset(title_y);

        // This one isn't, but for some reason it always does it
        this.widget_texture.registerYOffset(this.y);
    }

    /**
     * Get the nearest slot index starting from the left
     */
    protected int getNearestSlotIndexFromLeft(ScreenInfo info, int x, int y, int width) {
        int result = info.getSlotIndex(x, y);

        if (result > -1) {
            return result;
        }

        for (int test_x = x + 15; test_x < x + width; test_x += 15) {
            result = info.getSlotIndex(test_x, y);

            if (result > -1) {
                return result;
            }
        }

        return -1;
    }

    /**
     * Get the nearest slot index starting from the right
     */
    protected int getNearestSlotIndexFromRight(ScreenInfo info, int x, int y, int width) {
        int result = info.getSlotIndex(x, y);

        if (result > -1) {
            return result;
        }

        for (int test_x = x - 15; test_x > x - width; test_x -= 15) {
            result = info.getSlotIndex(test_x, y);

            if (result > -1) {
                return result;
            }
        }

        return -1;
    }

    /**
     * Add the widget to the given screenbuilder.
     *
     * @param   builder   The builder to add to
     * @param   id        The unique id of the widget in this screenbuilder
     * @param   x         The x position of the widget in the current screenbuilder's gui
     * @param   y         The y position of the widget in the current screenbuilder's gui
     */
    @Override
    public void addToScreenBuilder(ScreenBuilder builder, String id, int x, int y) {
        super.addToScreenBuilder(builder, id, x, y);

        if (this.slot_event_listener == null) {
            return;
        }

        ScreenInfo screen_info = builder.getScreenInfo();
        int width = this.widget_texture.getOriginalWidth();
        int start_slot_index = this.getNearestSlotIndexFromLeft(screen_info, x, y, width);

        if (start_slot_index == -1) {
            return;
        }

        int end_slot_index = this.getNearestSlotIndexFromRight(screen_info, x + width, y, width);

        if (end_slot_index == -1) {
            return;
        }

        int max_slot_nr = end_slot_index - start_slot_index;

        for (int slot_index = start_slot_index; slot_index <= end_slot_index; slot_index++) {

            ButtonWidgetSlot slot = builder.addButton(slot_index);
            ItemStack empty = new ItemStack(BBSB.GUI_TRANSPARENT);
            // Hide tooltip using the new TooltipDisplayComponent API in 1.21.6
            empty.set(DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(true, new java.util.LinkedHashSet<>()));
            slot.setStack(empty);

            int current_slot_nr = slot_index - start_slot_index;

            slot.addLeftClickListener((screen, base_slot) -> {
                if (base_slot instanceof ListenerWidgetSlot button_slot) {
                    this.callEventListener(ClickType.LEFT, screen, button_slot, current_slot_nr, max_slot_nr);
                }
            });

            slot.addMiddleClickListener((screen, base_slot) -> {
                if (base_slot instanceof ListenerWidgetSlot button_slot) {
                    this.callEventListener(ClickType.MIDDLE, screen, button_slot, current_slot_nr, max_slot_nr);
                }
            });

            slot.addRightClickListener((screen, base_slot) -> {
                if (base_slot instanceof ListenerWidgetSlot button_slot) {
                    this.callEventListener(ClickType.RIGHT, screen, button_slot, current_slot_nr, max_slot_nr);
                }
            });
        }
    }

    /**
     * Actually call the event listener
     *
     * @param   click_type
     * @param   screen
     * @param   slot
     */
    protected void callEventListener(ClickType click_type, TexturedScreenHandler screen, ListenerWidgetSlot slot, int slot_nr, int total_slots) {

        ItemStack stack = slot.getStack();
        Lump lump = null;

        var opener = screen.getSessionOpenerFactory();

        Inventory inventory = screen.getActualInventory();

        if (this.slot_event_listener != null) {
            this.slot_event_listener.onClick(screen, slot, click_type, stack, slot_nr, total_slots);
        }
    }

    /**
     * Append to a BibLog.Arg representation
     *
     * @since 0.5.0
     */
    @Override
    public void appendToBibLogArg(@NotNull BibLog.Arg arg) {
        super.appendToBibLogArg(arg);
        arg.add("max_amount", this.max_amount);
    }

    @FunctionalInterface
    public interface SlotEventListener {
        void onClick(TexturedScreenHandler screen, ListenerWidgetSlot slot, ClickType type, ItemStack stack, int slot_nr, int total_slots);
    }
}
