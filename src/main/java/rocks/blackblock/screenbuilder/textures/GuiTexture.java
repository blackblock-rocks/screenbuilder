package rocks.blackblock.screenbuilder.textures;

import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.screen.ScreenInfo;
import rocks.blackblock.screenbuilder.slots.StaticSlot;
import rocks.blackblock.screenbuilder.text.TextBuilder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Texture class used for GUIs
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.1
 * @version  0.1.1
 */
public class GuiTexture extends BaseTexture {

    // Some calculations can be stored here, in case there needs to be some sharing
    private static Map<Identifier, GuiTexture> textures = new HashMap<>();

    /**
     * The X coordinate of the original texture inside the replacement
     */
    private int original_x = 0;

    /**
     * The Y coordinate of the original texture inside the replacement
     */
    private int original_y = 0;

    // The X coordinate of where text can start
    private Integer text_x = null;

    // The Y coordinate of where text can start
    private Integer text_y = null;

    // The screenbuilder this is used for
    private ScreenBuilder screenbuilder = null;

    // The original instance
    private GuiTexture original = null;

    public GuiTexture(Identifier texture_path, int original_x, int original_y) {
        super(texture_path);
        this.original_x = original_x;
        this.original_y = original_y;
    }

    /**
     * Make a copy of the given GuiTexture
     *
     * @param   original
     *
     * @since   0.1.1
     */
    public GuiTexture(GuiTexture original) {
        super(original.texture_identifier);

        this.original = original;

        this.original_x = original.original_x;
        this.original_y = original.original_y;
        this.gui_nr = original.gui_nr;

        this.width = original.width;
        this.height = original.height;

        this.text_x = original.text_x;
        this.text_y = original.text_y;
    }

    /**
     * Get the pieces from the original instance
     *
     * @since   0.1.1
     */
    @Override
    public List<TexturePiece> getPieces() {

        if (this.original != null) {
            return this.original.getPieces();
        }

        return super.getPieces();
    }

    /**
     * Calculate all the pieces
     *
     * @since   0.1.3
     */
    @Override
    public void calculate() {
        this.getPieces();
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

        if (this.original != null && this.original.screenbuilder == null) {
            // Assume the first screenbuilder set is a template one
            this.original.setScreenBuilder(screenbuilder);
        }

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
                result = this.screenbuilder.getScreenInfo().getTitleTopY();
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
     */
    public int getOriginalScreenTitleBaselineY() {

        int result = 0;

        if (this.screenbuilder != null) {
            result = this.screenbuilder.getScreenInfo().getTitleBaselineY();
        } else {
            result = 13;
        }

        return result;
    }

    public int getInitialCursorAdjustmentX() {
        // Subtract the position of the container thingy
        return 0 - (this.getOriginalScreenTitleX() + this.original_x);
    }

    /**
     * Calculate the font's ascent
     */
    @Override
    public int getAscent(int y_offset) {

        int result = 0;
        int original_y = this.getOriginalY();

        if (original_y != 0) {
            result += original_y;
        }

        result += this.getOriginalScreenTitleBaselineY();

        return result;
    }

    /**
     * Add this texture to the given TextBuilder
     * This will also change the origin position of the text builder.
     *
     * @param   builder
     *
     * @since   0.1.1
     */
    //@Override
    public void addToTextBuilder(TextBuilder builder) {

        String str = "";
        int count = -1;
        int width = 0;

        // We probably have to move the cursor horizontally
        int initial_cursor_adjustment = this.getInitialCursorAdjustmentX();

        // Get the current cursor position
        int start_cursor = builder.getRawCursorPosition();

        // Make sure the cursor is at the wanted position
        builder.setCursor(initial_cursor_adjustment);

        for (TexturePiece piece : this.getPieces()) {
            count++;

            if (count > 0) {
                // There is always a 1 pixel gap between characters
                builder.moveCursorUnsafe(-1);
            }

            builder.insertUnsafe(""+piece.getCharacter(), GUI_FONT);
            width += piece.getWidth();
        }

        builder.moveCursorUnsafe(-width - 1);

        // The cursor is now back at the start position,
        // so make that the new origin
        builder.setCurrentOriginPosition(builder.getRawCursorPosition(), -this.getOriginalY() + this.getOriginalScreenTitleBaselineY());

        // Move the X-coordinate cursor to where the text should start
        builder.setCursor(this.getTextX());
        builder.setTextStartX(this.getTextX());

        // This isn't really needed?
        builder.setY(this.getTextY());

        // For now we'll assume the first line is only for the title.
        // So move a line down
        builder.setLine(1);
    }

    /**
     * The X coordinate of the original texture inside the underlying container
     */
    public int getOriginalX() {
        return original_x;
    }

    /**
     * The Y coordinate of the original texture inside the underlying container
     */
    public int getOriginalY() {
        return original_y;
    }

    /**
     * Turn the X coordinate in this GUI texture
     * into the underlying screen's X coordinate.
     *
     * @param   gui_x   The X coordinate inside the custom gui
     */
    public int getContainerX(int gui_x) {

        int result = gui_x;

        int original_x = this.getOriginalX();

        if (original_x != 0) {
            result += original_x;
        }

        //result -= this.getOriginalScreenTitleX();

        return result;
    }

    /**
     * Turn the Y coordinate in this GUI texture
     * into the underlying screen's Y coordinate.
     *
     * @param   gui_y   The Y coordinate inside the custom gui
     */
    public int getContainerY(int gui_y) {

        int result = gui_y;

        int original_y = this.getOriginalY();

        if (original_y != 0) {
            result -= original_y;
        }

        //result -= this.getOriginalScreenTitleY();

        return result;
    }

    /**
     * Get the source image.
     * We'll modify the source image first to make the used slots light up when hovering over them.
     * (This is only needed for FontTextures, not for Guis using item textures)
     *
     * @since   0.1.1
     */
    @Override
    public BufferedImage getSourceImage() throws IOException {

        // Get the actual source image
        BufferedImage source_image = super.getSourceImage();

        // If there is a screenbuilder present, we can use it to get info on the slot coordinates
        if (this.screenbuilder != null) {

            int width = source_image.getWidth();
            int height = source_image.getHeight();

            // This is the default slot color vanilla Minecraft uses
            int default_slot_color = 0xFF000000 + 0x8b8b8b;

            // Get all the defined slots
            List<Slot> slots = this.screenbuilder.getSlots();

            // Iterate over all the slots
            for (int i = 0; i < slots.size(); i++) {
                Slot slot = slots.get(i);

                // If there's no slot there, don't do anything
                if (slot == null) {
                    continue;
                }

                // If the slot is a static slot, also skip it.
                // We'll only make slots the user can interact with highlightable
                if (slot instanceof StaticSlot) {
                    continue;
                }

                // Get the coordinates of this slot on the vanilla screen
                ScreenInfo.Coordinates coords = this.screenbuilder.getScreenInfo().getSlotCoordinates(i);

                // Now modify the coordinates to be relative to the texture
                coords.x += this.getOriginalX();
                coords.y += this.getOriginalY();

                // Now iterate over each pixel in this slot
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {

                        int pixel_x = coords.x + x;
                        int pixel_y = coords.y + y;

                        if (pixel_x > width || pixel_y > height) {
                            continue;
                        }

                        int pixel = source_image.getRGB(pixel_x, pixel_y);

                        Color color = new Color(pixel);
                        var hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

                        // We have to increase the saturation & lightness of the pixel
                        // (because it'll be overlayed on the default dark grey slot color)
                        // When the opacity is very low, don't increase the brightness too much.
                        if (hsb[1] < 0.05) {
                            // Increase saturation
                            hsb[1] *= 1.1;

                            // Increase lightness
                            hsb[2] *= 1.05;
                        } else {
                            // Increase saturation
                            hsb[1] *= 1.1;

                            // Increase lightness
                            hsb[2] *= 1.15;
                        }

                        // Clip the values or it'll mess with the wrong components
                        if (hsb[1] > 1) {
                            hsb[1] = 1;
                        }

                        if (hsb[2] > 1) {
                            hsb[2] = 1;
                        }

                        // Turn the HSB values back into RGB
                        pixel = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);

                        // Make it about 25% transparent
                        pixel = (192 << 24) + pixel;

                        // Put the modified pixel back on the image
                        source_image.setRGB(coords.x + x, coords.y + y, pixel);
                    }
                }
            }
        }

        return source_image;
    }

    /**
     * Return the string representation of this instance
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    @Override
    public String toString() {
        String result = this.getClass().getSimpleName() + "{\"" + this.texture_identifier + "\""
                + ", pieces=" + this.getPieceCount()
                + ", piecewidth=" + this.getPieceWidth()
                + ", original_x=" + this.original_x
                + ", original_y=" + this.original_y
                + "}";
        return result;
    }

}
