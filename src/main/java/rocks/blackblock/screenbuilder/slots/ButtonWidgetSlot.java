package rocks.blackblock.screenbuilder.slots;

import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import rocks.blackblock.screenbuilder.utils.NbtUtils;

public class ButtonWidgetSlot extends ListenerWidgetSlot {

    private MutableText title = null;
    private MutableText lore = null;

    /**
     * Set the title of this button
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ButtonWidgetSlot setTitle(MutableText title) {
        this.title = title;
        this.updateStack();
        return this;
    }

    /**
     * Set the lore of this button
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ButtonWidgetSlot setLore(MutableText lore) {
        this.lore = lore;
        this.updateStack();
        return this;
    }

    /**
     * Set the title of this button
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ButtonWidgetSlot setTitle(String title) {
        return this.setTitle(new LiteralText(title).setStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE)));
    }

    /**
     * Set the lore of this button
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ButtonWidgetSlot setLore(String lore) {
        return this.setLore(new LiteralText(lore).setStyle(Style.EMPTY.withItalic(false)));
    }

    /**
     * Update the stack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void updateStack() {

        ItemStack stack = this.getStack();

        if (stack == null || stack.isEmpty()) {
            return;
        }

        if (this.title != null) {
            stack.setCustomName(this.title);
            this.markDirty();
        }

        if (this.lore != null) {
            NbtUtils.appendLore(stack, this.lore);
            this.markDirty();
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
    public void copyPropertiesToSlot(ButtonWidgetSlot slot) {
        super.copyPropertiesToSlot(slot);

        if (this.title != null) {
            slot.title = this.title.copy();
        }

        ItemStack stack = this.getStack();

        if (stack != null) {
            slot.setStack(stack.copy());
            slot.updateStack();
        }
    }

    /**
     * Clone this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ButtonWidgetSlot clone() {
        ButtonWidgetSlot slot = new ButtonWidgetSlot();
        this.copyPropertiesToSlot(slot);
        return slot;
    }

}
