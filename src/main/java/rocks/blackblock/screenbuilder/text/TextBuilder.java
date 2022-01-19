package rocks.blackblock.screenbuilder.text;

import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;

public class TextBuilder {

    private int line = 0;
    private int cursor = 0;
    private List<Text> text_list = new ArrayList<>();

    public TextBuilder() {
        
    }

    public TextBuilder setY(int y) {
        this.line = y;
        return this;
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
     * Insert some text without updating the cursor
     * @param text
     * @param font
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

        System.out.println("Adding line: " + line);
        System.out.println("  »» Using font " + font + " for line " + this.line + " -- " + font.getId());

        text = font.getText(line);

        //text = text.getWithStyle(text.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("HI " + this.line)))).get(0);

        this.line++;
        
        // Only add text to the list if it has been initialized
        if (text != null) {
            this.text_list.add(text);

            int width = Font.DEFAULT.getWidth(line);

            System.out.println("  »» Line width: " + width);

            if (width != 0) {
                this.text_list.add(Font.SPACE.convertMovement(0, width));
            }
        }
        
        return this;
    }

    /**
     * Move the cursor on the current line the given amount of pixels
     * (This can only be done horizontally)
     * @return
     */
    public TextBuilder moveCursor(int move_x) {
        BaseText move = Font.SPACE.convertMovement(move_x, 0);
        this.text_list.add(move);
        this.cursor += move_x;
        return this;
    }
    
    public BaseText build() {

        LiteralText text = new LiteralText("");

        for (Text t : this.text_list) {
            text.append(t);
        }

        return text;
    }

    public String toString() {

        String result = "TextBuilder {";

        for (Text t : this.text_list) {
            result += t.asString();
        }

        result += "}";

        return result;
    }
}
