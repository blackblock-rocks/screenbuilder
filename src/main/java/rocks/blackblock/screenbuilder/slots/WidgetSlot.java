package rocks.blackblock.screenbuilder.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.textures.TexturePlacement;
import rocks.blackblock.screenbuilder.textures.WidgetTexture;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class WidgetSlot extends StaticSlot {

    private TexturePlacement background_texture = null;
    private List<TexturePlacement> foreground_textures = null;

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
        System.out.println("Huh? TakeStack shouldn't be called");
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

        super.addToTextBuilder(builder);
    }
}