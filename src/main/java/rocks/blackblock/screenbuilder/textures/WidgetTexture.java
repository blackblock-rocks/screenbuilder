package rocks.blackblock.screenbuilder.textures;

import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.ScreenBuilder;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * The Texture class used for GUI widgets
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.1
 * @version  0.1.1
 */
public class WidgetTexture extends BaseTexture {

    // All the registered textures
    public static Map<Identifier, Map<Integer, WidgetTexture>> widget_textures = new HashMap<>();

    // The y-coordinate of where the texture should go in its respective container
    protected Integer target_y;

    // The original target_y, before it was adjusted to the parent texture
    protected Integer original_target_y;

    // The parent gui texture
    protected GuiTexture parent_gui;

    // The minimum amount of pieces this widget should be split into
    protected Integer min_pieces = null;

    /**
     * Get a WidgetTexture
     *
     * @param   texture_identifier   The path to the texture
     * @param   min_pieces           The minimum amount of pieces this widget should be split into
     */
    public static WidgetTexture getWidgetTexture(Identifier texture_identifier, Integer min_pieces) {

        if (!widget_textures.containsKey(texture_identifier)) {
            widget_textures.put(texture_identifier, new HashMap<>());
        }

        if (!widget_textures.get(texture_identifier).containsKey(min_pieces)) {
            widget_textures.get(texture_identifier).put(min_pieces, new WidgetTexture(texture_identifier, min_pieces));
        }

        return widget_textures.get(texture_identifier).get(min_pieces);
    }

    /**
     * Register the given texture so it fits in all the slots
     *
     * @since   0.3.1
     */
    public static void registerForAllSlots(WidgetTexture texture) {
        registerForAllSlots(new WidgetTexture[]{texture});
    }

    /**
     * Register the given texture so it fits in all the slots
     *
     * @since   0.3.1
     */
    public static void registerForAllSlots(WidgetTexture[] textures) {

        forEachRowOffset((dummy, row, row_offset, jitter) -> {
            for (WidgetTexture texture : textures) {
                texture.registerYOffset(dummy, 13 + row_offset + jitter);
                texture.registerYOffset(dummy, 17 + row_offset + jitter);
            }
        });
    }

    /**
     * Do something for each row offset
     *
     * @since   0.3.1
     */
    public static void forEachRowOffset(RowOffsetCallback callback) {

        ScreenBuilder dummy = new ScreenBuilder("dummy");

        // The are 10 rows in total:
        // 6 top inventory rows,
        // 4 bottom inventory rows
        // 1 hotbar row
        for (int row = 0; row < 10; row++) {
            int offset = 18 * row;

            if (row == 6) {
                // Also register it without the later "+14" and "+4" offset,
                // probably some "off by 1" error, because without this icons for the 6th row (0-indexed at row 5)
                // get registered after initialization ...
                for (int i = -1; i < 4; i++) {
                    callback.call(dummy, row, offset, i);
                }
            }

            if (row >= 6) {
                offset += 14;
            }

            if (row == 9) {
                offset += 4;
            }

            // Let the texture have 5 different Y offsets
            for (int i = -1; i < 4; i++) {
                callback.call(dummy, row, offset, i);
            }
        }
    }

    /**
     * The callback for the forEachRowOffset method
     *
     * @since   0.3.1
     */
    @FunctionalInterface
    public interface RowOffsetCallback {
        void call(ScreenBuilder dummy, int row, int row_offset, int jitter);
    }

    /**
     * Create an uninitialized texture
     *
     * @param   texture_identifier   The path to the texture
     *
     * @since   0.1.2
     */
    public WidgetTexture(Identifier texture_identifier, Integer min_pieces) {
        super(texture_identifier);
        this.setMinPieces(min_pieces);
    }

    /**
     * Create a new WidgetTexture
     *
     * @param   texture_identifier   The path to the texture
     * @param   parent_gui           The GUI this texture will be used in
     * @param   target_y             The y-coordinate of where the texture should go in its respective container
     * @param   min_pieces           The minimum amount of pieces this widget should be split into
     */
    public WidgetTexture(Identifier texture_identifier, GuiTexture parent_gui, int target_y, Integer min_pieces) {
        //this(texture_identifier, target_y - parent_gui.getOriginalY() - parent_gui.getOriginalScreenTitleY(), min_pieces);
        this(texture_identifier);
        this.setParentGuiTexture(parent_gui);
        this.setMinPieces(min_pieces);
        this.setTargetY(target_y);
        this.calculate();
    }

    /**
     * Create a new WidgetTexture
     *
     * @param   texture_identifier   The path to the texture
     * @param   target_y             The y-coordinate of where the texture should go in its respective container
     * @param   min_pieces           The minimum amount of pieces this widget should be split into
     */
    public WidgetTexture(Identifier texture_identifier, int target_y, Integer min_pieces) {
        super(texture_identifier);
        this.setTargetY(target_y);
        this.setMinPieces(min_pieces);

        // Make sure the pieces are generated
        this.calculate();
    }

    /**
     * Create a new WidgetTexture with a specific path to the source image
     *
     * @param   texture_identifier   The path to the texture
     * @param   target_y             The y-coordinate of where the texture should go in its respective container
     * @param   min_pieces           The minimum amount of pieces this widget should be split into
     */
    public WidgetTexture(Identifier texture_identifier, Path texture_path, int target_y, Integer min_pieces) {
        super(texture_identifier);
        this.setTargetY(target_y);
        this.setMinPieces(min_pieces);

        this.setTexturePath(texture_path);

        // Make sure the pieces are generated
        this.calculate();
    }

    /**
     * Create an uninitialized texture
     *
     * @param   texture_identifier   The path to the texture
     *
     * @since   0.1.2
     */
    public WidgetTexture(Identifier texture_identifier) {
        super(texture_identifier);
    }

    /**
     * Set the target_y
     *
     * @since   0.1.2
     */
    public void setTargetY(int target_y) {
        this.original_target_y = target_y;

        if (this.parent_gui != null) {
            target_y -= this.parent_gui.getOriginalY();
            target_y -= this.parent_gui.getOriginalScreenTitleBaselineY();
        }

        this.target_y = target_y;
    }

    /**
     * Set the minimum amount of pieces
     *
     * @since   0.1.2
     */
    public void setMinPieces(int min_pieces) {
        this.min_pieces = min_pieces;
    }

    /**
     * Set the parent GUI texture
     *
     * @since   0.1.2
     */
    public void setParentGuiTexture(GuiTexture parent_gui) {
        this.parent_gui = parent_gui;

        if (this.original_target_y != null) {
            this.setTargetY(this.original_target_y);
        }
    }

    /**
     * Calculate the font's ascent
     *
     * @since   0.1.1
     */
    @Override
    public int getAscent(int y_offset) {
        int result = -y_offset;
        return result;
    }

    /**
     * Get the amount of pieces this widget should be split into
     *
     * @since   0.1.1
     */
    @Override
    public Integer getPreferredAmountOfPieces() {
        return this.min_pieces;
    }
}
