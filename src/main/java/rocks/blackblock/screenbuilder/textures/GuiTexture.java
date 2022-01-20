package rocks.blackblock.screenbuilder.textures;

import io.github.theepicblock.polymc.api.resource.ResourcePackMaker;
import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.text.GuiFont;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import javax.imageio.ImageIO;
import rocks.blackblock.screenbuilder.text.Font;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Texture class used for GUIs
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.1
 * @version  0.1.1
 */
public class GuiTexture {

    // The max allowed width of a piece of GUI
    private static final int MAX_WIDTH = 128;

    // The "font" to use for textures
    public static final GuiFont GUI_FONT = new GuiFont("bbsb:gui");

    // Some calculations can be stored here, in case there needs to be some sharing
    private static HashMap<Identifier, GuiTexture> textures = new HashMap<>();

    // A running counter of registered gui textures
    private static int gui_counter = 0;

    // The pieces of this texture
    private ArrayList<GuiTexturePiece> pieces = new ArrayList<>();

    // The identifier/path to the original texture
    private Identifier texture_path = null;

    // The X coordinate of the original texture
    private int original_x = 0;

    // The Y coordinate of the original texture
    private int original_y = 0;

    // The X coordinate of where text can start
    private int text_x = 0;

    // The Y coordinate of where text can start
    private int text_y = 0;

    // The height of the texture
    private int height = 0;

    // The width of the texture
    private int width = 0;

    // The counter of this index
    private final int gui_nr;

    public GuiTexture(Identifier texture_path, int original_x, int original_y) {
        this.texture_path = texture_path;
        this.original_x = original_x;
        this.original_y = original_y;
        this.gui_nr = gui_counter++;

        this.calculate();
    }

    /**
     * Get and/or create & register a texture
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

        return result;
    }

    /**
     * Add all GuiTexture resources to the given data pack
     */
    public static void addToResourcePack(ResourcePackMaker pack) {
        GUI_FONT.addToResourcePack(pack);
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
     * Get the texture identifier
     *
     * @since   0.1.1
     */
    public Identifier getTextureIdentifier() {
        return this.texture_path;
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
     * Calculate all the pieces of this texture
     */
    public void calculate() {

        byte[] data;

        try {
            data = getFileStream(this.texture_path).readAllBytes();
        } catch (Exception e) {
            System.out.println("Failed to load GUI texture file: " + this.texture_path);
            return;
        }

        BufferedImage source_image = null;

        try {
            source_image = ImageIO.read(new ByteArrayInputStream(data));
        } catch (Exception e) {
            System.out.println("Failed to read GUI texture data: " + this.texture_path);
            return;
        }

        this.height = source_image.getHeight();
        this.width = source_image.getWidth();

        // Calculate the amount of pieces we need
        int pieces = (int) Math.ceil((double) this.width / (double) MAX_WIDTH);

        for (int i = 0; i < pieces; i++) {
            BufferedImage piece_image = new BufferedImage(MAX_WIDTH, this.height, BufferedImage.TYPE_INT_ARGB);
            Graphics piece_graphics = piece_image.getGraphics();

            int piece_width = Math.min(MAX_WIDTH, this.width - i * MAX_WIDTH);

            piece_graphics.drawImage(
                    source_image,
                    // Destination coordinates
                    0, 0,
                    piece_width, this.height,

                    // Source coordinates
                    i * MAX_WIDTH, 0,
                    (i * MAX_WIDTH) + piece_width, this.height,
                    null
            );

            GuiTexturePiece piece = new GuiTexturePiece(this, i, GUI_FONT.getNextChar());
            this.pieces.add(piece);
            GUI_FONT.registerTexturePiece(piece);
            piece.setImage(piece_image);
        }
    }

    /**
     * Get the text to use for the texture.
     * This should be the first text of the displayname of the screen.
     *
     * @since   0.1.1
     */
    public String getTextureString() {

        String result = "";

        return result;

    }

    public static InputStream getFileStream(Identifier texture_path) {
        String namespace = texture_path.getNamespace();

        String path = "assets/" + namespace + "/textures/" + texture_path.getPath() + ".png";

        return GuiUtils.findModResource(namespace, path);
    }

    /**
     * Return the X-coordinate where the title starts in the original, unmodded container
     * (Probably a generic inventory container screen)
     * @return
     */
    public int getOriginalContainerTitleStartX() {
        // @TODO: get for any type of container!
        return 8;
    }

    /**
     * Return the Y-coordinate where the title starts in the original, unmodded container
     * (Probably a generic inventory container screen)
     * @return
     */
    public int getOriginalContainerTitleStartY() {
        return 13;
    }

    public int getInitialCursorAdjustmentX() {
        // Subtract the position of the container thingy
        return 0 - (this.getOriginalContainerTitleStartX() + this.original_x);
    }

    public void addToBuilder(TextBuilder builder) {

        String str = "";
        int count = -1;
        int width = 0;

        int initial_cursor_adjustment = this.getInitialCursorAdjustmentX();

        builder.insertAndMoveBack("++", Font.DEFAULT);

        System.out.println("Initial cursor adjustment: " + initial_cursor_adjustment);

        // Move the cursor, so it's at the correct X coordinate
        // (This actually depends on the container used)
        if (initial_cursor_adjustment != 0) {
            builder.moveCursor(initial_cursor_adjustment);
        }

        builder.insertAndMoveBack("==", Font.DEFAULT);

        for (GuiTexturePiece piece : this.pieces) {
            count++;

            if (count > 0) {
                System.out.println("Moving cursor by -1!");
                // There is always a 1 pixel gap between characters
                builder.moveCursor(-1);
            }

            builder.insertUnsafe(""+piece.getCharacter(), GUI_FONT);
            width += piece.getWidth();

            System.out.println("Gui piece width is:: " + piece.getWidth());
        }

        if (initial_cursor_adjustment != 0) {
            builder.moveCursor(-initial_cursor_adjustment);
        }

        builder.moveCursor(-width);

        builder.insertAndMoveBack("?", Font.DEFAULT);
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
