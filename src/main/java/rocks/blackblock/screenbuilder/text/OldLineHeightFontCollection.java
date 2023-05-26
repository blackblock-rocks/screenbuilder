package rocks.blackblock.screenbuilder.text;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import org.jetbrains.annotations.NotNull;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;

public class OldLineHeightFontCollection extends LineHeightFontCollection {

    private static final HashMap<String, LineHeightFontCollection> collection = new HashMap<>();
    public static JsonObject BASE_NEGATIVE = null;
    public static JsonObject BASE_POSITIVE = null;

    protected final int line_gap;
    protected final int ascent_adjustment;
    protected final int height_adjustment;
    protected final int top_margin;
    protected final Font parent_font;
    protected final String name_suffix;

    /**
     * Create the collection
     *
     * @param   original_height     The original height of the font
     * @param   line_gap            The wanted gap between lines (used for ID)
     * @param   ascent_adjustment   How much to adjust the ascent for each line
     * @param   height_adjustment   How much to adjust the height for each line
     * @param   parent              The parent font
     *
     * @since   0.1.1
     */
    public OldLineHeightFontCollection(int original_height, int line_gap, int ascent_adjustment, int height_adjustment, int top_margin, String name_suffix, Font parent) {
        super(original_height);

        // The gap between lines in pixels
        this.line_gap = line_gap;
        this.ascent_adjustment = ascent_adjustment;
        this.height_adjustment = height_adjustment;
        this.top_margin = top_margin;
        this.parent_font = parent;
        this.name_suffix = name_suffix;

        String id = line_gap + "-" + ascent_adjustment + "-" + height_adjustment + "-" + top_margin + "-" + name_suffix;

        collection.put(id, this);

        this.loadBaseFiles();
        this.generateFonts();
    }

    /**
     * Get all the font collections
     *
     * @since   0.1.3
     */
    public static Collection<LineHeightFontCollection> getAllFontCollections() {
        return collection.values();
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
     * @since   0.1.1
     */
    @Override
    protected void generateFonts() {

        LineHeightFont font;

        int min = -20;
        int max = 20;

        for (int i = min; i < max; i++) {

            font = new LineHeightFont(this, i);
            line_height_fonts.put(i, font);
            Font.register(font);

            if (this.first_font == null) {
                this.first_font = font;
            }
        }
    }

    /**
     * Get the height adjustment
     * @since   0.1.1
     */
    public int getHeightAdjustment() {
        return height_adjustment;
    }

    /**
     * Get the ascent adjustment
     * @since   0.1.1
     */
    public int getAscentAdjustment() {
        return this.ascent_adjustment;
    }

    /**
     * Get the ascent for the given line
     * @since   0.1.3
     */
    @Override
    public int getAscentForLine(int base_ascent, int line_index) {

        int ascent_adjustment = line_index * this.getAscentAdjustment() * -1;

        ascent_adjustment -= this.top_margin;

        return base_ascent + ascent_adjustment;
    }

    /**
     * Convert the given Y pixel coordinate into a line index.
     * Warning: This Y pixel has to be relative to the top of the
     * screen title, not the top of the screen.
     *
     * @param   y   The y position relative to the title
     *
     * @since   0.1.1
     */
    public int convertYToLine(int y) {

        y -= this.top_margin;

        int total_height = this.character_height + this.line_gap;

        int result = y / total_height;

        return result;
    }

    /**
     * Get the font for the given line index
     *
     * @param   line_index   The line index to get the font for
     *
     * @since   0.1.1
     */
    @Override
    @NotNull
    public Font getFontForLine(int line_index) {
        Font result = this.line_height_fonts.getOrDefault(line_index, null);

        if (result == null) {
            result = this.parent_font;

            if (result == null) {
                result = Font.DEFAULT;
            }
        }

        return result;
    }

    /**
     * Get the line-height as string
     * @since   0.1.1
     */
    public String getLineHeightString() {

        String result = "" + this.line_gap;

        if (this.line_gap < 10) {
            result = "0" + result;
        }

        return result;
    }

    /**
     * Get the font folder id
     *
     * @since   0.1.3
     */
    public String getFontFolderId() {
        String result = "lh" + this.getLineHeightString();

        if (this.name_suffix != null) {
            result += "_" + this.name_suffix;
        }

        return result;
    }

    /**
     * Get the string representation of this collection
     *
     * @since   0.1.3
     */
    public String toString() {
        return this.getClass().getSimpleName()
                + "{font=\"" + this.parent_font.id
                + "\", line_gap=" + this.line_gap
                + ", original_height=" + this.parent_font.height
                + ", height_adjustment=" + this.height_adjustment
                + ", ascent_adjustment=" + this.ascent_adjustment
                + ", name_suffix=\"" + this.name_suffix + "\""
                + "}";
    }

}
