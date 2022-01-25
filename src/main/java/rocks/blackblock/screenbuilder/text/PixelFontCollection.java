package rocks.blackblock.screenbuilder.text;

import io.github.theepicblock.polymc.api.resource.ResourcePackMaker;
import net.minecraft.block.MapColor;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class PixelFontCollection {

    // All the generated line fonts (each line has to get its own font)
    private final HashMap<Integer, PixelFont> line_fonts = new HashMap<>();

    // All the available colors
    private static ArrayList<Color> COLORS = createColors();
    private static IndexColorModel COLOR_MODEL;

    // The top & bottom pixels
    private BufferedImage positive_pixels = null;
    private BufferedImage negative_pixels = null;

    // The width & height of a character in this font
    private int width;
    private int height;

    // The map of colors: Top pixel color, bottom pixel color »» character
    protected HashMap<Integer, HashMap<Integer, Character>> color_map = new HashMap<>();

    // The list of characters
    protected ArrayList<Character> color_characters = new ArrayList<>();

    // Create the actual collection
    public static PixelFontCollection PX01 = new PixelFontCollection(1, 2);

    // The current char while generating (we don't use 32)
    private char current_char;

    /**
     * Create a PixelFontCollection
     *
     * @param   width
     * @param   height
     */
    public PixelFontCollection(int width, int height) {
        this.width = width;
        this.height = height;

        this.generateFonts();
    }

    /**
     * Get the closest color to the given color
     *
     * @since   0.1.1
     */
    public static Color getNearestColor(Color color) {
        final byte index = ((byte[])COLOR_MODEL.getDataElements(color.getRGB(), null))[0];
        Color result = COLORS.get(index);
        return result;
    }

    /**
     * Create all the colors once, because the Graphics class needs it
     *
     * @since   0.1.1
     */
    private static ArrayList<Color> createColors() {
        return createMapColors();
    }

    /**
     * Create colors equally separated from each other
     *
     * @since   0.1.1
     */
    private static ArrayList<Color> createHslColors() {

        int color_count = 64;

        ArrayList<Color> colors = new ArrayList<>();

        // The first colour is transparent
        colors.add(new Color(0, 0, 0, 1));
        colors.add(new Color(0, 0, 0, 255));
        colors.add(new Color(255, 255, 255, 255));

        for (int i = 3; i < (color_count / 2); i++) {
            float hue = (float) i / (float) (color_count / 2);
            colors.add(hslColor(hue, 1.0f, 0.5f));
            colors.add(hslColor(hue, 0.5f, 0.3f));
        }

        final int[] cmap = new int[colors.size()];

        for (int i = 0; i < colors.size(); i++) {
            cmap[i] = colors.get(i).getRGB();
        }

        final int bits = (int) Math.ceil(Math.log(cmap.length) / Math.log(2));
        COLOR_MODEL = new IndexColorModel(bits, cmap.length, cmap, 0, false, -1, DataBuffer.TYPE_BYTE);

        return colors;

    }

    /**
     * Create colors based on the existing map colors
     *
     * @since   0.1.1
     */
    private static ArrayList<Color> createMapColors() {

        // The amount of available colors
        int color_count = 62;

        ArrayList<Color> colors = new ArrayList<>();

        // The first colour is transparent
        colors.add(new Color(0, 0, 0, 1));

        for (int i = 1; i < color_count; i++) {
            MapColor map_color = MapColor.get(i);
            colors.add(new Color(map_color.color));
        }

        // Create the color model
        final int[] cmap = new int[colors.size()];

        for (int i = 0; i < colors.size(); i++) {
            cmap[i] = colors.get(i).getRGB();
        }

        final int bits = (int) Math.ceil(Math.log(cmap.length) / Math.log(2));
        COLOR_MODEL = new IndexColorModel(bits, cmap.length, cmap, 0, false, -1, DataBuffer.TYPE_BYTE);

        return colors;
    }

    /**
     * Create a color based on HSL values
     * @param   hue
     * @param   saturation
     * @param   lightness
     *
     * @since   0.1.1
     */
    static public Color hslColor(float hue, float saturation, float lightness) {
        float q, p, r, g, b;

        if (saturation == 0) {
            r = g = b = lightness; // achromatic
        } else {
            q = lightness < 0.5 ? (lightness * (1 + saturation)) : (lightness + saturation - lightness * saturation);
            p = 2 * lightness - q;
            r = hue2rgb(p, q, hue + 1.0f / 3);
            g = hue2rgb(p, q, hue);
            b = hue2rgb(p, q, hue - 1.0f / 3);
        }
        return new Color(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255));
    }

    /**
     * Convert hue values to RGB
     *
     * @param   p
     * @param   q
     * @param   h
     *
     * @since   0.1.1
     */
    private static float hue2rgb(float p, float q, float h) {
        if (h < 0) {
            h += 1;
        }

        if (h > 1) {
            h -= 1;
        }

        if (6 * h < 1) {
            return p + ((q - p) * 6 * h);
        }

        if (2 * h < 1) {
            return q;
        }

        if (3 * h < 2) {
            return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
        }

        return p;
    }

    /**
     * Is the given character right-to-left?
     *
     * @param   c   The character to test
     *
     * @since   0.1.1
     */
    private boolean isRightToLeft(char c) {
        byte directionality = Character.getDirectionality(c);

        if (directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT
                || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
                || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING
                || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE
        ) {
            return true;
        }

        return false;
    }

    /**
     * Get the next (safe) character to use in the custom font
     *
     * @since   0.1.1
     */
    private char getNextChar() {

        this.current_char++;

        // Skip the space, because that's a fixed width
        if (this.current_char == 32) {
            this.current_char++;
        } else if (this.current_char == 56) {
            // Skip 8 & 9
            this.current_char = 58;
        } else {

            String test;

            do {

                // Do a directionality test first
                // (Right-to-left characters break stuff)
                if (this.isRightToLeft(this.current_char)) {
                    this.current_char++;
                    continue;
                }

                // Skip combining characters too
                test = "" + this.current_char;

                if (test.matches("\\p{M}")) {
                    this.current_char++;
                } else {
                    break;
                }
            } while (true);
        }

        return this.current_char;
    }

    /**
     * Generate the actual fonts
     *
     * @since   0.1.1
     */
    private void generateFonts() {

        // Generate the colors & pixels first
        int color_count = COLORS.size();
        int chars = color_count * color_count;

        // Reset the current char (we won't actually use 34)
        this.current_char = 34;

        this.positive_pixels = new BufferedImage(chars, 2, BufferedImage.TYPE_INT_ARGB);
        this.negative_pixels = new BufferedImage(chars, 120, BufferedImage.TYPE_INT_ARGB);

        Graphics positive = this.positive_pixels.getGraphics();
        Graphics negative = this.negative_pixels.getGraphics();

        for (int top_index = 0; top_index < color_count; top_index++) {
            Color top_color = COLORS.get(top_index);
            int start_x = top_index * color_count;

            HashMap<Integer, Character> map = new HashMap<>();
            color_map.put(top_color.getRGB(), map);

            for (int bottom_index = 0; bottom_index < color_count; bottom_index++) {
                Color bottom_color = COLORS.get(bottom_index);

                int x = start_x + bottom_index;

                positive.setColor(top_color);
                positive.drawRect(x, 0, 1, 1);

                positive.setColor(bottom_color);
                positive.drawRect(x, 1, 1, 1);

                char pair_char = this.getNextChar();
                map.put(bottom_color.getRGB(), pair_char);
                color_characters.add(pair_char);
            }
        }

        PixelFont font;

        for (int i = 0; i < 120; i++) {
            font = new PixelFont(this, i);
            line_fonts.put(i, font);
            Font.register(font);
        }
    }

    /**
     * Get the font for the given line index
     *
     * @param   line_index   The line index to get the font for
     *
     * @since   0.1.1
     */
    public Font getFontForLine(int line_index) {
        Font result = this.line_fonts.getOrDefault(line_index, null);

        return result;
    }

    /**
     * Get the filename of the pixel image for the json
     *
     * @since   0.1.1
     */
    public String getPixelImageJsonFilename() {
        return "bbsb:font/pxtop.png";
    }

    /**
     * Get the height of the font
     *
     * @since   0.1.1
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Get the font-id for the given line index
     *
     * @since   0.1.1
     */
    public String getFontIdForLine(int line_index) {
        return "bbsb:px" + this.width + "x" + this.height + "/l"+line_index;
    }

    /**
     * Get the character for the given colors
     *
     * @since   0.1.1
     */
    public Character getCharacter(Color top, Color bottom) {

        Character result = null;

        HashMap<Integer, Character> map = this.color_map.get(top.getRGB());

        if (map != null) {
            result = map.get(bottom.getRGB());
        }

        return result;
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

        for (PixelFont font : this.line_fonts.values()) {
            String json = font.getJson().toString();
            String path_str = "assets/bbsb/font/px" + this.width + "x" + this.height + "/l" + font.getLineIndex() + ".json";

            Path path = buildLocation.resolve(path_str);
            GuiUtils.writeToPath(path, json);
        }

        Path pixel_path = buildLocation.resolve("assets/bbsb/textures/font/pxtop.png");

        GuiUtils.writeToPath(pixel_path, this.positive_pixels);

    }

}
