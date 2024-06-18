package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import rocks.blackblock.bib.util.BibItem;
import rocks.blackblock.chunker.chunk.Lump;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.interfaces.MapSlotEventListener;
import rocks.blackblock.screenbuilder.interfaces.MapWidgetAddedListener;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.slots.ClickType;
import rocks.blackblock.screenbuilder.slots.ListenerWidgetSlot;
import rocks.blackblock.screenbuilder.text.MiniText;
import rocks.blackblock.screenbuilder.text.TextBuilder;

/**
 * Show a map tile
 *
 * @since   0.1.1
 */
public class MapWidget extends TextureWidget {

    /**
     * The optional slot index this might use to add a click listener to
     */
    protected Integer slot_index = null;

    /**
     * The optional slot event listener to use
     */
    protected MapSlotEventListener slot_event_listener = null;

    /**
     * An optional listener for when the map is added to the screen
     */
    protected MapWidgetAddedListener added_with_value_listener = null;

    /**
     * Create the widget
     *
     * @since 0.1.1
     */
    public MapWidget() {
        super((Identifier) null);
    }

    /**
     * Listen for value updates
     *
     * @since   0.1.1
     */
    @Override
    public void addWithValue(TextBuilder builder, Object value) {
        if (value instanceof Lump lump) {
            Slot slot = null;

            builder.printImage(lump.getImage(), this.x, this.y);

            if (this.slot_index != null) {
                TexturedScreenHandler screen = builder.getScreenHandler();

                if (screen != null) {
                    slot = screen.getSlot(this.slot_index);

                    if (slot != null) {
                        ItemStack stack = slot.getStack();
                        NbtCompound nbt = BibItem.getCustomNbt(stack);

                        ChunkPos pos = lump.getPos();
                        nbt.putLong("lump_id", pos.toLong());

                        MiniText title = new MiniText("Chunk ");
                        title.setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false));

                        title.append("" + pos.x).setStyle(Style.EMPTY.withColor(Formatting.AQUA));
                        title.append("x").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
                        title.append("" + pos.z).setStyle(Style.EMPTY.withColor(Formatting.AQUA));

                        BibItem.setCustomName(stack, title);
                    }
                }
            }

            if (this.added_with_value_listener != null) {
                this.added_with_value_listener.onAdded(builder, lump, slot);
            }
        }
    }

    /**
     * Set the slot click listener
     *
     * @since   0.1.1
     */
    public void setSlotClickListener(int slot_index, MapSlotEventListener listener) {
        this.slot_index = slot_index;
        this.slot_event_listener = listener;
    }

    /**
     * Set tha added listener
     *
     * @since   0.1.1
     */
    public void setMapAddedListener(MapWidgetAddedListener listener) {
        this.added_with_value_listener = listener;
    }

    /**
     * Add the widget to the given screenbuilder.
     *
     * @param   builder   The builder to add to
     * @param   id        The unique id of the widget in this screenbuilder
     * @param   x         The x position of the widget in the current screenbuilder's gui
     * @param   y         The y position of the widget in the current screenbuilder's gui
     *
     * @since   0.1.1
     */
    @Override
    public void addToScreenBuilder(ScreenBuilder builder, String id, int x, int y) {
        super.addToScreenBuilder(builder, id, x, y);

        MapWidget that = this;

        if (this.slot_index != null && this.slot_event_listener != null) {
            ButtonWidgetSlot slot = builder.addButton(this.slot_index);

            slot.setStack(BBSB.GUI_TRANSPARENT);

            slot.addLeftClickListener((screen, base_slot) -> {
                if (base_slot instanceof ListenerWidgetSlot button_slot) {
                    that.callEventListener(ClickType.LEFT, screen, button_slot);
                }
            });

            slot.addMiddleClickListener((screen, base_slot) -> {
                if (base_slot instanceof ListenerWidgetSlot button_slot) {
                    that.callEventListener(ClickType.MIDDLE, screen, button_slot);
                }
            });

            slot.addRightClickListener((screen, base_slot) -> {
                if (base_slot instanceof ListenerWidgetSlot button_slot) {
                    that.callEventListener(ClickType.RIGHT, screen, button_slot);
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
     *
     * @since   0.1.1
     */
    protected void callEventListener(ClickType click_type, TexturedScreenHandler screen, ListenerWidgetSlot slot) {

        ItemStack stack = slot.getStack();
        Lump lump = null;

        Inventory inventory = screen.getActualInventory();

        if (this.slot_event_listener != null) {
            this.slot_event_listener.onClick(screen, slot, click_type, stack, lump);
        }
    }
}
