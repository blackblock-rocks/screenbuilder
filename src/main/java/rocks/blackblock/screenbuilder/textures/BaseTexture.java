package rocks.blackblock.screenbuilder.textures;

import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.text.GuiFont;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * The base Texture class
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.1
 * @version  0.1.1
 */
public abstract class BaseTexture {

    // The max allowed width of an image
    private static final int MAX_WIDTH = 128;

    // A running counter of registered gui textures
    protected static int gui_counter = 0;

    // The "font" to use for textures
    public static final GuiFont GUI_FONT = new GuiFont("bbsb:gui");

    // The identifier/path to the original texture
    protected Identifier texture_path = null;

    // The height of the texture
    protected int height = 0;

    // The width of the texture
    protected int width = 0;

    // The counter of this index
    protected Integer gui_nr = null;

    // The pieces of this texture
    private ArrayList<TexturePiece> pieces = null;

    /**
     * Set the texture
     *
     * @since   0.1.1
     */
    public BaseTexture(Identifier texture_path) {
        this.texture_path = texture_path;
    }

    /**
     * Get the texture pieces
     * (Will also call the `calculate` method the first time)
     *
     * @since   0.1.1
     */
    public ArrayList<TexturePiece> getPieces() {

        if (this.pieces == null) {
            this.pieces = this.calculate();
        }

        return this.pieces;
    }

    /**
     * Set the texture pieces
     *
     * @since   0.1.1
     */
    public void setPieces(ArrayList<TexturePiece> pieces) {
        this.pieces = pieces;
    }

    /**
     * Get the texture identifier
     *
     * @since   0.1.1
     */
    public Identifier getTextureIdentifier() {
        return this.texture_path;
    }

    /**
     * Get the gui number
     *
     * @since   0.1.1
     */
    public int getGuiNumber() {
        return this.gui_nr;
    }

    /**
     * Get the ascent of the texture for the font
     *
     * @since   0.1.1
     */
    public abstract int getAscent();

    /**
     * Get the maximum width a piece can be
     *
     * @since   0.1.1
     */
    public int getMaxPieceWidth() {
        return MAX_WIDTH;
    }

    /**
     * Get the minimum width a piece can be
     *
     * @since   0.1.1
     */
    public int getPreferredPieceWidth() {

        if (this.getPreferredAmountOfPieces() != null) {
            return this.width / this.getPreferredAmountOfPieces();
        }

        return this.getMaxPieceWidth();
    }

    /**
     * Get the minimum amount of pieces this texture should have
     *
     * @since   0.1.1
     */
    public Integer getPreferredAmountOfPieces() {
        return null;
    }

    /**
     * Get the amount of pieces this texture should have
     *
     * @since   0.1.1
     */
    public int getAmountOfPieces() {
        Integer min_amount = this.getPreferredAmountOfPieces();
        int max_amount = (int) Math.ceil((double) this.width / (double) this.getPreferredPieceWidth());

        if (min_amount != null && min_amount > max_amount) {
            return min_amount;
        }

        return max_amount;
    }

    /**
     * Get the width of a piece
     */
    public int getPieceWidth() {
        return this.getPieceWidth(0);
    }

    /**
     * Get the width of a specific piece
     *
     * @since   0.1.1
     */
    public int getPieceWidth(int piece_index) {

        return (int) Math.ceil((double) this.width / (double) this.getAmountOfPieces());

        /*
        int preferred_width = this.getPreferredPieceWidth();
        return Math.min(preferred_width, this.width - piece_index * preferred_width);

         */
    }

    /**
     * Get the target image width
     *
     * @since   0.1.1
     */
    public int getTargetImageWidth() {
        return this.getPieceWidth(0) * this.getAmountOfPieces();
    }

    /**
     * Get the source start X of a specific piece
     *
     * @since   0.1.1
     */
    public int getPieceSourceXStart(int piece_index) {
        return piece_index * this.getMaxPieceWidth();
    }

    /**
     * Get the source end X of a specific piece
     *
     * @since   0.1.1
     */
    public int getPieceSourceXEnd(int piece_index) {
        return (piece_index * this.getMaxPieceWidth()) + this.getPieceWidth(piece_index);
    }

    /**
     * Calculate all the pieces of this texture
     *
     * @since   0.1.1
     */
    protected ArrayList<TexturePiece> calculate() {

        ArrayList<TexturePiece> texturePieces = new ArrayList<>();
        byte[] data;

        // Only set the gui_nr if it hasn't been assigned one yet
        if (this.gui_nr == null) {
            this.gui_nr = BaseTexture.gui_counter++;
        }

        try {
            data = getFileStream(this.texture_path).readAllBytes();
        } catch (Exception e) {
            System.out.println("Failed to load texture file: " + this.texture_path);
            return texturePieces;
        }

        BufferedImage source_image = null;

        try {
            source_image = ImageIO.read(new ByteArrayInputStream(data));
        } catch (Exception e) {
            System.out.println("Failed to read texture data: " + this.texture_path);
            return texturePieces;
        }

        this.height = source_image.getHeight();
        this.width = source_image.getWidth();

        int max_width = this.getMaxPieceWidth();

        // Calculate the amount of pieces we need
        int pieces = this.getAmountOfPieces();

        int target_width = this.getTargetImageWidth();
        int piece_width = this.getPieceWidth(0);

        // Create the target image (the widths remain the same)
        BufferedImage target_image = new BufferedImage(target_width, this.height, BufferedImage.TYPE_INT_ARGB);
        Graphics target_graphics = target_image.getGraphics();

        for (int i = 0; i < pieces; i++) {

            int x_start = i * piece_width;
            int x_end = x_start + piece_width;

            target_graphics.drawImage(
                    source_image,
                    // Destination coordinates
                    x_start, 0,
                    x_end, this.height,

                    // Source coordinates
                    x_start, 0,
                    x_end, this.height,
                    null
            );

            boolean has_transparent_last_column = true;

            // Iterate over all the pixels in the last column.
            // If all the pixels are transparent,
            // we will make the top-right pixel a tiny bit opaque
            for (int y = 0; y < this.height; y++) {
                int pixel = target_image.getRGB(x_end - 1, y);

                int alpha = (pixel & 0xff000000) >>> 24;

                if (alpha != 0) {
                    has_transparent_last_column = false;
                    break;
                }
            }

            // If the last column is totally transparent,
            // make the top-right pixel a tiny bit opaque
            if (has_transparent_last_column) {
                target_image.setRGB(x_end - 1, 0, 0x01000001);
            }

            TexturePiece piece = new TexturePiece(this, i, GUI_FONT.getNextChar());
            piece.setUsesSharedImage(true);
            texturePieces.add(piece);
            GUI_FONT.registerTexturePiece(piece);
            piece.setImage(target_image);
        }

        return texturePieces;
    }

    /**
     * Add this texture to the given TextBuilder
     * This will also move the cursor position to the left
     *
     * @param   builder
     *
     * @since   0.1.1
     */
    public void addToBuilder(TextBuilder builder, int x) {
        this.addToBuilder(builder, x, this.getAmountOfPieces());
    }

    /**
     * Add this texture to the given TextBuilder
     * This will also move the cursor position to the left
     *
     * @param   builder
     *
     * @since   0.1.1
     */
    public void addToBuilder(TextBuilder builder, int x, int amount_of_pieces_to_add) {

        String str = "";
        int count = -1;

        // Get the current cursor position
        int start_cursor = builder.getRawCursorPosition();

        // Make sure the cursor is at the wanted position
        builder.setCursor(x);

        // Get the amount of pieces
        int total_piece_count = this.getPieces().size();

        int piece_count = amount_of_pieces_to_add;

        if (piece_count > total_piece_count) {
            piece_count = total_piece_count;
        }

        // Should we mix texture pieces & negative spaces?
        boolean print_mixed = true;
        if (this.getPieceWidth() == 1 && piece_count > 6) {
            print_mixed = false;
        }

        if (print_mixed) {
            int width = 0;

            for (TexturePiece piece : this.getPieces()) {
                count++;

                if (count > 0) {
                    // There is always a 1 pixel gap between characters
                    builder.moveCursorUnsafe(-1);
                }

                // Make sure we don't print too many pieces
                if (count >= piece_count) {
                    break;
                }

                builder.insertUnsafe("" + piece.getCharacter(), GUI_FONT);
                width += piece.getWidth();
            }

            if (count > 0) {
                // Move the cursor back to where the builder thinks it is
                builder.moveCursorUnsafe(-width - 1);
            }
        } else {
            // We should print all the even pieces first and then go back for the uneven ones
            Character pixel_char = null;
            TexturePiece piece;

            // Because each pixel prints an invisible pixel on the right,
            // we need to print the line in 2 passes
            for (int pass = 0; pass < 2; pass++) {
                StringBuilder pass_line = new StringBuilder();
                int placed = 0;

                for (int px = -pass; px < piece_count; px += 2) {

                    if (px < 0) {
                        // Spaces are hardcoded, so we can use that to move forward 4 pixels
                        pass_line.append(' ');
                        continue;
                    }

                    piece = this.getPieces().get(px);
                    pixel_char = piece.getCharacter();
                    pass_line.append(pixel_char);
                    placed++;
                }

                if (placed > 0) {

                    builder.insertUnsafe(pass_line.toString(), GUI_FONT);

                    int move_back = -(placed * 2) - pass;

                    if (pass == 0) {
                        // Subtract another 3 pixels so we can use a regular space to move forward next pass
                        move_back -= 3;
                    } else {
                        move_back -= 1;
                    }

                    // Move the cursor back to the start of the line.
                    // Move it back 1 more pixel after the second pass
                    builder.moveCursorUnsafe(move_back);
                }
            }
        }

        // And now move it back to where it was at the beginning
        builder.setRawCursor(start_cursor);
    }

    /**
     * Add all Texture resources to the given data pack
     */
    public static void addToResourcePack(ModdedResources moddedResources, PolyMcResourcePack pack, SimpleLogger logger) {
        GUI_FONT.addToResourcePack(moddedResources, pack, logger);
    }

    public static InputStream getFileStream(Identifier texture_path) {
        String namespace = texture_path.getNamespace();

        String path = "assets/" + namespace + "/textures/" + texture_path.getPath() + ".png";

        return GuiUtils.findModResource(namespace, path);
    }

}
