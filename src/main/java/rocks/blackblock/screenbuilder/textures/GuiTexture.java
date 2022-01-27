package rocks.blackblock.screenbuilder.textures;

import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.text.TextBuilder;

import java.util.HashMap;

/**
 * The Texture class used for GUIs
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.1
 * @version  0.1.1
 */
public class GuiTexture extends BaseTexture {

    // Some calculations can be stored here, in case there needs to be some sharing
    private static HashMap<Identifier, GuiTexture> textures = new HashMap<>();

    // The X coordinate of the original texture
    private int original_x = 0;

    // The Y coordinate of the original texture
    private int original_y = 0;

    // The X coordinate of where text can start
    private Integer text_x = null;

    // The Y coordinate of where text can start
    private Integer text_y = null;

    // The screenbuilder this is used for
    private ScreenBuilder screenbuilder = null;

    public GuiTexture(Identifier texture_path, int original_x, int original_y) {
        super(texture_path, false);
        this.original_x = original_x;
        this.original_y = original_y;

        this.calculate();
    }

    /**
     * Make a copy of the given GuiTexture
     *
     * @param   original
     *
     * @since   0.1.1
     */
    public GuiTexture(GuiTexture original) {
        super(original.texture_path, false);

        this.original_x = original.original_x;
        this.original_y = original.original_y;
        this.gui_nr = original.gui_nr;

        // The pieces aren't cloned
        this.pieces.addAll(original.pieces);

        this.width = original.width;
        this.height = original.height;

        this.text_x = original.text_x;
        this.text_y = original.text_y;
    }

    /**
     * Get and/or create & register a texture
     *
     * @param   texture_path   The path to the texture image
     * @param   original_x     The X coordinate of where the original texture would be inside this texture
     * @param   original_y     The Y coordinate of where the original texture would be inside this texture
     */
    public static GuiTexture get(Identifier texture_path, int original_x, int original_y) {

        GuiTexture result = textures.get(texture_path);

        if (result == null) {
            result = new GuiTexture(texture_path, original_x, original_y);
            textures.put(texture_path, result);
        }

        return result.copy();
    }

    /**
     * Create and return a copy of this instance
     *
     * @since   0.1.1
     */
    public GuiTexture copy() {
        return new GuiTexture(this);
    }

    /**
     * Set the screenbuilder this texture is used for
     *
     * @since   0.1.1
     */
    public GuiTexture setScreenBuilder(ScreenBuilder screenbuilder) {
        this.screenbuilder = screenbuilder;
        return this;
    }

    /**
     * Get a copy with the given screenbuilder
     *
     * @since   0.1.1
     */
    public GuiTexture with(ScreenBuilder screenbuilder) {
        GuiTexture result = this.copy();
        result.setScreenBuilder(screenbuilder);
        return result;
    }

    /**
     * Set the coordinate within the original image where text can be placed
     * (The Y-coordinate can be moved down a bit in case it doesn't match up)
     *
     * @param text_x
     * @param text_y
     */
    public void setTextCoordinates(int text_x, int text_y) {
        this.text_x = text_x;
        this.text_y = text_y;
    }

    /**
     * Get the X coordinate where text can start
     *
     * @since   0.1.1
     */
    public int getTextX() {

        Integer result = this.text_x;

        if (result == null) {

            if (this.screenbuilder != null) {
                result = this.screenbuilder.getScreenInfo().getTitleX();
            }

            if (result == null) {
                result = 8;
            }
        }

        return result;
    }

    /**
     * Get the Y coordinate where text can start
     *
     * @since   0.1.1
     */
    public int getTextY() {

        Integer result = this.text_y;

        if (result == null) {

            if (this.screenbuilder != null) {
                result = this.screenbuilder.getScreenInfo().getTitleY();
            }

            if (result == null) {
                result = 6;
            }
        }

        return result;
    }

    /**
     * Return the X-coordinate where the title starts in the original, unmodded container
     * (Probably a generic inventory container screen)
     * @return
     */
    public int getOriginalScreenTitleX() {

        if (this.screenbuilder != null) {
            return this.screenbuilder.getScreenInfo().getTitleX();
        }

        return 8;
    }

    /**
     * Return the Y-coordinate where the title starts in the original, unmodded container
     * (Probably a generic inventory container screen)
     * @return
     */
    public int getOriginalScreenTitleY() {

        int result = 0;

        if (this.screenbuilder != null) {
            result = this.screenbuilder.getScreenInfo().getTitleY();
        } else {
            result = 6;
        }

        result += 7;

        return result;
    }

    public int getInitialCursorAdjustmentX() {
        // Subtract the position of the container thingy
        return 0 - (this.getOriginalScreenTitleX() + this.original_x);
    }

    /**
     * Calculate the font's ascent
     */
    public int getAscent() {

        int result = 0;
        int original_y = this.getOriginalY();

        if (original_y != 0) {
            result += original_y;
        }

        result += this.getOriginalScreenTitleY();

        // I think 1 always needs to be subtraced if it's negative?
        if (result < 0) {
            result -= 1;
        }

        return result;
    }

    /**
     * Add this texture to the given TextBuilder
     * This will also move the cursor position to the left
     *
     * @param   builder
     *
     * @since   0.1.1
     */
    public void addToBuilder(TextBuilder builder) {

        String str = "";
        int count = -1;
        int width = 0;

        // We probably have to move the cursor horizontally
        int initial_cursor_adjustment = this.getInitialCursorAdjustmentX();

        // Get the current cursor position
        int start_cursor = builder.getCursor();

        // Make sure the cursor is at the wanted position
        builder.setCursor(initial_cursor_adjustment);

        for (GuiTexturePiece piece : this.pieces) {
            count++;

            if (count > 0) {
                // There is always a 1 pixel gap between characters
                builder.moveCursorUnsafe(-1);
            }

            builder.insertUnsafe(""+piece.getCharacter(), GUI_FONT);
            width += piece.getWidth();
        }

        builder.moveCursorUnsafe(-width + 1);

        // The cursor is now back at the start position,
        // so make that the new origin
        builder.setCurrentOriginPosition(builder.getCursor(), -this.original_y);

        builder.setCursor(this.getTextX());
        builder.setY(this.getTextY() - this.original_y);

        // For now we'll assume the first line is only for the title.
        // So move a line down
        builder.setLine(1);
    }

    /**
     * Get the original texture's X-coordinate
     */
    public int getOriginalX() {
        return original_x;
    }

    /**
     * Get the original texture's Y-coordinate
     */
    public int getOriginalY() {
        return original_y;
    }

}
