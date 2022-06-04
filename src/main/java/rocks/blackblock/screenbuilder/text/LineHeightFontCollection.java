package rocks.blackblock.screenbuilder.text;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;

public class LineHeightFontCollection {

    private static final HashMap<String, LineHeightFontCollection> collection = new HashMap<>();
    public static JsonObject BASE_NEGATIVE = null;
    public static JsonObject BASE_POSITIVE = null;

    private final int original_height;
    private final int line_gap;
    private final int ascent_adjustment;
    private final int height_adjustment;
    private final int top_margin;
    private final Font parent_font;
    private final String name_suffix;
    private final HashMap<Integer, LineHeightFont> line_height_fonts = new HashMap<>();
    private LineHeightFont first_font = null;

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
    public LineHeightFontCollection(int original_height, int line_gap, int ascent_adjustment, int height_adjustment, int top_margin, String name_suffix, Font parent) {

        // The gap between lines in pixels
        this.line_gap = line_gap;
        this.ascent_adjustment = ascent_adjustment;
        this.height_adjustment = height_adjustment;
        this.original_height = original_height;
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
    private void generateFonts() {

        LineHeightFont font;

        for (int i = -20; i < 20; i++) {

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

        int total_height = this.original_height + this.line_gap;

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
    public Font getFontForLine(int line_index) {
        Font result = this.line_height_fonts.getOrDefault(line_index, null);

        if (result == null) {
            result = this.parent_font;
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
     * Get the parent font
     * @version   0.1.1
     */
    public Font getParentFont() {
        return this.parent_font;
    }

    /**
     * Get the original height of the font
     * @version   0.1.1
     */
    public int getOriginalHeight() {
        return this.original_height;
    }

    /**
     * Calculate the width of the given string for this font
     *
     * @param   text    The String to calculate the width for
     * @param   scale   Scale the width according to the font's height
     *
     * @since   0.1.2
     */
    public int getWidth(String text, boolean scale) {
        return this.first_font.getWidth(text, scale);
    }

    /**
     * Calculate the width of the given string for this font
     *
     * @param   text    The String to calculate the width for
     *
     * @since   0.1.2
     */
    public int getWidth(String text) {
        return this.getWidth(text, false);
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
     * Get the font-id for the given line index
     *
     * @since   0.1.1
     */
    public String getFontIdForLine(int line_index) {
        return "bbsb:" + this.getFontFolderId() + "/l"+line_index;
    }

    /**
     * Add all fonts to the given resource pack
     *
     * @param   pack   The (PolyMC) resource pack to add the fonts to
     *
     * @since   0.1.1
     */
    public void addToResourcePack(ModdedResources moddedResources, PolyMcResourcePack pack, SimpleLogger logger) {

        for (LineHeightFont font : this.line_height_fonts.values()) {
            String json = font.getJson();
            String path_str = "font/" + this.getFontFolderId() + "/l" + font.getLineIndex() + ".json";

            pack.setAsset(BBSB.NAMESPACE, path_str, (location, gson) -> {
                GuiUtils.writeToPath(location, json);
            });
        }


        /*
        Path buildLocation = pack.getBuildLocation();

        for (LineHeightFont font : this.line_height_fonts.values()) {
            String json = font.getJson();
            String path_str = "assets/bbsb/font/lh" + this.getLineHeightString() + "/l" + font.getLineIndex() + ".json";

            Path path = buildLocation.resolve(path_str);
            GuiUtils.writeToPath(path, json);
        }*/
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
