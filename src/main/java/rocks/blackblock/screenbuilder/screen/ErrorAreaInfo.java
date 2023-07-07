package rocks.blackblock.screenbuilder.screen;

import rocks.blackblock.screenbuilder.text.Font;

/**
 * Info on where errors can be printed
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.4.0
 */
public class ErrorAreaInfo {

    // Set the position where errors should be drawn
    private Integer start_x = null;
    private Integer start_y = null;
    private Integer width = null;
    private Integer height = null;
    private boolean start_from_bottom = true;
    private boolean centered = true;

    /**
     * Set the error area info
     *
     * @since   0.4.0
     */
    public ErrorAreaInfo setAll(Integer x_start, Integer y_start, Integer width, Integer height, boolean start_from_bottom, boolean centered) {
        this.start_x = x_start;
        this.start_y = y_start;
        this.width = width;
        this.height = height;
        this.start_from_bottom = start_from_bottom;
        this.centered = centered;
        return this;
    }

    /**
     * Get the x start position of the error area
     *
     * @since   0.4.0
     */
    public Integer getStartX() {
        return this.start_x;
    }

    /**
     * Set the x start position of the error area
     *
     * @since   0.4.0
     */
    public ErrorAreaInfo setStartX(Integer x_start) {
        this.start_x = x_start;
        return this;
    }

    /**
     * Get the y start position of the error area
     *
     * @since   0.4.0
     */
    public Integer getStartY() {
        return this.start_y;
    }

    /**
     * Set the y start position of the error area
     *
     * @since   0.4.0
     */
    public ErrorAreaInfo setStartY(Integer y_start) {
        this.start_y = y_start;
        return this;
    }

    /**
     * Get the width of the error area
     *
     * @since   0.4.0
     */
    public Integer getWidth() {
        return this.width;
    }

    /**
     * Set the width of the error area
     *
     * @since   0.4.0
     */
    public ErrorAreaInfo setWidth(Integer width) {
        this.width = width;
        return this;
    }

    /**
     * Get the height of the error area
     *
     * @since   0.4.0
     */
    public Integer getHeight() {
        return this.height;
    }

    /**
     * Set the height of the error area
     *
     * @since   0.4.0
     */
    public ErrorAreaInfo setHeight(Integer height) {
        this.height = height;
        return this;
    }

    /**
     * Get the Y coordinate for the given error line index
     *
     * @since   0.4.0
     */
    public int getYForLine(int line_index) {
        if (this.start_from_bottom) {
            return this.start_y - (line_index * 8);
        } else {
            return this.start_y + (line_index * 8);
        }
    }

    /**
     * Get the X coordinate for the given error line index & message
     *
     * @since   0.4.0
     */
    public int getXForLine(int line_index, String message) {

        if (!this.centered) {
            return this.start_x;
        }

        // This Y coordinate is probbaly not correct (because of the gui changes),
        // but we only need this font for the width calculation
        Font font = Font.ABSOLUTE_DEFAULT_COLLECTION.getClosestFont(this.getYForLine(line_index));

        int message_width = font.getWidth(message);

        return this.start_x + ((this.width - message_width) / 2);
    }

    /**
     * Get whether the error area should start from the bottom
     *
     * @since   0.4.0
     */
    public boolean getShouldStartFromBottom() {
        return this.start_from_bottom;
    }

    /**
     * Set whether the error area should start from the bottom
     *
     * @since   0.4.0
     */
    public ErrorAreaInfo setShouldStartFromBottom(boolean start_from_bottom) {
        this.start_from_bottom = start_from_bottom;
        return this;
    }

    /**
     * Get whether the error area text content should be centered
     *
     * @since   0.4.0
     */
    public boolean getShouldBeCentered() {
        return this.centered;
    }

}
