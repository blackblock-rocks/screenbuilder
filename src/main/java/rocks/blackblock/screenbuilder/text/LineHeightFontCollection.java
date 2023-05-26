package rocks.blackblock.screenbuilder.text;

import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import java.util.HashMap;

/**
 * A collection of fonts:
 * each "line" has its own font.
 *
 * @since   0.3.1
 */
public abstract class LineHeightFontCollection extends FontCollection {

    // All the fonts by their line index
    protected final HashMap<Integer, LineHeightFont> line_height_fonts = new HashMap<>();

    // The first generated font
    protected LineHeightFont first_font = null;

    // The parent font
    protected final Font parent_font;

    /**
     * Create the collection
     *
     * @param  character_height    The original height of a character
     * @since  0.3.1
     */
    public LineHeightFontCollection(int character_height) {
        this(character_height, null);
    }

    /**
     * Create the collection
     *
     * @param  character_height    The original height of a character
     * @since  0.3.1
     */
    public LineHeightFontCollection(int character_height, Font parent_font) {
        super(character_height);
        this.parent_font = parent_font;
    }

    /**
     * Get the parent font
     *
     * @since   0.3.1
     */
    public Font getParentFont() {
        return this.parent_font;
    }

    /**
     * Return the font path for the given line index
     *
     * @since   0.3.1
     */
    public String getFontPathForLine(int line_index) {

        String folder_id = this.getFontFolderId();

        if (folder_id == null) {
            folder_id = "unknown";
        }

        return folder_id + "/l" + line_index;
    }

    /**
     * Get the identifier of the Font for the given line index
     * (But as a String, not an Identifier)
     *
     * @since   0.3.1
     */
    public String getFontIdForLine(int line_index) {
        return "bbsb:" + this.getFontPathForLine(line_index);
    }

    /**
     * Get a LineHeightFont for the given line index
     * If it can't be found, return null
     *
     * @param   line_index   The line index to get the font for
     *
     * @since   0.3.1
     */
    @Nullable
    public LineHeightFont getLineHeightFontForLine(int line_index) {
        return this.line_height_fonts.get(line_index);
    }

    /**
     * Convert an old-style "line index" to a Y value
     *
     * @param   line_index   The line index to get the font for
     *
     * @since   0.3.1
     */
    public int lineIndexToY(int line_index) {
        return line_index * 10;
    }

    /**
     * Get the font for the given line index
     *
     * @since   0.3.1
     */
    public Font getFontForLine(int line_index) {

        LineHeightFont font = this.getLineHeightFontForLine(line_index);

        if (font == null) {
            return Font.DEFAULT;
        }

        return font;
    }

    /**
     * Get the closest font for the given Y pixel coordinate
     *
     * @param   y   The Y coordinate to get the font for
     *
     * @since   0.3.1
     */
    public Font getClosestFont(int y) {
        int line_index = this.convertYToLine(y);
        return this.getFontForLine(line_index);
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
     * Convert the given Y pixel coordinate into a line index.
     * Warning: This Y pixel has to be relative to the top of the
     * screen title, not the top of the screen.
     *
     * @param   y   The y position relative to the title
     *
     * @since   0.3.1
     */
    abstract public int convertYToLine(int y);

    /**
     * Get the font folder id:
     * this is the name of the folder this font collection is stored in.
     *
     * @since   0.3.1
     */
    abstract public String getFontFolderId();

    /**
     * Get the ascent adjustment (per line)
     * @since   0.1.1
     */
    @Deprecated
    public int getAscentAdjustment() {
        return 1;
    }

    /**
     * Get the ascent for the given line
     * @since   0.1.3
     */
    public int getAscentForLine(int base_ascent, int line_index) {

        int ascent_adjustment = line_index * this.getAscentAdjustment() * -1;

        // - top margin?
        ascent_adjustment -= 0;

        return base_ascent + ascent_adjustment;
    }

    /**
     * Add all fonts to the given resource pack
     *
     * @param   pack   The (PolyMC) resource pack to add the fonts to
     *
     * @since   0.1.1
     */
    @Override
    public void addToResourcePack(ModdedResources moddedResources, PolyMcResourcePack pack, SimpleLogger logger) {

        for (LineHeightFont font : this.line_height_fonts.values()) {
            String json = font.getJson();
            String path_str = "font/" + this.getFontPathForLine(font.getLineIndex()) + ".json";

            pack.setAsset(BBSB.NAMESPACE, path_str, (location, gson) -> {
                GuiUtils.writeToPath(location, json);
            });
        }
    }

    /**
     * Get the string representation of this collection
     *
     * @since   0.3.1
     */
    public String toString() {

        String result = this.getClass().getSimpleName() + "{";

        if (this.parent_font != null) {
            result += "parent_font=\"" + this.parent_font.id + "\",";
            result += "original_height=" + this.parent_font.height + ",";
        }

        result += "font_id=\"" + this.getFontFolderId() + "\"";
        result += "}";

        return result;
    }
}