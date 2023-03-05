package rocks.blackblock.screenbuilder.textures;

import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.text.GuiFont;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;

/**
 * The base Texture class
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.1
 * @version  0.1.1
 */
public abstract class BaseTexture {

    // All textures
    public static final Set<BaseTexture> TEXTURES = new HashSet<>();

    // The max allowed width of an image
    private static final int MAX_WIDTH = 128;

    // A running counter of registered gui textures
    protected static int gui_counter = 0;

    // The "font" to use for textures
    public static final GuiFont GUI_FONT = new GuiFont("bbsb:gui");

    // The identifier/path to the original texture
    protected Identifier texture_identifier;

    // Set the optional texture path
    protected Path texture_path = null;

    // The height of the texture (as seen in the gui)
    protected int height = 0;

    // The width of the texture (as seen in the gui)
    protected int width = 0;

    // The original height of the texture
    protected int original_height = 0;

    // The original width of the texture
    protected int original_width = 0;

    // The scale of this texture (how bigger the texture is in the file)
    protected double scale = 1;

    // The counter of this index
    protected Integer gui_nr = null;

    // The pieces of this texture
    private List<TexturePiece> pieces = null;

    // The image pieces
    private List<BufferedImage> image_pieces = null;

    // The Y offsets
    private Map<Integer, List<TexturePiece>> y_pieces = new HashMap<>();

    // The colour to apply to the texture
    protected TextColor texture_colour = null;

    /**
     * Create the instance without registering
     *
     * @since   0.2.1
     */
    protected BaseTexture(Identifier texture_identifier, boolean register) {
        this.texture_identifier = texture_identifier;

        if (register) {
            TEXTURES.add(this);
        }
    }

    /**
     * Create the instance
     *
     * @since   0.1.1
     */
    public BaseTexture(Identifier texture_identifier) {
        this(texture_identifier, true);
    }

    /**
     * Calculate all textures
     *
     * @since   0.1.3
     */
    public static void calculateAll() {
        for (BaseTexture texture : TEXTURES) {
            texture.calculate();
        }
    }

    /**
     * Get a coloured version of this texture
     *
     * @since   0.2.1
     */
    public BaseTexture getColoured(TextColor colour) {

        if (colour == null) {
            return this;
        }

        ColouredTexture coloured = new ColouredTexture(this);
        coloured.texture_colour = colour;
        return coloured;
    }

    /**
     * Get the root, uncouloured texture
     *
     * @since   0.2.1
     */
    public BaseTexture getOriginalTexture() {
        return this;
    }

    /**
     * Get the texture pieces of a specific Y offset
     * (Will also call the `calculate` method the first time)
     *
     * @since   0.1.1
     */
    public List<TexturePiece> getPieces() {
        return this.getPieces(0);
    }

    /**
     * Get the texture pieces of a specific Y offset
     * (Will also call the `calculate` method the first time)
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.1
     *
     * @param   title_y   The Y coordinate relative to the title
     */
    public List<TexturePiece> getPieces(int title_y) {

        if (!this.y_pieces.containsKey(title_y)) {
            this.y_pieces.put(title_y, this.generateTexturePieces(title_y));
        }

        return this.y_pieces.get(title_y);
    }

    /**
     * Register an absolute Y offset
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     *
     * @param   title_y   The Y coordinate relative to the title
     */
    public void registerYOffset(int title_y) {
        if (!this.y_pieces.containsKey(title_y)) {
            this.y_pieces.put(title_y, this.generateTexturePieces(title_y));
        }
    }

    /**
     * Register a relative Y offset
     *
     * @since   0.1.3
     */
    public void registerYOffset(ScreenBuilder gui, int y) {

        GuiTexture gui_texture = gui.getFontTexture();
        int container_y;

        if (gui_texture == null) {
            container_y = y - (gui.getScreenInfo().getTitleBaselineY());
        } else {
            container_y = gui_texture.getContainerY(y);
        }

        this.registerYOffset(container_y);
    }

    /**
     * Get the texture pieces
     * (Will also call the `calculate` method the first time)
     *
     * @since   0.1.1
     */
    public List<BufferedImage> getImagePieces() {

        if (this.image_pieces == null) {
            this.image_pieces = this.generateImagePieces();
        }

        return this.image_pieces;
    }

    /**
     * Set the texture pieces
     *
     * @since   0.1.1
     */
    public void setPieces(List<TexturePiece> pieces) {
        this.pieces = pieces;
    }

    /**
     * Get the path to the texture file
     *
     * @since   0.1.2
     */
    public Path getTexturePath() {

        if (this.texture_path != null) {
            return this.texture_path;
        }

        return null;
    }

    /**
     * Set the path to the texture file
     *
     * @since   0.1.2
     */
    public void setTexturePath(Path texture_path) {
        this.texture_path = texture_path;
        this.recalculate();
    }

    /**
     * Get the texture identifier
     *
     * @since   0.1.1
     */
    public Identifier getTextureIdentifier() {
        return this.texture_identifier;
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
    public abstract int getAscent(int y_offset);

    /**
     * Get the maximum width an image piece can be
     *
     * @since   0.1.1
     */
    public int getMaxImagePieceWidth() {
        return MAX_WIDTH;
    }

    /**
     * Get the minimum width a piece can be
     *
     * @since   0.1.1
     */
    public int getPreferredImagePieceWidth() {

        if (this.getPreferredAmountOfPieces() != null) {
            return this.original_width / this.getPreferredAmountOfPieces();
        }

        return this.getMaxImagePieceWidth();
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
        int max_amount = (int) Math.ceil((double) this.original_width / (double) this.getPreferredImagePieceWidth());

        if (min_amount != null && min_amount > max_amount) {
            return min_amount;
        }

        return max_amount;
    }

    /**
     * Get the width of a piece
     */
    public int getImagePieceWidth() {
        return this.getImagePieceWidth(0);
    }

    /**
     * Get the width of a specific piece
     *
     * @since   0.1.1
     */
    public int getImagePieceWidth(int piece_index) {
        return (int) Math.ceil((double) this.original_width / (double) this.getAmountOfPieces());
    }

    /**
     * Get the target image width
     *
     * @since   0.1.1
     */
    public int getTargetImageWidth() {
        return this.getImagePieceWidth(0) * this.getAmountOfPieces();
    }

    /**
     * Get the source start X of a specific piece
     *
     * @since   0.1.1
     */
    public int getPieceSourceXStart(int piece_index) {
        return piece_index * this.getMaxImagePieceWidth();
    }

    /**
     * Get the source end X of a specific piece
     *
     * @since   0.1.1
     */
    public int getPieceSourceXEnd(int piece_index) {
        return (piece_index * this.getMaxImagePieceWidth()) + this.getImagePieceWidth(piece_index);
    }

    /**
     * Make sure all the pieces are generated
     *
     * @since   0.1.1
     */
    public void calculate() {
        for (Integer y : this.y_pieces.keySet()) {
            this.getPieces(y);
        }
    }

    /**
     * Make sure all the pieces are re-generated
     *
     * @since   0.1.1
     */
    public List<TexturePiece> recalculate() {
        this.pieces = null;
        return this.getPieces();
    }

    /**
     * Get the source image
     *
     * @since   0.1.1
     */
    public BufferedImage getSourceImage() throws IOException {
        byte[] data;
        BufferedImage source_image = null;

        if (this.texture_path != null) {
            data = getFileStream(this.texture_path).readAllBytes();
        } else {
            data = getFileStream(this.texture_identifier).readAllBytes();
        }

        source_image = ImageIO.read(new ByteArrayInputStream(data));

        return source_image;
    }

    /**
     * Set the scale of this texture
     *
     * @since   0.3.0
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * Calculate all the pieces of this texture
     * (without setting them)
     *
     * @since   0.1.1
     */
    protected List<BufferedImage> generateImagePieces() {

        List<BufferedImage> image_pieces = new ArrayList<>();

        // Only set the gui_nr if it hasn't been assigned one yet
        if (this.gui_nr == null) {
            this.gui_nr = BaseTexture.gui_counter++;
        }

        BufferedImage source_image = null;

        try {
            source_image = this.getSourceImage();
        } catch (Exception e) {
            BBSB.log("Failed to load texture file:", this.texture_identifier, "\n" + e.getMessage());
            return image_pieces;
        }

        this.original_height = source_image.getHeight();
        this.original_width = source_image.getWidth();

        // Calculate the dimensions as seen in the GUI
        this.height = (int) Math.floor(this.original_height / this.scale);
        this.width = (int) Math.floor(this.original_width / this.scale);

        // Calculate the amount of pieces we need
        int pieces = this.getAmountOfPieces();

        int target_width = this.getTargetImageWidth();
        int piece_width = this.getImagePieceWidth(0);

        // Create the target image (the widths remain the same)
        BufferedImage target_image = new BufferedImage(target_width, this.original_height, BufferedImage.TYPE_INT_ARGB);
        Graphics target_graphics = target_image.getGraphics();

        for (int i = 0; i < pieces; i++) {

            int x_start = i * piece_width;
            int x_end = x_start + piece_width;

            target_graphics.drawImage(
                    source_image,
                    // Destination coordinates
                    x_start, 0,
                    x_end, this.original_height,

                    // Source coordinates
                    x_start, 0,
                    x_end, this.original_height,
                    null
            );

            boolean has_transparent_last_column = true;

            // Iterate over all the pixels in the last column.
            // If all the pixels are transparent,
            // we will make the top-right pixel a tiny bit opaque
            for (int y = 0; y < this.original_height; y++) {
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

            image_pieces.add(target_image);
        }

        return image_pieces;
    }

    /**
     * Calculate all the pieces of this texture for the given Y position
     * (without setting them)
     *
     * @since   0.1.1
     */
    protected List<TexturePiece> generateTexturePieces(int y) {

        List<BufferedImage> image_pieces = this.getImagePieces();
        List<TexturePiece> pieces = new ArrayList<>();

        for (int i = 0; i < image_pieces.size(); i++) {
            BufferedImage image = image_pieces.get(i);
            char piece_char = GUI_FONT.getNextChar();

            TexturePiece piece = new TexturePiece(this, i, y, piece_char);
            piece.setUsesSharedImage(true);
            pieces.add(piece);
            GUI_FONT.registerTexturePiece(piece);
            piece.setImage(image);
        }

        return pieces;
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
    public void addToBuilder(TextBuilder builder, int x, int y) {
        this.addToBuilder(builder, x, y, this.getAmountOfPieces());
    }

    /**
     * Add this texture to the given TextBuilder
     * This will also move the cursor position to the left
     *
     * @param   builder
     *
     * @since   0.1.1
     */
    public void addAmountToBuilder(TextBuilder builder, int x, int amount_of_pieces_to_add) {
        this.addToBuilder(builder, x, 0, amount_of_pieces_to_add);
    }

    /**
     * Get the amount of pieces this texture has
     *
     * @since   0.1.3
     */
    public int getPieceCount() {
        return this.getImagePieces().size();
    }

    /**
     * Add this texture to the given TextBuilder
     * This will also move the cursor position to the left
     *
     * @param   builder
     *
     * @since   0.1.1
     */
    public void addToBuilder(TextBuilder builder, int gui_x, int gui_y, int amount_of_pieces_to_add) {

        String str = "";
        int count = -1;

        // Get the current cursor position
        int start_cursor = builder.getRawCursorPosition();

        // Get the screenbuilder
        ScreenBuilder screenBuilder = builder.getScreenBuilder();

        // Get the gui texture (if any)
        GuiTexture gui_texture = screenBuilder.getFontTexture();

        // Get the coordinate to use in the container
        int container_y = screenBuilder.getContainerY(gui_y);
        //int container_y = screenBuilder.calculateTitleOffsetY(gui_y);
        //int container_y = gui_y;

        int title_y = screenBuilder.convertToUnderlyingTitleY(gui_y);

        // Register the container gui_y
        this.registerYOffset(container_y);

        // Make sure the cursor is at the wanted position
        builder.setCursor(gui_x);

        // Get the amount of pieces
        int total_piece_count = this.getPieceCount();

        int piece_count = amount_of_pieces_to_add;

        if (piece_count > total_piece_count) {
            piece_count = total_piece_count;
        }

        // Should we mix texture pieces & negative spaces?
        boolean print_single_pass = true;
        if (this.getImagePieceWidth() == 1 && piece_count > 6) {
            print_single_pass = false;
        }

        // Add the splitting '$' (1px back) and the '9' (1px forward)
        builder.insertUnsafe("9$", Font.SPACE);

        if (print_single_pass) {
            int width = 0;
            int placed = 0;

            for (TexturePiece piece : this.getPieces(title_y)) {
                count++;

                if (count > 0) {
                    // There is always a 1 pixel gap between characters
                    builder.moveCursorUnsafe(-1);
                }

                // Make sure we don't print too many pieces
                if (count >= piece_count) {
                    break;
                }

                // This also adds unsafe, without moving the cursor back
                if (this.texture_colour != null) {
                    GUI_FONT.addTo(builder, ""+piece.getCharacter(), Style.EMPTY.withColor(this.texture_colour));
                } else {
                    GUI_FONT.addTo(builder, "" + piece.getCharacter());
                }

                placed++;
                width += piece.getGuiWidth();
            }

            if (placed > 0) {
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
                        // Spaces are always added to GUI fonts as 4px wide
                        pass_line.append(' ');
                        continue;
                    }

                    piece = this.getPieces(title_y).get(px);
                    pixel_char = piece.getCharacter();
                    pass_line.append(pixel_char);
                    placed++;
                }

                if (placed > 0) {

                    // This also adds unsafe, without moving the cursor back
                    if (this.texture_colour != null) {
                        GUI_FONT.addTo(builder, pass_line.toString(), Style.EMPTY.withColor(this.texture_colour));
                    } else {
                        GUI_FONT.addTo(builder, pass_line.toString());
                    }

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

        // Add the splitting '$' (1px back) and the '9' (1px forward)
        builder.insertUnsafe("9$", Font.SPACE);
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

    public static InputStream getFileStream(Path actual_path) {
        try {
            return Files.newInputStream(actual_path, StandardOpenOption.READ);
        } catch (IOException e) {
            System.out.printf("Failed to get resource '%s'%n", actual_path);
        }
        return null;
    }

    /**
     * Return the string representation of this instance
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    @Override
    public String toString() {
        String result = this.getClass().getSimpleName() + "{\"" + this.texture_identifier + "\", pieces=" + this.getPieceCount() + ", piecewidth=" + this.getImagePieceWidth() + "}";
        return result;
    }

}
