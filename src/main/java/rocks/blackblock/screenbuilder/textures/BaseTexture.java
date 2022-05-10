package rocks.blackblock.screenbuilder.textures;

import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.ScreenBuilder;
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

    // The height of the texture
    protected int height = 0;

    // The width of the texture
    protected int width = 0;

    // The counter of this index
    protected Integer gui_nr = null;

    // The pieces of this texture
    private List<TexturePiece> pieces = null;

    // The image pieces
    private List<BufferedImage> image_pieces = null;

    // The Y offsets
    private Map<Integer, List<TexturePiece>> y_pieces = new HashMap<>();

    /**
     * Set the texture
     *
     * @since   0.1.1
     */
    public BaseTexture(Identifier texture_identifier) {
        this.texture_identifier = texture_identifier;
        TEXTURES.add(this);
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
     * @since   0.1.1
     */
    public List<TexturePiece> getPieces(int y) {

        if (!this.y_pieces.containsKey(y)) {
            this.y_pieces.put(y, this.generateTexturePieces(y));
        }

        return this.y_pieces.get(y);
    }

    /**
     * Register an absolute Y offset
     *
     * @since   0.1.3
     */
    public void registerYOffset(int y) {
        if (!this.y_pieces.containsKey(y)) {
            this.y_pieces.put(y, this.generateTexturePieces(y));
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
            container_y = y - (gui.getScreenInfo().getTitleY() + 7);
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
            System.out.println("Failed to load texture file: " + this.texture_identifier + "\n" + e.getMessage());
            return image_pieces;
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

            TexturePiece piece = new TexturePiece(this, i, y, GUI_FONT.getNextChar());
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
    public void addToBuilder(TextBuilder builder, int x, int y, int amount_of_pieces_to_add) {

        String str = "";
        int count = -1;

        // Get the current cursor position
        int start_cursor = builder.getRawCursorPosition();

        // Get the screenbuilder
        ScreenBuilder screenBuilder = builder.getScreenBuilder();

        // Get the gui texture (if any)
        GuiTexture gui_texture = screenBuilder.getFontTexture();

        // Get the coordinate to use in the container
        int container_y = screenBuilder.calculateTitleOffsetY(y);

        // Register the container y
        this.registerYOffset(container_y);

        // Make sure the cursor is at the wanted position
        builder.setCursor(x);

        // Get the amount of pieces
        int total_piece_count = this.getPieceCount();

        int piece_count = amount_of_pieces_to_add;

        if (piece_count > total_piece_count) {
            piece_count = total_piece_count;
        }

        // Should we mix texture pieces & negative spaces?
        boolean print_single_pass = true;
        if (this.getPieceWidth() == 1 && piece_count > 6) {
            print_single_pass = false;
        }

        if (print_single_pass) {
            int width = 0;
            int placed = 0;

            for (TexturePiece piece : this.getPieces(container_y)) {
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
                placed++;
                width += piece.getWidth();
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
                        // Spaces are hardcoded, so we can use that to move forward 4 pixels
                        pass_line.append(' ');
                        continue;
                    }

                    piece = this.getPieces(container_y).get(px);
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

    public static InputStream getFileStream(Path actual_path) {
        try {
            return Files.newInputStream(actual_path, StandardOpenOption.READ);
        } catch (IOException e) {
            System.out.printf("Failed to get resource '%s'%n", actual_path);
        }
        return null;
    }

}
