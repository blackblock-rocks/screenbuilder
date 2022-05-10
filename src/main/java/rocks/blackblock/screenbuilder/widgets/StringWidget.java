package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import rocks.blackblock.screenbuilder.text.*;

import java.util.ArrayList;
import java.util.List;

public class StringWidget extends Widget {

    private int x = 0;
    private int y = 0;
    private String text = null;
    private boolean centered = false;
    private int width = 0;
    private LineHeightFontCollection font_collection = Font.LH04;
    private TextColor color = TextColor.fromRgb(0x3f3f3f);
    private Integer y_line = null;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setYLine(int line) {
        this.y_line = line;
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
     * Add the widget to the text builder
     *
     * @since   0.1.1
     */
    @Override
    public void addToTextBuilder(TextBuilder builder) {
        if (this.text == null) {
            return;
        }

        int line_index;

        if (this.y_line == null) {
            line_index = this.font_collection.convertYToLine(this.y);
        } else {
            line_index = this.y_line;
        }

        Font font = this.font_collection.getFontForLine(line_index);
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
}
