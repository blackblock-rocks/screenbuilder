package rocks.blackblock.screenbuilder.text;

import net.minecraft.text.*;
import rocks.blackblock.screenbuilder.ScreenBuilder;

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

    // The title to use
    private Text title = null;

    // All the text pieces added so far
    private final List<Text> text_list = new ArrayList<>();

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
}
