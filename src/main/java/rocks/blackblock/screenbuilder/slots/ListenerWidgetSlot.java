package rocks.blackblock.screenbuilder.slots;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.interfaces.SlotEventListener;
import rocks.blackblock.screenbuilder.text.TextBuilder;

public class ListenerWidgetSlot extends WidgetSlot {

    private SlotEventListener on_left_click = null;
    private SlotEventListener on_right_click = null;
    private SlotEventListener on_middle_click = null;

    /**
     * ListenerWidgetSlot constructor
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     */
    public ListenerWidgetSlot() {
        super(new ItemStack(BBSB.GUI_TRANSPARENT));
    }

    /**
     * WidgetSlot constructor
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public ListenerWidgetSlot(Inventory inventory, Integer index) {
        super(inventory, index);
    }

    /**
     * Add an onClick listener
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public ListenerWidgetSlot addLeftClickListener(SlotEventListener listener) {
        this.on_left_click = listener;
        return this;
    }

    /**
     * Add an onRightClick listener
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public ListenerWidgetSlot addRightClickListener(SlotEventListener listener) {
        this.on_right_click = listener;
        return this;
    }

    /**
     * Add an onMiddleClick listener
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public ListenerWidgetSlot addMiddleClickListener(SlotEventListener listener) {
        this.on_middle_click = listener;
        return this;
    }

    /**
     * Called when the user left-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public void onLeftClick() {
        if (this.on_left_click != null) {
            this.on_left_click.onEvent(this.active_handler, this);
        }
    }

    /**
     * Called when the user right-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public void onRightClick() {

        if (this.on_right_click != null) {
            this.on_right_click.onEvent(this.active_handler, this);
        }

    }

    /**
     * Called when the user middle-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public void onMiddleClick() {

        if (this.on_middle_click != null) {
            this.on_middle_click.onEvent(this.active_handler, this);
        }

    }

    /**
     * Copy over properties to the given slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    slot   The target slot
     */
    public void copyPropertiesToSlot(ListenerWidgetSlot slot) {
        super.copyPropertiesToSlot(slot);
        slot.on_left_click = this.on_left_click;
        slot.on_right_click = this.on_right_click;
        slot.on_middle_click = this.on_middle_click;
    }

    /**
     * Clone this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ListenerWidgetSlot clone(Inventory inventory, Integer index) {

        ListenerWidgetSlot slot;

        if (inventory == null) {
            slot = new ListenerWidgetSlot();
        } else {
            slot = new ListenerWidgetSlot(inventory, index);
        }

        this.copyPropertiesToSlot(slot);
        return slot;
    }
}