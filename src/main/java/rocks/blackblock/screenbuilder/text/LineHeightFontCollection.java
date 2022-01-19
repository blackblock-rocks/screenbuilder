package rocks.blackblock.screenbuilder.text;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import io.github.theepicblock.polymc.api.resource.ResourcePackMaker;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;

public class LineHeightFontCollection {

    public static final HashMap<Integer, LineHeightFontCollection> collection = new HashMap<>();
    public static JsonObject BASE_NEGATIVE = null;
    public static JsonObject BASE_POSITIVE = null;

    private final int original_height;
    private final int line_gap;
    private final int ascent_adjustment;
    private final int height_adjustment;
    private final Font parent_font;
    private final HashMap<Integer, LineHeightFont> line_height_fonts = new HashMap<>();

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
    public LineHeightFontCollection(int original_height, int line_gap, int ascent_adjustment, int height_adjustment, Font parent) {

        // The gap between lines in pixels
        this.line_gap = line_gap;
        this.ascent_adjustment = ascent_adjustment;
        this.height_adjustment = height_adjustment;
        this.original_height = original_height;
        this.parent_font = parent;

        collection.put(line_gap, this);

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
     * @since   0.1.1
     */
    private void generateFonts() {

        LineHeightFont font;

        for (int i = -20; i < 20; i++) {

            if (i == 0) {
                continue;
            }

            font = new LineHeightFont(this, i);
            line_height_fonts.put(i, font);
            Font.register(font);
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
        return ascent_adjustment;
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
     * Get the font-id for the given line index
     * @version   0.1.1
     */
    public String getFontIdForLine(int line_index) {
        return "bbsb:lh" + this.getLineHeightString() + "/l"+line_index;
    }

    /**
     * Add all fonts to the given resource pack
     *
     * @param   pack   The (PolyMC) resource pack to add the fonts to
     *
     * @since   0.1.1
     */
    public void addToResourcePack(ResourcePackMaker pack) {

        Path buildLocation = pack.getBuildLocation();

        for (LineHeightFont font : this.line_height_fonts.values()) {
            String json = font.getJson();
            String path_str = "assets/bbsb/font/lh" + this.getLineHeightString() + "/l" + font.getLineIndex() + ".json";

            Path path = buildLocation.resolve(path_str);
            GuiUtils.writeToPath(path, json);
        }
    }



}
