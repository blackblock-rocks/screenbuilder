package rocks.blackblock.screenbuilder.slots;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import rocks.blackblock.bib.monitor.GlitchGuru;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.interfaces.SlotEventListener;

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
     * Add a listener for all clicks
     *
     * @since 0.5.0
     */
    public ListenerWidgetSlot addAllClicksListener(SlotEventListener listener) {
        this.on_left_click = listener;
        this.on_right_click = listener;
        this.on_middle_click = listener;
        return this;
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
        this.doEventHandler(this.on_left_click);
    }

    /**
     * Called when the user right-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public void onRightClick() {
        this.doEventHandler(this.on_right_click);
    }

    /**
     * Called when the user middle-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public void onMiddleClick() {
        this.doEventHandler(this.on_middle_click);
    }

    /**
     * Safely call an event handler
     * @since 0.5.0
     */
    private void doEventHandler(SlotEventListener listener) {

        if (listener == null) {
            return;
        }

        try {
            listener.onEvent(this.active_handler, this);
        } catch (Throwable e) {
            GlitchGuru.registerThrowable(e);
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