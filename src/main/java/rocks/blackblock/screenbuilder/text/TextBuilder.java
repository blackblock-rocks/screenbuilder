package rocks.blackblock.screenbuilder.text;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.textures.GuiTexture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The TextBuilder class is used to build a list of {@link Text} objects.
 *
 * @author  Jelle De Loecker   <jelle@elevenways.be>
 * @since   0.1.1
 * @version 0.1.1
 */
public class TextBuilder {

    // The current VIRTUAL Y-index the cursor is on
    private int line = 0;

    // The current X-index the cursor is on
    private int raw_x = 0;

    // The current X-start coordinate
    private int x_origin = 0;

    // The current Y-start coordinate
    private int y_origin = 0;

    // The X coordinate where text can start
    private int x_text_start = 0;

    // The current line coordinate
    private int line_origin = 0;

    // The default color, if it is known
    private TextColor default_color = null;

    // The default font, if it is known
    private Identifier default_font = null;

    // The title to use
    private Text title = null;

    // All the text pieces added so far
    private final List<Text> text_list = new ArrayList<>();

    // All the text groups
    private final List<TextGroup> groups = new ArrayList<>();

    // The current text group
    private TextGroup current_group = null;

    // The screenbuilder instance, if any
    private ScreenBuilder screen_builder;

    // The textured screen handler instance, if any
    private TexturedScreenHandler screen_handler;

    /**
     * Create a simple TextBuilder instance
     *
     * @since   0.1.1
     */
    public TextBuilder() {
        this((ScreenBuilder) null);
    }

    /**
     * Create a TextBuilder instance with the given ScreenBuilder
     *
     * @param   builder
     *
     * @since   0.1.1
     */
    public TextBuilder(ScreenBuilder builder) {
        this.screen_builder = builder;
        this.createNewGroup();
    }

    /**
     * Create a TextBuilder instance with the given TexturedScreenHandler
     *
     * @param   screen_handler
     *
     * @since   0.1.1
     */
    public TextBuilder(TexturedScreenHandler screen_handler) {
        this(screen_handler.getScreenBuilder());
        this.screen_handler = screen_handler;
    }

    /**
     * Create a new group
     *
     * @since   0.1.1
     */
    public TextGroup createNewGroup() {
        this.current_group = new TextGroup(this);
        this.groups.add(this.current_group);
        return this.current_group;
    }

    /**
     * Set the ScreenBuilder instance
     *
     * @since   0.1.1
     */
    public void setScreenBuilder(ScreenBuilder builder) {
        this.screen_builder = builder;
    }

    /**
     * Get the ScreenBuilder instance
     *
     * @since   0.1.1
     */
    public ScreenBuilder getScreenBuilder() {
        return this.screen_builder;
    }

    /**
     * Get the TexturedScreenHandler instance
     *
     * @since   0.1.1
     */
    public TexturedScreenHandler getScreenHandler() {
        return this.screen_handler;
    }

    /**
     * Make sure we're on a group with the given style
     *
     * @since   0.1.1
     */
    public TextGroup ensureGroup(Style style) {

        if (style == null) {
            return this.current_group;
        }

        if (this.current_group.usesStyle(style)) {
            return this.current_group;
        }

        TextGroup result = this.createNewGroup();
        result.setStyle(style);

        return result;
    }

    /**
     * Get the default color
     *
     * @since   0.1.1
     */
    public TextColor getDefaultColor() {
        return this.default_color;
    }

    /**
     * Set the default color
     *
     * @since   0.1.1
     */
    public TextBuilder setDefaultColor(TextColor color) {
        this.default_color = color;
        return this;
    }

    /**
     * Get the default font
     *
     * @since   0.1.1
     */
    public Identifier getDefaultFont() {
        return this.default_font;
    }

    /**
     * Set the default font
     *
     * @since   0.1.1
     */
    public TextBuilder setDefaultFont(Identifier font) {
        this.default_font = font;
        return this;
    }

    /**
     * Make the current position the start position
     *
     * @since   0.1.1
     */
    public TextBuilder makeCurrentPositionOrigin() {
        this.x_origin = this.raw_x;
        this.line_origin = this.line;

        // @TODO: fix
        this.y_origin = 0;

        return this;
    }

    /**
     * Set the origin position (in pixels) based on the absolute position
     * compared to the original screen
     *
     * @param   x_pixels   The x position in pixels
     * @param   y_pixels   The y position in pixels
     *
     * @since   0.1.1
     */
    public TextBuilder setCurrentOriginPosition(int x_pixels, int y_pixels) {

        this.x_origin = x_pixels;
        this.y_origin = y_pixels;

        //this.line_origin = Font.LH04.convertYToLine(y_pixels);
        //this.y_origin = y_pixels + y_text;

        return this;
    }

    /**
     * Set the title to eventually use
     *
     * @since   0.1.1
     */
    public TextBuilder setTitle(Text title) {
        this.title = title;
        return this;
    }

    /**
     * Set the X text start coordinate
     * (Compared to the current origin)
     *
     * @since   0.1.1
     */
    public void setTextStartX(int x) {
        this.x_text_start = x;
    }

    /**
     * Get the X text start coordinate
     *
     * @since   0.1.1
     */
    public int getTextStartX() {
        return this.x_text_start;
    }

    /**
     * Go to the start X position of where text can be put
     *
     * @since   0.1.1
     */
    public void moveCursorToTextStart() {
        this.setCursor(this.x_text_start);
    }

    /**
     * Translate the given X coordinate compared to the current X-origin
     *
     * @since   0.1.1
     */
    public int translateX(int x) {
        return x + this.x_origin;
    }

    /**
     * Translate the given Y coordinate compared to the current Y-origin
     *
     * @since   0.1.1
     */
    public int translateY(int y) {
        return y + this.y_origin;
    }

    /**
     * Untranslate the given X coordinate compared to the current X-origin
     *
     * @since   0.1.1
     */
    public int untranslateX(int x) {
        return x - this.x_origin;
    }

    /**
     * Untranslate the given Y coordinate compared to the current Y-origin
     *
     * @since   0.1.1
     */
    public int untranslateY(int y) {
        return y - this.y_origin;
    }

    /**
     * Set the current virtual Y index with pixels
     *
     * @param   y
     *
     * @since   0.1.1
     */
    public TextBuilder setY(int y) {
        int line = this.convertYToLine(y);
        this.line = this.translateY(line);
        return this;
    }

    /**
     * Convert a Y value to a line number
     *
     * @param   y
     *
     * @since   0.1.1
     */
    public int convertYToLine(int y) {
        return Font.LH04.convertYToLine(y);
    }

    /**
     * Convert a Y value to a pixel line number
     *
     * @param   y
     *
     * @since   0.1.1
     */
    public int convertYToPixelLine(int y) {
        int result = y - this.y_origin;
        return result / 2;
    }

    /**
     * Set the current virtual Y index
     *
     * @param   line_index
     *
     * @since   0.1.1
     */
    public TextBuilder setLine(int line_index) {
        //this.line = this.translateY(line_index);
        this.line = line_index;
        return this;
    }

    /**
     * Get the current line the cursor is on
     *
     * @since   0.1.1
     */
    public int getLine() {
        return this.line;
    }

    /**
     * Get the font to use for the current line
     *
     * @since   0.1.1
     */
    public Font getCurrentFont() {

        if (this.line == 0) {
            return Font.DEFAULT;
        }

        Font font = Font.getLhFont(this.line);

        if (font == null) {
            font = Font.DEFAULT;
        }

        return font;
    }

    /**
     * Get the current text group
     *
     * @since   0.1.1
     */
    public TextGroup getCurrentGroup() {
        return this.current_group;
    }

    /**
     * Insert some text and move the cursor back
     * @param text
     * @param font
     * @return
     */
    public TextBuilder insertAndMoveBack(String text, Font font) {

        Text t = font.getText(text);

        this.text_list.add(t);

        int width = font.getWidth(text);

        if (width != 0) {
            Font.SPACE.addMovementToBuilder(this, 0, width);
        }

        return this;
    }

    /**
     * Insert some text on the current line and update the cursor
     *
     * @param   text
     *
     * @return
     */
    public TextBuilder print(String text) {
        return this.print(text, this.getCurrentFont());
    }

    /**
     * Insert some text on the current line and update the cursor
     *
     * @param   text
     * @param   font
     *
     * @return
     */
    public TextBuilder print(String text, Font font) {

        font.addTo(this, text);

        int width = font.getWidth(text);
        this.raw_x += width;

        return this;
    }

    /**
     * Insert some text without updating the cursor
     *
     * @param text
     * @param font
     *
     * @return
     */
    public TextBuilder insertUnsafe(String text, Font font) {

        font.addTo(this, text);

        return this;
    }

    /**
     * Add some text to the current line and move to the next line
     * @param line
     * @return
     */
    public TextBuilder addLine(String line) {
        
        Text text = null;
        Font font = Font.getLhFont(this.line);


        //text = text.getWithStyle(text.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("HI " + this.line)))).get(0);

        this.line++;

        font.addTo(this, line);

        int width = Font.DEFAULT.getWidth(line);

        if (width != 0) {
            Font.SPACE.addMovementToBuilder(this, 0, width);
        }

        
        return this;
    }

    /**
     * Move the cursor on the current line the given amount of pixels
     * (This can only be done horizontally)
     *
     * @since   0.1.1
     */
    public TextBuilder moveCursor(int move_x) {
        Font.SPACE.addMovementToBuilder(this, move_x, 0);
        this.raw_x += move_x;
        return this;
    }

    /**
     * Move the cursor on the current line the given amount of pixels in an unsafe way
     * (This can only be done horizontally)
     *
     * @since   0.1.1
     */
    public TextBuilder moveCursorUnsafe(int move_x) {
        Font.SPACE.addMovementToBuilder(this, move_x, 0);
        return this;
    }

    /**
     * Set the cursor to this absolute horizontal position
     * (Absolute compared to the current origin point)
     *
     * @since   0.1.1
     */
    public TextBuilder setCursor(int x) {
        x = this.translateX(x);
        return this.setRawCursor(x);
    }

    /**
     * Set the raw cursor to this absolute horizontal position
     *
     * @since   0.1.1
     */
    public TextBuilder setRawCursor(int x) {

        if (this.raw_x != x) {
            Font.SPACE.addMovementToBuilder(this, x, this.raw_x);
            this.raw_x = x;
        }

        return this;
    }

    /**
     * Get the current cursor position (possible translated back)
     *
     * @since   0.1.1
     */
    public int getCursor() {
        return this.untranslateX(this.raw_x);
    }

    /**
     * Get the current raw cursor position
     *
     * @since   0.1.1
     */
    public int getRawCursorPosition() {
        return this.raw_x;
    }

    /**
     * Compile all the pieces of text into a single text object
     *
     * @since   0.1.1
     */
    public Text build() {

        if (this.groups.size() == 1 && this.title == null) {
            return this.groups.get(0).build();
        }

        LiteralText text = new LiteralText("");

        // If there is a title, move the cursor to the right start
        if (this.title != null) {
            this.setLine(0);
            this.setCursor(this.x_text_start);
        }

        for (TextGroup group : this.groups) {
            group.buildInto(text);
        }

        //System.out.println("Built text: " + Text.Serializer.toJson(text));

        if (this.title != null) {

            Font font = this.getCurrentFont();
            MutableText title_with_font = this.title.shallowCopy();
            //title_with_font.setStyle(font.font_style);

            //System.out.println("Title with font: " + Text.Serializer.toJson(title_with_font));

            text.append(title_with_font);
        }

        boolean debug = false;

        if (debug) {
            System.out.println("Dumping GUI text to /tmp/mc_textbuilder.json");
            String json_string = Text.Serializer.toJson(text);

            Path path = Paths.get("/tmp/mc_textbuilder.json");
            byte[] strToBytes = json_string.getBytes();

            try {
                Files.write(path, strToBytes);
            } catch (Exception e) {

            }
        }

        return text;
    }

    /**
     * Return a debug string representation of the text builder
     *
     * @since   0.1.1
     */
    public String toString() {

        String result = "TextBuilder {";

        for (Text t : this.text_list) {
            result += t.asString();
        }

        result += "}";

        return result;
    }

    /**
     * Return the JSON representation
     *
     * @since   0.1.1
     */
    public String getJsonString() {
        return Text.Serializer.toJson(this.build());
    }

    /**
     * Print a texture to the screen
     *
     * @param texture_path
     * @param x
     * @param y
     *
     * @since   0.1.1
     */
    public void printTexture(Identifier texture_path, int x, int y) {

        InputStream image_stream = GuiTexture.getFileStream(texture_path);
        byte[] data;
        BufferedImage source_image = null;

        try {
            data = image_stream.readAllBytes();
            source_image = ImageIO.read(new ByteArrayInputStream(data));
        } catch (Exception e) {
            System.out.println("Error loading image: " + texture_path);
            return;
        }

        this.printImage(source_image, x, y);

    }

    /**
     * Ensure we're in a SPACE font group
     *
     * @since   0.1.1
     */
    public TextGroup ensureSpaceGroup() {
        TextGroup group = this.ensureGroup(Style.EMPTY.withFont(Font.SPACE.identifier).withColor(TextColor.fromFormatting(Formatting.WHITE)));
        this.current_group = group;
        return group;
    }

    /**
     * Print an image to the screen
     *
     * @param image
     * @param dx
     * @param dy
     *
     * @since   0.1.1
     */
    public void printImage(BufferedImage image, int dx, int dy) {

        int width = image.getWidth();
        int height = image.getHeight();

        Color original_top_color;
        Color replacement_top_color;
        Color original_bottom_color;
        Color replacement_bottom_color;
        Character pixel_char = null;

        TextGroup group = this.ensureSpaceGroup();

        // Make sure the cursor is at the wanted position
        this.setCursor(dx);

        for (int y = 0; y < height; y += 2) {

            // Because each pixel prints an invisible pixel on the right,
            // we need to print the line in 2 passes
            for (int pass = 0; pass < 2; pass++) {
                String pass_line = "";

                int line_index = this.convertYToPixelLine(dy + y);

                Font font = PixelFontCollection.PX01.getFontForLine(line_index);
                int placed = 0;

                for (int x = 0 - pass; x < width; x += 2) {

                    if (x < 0) {
                        // This will move it 1 pixel to the right
                        pass_line += '9';
                        continue;
                    }

                    original_top_color = new Color(image.getRGB(x, y));
                    original_bottom_color = new Color(image.getRGB(x, y + 1));

                    replacement_top_color = PixelFontCollection.getNearestColor(original_top_color);
                    replacement_bottom_color = PixelFontCollection.getNearestColor(original_bottom_color);

                    pixel_char = PixelFontCollection.PX01.getCharacter(replacement_top_color, replacement_bottom_color);

                    if (pixel_char == null) {
                        pixel_char = '8';
                    }

                    pass_line += pixel_char;
                    placed++;
                }

                if (placed > 0) {

                    this.insertUnsafe(pass_line, font);

                    // Move the cursor back to the beginning of the image.
                    // Move it back 1 more pixel after the second pass
                    this.moveCursorUnsafe(0 - (placed * 2) - 1 * pass);
                }
            }
        }

    }
}
