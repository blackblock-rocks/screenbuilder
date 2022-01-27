package rocks.blackblock.screenbuilder.textures;

import io.github.theepicblock.polymc.api.resource.ResourcePackMaker;
import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.text.GuiFont;
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
    protected ArrayList<TexturePiece> pieces = new ArrayList<>();

    /**
     * Set the texture
     *
     * @since   0.1.1
     */
    public BaseTexture(Identifier texture_path) {
        this(texture_path, true);
    }

    /**
     * Set the texture
     *
     * @since   0.1.1
     */
    public BaseTexture(Identifier texture_path, boolean do_calculation) {
        this.texture_path = texture_path;

        if (do_calculation) {
            this.calculate();
        }
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
     * Get the amount of pieces this texture should have
     *
     * @since   0.1.1
     */
    public int getAmountOfPieces() {
        return (int) Math.ceil((double) this.width / (double) this.getMaxPieceWidth());
    }

    /**
     * Get the width of a specific piece
     *
     * @since   0.1.1
     */
    public int getPieceWidth(int piece_index) {
        int max_width = this.getMaxPieceWidth();
        return Math.min(max_width, this.width - piece_index * max_width);
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
    protected void calculate() {

        byte[] data;

        // Only set the gui_nr if it hasn't been assigned one yet
        if (this.gui_nr == null) {
            this.gui_nr = BaseTexture.gui_counter++;
        }

        try {
            data = getFileStream(this.texture_path).readAllBytes();
        } catch (Exception e) {
            System.out.println("Failed to load texture file: " + this.texture_path);
            return;
        }

        BufferedImage source_image = null;

        try {
            source_image = ImageIO.read(new ByteArrayInputStream(data));
        } catch (Exception e) {
            System.out.println("Failed to read texture data: " + this.texture_path);
            return;
        }

        this.height = source_image.getHeight();
        this.width = source_image.getWidth();

        int max_width = this.getMaxPieceWidth();

        // Calculate the amount of pieces we need
        int pieces = this.getAmountOfPieces();

        for (int i = 0; i < pieces; i++) {
            int piece_width = this.getPieceWidth(i);

            BufferedImage piece_image = new BufferedImage(piece_width, this.height, BufferedImage.TYPE_INT_ARGB);
            Graphics piece_graphics = piece_image.getGraphics();

            piece_graphics.drawImage(
                    source_image,
                    // Destination coordinates
                    0, 0,
                    piece_width, this.height,

                    // Source coordinates
                    this.getPieceSourceXStart(i), 0,
                    this.getPieceSourceXEnd(i), this.height,
                    null
            );

            boolean has_transparent_last_column = true;

            // Iterate over all the pixels in the last column.
            // If all the pixels are transparent,
            // we will make the top-right pixel a tiny bit opaque
            for (int y = 0; y < this.height; y++) {
                int pixel = piece_image.getRGB(piece_width - 1, y);

                int alpha = (pixel & 0xff000000) >>> 24;

                if (alpha != 0) {
                    has_transparent_last_column = false;
                    break;
                }
            }

            // If the last column is totally transparent,
            // make the top-right pixel a tiny bit opaque
            if (has_transparent_last_column) {
                piece_image.setRGB(piece_width - 1, 0, 0x01000001);
            }

            TexturePiece piece = new TexturePiece(this, i, GUI_FONT.getNextChar());
            this.pieces.add(piece);
            GUI_FONT.registerTexturePiece(piece);
            piece.setImage(piece_image);
        }
    }

    /**
     * Add all Texture resources to the given data pack
     */
    public static void addToResourcePack(ResourcePackMaker pack) {
        GUI_FONT.addToResourcePack(pack);
    }

    public static InputStream getFileStream(Identifier texture_path) {
        String namespace = texture_path.getNamespace();

        String path = "assets/" + namespace + "/textures/" + texture_path.getPath() + ".png";

        return GuiUtils.findModResource(namespace, path);
    }

}
