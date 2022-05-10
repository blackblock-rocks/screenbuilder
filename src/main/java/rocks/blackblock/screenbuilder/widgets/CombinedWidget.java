package rocks.blackblock.screenbuilder.widgets;

import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.slots.BaseSlot;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.textures.TexturePlacement;
import rocks.blackblock.screenbuilder.textures.WidgetTexture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CombinedWidget<T> extends Widget<T> {

    protected int slot_width = 1;
    protected int slot_height = 1;

    protected int slot_index = 0;

    private List<TexturePlacement> background_textures = null;
    private List<TexturePlacement> foreground_textures = null;
    private Map<Integer, BaseSlot> slots = new HashMap<>();

    /**
     * Set the slot index
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public void setSlotIndex(int index) {
        this.slot_index = index;
    }

    /**
     * Set a Slot at the specified index
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     *
     * @param    index   The index of the slot inside this GUI
     * @param    slot    The slot to set
     */
    public BaseSlot setSlot(int index, BaseSlot slot) {
        this.slots.put(index, slot);
        return slot;
    }

    /**
     * Get the source Y coordinate
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public int getY() {

        if (!this.slots.containsKey(0)) {
            return 0;
        }

        return this.slots.get(0).getSlotYInPixels();
    }

    /**
     * Get the source Y coordinate
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public int getX() {

        if (!this.slots.containsKey(0)) {
            return 0;
        }

        return this.slots.get(0).getSlotXInPixels();
    }

    /**
     * Add a background image
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public void addBackground(WidgetTexture texture) {
        this.addBackground(texture, 0, 0);
    }

    /**
     * Use a font image for this widget
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public void addBackground(WidgetTexture texture, int x, int y) {

        if (this.background_textures == null) {
            this.background_textures = new ArrayList<>();
        }

        texture.registerYOffset(y + this.getY());
        this.background_textures.add(new TexturePlacement(texture, x, y));
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

        texture.registerYOffset(y + this.getY());

        this.foreground_textures.add(new TexturePlacement(texture, x, y));
    }

    /**
     * Prepare the slots
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    @Override
    public void prepareSlots(ScreenBuilder builder) {

        int index;
        int local_x = 0;
        int local_y = 0;

        int start_x = this.slot_index % 9;
        int start_y = this.slot_index / 9;

        for (int i : this.slots.keySet()) {
            BaseSlot slot = this.slots.get(i);

            // Get the local X coordinate
            local_x = i % this.slot_width;

            // Get the local Y coordinate
            local_y = i / this.slot_width;

            // Calculate the target index based on this unit's position and width
            index = (start_y + local_y) * 9 + (start_x + local_x);

            builder.setSlot(index, slot);
        }
    }

    /**
     * Add the unit to the text builder
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    @Override
    public void addToTextBuilder(TextBuilder builder) {

        int slot_x = this.getX();
        int slot_y = this.getY();

        // Do backgrounds first
        if (this.background_textures != null) {
            for (TexturePlacement bg : this.background_textures) {
                bg.texture.addToBuilder(builder, bg.x + slot_x, bg.y + slot_y);
            }
        }

        // Now do specific "addWithValue" stuff
        super.addToTextBuilder(builder);

        // And add foreground textures
        if (this.foreground_textures != null) {
            for (TexturePlacement placement : this.foreground_textures) {
                placement.texture.addToBuilder(builder, placement.x + slot_x, placement.y + slot_y);
            }
        }
    }


}
