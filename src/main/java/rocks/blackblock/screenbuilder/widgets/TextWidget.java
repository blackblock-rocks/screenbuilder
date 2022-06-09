package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.text.TextColor;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.text.MiniText;
import rocks.blackblock.screenbuilder.text.TextBuilder;

import java.util.ArrayList;
import java.util.List;

public class TextWidget extends StringWidget {

    private List<MiniText> text = new ArrayList<>();

    /**
     * Print text and a new line
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public void printLine(String line) {
        this.printText(new MiniText(line + "\n"));
    }

    /**
     * Add a line of text
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public void printLine(MiniText text) {
        this.printText(text);
        this.printText(new MiniText("\n"));
    }

    /**
     * Print text
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public void printText(MiniText text) {
        // Add the text
        this.text.add(text);
    }

    @Override
    public void setText(String text) {
        this.printText(new MiniText(text));
    }

    /**
     * Add the widget to the text builder
     *
     * @since   0.1.1
     */
    @Override
    public void addToTextBuilder(TextBuilder builder) {

        int line_index;

        if (this.y_line == null) {
            line_index = this.getLineIndex(this.y);
        } else {
            line_index = this.y_line;
        }

        int space_width = this.font_collection.getWidth(" ");

        // Add each line of text
        for (MiniText line : this.text) {
            String raw_string = line.getRawString();

            if (raw_string == null) {
                raw_string = "";
            }

            // Split on forced newlines
            String[] lines = raw_string.split("\r?\n|\r");

            // Prepare actual lines to print
            List<String> actual_lines = new ArrayList<>();

            // Split the text into multiple, actual lines
            for (String line_string : lines) {
                // Get the width of this line
                int line_width = this.font_collection.getWidth(line_string);

                // If the line fits, just add it as is
                if (line_width <= this.width) {
                    actual_lines.add(line_string);
                    continue;
                }

                // It doesn't fit, so plit it into each word first
                String[] words = line_string.split(" ");

                int current_width = 0;
                String current_line = "";

                // Add each word to the line
                for (String word : words) {
                    // Get the width of this word (including space)
                    int word_width = this.font_collection.getWidth(word);

                    if (current_width > 0) {
                        word_width += space_width;
                    }

                    // If the word fits, just add it as is
                    if (current_width + word_width <= this.width) {
                        current_width += word_width;

                        // It's safe to always add the space at the end
                        current_line += word + " ";
                    } else {
                        actual_lines.add(current_line);
                        current_width = word_width;
                        current_line = word + " ";
                    }
                }

                if (current_line.length() > 0) {
                    actual_lines.add(current_line);
                }
            }


            // Now iterate over the actual lines
            for (String actual_line : actual_lines) {

                Font font = this.font_collection.getFontForLine(line_index);
                int start_x = this.x;
                int string_width = font.getWidth(actual_line);

                if (this.centered) {
                    start_x += (this.width - string_width) / 2;
                }

                builder.setCursor(start_x);
                builder.setColor(TextColor.fromRgb(0x3f3f3f));
                builder.print(actual_line, font);

                line_index++;
            }

        }

    }



}
