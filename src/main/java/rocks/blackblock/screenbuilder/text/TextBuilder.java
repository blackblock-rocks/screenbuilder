package rocks.blackblock.screenbuilder.text;

import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.textures.GuiTexture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
    private int cursor = 0;

    // The current X-start coordinate
    private int x_origin = 0;

    // The current Y-start coordinate
    private int y_origin = 0;

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

    /**
     * Create a simple TextBuilder instance
     *
     * @since   0.1.1
     */
    public TextBuilder() {
        this(null);
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
        this.x_origin = this.cursor;
        this.y_origin = this.line;

        return this;
    }

    /**
     * Set the origin position (in pixels) based on the absolute position
     * compared to the original screen
     *
     * @since   0.1.1
     */
    public TextBuilder setCurrentOriginPosition(int x_pixels, int y_pixels) {
        this.x_origin = x_pixels;
        this.y_origin = Font.LH04.convertYToLine(y_pixels);
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
     * Set the current virtual Y index with pixels
     *
     * @param   y
     *
     * @since   0.1.1
     */
    public TextBuilder setY(int y) {
        int line = Font.LH04.convertYToLine(y);
        this.line = this.translateY(line);
        return this;
    }

    /**
     * Set the current virtual Y index
     *
     * @param   line_index
     *
     * @since   0.1.1
     */
    public TextBuilder setLine(int line_index) {
        this.line = this.translateY(line_index);
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
            this.text_list.add(Font.SPACE.convertMovement(0, width));
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

        Text t = font.getText(text);

        this.text_list.add(t);

        int width = font.getWidth(text);

        this.cursor += width;

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

        Text t = font.getText(text);
        this.text_list.add(t);

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

        text = font.getText(line);

        //text = text.getWithStyle(text.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("HI " + this.line)))).get(0);

        this.line++;
        
        // Only add text to the list if it has been initialized
        if (text != null) {
            this.text_list.add(text);

            int width = Font.DEFAULT.getWidth(line);

            if (width != 0) {
                this.text_list.add(Font.SPACE.convertMovement(0, width));
            }
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
        BaseText move = Font.SPACE.convertMovement(move_x, 0);
        this.text_list.add(move);
        this.cursor += move_x;
        return this;
    }

    /**
     * Move the cursor on the current line the given amount of pixels in an unsafe way
     * (This can only be done horizontally)
     *
     * @since   0.1.1
     */
    public TextBuilder moveCursorUnsafe(int move_x) {
        BaseText move = Font.SPACE.convertMovement(move_x, 0);
        this.text_list.add(move);
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

        if (this.cursor != x) {
            BaseText move = Font.SPACE.convertMovement(x, this.cursor);
            this.text_list.add(move);
            this.cursor = x;
        }

        return this;
    }

    /**
     * Get the current cursor position
     *
     * @since   0.1.1
     */
    public int getCursor() {
        return this.cursor;
    }

    /**
     * Compile all the pieces of text into a single text object
     *
     * @since   0.1.1
     */
    public BaseText build() {

        LiteralText text = new LiteralText("");

        for (Text t : this.text_list) {
            text.append(t);
        }

        //System.out.println("Built text: " + Text.Serializer.toJson(text));

        if (this.title != null) {
            this.setLine(0);
            this.setCursor(0);

            Font font = this.getCurrentFont();
            Text title_with_font = this.title.getWithStyle(font.font_style).get(0);

            //System.out.println("Title with font: " + Text.Serializer.toJson(title_with_font));

            text.append(title_with_font);
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

        this.moveCursorUnsafe(dx);

        for (int y = 0; y < height; y += 2) {

            // Because each pixel prints an invisible pixel on the right,
            // we need to print the line in 2 passes
            for (int pass = 0; pass < 2; pass++) {
                String pass_line = "";
                int line_index = (dy + y) / 2;
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

                this.insertUnsafe(pass_line, font);

                // Move the cursor back to the start of the line.
                // Move it back 1 more pixel after the second pass
                this.moveCursorUnsafe(0 - (placed * 2) - 1 * pass);
            }
        }

        this.moveCursorUnsafe(-dx);

    }
}
