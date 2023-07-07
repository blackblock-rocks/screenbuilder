package rocks.blackblock.screenbuilder.text;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A collection of fonts to be used for absolute positioning.
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.3.1
 */
public class AbsoluteFontCollection extends LineHeightFontCollection {

    public static JsonObject BASE_NEGATIVE = null;
    public static JsonObject BASE_POSITIVE = null;

    /**
     * Create the collection
     *
     * @since 0.3.1
     */
    public AbsoluteFontCollection() {
        super(8, Font.DEFAULT);

        this.loadBaseFiles();
        this.generateFonts();
    }

    /**
     * Load the font files that serve as the basis for each line
     * (These have been created manually)
     *
     * @since   0.1.1
     */
    private void loadBaseFiles() {

        if (BASE_NEGATIVE != null) {
            return;
        }

        InputStream negative_stream = this.getClass().getResourceAsStream("/assets/bbsb/font/lh_negative.json");

        if (negative_stream == null) {
            return;
        }

        JsonReader reader = new JsonReader(new InputStreamReader(negative_stream));
        JsonObject object = (JsonObject) JsonParser.parseReader(reader);
        BASE_NEGATIVE = object;

        InputStream positive_stream = this.getClass().getResourceAsStream("/assets/bbsb/font/lh_positive.json");
        reader = new JsonReader(new InputStreamReader(positive_stream));
        object = (JsonObject) JsonParser.parseReader(reader);
        BASE_POSITIVE = object;
    }

    /**
     * Generate the actual fonts
     * @since   0.3.1
     */
    @Override
    protected void generateFonts() {

        LineHeightFont font;

        int min = -110;
        int max = 200;

        for (int i = min; i < max; i++) {

            int ascent = i * - 1;

            font = new LineHeightFont(this, i);

            this.line_height_fonts.put(i, font);
            Font.register(font);

            if (this.first_font == null) {
                this.first_font = font;
            }
        }
    }

    /**
     * Get the font for the given line index
     *
     * @param   line_index   The line index to get the font for
     *
     * @since   0.3.1
     */
    @Override
    @NotNull
    public Font getFontForLine(int line_index) {
        Font result = this.line_height_fonts.get(line_index);

        if (result == null) {
            result = Font.DEFAULT;
        }

        return result;
    }

    /**
     * Get the ascent adjustment
     * @since   0.1.1
     */
    @Override
    @Deprecated
    public int getAscentAdjustment() {
        return 1;
    }

    /**
     * Get the ascent for the given line
     * @since   0.4.1
     */
    @Override
    public int getAscentForLine(int base_ascent, int line_index) {

        int ascent_adjustment = line_index * this.getAscentAdjustment() * -1;

        // - top margin?
        //ascent_adjustment += 14;

        return base_ascent + ascent_adjustment;
    }

    /**
     * Convert an old-style "line index" to a Y value
     *
     * @param   line_index   The line index to get the font for
     *
     * @since   0.4.1
     */
    @Override
    public int lineIndexToY(int line_index) {
        return line_index + 6;
    }

    /**
     * Convert the given Y pixel coordinate into a line index.
     * For this collection, the line index matches the Y pixel value.
     *
     * @param   y   The y position relative to the title
     *
     * @since   0.3.1
     */
    @Override
    public int convertYToLine(int y) {
        return y - 6;
    }

    /**
     * Get the font folder id
     *
     * @since   0.3.1
     */
    @Override
    public String getFontFolderId() {
        return "a";
    }
}
