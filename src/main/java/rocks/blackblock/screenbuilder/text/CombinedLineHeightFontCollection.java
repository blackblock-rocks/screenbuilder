package rocks.blackblock.screenbuilder.text;

import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;

/**
 * An old-style LineHeightFontCollection,
 * which is actually made up out of Absolute Fonts
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.3.1
 */
public class CombinedLineHeightFontCollection extends LineHeightFontCollection {

    // The absolute font collection to use
    protected final AbsoluteFontCollection absolute_collection;

    // The gap between the lines
    protected final int line_gap;

    // The top margin
    protected final int top_margin;

    /**
     * Create the collection
     *
     * @param   absolute_collection   The absolute font collection to use
     * @param   line_gap              The gap between the lines
     *
     * @since   0.3.1
     */
    public CombinedLineHeightFontCollection(AbsoluteFontCollection absolute_collection, int line_gap, int top_margin) {
        super(absolute_collection.getCharacterHeight());
        this.absolute_collection = absolute_collection;
        this.line_gap = line_gap;
        this.top_margin = top_margin;

        this.generateFonts();
    }

    /**
     * Generate the actual fonts
     *
     * @since 0.3.1
     */
    @Override
    protected void generateFonts() {

        int min = -20;
        int max = 20;

        for (int i = min; i < max; i++) {
            int y = this.convertLineToY(i);

            LineHeightFont line_font = this.absolute_collection.getLineHeightFontForLine(y);

            if (line_font == null) {
                continue;
            }

            this.line_height_fonts.put(i, line_font);

            if (this.first_font == null) {
                this.first_font = line_font;
            }
        }
    }

    /**
     * Convert a Y coordinate to a line index
     *
     * @since 0.3.1
     */
    @Override
    public int convertYToLine(int y) {

        // Subtract the top margin that might want to shift the text up
        y -= this.top_margin;

        // Get the total height of a line
        int total_height = this.character_height + this.line_gap;

        // Divide the Y coordinate by the total height to get the line index
        int result = y / total_height;

        return result;
    }

    /**
     * Convert a line index to a Y coordinate
     *
     * @since 0.3.1
     */
    public int convertLineToY(int line) {

        // Get the total height of a line
        int total_height = this.character_height + this.line_gap;

        // Multiply the line index by the total height to get the Y coordinate
        int result = line * total_height;

        // Add the top margin that might want to shift the text up
        result += this.top_margin;

        return result;
    }

    /**
     * Add all fonts to the given resource pack.
     * This is not required for this combined collection.
     *
     * @since   0.3.1
     */
    @Override
    public void addToResourcePack(ModdedResources moddedResources, PolyMcResourcePack pack, SimpleLogger logger) {
        // Do nothing
    }

    @Override
    public String getFontFolderId() {
        return null;
    }
}
