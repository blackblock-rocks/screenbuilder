package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.text.TextColor;
import org.jetbrains.annotations.NotNull;
import rocks.blackblock.bib.util.BibLog;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.text.LineHeightFontCollection;
import rocks.blackblock.screenbuilder.text.TextBuilder;

public class StringWidget extends Widget {

    protected int x = 0;
    protected int y = 0;
    protected String text = null;
    protected boolean centered = false;
    protected int width = 0;
    protected LineHeightFontCollection font_collection = Font.ABSOLUTE_DEFAULT_COLLECTION;
    protected TextColor color = TextColor.fromRgb(0x3f3f3f);
    protected Integer y_line = null;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Deprecated
    public void setYLine(int line) {
        int y = this.font_collection.lineIndexToY(line);
        this.setY(y);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setColor(TextColor color) {
        this.color = color;
    }

    public void setFontCollection(LineHeightFontCollection font_collection) {
        this.font_collection = font_collection;
    }

    @Override
    public void addWithValue(TextBuilder builder, Object value) {

    }

    /**
     * Convert the Y coordinate inside this GUI into a line index
     *
     * @since   0.1.3
     */
    public int getLineIndex(int gui_y) {

        ScreenBuilder screen_builder = this.getScreenBuilder();

        int y;

        if (screen_builder == null) {
            y = gui_y;
        } else {
            y = screen_builder.convertToUnderlyingTitleY(gui_y);
        }

        int line_index = this.font_collection.convertYToLine(y);

        return line_index;
    }

    /**
     * Get the starting Y index adjusted to the current GUI texture
     *
     * @since   0.1.3
     */
    public int getAdjustedY() {

        ScreenBuilder screen_builder = this.getScreenBuilder();

        if (screen_builder == null) {
            return this.y;
        }

        return screen_builder.getContainerY(this.y);
    }

    /**
     * Add the widget to the text builder
     *
     * @since   0.1.1
     */
    @Override
    public void addToTextBuilder(TextBuilder builder) {
        if (this.text == null) {
            return;
        }

        Font font = this.font_collection.getClosestFont(this.getAdjustedY());
        int start_x = this.x;

        int string_width = font.getWidth(this.text);

        if (this.centered) {
            start_x += (this.width - string_width) / 2;
        }

        if (this.color != null) {
            builder.setColor(this.color);
        }

        builder.setCursor(start_x);
        builder.print(this.text, font);
    }

    /**
     * Append to a BibLog.Arg representation
     *
     * @since 0.5.0
     */
    @Override
    public void appendToBibLogArg(@NotNull BibLog.Arg arg) {
        arg.add("text", this.text)
                .add("color", this.color)
                .add("font", this.font_collection)
                .add("width", this.width)
                .add("centered", this.centered)
                .add("x", this.x)
                .add("y", this.y);
    }
}
