package rocks.blackblock.screenbuilder.screen;

import net.minecraft.screen.ScreenHandler;
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
    protected int slot_count = 0;

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
    public void setTitlePosition(int x, int y) {
        this.title_x = x;
        this.title_y = y;
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
     * Get the title Y position
     *
     * @since   0.1.1
     */
    public int getTitleY() {
        return title_y;
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
     * Get the slot count
     *
     * @since   0.1.1
     */
    public int getSlotCount() {
        return slot_count;
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
     * Register info for a vanilla screen
     *
     * @param   screen       The vanilla screen type
     * @param   x            The X coordinate of the title
     * @param   y            The Y coordinate of the title
     * @param   slot_count   The number of slots
     */
    public static ScreenInfo create(ScreenHandlerType<?> screen, int x, int y, int slot_count) {

        ScreenInfo info = new ScreenInfo(screen);
        info.setTitlePosition(x, y);
        info.setSlotCount(slot_count);

        SCREENS.put(screen, info);

        return info;
    }

    static {
        // Register the vanilla screen title positions.
        // The default values of the title position should be 8 & 6
        create(ScreenHandlerType.GENERIC_9X1, 8, 6, 9);
        create(ScreenHandlerType.GENERIC_9X2, 8, 6, 18);
        create(ScreenHandlerType.GENERIC_9X3, 8, 6, 27);
        create(ScreenHandlerType.GENERIC_9X4, 8, 6, 36);
        create(ScreenHandlerType.GENERIC_9X5, 8, 6, 45);
        create(ScreenHandlerType.GENERIC_9X6, 8, 6, 54);

        // The generic 3x3 (dispenser/dropper) screen actually always centers its title,
        // so the horizontal starting position depends on the length of the text.
        create(ScreenHandlerType.GENERIC_3X3, 0, 6, 9);

        // The brewing stand does the same thing
        create(ScreenHandlerType.BREWING_STAND, 0, 6, 5);

        create(ScreenHandlerType.ANVIL, 60, 6, 3);
        create(ScreenHandlerType.BEACON, 8, 6, 1);
        create(ScreenHandlerType.BLAST_FURNACE, 8, 6, 3);
        create(ScreenHandlerType.CARTOGRAPHY_TABLE, 8, 4, 3);
        create(ScreenHandlerType.CRAFTING, 29, 6, 10);
        create(ScreenHandlerType.ENCHANTMENT, 8, 6, 2);
        create(ScreenHandlerType.FURNACE, 8, 6, 3);
        create(ScreenHandlerType.GRINDSTONE, 8, 6, 3);
        create(ScreenHandlerType.HOPPER, 8, 6, 5);
        create(ScreenHandlerType.LECTERN, 8, 6, 0);
        create(ScreenHandlerType.LOOM, 8, 4, 4);
        create(ScreenHandlerType.MERCHANT, 8, 6, 3);
        create(ScreenHandlerType.SHULKER_BOX, 8, 6, 27);
        create(ScreenHandlerType.SMITHING, 60, 18, 3);
        create(ScreenHandlerType.SMOKER, 8, 6, 3);
        create(ScreenHandlerType.STONECUTTER, 8, 5, 2);

    }
}
