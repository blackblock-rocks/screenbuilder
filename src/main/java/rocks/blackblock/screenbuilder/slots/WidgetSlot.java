package rocks.blackblock.screenbuilder.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.TextColor;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.text.TextGroup;
import rocks.blackblock.screenbuilder.textures.TexturePlacement;
import rocks.blackblock.screenbuilder.textures.WidgetTexture;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class WidgetSlot extends StaticSlot {

    private TexturePlacement background_texture = null;
    private List<TexturePlacement> foreground_textures = null;
    protected String label = null;
    protected boolean print_label_right = true;
    protected TextColor label_color = null;
    protected int min_label_width = 0;

    /**
     * WidgetSlot constructor
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public WidgetSlot(ItemStack stack) {
        super(stack);
    }

    /**
     * WidgetSlot constructor
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public WidgetSlot() {
        this(new ItemStack(Items.STRUCTURE_VOID));
    }

    /**
     * WidgetSlot constructor
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public WidgetSlot(Inventory inventory, Integer index) {
        super(inventory, index);
    }

    /**
     * Called when the user left-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public abstract void onLeftClick();

    /**
     * Called when the user right-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public abstract void onRightClick();

    /**
     * Called when the user middle-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public abstract void onMiddleClick();

    /**
     * Handle click
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public WidgetSlot handleSlotClick(int button, SlotActionType action_type, PlayerEntity player) {

        // button 0 = left, 1 = right, 2 = middle

        if (button == 0) { // Left click
            this.onLeftClick();
        } else if (button == 1) { // Right click
            this.onRightClick();
        } else if (button == 2) { // Middle click
            this.onMiddleClick();
        }

        GuiUtils.resyncPlayerInventory(player);

        return this;
    }

    /**
     * Trigger leftClick when taking a stack
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public ItemStack takeStack(int amount) {
        return ItemStack.EMPTY;
    }

    /**
     * Use a font image for this widget
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public void setBackground(WidgetTexture texture) {
        this.setBackground(texture, 0, 0);
    }

    /**
     * Use a font image for this widget
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public void setBackground(WidgetTexture texture, int x, int y) {
        texture.registerYOffset(y + this.getSlotYInPixels());
        this.background_texture = new TexturePlacement(texture, x, y);
    }

    /**
     * Add images on top of the background
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public void addTexture(WidgetTexture texture) {
        this.addTexture(texture, 0, 0);
    }

    /**
     * Add images on top of the background
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public void addTexture(WidgetTexture texture, int x, int y) {

        if (this.foreground_textures == null) {
            this.foreground_textures = new ArrayList<>();
        }

        texture.registerYOffset(y + this.getSlotYInPixels());

        this.foreground_textures.add(new TexturePlacement(texture, x, y));
    }

    /**
     * Set the label to print next to the widget
     *
     * @since    0.5.0
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Should the label be print on the right?
     *
     * @since    0.5.0
     */
    public void setPrintLabelRight(boolean value) {
        this.print_label_right = value;
    }

    /**
     * Set the label color
     *
     * @since    0.5.0
     */
    public void setLabelColor(TextColor color) {
        this.label_color = color;
    }

    /**
     * Set the minimum width of the label
     *
     * @since    0.5.0
     */
    public void setMinimumLabelWidth(int width) {
        this.min_label_width = width;
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

        int slot_x = this.getSlotXInPixels();
        int slot_y = this.getSlotYInPixels();

        if (this.background_texture != null) {
            this.background_texture.texture.addToBuilder(builder, this.background_texture.x + slot_x, this.background_texture.y + slot_y);
        }

        if (this.foreground_textures != null) {
            for (TexturePlacement placement : this.foreground_textures) {
                placement.texture.addToBuilder(builder, placement.x + slot_x, placement.y + slot_y);
            }
        }

        if (this.label != null) {
            int vertical_centered_y = this.getYForVerticallyCenteredText();
            Font font = Font.ABSOLUTE_DEFAULT_COLLECTION.getClosestFont(vertical_centered_y);

            // Get the width of the text to place on the button
            int text_width = font.getWidth(this.label);

            if (text_width < this.min_label_width) {
                text_width = this.min_label_width;
            }

            // Calculate where to place the text
            int x = this.getSlotXInPixels();

            if (this.print_label_right) {
                x += 19;
            } else {
                x -= text_width + 5;
            }

            builder.setCursor(x);
            TextGroup group = builder.createNewGroup();

            if (this.label_color == null) {
                group.setColor(TextColor.fromRgb(0x3f3f3f));
            } else {
                group.setColor(this.label_color);
            }

            builder.print(this.label, font);
        }

        super.addToTextBuilder(builder);
    }
}