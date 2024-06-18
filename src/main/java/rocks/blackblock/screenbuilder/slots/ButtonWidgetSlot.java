package rocks.blackblock.screenbuilder.slots;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import rocks.blackblock.bib.util.BibItem;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.text.TextGroup;
import rocks.blackblock.screenbuilder.textures.BaseTexture;
import rocks.blackblock.screenbuilder.textures.IconTexture;

import java.util.ArrayList;
import java.util.List;

public class ButtonWidgetSlot extends ListenerWidgetSlot {

    public enum BackgroundType {
        LARGE,
        MEDIUM,
        SMALL,
        EXTRA_SMALL,
        LOWER_SMALL,
        TOP_TAB_SELECTED,
        TOP_TAB_UNSELECTED,
        LEFT_TAB_SELECTED,
        LEFT_TAB_UNSELECTED
    }

    private MutableText title = null;
    private List<MutableText> lore = null;
    private BackgroundType background_type = null;
    private boolean show_background_image = true;
    private String button_text = null;
    private TextColor background_colour = null;

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
     */
    public ButtonWidgetSlot setLore(MutableText lore) {

        this.lore = new ArrayList<>();
        this.lore.add(lore);

        this.updateStack();
        return this;
    }

    /**
     * Set the lore of this button
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.4.0
     */
    public ButtonWidgetSlot setLore(List<MutableText> lore) {

        this.lore = new ArrayList<>(lore);

        this.updateStack();
        return this;
    }

    /**
     * Add to the lore
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.4.0
     */
    public ButtonWidgetSlot addLore(List<MutableText> lore) {

        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }

        this.lore.addAll(lore);

        this.updateStack();
        return this;
    }

    /**
     * Add to the lore
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.4.0
     */
    public ButtonWidgetSlot addLore(MutableText lore) {
        this.appendLoreLine(lore);
        this.updateStack();
        return this;
    }

    /**
     * Set the title of this button
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public ButtonWidgetSlot setTitle(String title) {
        return this.setTitle(Text.literal(title).setStyle(Style.EMPTY.withItalic(false).withColor(Formatting.WHITE)));
    }

    /**
     * Set the lore of this button.
     * The lore can contain newlines (\n)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public ButtonWidgetSlot setLore(String lore) {

        this.lore = new ArrayList<>();

        // If the lore contains newlines, split it up
        if (lore.contains("\n")) {
            String[] lines = lore.split("\n");

            for (String line : lines) {
                this.appendLoreLine(line);
            }
        } else {
            this.appendLoreLine(lore);
        }

        this.updateStack();

        return this;
    }

    /**
     * Add a line of pre-spliced lore
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.0
     */
    private void appendLoreLine(String line) {
        MutableText text_line = Text.literal(line).setStyle(Style.EMPTY.withItalic(false));
        this.appendLoreLine(text_line);
    }

    /**
     * Add a line of lore
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.0
     */
    private void appendLoreLine(MutableText line) {

        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }

        this.lore.add(line);
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
            BibItem.setCustomName(stack, this.title);
            this.markDirty();
        }

        if (this.lore != null) {
            BibItem.replaceLore(stack, this.lore);
            this.markDirty();
        }

        NbtCompound nbt = BibItem.getCustomNbt(stack);
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
        slot.show_background_image = this.show_background_image;

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
     * Set the background type and use the default image for it
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public ButtonWidgetSlot setBackgroundType(BackgroundType type) {
        return this.setBackgroundType(type, true);
    }

    /**
     * Set the background type & optionally show the background image
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.0
     */
    public ButtonWidgetSlot setBackgroundType(BackgroundType type, boolean show_background_image) {
        this.background_type = type;
        this.show_background_image = show_background_image;
        return this;
    }

    /**
     * Set an optional background recolour
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.1
     */
    public ButtonWidgetSlot setBackgroundColour(TextColor colour) {
        this.background_colour = colour;
        return this;
    }

    /**
     * Set an optional background recolour
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.1
     */
    public ButtonWidgetSlot setBackgroundColour(Formatting format) {
        return this.setBackgroundColour(TextColor.fromFormatting(format));
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

        BaseTexture button_image = null;

        if (this.show_background_image) {
            button_image = switch (this.background_type) {
                case LARGE -> BBSB.BUTTON_LARGE;
                case MEDIUM -> BBSB.BUTTON_MEDIUM;
                case SMALL, LOWER_SMALL -> BBSB.BUTTON_SMALL;
                case EXTRA_SMALL -> BBSB.BUTTON_EXTRA_SMALL;
                case TOP_TAB_SELECTED -> BBSB.BUTTON_TAB_TOP_SELECTED;
                case TOP_TAB_UNSELECTED -> BBSB.BUTTON_TAB_TOP_UNSELECTED;
                case LEFT_TAB_SELECTED -> BBSB.BUTTON_TAB_LEFT_SELECTED;
                case LEFT_TAB_UNSELECTED -> BBSB.BUTTON_TAB_LEFT_UNSELECTED;
            };
        }

        // The different type of button images have different sizes,
        // so they don't all start at the top left corner of the slot.
        int offset = switch (this.background_type) {
            case TOP_TAB_SELECTED, TOP_TAB_UNSELECTED, LEFT_TAB_SELECTED, LEFT_TAB_UNSELECTED  -> -2;
            case LARGE -> -1;
            case MEDIUM, SMALL -> 0;
            case LOWER_SMALL, EXTRA_SMALL -> 1;
        };

        if (this.overlays != null) {
            for (Overlay overlay : this.overlays) {

                if (overlay.original_x != null) {
                    continue;
                }

                if (overlay.texture.getOriginalTexture() instanceof IconTexture) {
                    overlay.external_x = offset + 2;
                    overlay.external_y = offset + 2;
                }
            }
        }

        int x = this.getSlotXInPixels() + offset;
        int y = this.getSlotYInPixels() + offset;

        if (button_image != null) {

            if (this.background_colour != null) {
                button_image = button_image.getColoured(this.background_colour);
            }

            button_image.addToBuilder(builder, x, y);
        }

        if (this.button_text != null) {
            //int font_line_number = this.getFontLineNumber();
            //Font font = Font.LH_INVENTORY_SLOT.getFontForLine(font_line_number);

            int vertical_centered_y = this.getYForVerticallyCenteredText();
            Font font = Font.ABSOLUTE_DEFAULT_COLLECTION.getClosestFont(vertical_centered_y);

            // Set the cursor to the middle of the button
            // (A button is about 18 pixels, so 9 pixels from the left)
            int text_x = x + 9;

            // Get the width of the text to place on the button
            int text_width = font.getWidth(this.button_text);

            // Change the cursor position so the text is centered
            text_x -= text_width / 2;

            builder.setCursor(text_x);
            TextGroup group = builder.createNewGroup();
            group.setColor(TextColor.fromRgb(0x878787));
            builder.print(this.button_text, font);
        }

        super.addToTextBuilder(builder);
    }

    /**
     * Return a string representation of this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.0
     */
    @Override
    public String toString() {
        return "ButtonWidgetSlot{" +
                ", button_text='" + button_text + '\'' +
                ", index=" + this.getIndex() +
                ", id=" + this.id +
                ", screen_index=" + this.getScreenIndex() +
                ", x=" + x +
                ", y=" + y +
                "}";

    }

}
