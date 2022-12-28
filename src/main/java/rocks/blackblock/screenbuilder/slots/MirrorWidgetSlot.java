package rocks.blackblock.screenbuilder.slots;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import rocks.blackblock.screenbuilder.interfaces.SelectEventListener;
import rocks.blackblock.screenbuilder.utils.NbtUtils;

public class MirrorWidgetSlot extends ListenerWidgetSlot {

    protected SelectEventListener on_change_item = null;

    /**
     * ListenerWidgetSlot constructor
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     */
    public MirrorWidgetSlot() {
        super();
    }

    /**
     * WidgetSlot constructor
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     */
    public MirrorWidgetSlot(Inventory inventory, Integer index) {
        super(inventory, index);
    }

    /**
     * Set a change listener
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     */
    public void setChangeListener(SelectEventListener on_change_item) {
        this.on_change_item = on_change_item;
    }

    /**
     * Called when the user left-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     */
    public void onLeftClick() {

        ItemStack cursor_stack = this.active_handler.getCursorStack();

        if (cursor_stack != null && !cursor_stack.isEmpty()) {
            ItemStack new_stack = cursor_stack.copy();

            this.setStack(new_stack);
            this.fireChangeEvent();
            return;
        }

        super.onLeftClick();
    }

    /**
     * Called when the user right-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     */
    public void onRightClick() {

        ItemStack cursor_stack = this.active_handler.getCursorStack();
        ItemStack current_stack = this.getStack();

        if (cursor_stack != null && !cursor_stack.isEmpty()) {
            ItemStack new_stack = cursor_stack.copy();

            if (current_stack == null || current_stack.isEmpty() || !current_stack.isStackable() || !NbtUtils.areEqualIgnoreDamage(current_stack, new_stack)) {
                this.setStack(new_stack);
                this.fireChangeEvent();
                return;
            }

            int max_count = current_stack.getMaxCount();

            if (current_stack.getCount() < max_count) {
                current_stack.setCount(current_stack.getCount() + 1);
                this.fireChangeEvent();
            }

            return;
        } else if (current_stack != null && !current_stack.isEmpty()) {
            current_stack.setCount(current_stack.getCount() - 1);
            this.fireChangeEvent();
        }

        super.onRightClick();
    }

    /**
     * Fire the change event
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public void fireChangeEvent() {

        if (this.on_change_item == null) {
            return;
        }

        this.on_change_item.onSelect(this.getHandler(), this.getStack());
    }

    /**
     * Copy over properties to the given slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    slot   The target slot
     */
    public void copyPropertiesToSlot(MirrorWidgetSlot slot) {
        super.copyPropertiesToSlot(slot);
        slot.on_change_item = this.on_change_item;
    }

    /**
     * Clone this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public MirrorWidgetSlot clone(Inventory inventory, Integer index) {

        MirrorWidgetSlot slot;

        if (inventory == null) {
            slot = new MirrorWidgetSlot();
        } else {
            slot = new MirrorWidgetSlot(inventory, index);
        }

        this.copyPropertiesToSlot(slot);
        return slot;
    }
}