package rocks.blackblock.screenbuilder.slots;

import rocks.blackblock.screenbuilder.interfaces.BooleanEventListener;
import rocks.blackblock.screenbuilder.items.GuiItem;

public class CheckboxWidgetSlot extends ButtonWidgetSlot {

    // The current value
    protected Boolean value = null;

    // The change listener
    protected BooleanEventListener on_change_listener = null;

    public CheckboxWidgetSlot() {
        super();
        this.refreshStack();
    }

    /**
     * Called when the user left-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public void onLeftClick() {

        if (this.value == null || this.value == false) {
            this.setValue(true);
        } else {
            this.setValue(false);
        }

        super.onLeftClick();
    }

    /**
     * Set the change listener
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setChangeListener(BooleanEventListener listener) {
        this.on_change_listener = listener;
    }

    /**
     * Set the value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setValue(Boolean value) {

        boolean emit_change = this.value != value;

        this.value = value;
        this.refreshStack();

        if (emit_change && this.on_change_listener != null) {
            this.on_change_listener.onChange(this.getHandler(), value);
        }
    }

    /**
     * Get the value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public Boolean getValue() {
        return this.value;
    }

    /**
     * Refresh the item
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    protected void refreshStack() {
        if (this.value == null || !this.value) {
            this.setStack(GuiItem.get("checkbox_unchecked"));
            this.setLore("FALSE");
        } else {
            this.setStack(GuiItem.get("checkbox_checked"));
            this.setLore("TRUE");
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
    public void copyPropertiesToSlot(CheckboxWidgetSlot slot) {
        super.copyPropertiesToSlot(slot);

        if (this.on_change_listener != null) {
            slot.on_change_listener = this.on_change_listener;
        }
    }

}