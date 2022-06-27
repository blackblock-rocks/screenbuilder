package rocks.blackblock.screenbuilder.slots;

import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.screen.ScreenInfo;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.textures.BaseTexture;

import javax.swing.plaf.basic.BasicButtonListener;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseSlot extends Slot {

    // For generated slots: the current ScreenHandler
    protected TexturedScreenHandler active_handler = null;

    // Store a reference to the current ScreenBuilder
    protected ScreenBuilder active_builder = null;

    // The index in the screen this slot takes up
    protected Integer screen_index = null;

    // The index in the inventory this slot is mapped to
    private Integer inventory_index = null;

    // Should this slot be cloned before using it on a screen?
    protected boolean clone_before_screen = true;

    // Should this slot use the real inventory?
    protected Boolean use_real_inventory = null;

    // Other widgets to overlay on this
    protected List<Overlay> overlays = null;

    /**
     * BaseSlot constructor
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public BaseSlot(Inventory inventory, int index) {
        super(inventory, index, 0, 0);
        this.init();
    }

    /**
     * Init method
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    protected void init() { }

    /**
     * Force it to use the inventory?
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public void setUseRealInventory(boolean value) {
        this.use_real_inventory = value;
    }

    /**
     * Is this using the inventory?
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     */
    public boolean getUseRealInventory() {
        return this.use_real_inventory;
    }

    /**
     * Should this use a real inventory?
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public boolean shouldUseInventory() {

        if (this.use_real_inventory == null) {
            return true;
        }

        return this.use_real_inventory;
    }

    /**
     * Set the current ScreenBuilder
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public void setScreenBuilder(ScreenBuilder builder) {
        this.active_builder = builder;
    }

    /**
     * Get the current ScreenBuilder
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public ScreenBuilder getScreenBuilder() {
        return this.active_builder;
    }

    /**
     * Set the active handler
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setHandler(TexturedScreenHandler handler) {
        this.active_handler = handler;
    }

    /**
     * Get the active handler
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public TexturedScreenHandler getHandler() {
        return this.active_handler;
    }

    /**
     * Set the index in the screen of this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setScreenIndex(Integer screen_index) {
        this.screen_index = screen_index;
    }

    /**
     * Set the actual index to use in the inventory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    inventory_index   The inventory index to use
     */
    public BaseSlot mapInventory(int inventory_index) {
        this.inventory_index = inventory_index;
        return this;
    }

    /**
     * Get the inventory index this maps to
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public Integer getInventoryIndex() {
        return this.inventory_index;
    }

    /**
     * Get the inventory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Should this slot be cloned before showing it on a screen?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setCloneBeforeScreen(boolean clone) {
        this.clone_before_screen = clone;
    }

    /**
     * Should this slot be cloned before showing it on a screen?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public boolean getCloneBeforeScreen() {
        return this.clone_before_screen;
    }

    /**
     * Set the stack from an item
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setStack(Item item) {
        this.setStack(new ItemStack(item));

        if (this.active_handler != null) {
            this.active_handler.sendContentUpdates();
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
    public void copyPropertiesToSlot(BaseSlot slot) {

        if (this.screen_index != null) {
            slot.setScreenIndex(this.screen_index);
        }

        if (this.inventory_index != null) {
            slot.mapInventory(this.inventory_index);
        }
    }

    /**
     * Clone this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public abstract BaseSlot clone(Inventory inventory, Integer index);

    /**
     * Clone this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public BaseSlot clone() {
        return this.clone(null, null);
    };

    /**
     * Generate the slot to use in an actual screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    handler   The handler we're creating a slot for
     */
    public BaseSlot createSlot(TexturedScreenHandler handler) {

        PlayerInventory player_inventory = handler.getPlayerInventory();
        Inventory inventory = null;
        Integer inventory_index = this.inventory_index;

        if (inventory_index != null) {
            // When this slot is specifically mapped to an inventory slot,
            // the handler's main inventory is what we should use
            inventory = handler.getActualInventory();
        }

        BaseSlot result;

        // Some slots are created on-the-fly, and contain data already
        // Do not clone these!
        // @WARNING: Except some slots HAVE to be cloned...
        if (!this.getCloneBeforeScreen()) {
            result = this;
        } else {
            result = this.clone(inventory, inventory_index);
        }

        result.setHandler(handler);

        return result;
    }

    /**
     * Get the X number coordinate of the slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public int getSlotX() {

        if (this.screen_index == null) {
            return 0;
        }

        return this.screen_index % 9;
    }

    /**
     * Get the Y number coordinate of the slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public int getSlotY() {

        if (this.screen_index == null) {
            return 0;
        }

        return this.screen_index / 9;
    }

    /**
     * Get the absolute Y coordinate of this slot
     * in the current applied GUI
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public int getSlotYInPixels() {

        int result;

        if (this.active_builder != null) {
            return this.active_builder.getSlotCoordinates(this.screen_index).y;
        } else {
            // Fallback to the 9x5 screen info
            result = 17 + this.getSlotY() * 18;
        }

        return result;
    }

    /**
     * Get the absolute X coordinate of this slot
     * in the current applied GUI
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public int getSlotXInPixels() {

        if (this.active_builder != null) {
            return this.active_builder.getSlotCoordinates(this.screen_index).x;
        } else {
            // Fallback to the 9x5 screen info
            return 7 + this.getSlotX() * 18;
        }
    }

    /**
     * This slot was used in the given TextBuilder
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     *
     * @param    builder   The text builder
     */
    public void addToTextBuilder(TextBuilder builder) {

        // Add the overlays if there are any
        if (this.overlays != null && !this.overlays.isEmpty()) {
            for (Overlay overlay : this.overlays) {
                overlay.addToBuilder(builder);
            }
        }

    }

    /**
     * Add an overlay to this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.1
     *
     * @param    texture   The texture to overlay
     */
    public Overlay addOverlay(BaseTexture texture) {

        if (this.overlays == null) {
            this.overlays = new ArrayList<>();
        }

        Overlay new_overlay = new Overlay(this, texture, null, null);

        this.overlays.add(new_overlay);

        return new_overlay;
    }

    /**
     * More textures to overlay on this slow
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.1
     */
    protected static class Overlay {

        public final BaseSlot slot;
        public BaseTexture texture;
        public Integer original_x = null;
        public Integer original_y = null;

        // Let certain classes recalculate things
        public Integer external_x = null;
        public Integer external_y = null;

        public Overlay(BaseSlot slot, BaseTexture texture, Integer x, Integer y) {
            this.slot = slot;
            this.texture = texture;
            this.original_x = x;
            this.original_y = y;
        }

        public void addToBuilder(TextBuilder builder) {
            int x = this.slot.getSlotXInPixels();
            int y = this.slot.getSlotYInPixels();

            if (this.external_x != null) {
                x += this.external_x;
            } else if (this.original_x != null) {
                x += this.original_x;
            }

            if (this.external_y != null) {
                y += this.external_y;
            } else if (this.original_y != null) {
                y += this.original_y;
            }

            this.texture.addToBuilder(builder, x, y);
        }
    }
}
