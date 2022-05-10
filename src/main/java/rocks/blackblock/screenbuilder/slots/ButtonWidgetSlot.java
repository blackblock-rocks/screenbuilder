package rocks.blackblock.screenbuilder.slots;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.text.TextGroup;
import rocks.blackblock.screenbuilder.textures.WidgetTexture;
import rocks.blackblock.screenbuilder.utils.NbtUtils;

public class ButtonWidgetSlot extends ListenerWidgetSlot {

    public enum BackgroundType {
        LARGE,
        MEDIUM,
        SMALL,
        EXTRA_SMALL,
        LOWER_SMALL
    }

    private MutableText title = null;
    private MutableText lore = null;
    private BackgroundType background_type = null;
    private String button_text = null;

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

        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean("polyvalent:hide_info", true);
    }

    /**
     * Copy over properties to the given slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    slot   The target slot
     */
    public void copyPropertiesToSlot(ButtonWidgetSlot slot) {
        super.copyPropertiesToSlot(slot);

        if (this.title != null) {
            slot.title = this.title.copy();
        }

        slot.background_type = this.background_type;

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

    /**
     * Set the background type
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public ButtonWidgetSlot setBackgroundType(BackgroundType type) {
        this.background_type = type;
        return this;
    }

    /**
     * Set the button text
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public ButtonWidgetSlot setButtonText(String text) {
        this.button_text = text;
        return this;
    }

    /**
     * This slot was used in the given TextBuilder
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     *
     * @param    builder   The text builder
     */
    @Override
    public void addToTextBuilder(TextBuilder builder) {

        if (this.background_type == null) {
            super.addToTextBuilder(builder);
            return;
        }

        WidgetTexture bg;

        bg = switch (this.background_type) {
            case LARGE -> BBSB.BUTTON_LARGE;
            case MEDIUM -> BBSB.BUTTON_MEDIUM;
            case SMALL, LOWER_SMALL -> BBSB.BUTTON_SMALL;
            case EXTRA_SMALL -> BBSB.BUTTON_EXTRA_SMALL;
        };

        int offset = switch (this.background_type) {
            case LARGE -> -1;
            case MEDIUM, SMALL -> 0;
            case LOWER_SMALL -> 1;
            case EXTRA_SMALL -> 1;
        };

        int x = this.getSlotX() * 18;
        int y = this.getSlotY() * 18;

        x += 7 + offset;
        y += 17 + offset;

        bg.addToBuilder(builder, x, y);

        if (this.button_text != null) {
            int slot_y = this.getSlotY();
            Font font = Font.LH_INVENTORY_SLOT.getFontForLine(slot_y);

            int text_x = this.getSlotXInPixels();

            text_x += (font.getWidth(this.button_text) + 9) / 2;

            builder.setCursor(text_x);
            TextGroup group = builder.createNewGroup();
            group.setColor(TextColor.fromRgb(0x878787));
            builder.print(this.button_text, font);
        }

        super.addToTextBuilder(builder);
    }

}
