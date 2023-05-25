package rocks.blackblock.screenbuilder.screen;

import net.minecraft.screen.ScreenHandlerType;

import java.util.HashMap;

/**
 * Information on the vanilla screens
 *
 * @since   0.1.1
 */
public class ScreenInfo {

    // All the registered screen info instances
    private static final HashMap<ScreenHandlerType<?>, ScreenInfo> SCREENS = new HashMap<>();

    protected final ScreenHandlerType<?> type;

    protected int title_x = 8;
    protected int title_y = 6;
    protected int title_baseline_y = 13;
    protected int slot_count = 0;
    protected int slots_per_row = 0;
    protected int slot_row_x = 0;
    protected int slot_row_y = 0;

    /**
     * Creates a new ScreenInfo instance
     *
     * @param   type   The type of screen this provides info for
     *
     * @since   0.1.1
     */
    public ScreenInfo(ScreenHandlerType<?> type) {
        this.type = type;
    }

    /**
     * Get the type of screen
     *
     * @since   0.1.1
     */
    public ScreenHandlerType<?> getType() {
        return type;
    }

    /**
     * Set the title position
     *
     * @since   0.1.1
     */
    public void setTitlePosition(int x, int title_baseline_y) {
        this.title_x = x;
        this.title_baseline_y = title_baseline_y;

        // "7" is the ascent of the default font
        this.title_y = title_baseline_y - 7;
    }

    /**
     * Get the title X position
     *
     * @since   0.1.1
     */
    public int getTitleX() {
        return title_x;
    }

    /**
     * Get the title's baseline Y starting position
     *
     * @since   0.1.1
     */
    public int getTitleBaselineY() {
        return title_baseline_y;
    }

    /**
     * Get the top Y coordinate of the title.
     * This is when using the original font.
     *
     * @since   0.1.1
     * @deprecated Use {@link #getTitleBaselineY()} instead
     */
    public int getTitleTopY() {
        return title_y;
    }

    /**
     * Get the title Y starting position
     * (This is the bottom of the title)
     *
     * @since   0.1.1
     * @deprecated Use {@link #getTitleBaselineY()} instead
     */
    public int getTitleBottomY() {
        return title_y + this.getTitleHeight();
    }

    /**
     * Get the height of the title used in the original screen
     * (This is always 8)
     *
     * @since   0.1.3
     */
    public int getTitleHeight() {
        return 8;
    }

    /**
     * Set the slot count
     *
     * @since   0.1.1
     */
    public void setSlotCount(int count) {
        this.slot_count = count;
    }

    /**
     * Get the slot count of the main screen,
     * excluding the player inventory or hotbar
     *
     * @since   0.1.1
     */
    public int getOwnSlotCount() {
        return slot_count;
    }

    /**
     * Get the total slot count,
     * including the player inventory and hotbar
     *
     * @since   0.3.1
     */
    public int getTotalSlotCount() {
        return slot_count + 36;
    }

    /**
     * Set the amount of slots per row
     *
     * @since   0.1.3
     */
    public void setSlotsPerRow(int count) {
        this.slots_per_row = count;
    }

    /**
     * Get the amount of slots per row
     *
     * @since   0.1.3
     */
    public int getSlotsPerRow() {
        return this.slots_per_row;
    }

    /**
     * Set the starting X position of a slot row
     *
     * @since   0.1.3
     */
    public void setSlotRowX(int slot_row_x) {
        this.slot_row_x = slot_row_x;
    }

    /**
     * Get the starting X position of a slot row
     *
     * @since   0.1.3
     */
    public int getSlotRowX() {
        return this.slot_row_x;
    }

    /**
     * Set the starting Y position of the first slot row
     *
     * @since   0.1.3
     */
    public void setSlotRowY(int slot_row_y) {
        this.slot_row_y = slot_row_y;
    }

    /**
     * Get the starting Y position of the first slot row
     *
     * @since   0.1.3
     */
    public int getSlotRowY() {
        return this.slot_row_y;
    }

    /**
     * Get the info for the given screen
     *
     * @param   screen   The vanilla screen type to get the info of
     */
    public static ScreenInfo get(ScreenHandlerType<?> screen) {
        return SCREENS.get(screen);
    }

    /**
     * Get the slot coordinates inside the original, unmodded screen
     *
     * @param   slot_index   The index of the wanted slot
     */
    public Coordinates getSlotCoordinates(int slot_index) {
        Coordinates coords = new Coordinates();

        // @TODO: this will only work for generic container screens
        //coords.y = 18 + ((slot_index / 9) * 18);
        //coords.x = 8 + ((slot_index % 9) * 18);

        int title_y = this.getTitleTopY() + this.getTitleHeight();
        int title_x = this.getTitleX();
        int slot_height = 18;
        int slot_width = 18;
        int slots_per_row = this.getSlotsPerRow();

        coords.y = this.getSlotRowY() + ((slot_index / slots_per_row) * slot_height);
        coords.x = this.getSlotRowX() + ((slot_index % slots_per_row) * slot_width);

        if (slot_index >= this.getOwnSlotCount()) {
            // Player inventory
            coords.y += 14;

            if (slot_index >= this.getOwnSlotCount() + 27) {
                // Player hotbar
                coords.y += 4;
            }
        }

        return coords;
    }

    /**
     * Register info for a vanilla screen.
     * Assumes there are 9 slots per row.
     *
     * @param   screen             The vanilla screen type
     * @param   title_x            The X coordinate of the title
     * @param   title_baseline_y   The Y coordinate of the title (the actual baseline)
     * @param   slot_count         The number of slots
     */
    public static ScreenInfo create(ScreenHandlerType<?> screen, int title_x, int title_baseline_y, int slot_count) {
        return create(screen, title_x, title_baseline_y, slot_count, 9);
    }

    /**
     * Register info for a vanilla screen
     *
     * @param   screen             The vanilla screen type
     * @param   title_x            The X coordinate of the title
     * @param   title_baseline_y   The Y coordinate of the title (the actual baseline)
     * @param   slot_count         The number of slots
     * @param   slots_per_row      The amount of slots per row
     */
    public static ScreenInfo create(ScreenHandlerType<?> screen, int title_x, int title_baseline_y, int slot_count, int slots_per_row) {

        ScreenInfo info = new ScreenInfo(screen);
        info.setTitlePosition(title_x, title_baseline_y);
        info.setSlotCount(slot_count);
        info.setSlotsPerRow(slots_per_row);

        // @TODO: these values are only correct for the vanilla container screens with 9 slots per row
        info.setSlotRowX(7);
        info.setSlotRowY(17);

        SCREENS.put(screen, info);

        return info;
    }

    public static class Coordinates {
        public int x;
        public int y;

        public Coordinates() {
            this(0, 0);
        }

        public Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Get the string representation of this instance
         *
         * @since   0.1.3
         */
        public String toString() {
            return "ScreenInfo.Coordinates{x=" + x + ", y=" + y + "}";
        }
    }

    static {
        // Register the vanilla screen title positions.
        // The default values of the title position should be 8 & 6
        create(ScreenHandlerType.GENERIC_9X1, 8, 13, 9);
        create(ScreenHandlerType.GENERIC_9X2, 8, 13, 18);
        create(ScreenHandlerType.GENERIC_9X3, 8, 13, 27);
        create(ScreenHandlerType.GENERIC_9X4, 8, 13, 36);
        create(ScreenHandlerType.GENERIC_9X5, 8, 13, 45);
        create(ScreenHandlerType.GENERIC_9X6, 8, 13, 54);

        // The generic 3x3 (dispenser/dropper) screen actually always centers its title,
        // so the horizontal starting position depends on the length of the text.
        create(ScreenHandlerType.GENERIC_3X3, 0, 13, 9);

        // The brewing stand does the same thing
        create(ScreenHandlerType.BREWING_STAND, 0, 13, 5);

        create(ScreenHandlerType.ANVIL,             60, 13, 3);
        create(ScreenHandlerType.BEACON,            8,  13, 1);
        create(ScreenHandlerType.BLAST_FURNACE,     8,  13, 3);
        create(ScreenHandlerType.CARTOGRAPHY_TABLE, 8,  11, 3);
        create(ScreenHandlerType.CRAFTING,          29, 13, 10);
        create(ScreenHandlerType.ENCHANTMENT,       8,  13, 2);
        create(ScreenHandlerType.FURNACE,           8,  13, 3);
        create(ScreenHandlerType.GRINDSTONE,        8,  13, 3);
        create(ScreenHandlerType.HOPPER,            8,  13, 5);
        create(ScreenHandlerType.LECTERN,           8,  13, 0);
        create(ScreenHandlerType.LOOM,              8,  11, 4);
        create(ScreenHandlerType.MERCHANT,          8,  13, 3);
        create(ScreenHandlerType.SHULKER_BOX,       8,  13, 27);
        create(ScreenHandlerType.SMITHING,          60, 25, 3);
        create(ScreenHandlerType.SMOKER,            8,  13, 3);
        create(ScreenHandlerType.STONECUTTER,       8,  12, 2);
    }
}
