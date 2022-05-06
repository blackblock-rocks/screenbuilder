package rocks.blackblock.screenbuilder.text;

import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * The base font class
 *
 * @author   Sentropic
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.1
 * @version  0.1.1
 */
public class Font {

    // The default width of a character
    private static final int DEFAULT_WIDTH = 6;

    // All the registered fonts
    private static final Map<String,Font> registeredFonts = new HashMap<>();

    // Register the default Minecraft font
    public static final Font DEFAULT = new Font("minecraft:default", 8);

    // Register the spacer font
    public static final SpacerFont SPACE = new SpacerFont("bbsb:space", 8);

    // The LH04 font collection (each line is separated by 4 pixels)
    public static final LineHeightFontCollection LH04 = new LineHeightFontCollection(
            8,
            4,
            9,
            0,
            0,
            null,
            DEFAULT
    );

    public static final LineHeightFontCollection LH18 = new LineHeightFontCollection(
            8,
            18,
            18,
            0,
            17,
            "slot",
            DEFAULT
    );

    public static final LineHeightFontCollection LH_INVENTORY_SLOT = LH18;

    // The LH22 font collection (each line is separated by 22 pixels)
    public static final LineHeightFontCollection LH22 = new LineHeightFontCollection(
            8,
            22,
            18,
            0,
            0,
            null,
            DEFAULT
    );

    protected Style font_style;
    protected Identifier identifier;
    protected final String id;
    protected final int height;
    protected Font parent;
    protected Map<Character, Integer> widths = new HashMap<>();

    /**
     * @param id     the namespaced ID of the font, as used by the resource pack (i.e. "minecraft:default")
     * @param height the default height of the characters in the font, as specified in the resource pack
     *
     * @since   0.1.1
     */
    public Font(String id, int height) {
        this.id = id;
        this.height = height;
        this.identifier = new Identifier(id);
        this.font_style = Style.EMPTY.withFont(this.identifier);
    }

    /**
     * Creates a font that inherits its character widths from a parent font
     * Used for fonts that share the same textures with another one
     *
     * @param   id       The identifier of the font, as used by the resource pack ("minecraft:default")
     * @param   height   The default height of the characters in the font
     * @param   parent   The font to inherit character widths from
     *
     * @since   0.1.1
     */
    public Font(String id, int height, Font parent) {
        this(id, height);
        this.parent = parent;
    }

    /**
     * Create a line-height font
     *
     * @param   line_height   The line-height of the font
     * @param   line_index    Which line this font is for
     *
     * @since   0.1.1
     */
    public static Font createLhFont(int line_height, int line_index) {

        String lh = "" + line_height;

        if (line_height < 10) {
            lh = "0" + lh;
        }

        Font result = new Font("bbsb:lh" + lh + "/l"+line_index, 8, DEFAULT);

        register(result);

        return result;
    }

    /**
     * Get the correct font for the given line_index
     *
     * @param   line_index    Which line to get the font for
     *
     * @since   0.1.1
     */
    public static Font getLhFont(int line_index) {
        return getLhFont(line_index, 4);
    }

    /**
     * Get the correct font for the given line_index
     *
     * @param   line_index    Which line to get the font for
     * @param   line_height   The line-height of the font
     *
     * @since   0.1.1
     */
    public static Font getLhFont(int line_index, int line_height) {

        if (line_index == 0) {
            return DEFAULT;
        }

        if (line_height == 4) {
            return LH04.getFontForLine(line_index);
        }

        if (line_height == 22) {
            return LH22.getFontForLine(line_index);
        }

        // @TODO: lookup others?
        return DEFAULT;
    }

    /**
     * Lookup a registered font by its identifier
     *
     * @param   id   The identifier of the wanted font
     *
     * @since   0.1.1
     */
    public static Font getRegistered(String id) {
        return registeredFonts.get(id);
    }

    /**
     * Registers a Font to be accessed statically later, through {@link Font#getRegistered(String)}
     *
     * @param font the Font to be registered
     */
    public static void register(Font font) {
        String id = font.getId();
        registeredFonts.put(id, font);
    }

    /**
     * Registers the width of a character for this font, if different from the default of 6
     *
     * @param character the character to register the width for
     * @param width     the width of the character
     */
    public void registerWidth(char character, int width) {
        widths.put(character, width);
    }

    /**
     * Gets the width of a given character for this font
     *
     * @param   character The character to get the width for
     * @param   scale     Whether to scale the width according to the font's height
     */
    public int getWidth(char character, boolean scale) {

        Integer result = widths.getOrDefault(character, null);

        if (result == null) {
            if (parent != null) {
                result = parent.getWidth(character, false);
            } else {
                result = DEFAULT_WIDTH;
            }
        }

        // Formula by Sentropic
        if (scale && this != DEFAULT && character != ' ') {
            // Formula figured out experimentally (pain)
            result = (int) Math.round(1.1249999d+(result-1)*height/8d);
        }

        return result;
    }

    /**
     * Calculate the width of the given string for this font
     *
     * @param   text    The String to calculate the width for
     * @param   scale   Scale the width according to the font's height
     *
     * @since   0.1.1
     */
    public int getWidth(String text, boolean scale) {
        int result = 0;

        for (char character : text.toCharArray()) {
            result += this.getWidth(character, scale);
        }
        return result;
    }

    /**
     * Calculate the width of the given string for this font
     *
     * @param   text    The String to calculate the width for
     *
     * @since   0.1.1
     */
    public int getWidth(String text) {
        return this.getWidth(text, false);
    }

    /**
     * Return Text with this font
     *
     * @param text  The String to turn into Text
     */
    public BaseText getText(String text) {

        LiteralText result = new LiteralText(text);

        result.setStyle(this.font_style);

        return result;
    }

    /**
     * Add the given text to the text builder
     *
     * @param   builder  The builder to add to
     * @param   text     The String to turn into Text
     */
    public void addTo(TextBuilder builder, String text) {
        this.addTo(builder.getCurrentGroup(), text);
    }

    /**
     * Add the given text to the text group, or a parent
     *
     * @param   group    The group to add to
     * @param   text     The String to turn into Text
     */
    public void addTo(TextGroup group, String text) {
        group.ensureGroup(this.font_style).append(text);
    }

    /**
     * Get the string representation of this font
     *
     * @since   0.1.1
     */
    public String toString() {
        return this.getClass().getName() + "{ " + this.getId() + " }";
    }

    /**
     * Get the identifier of this font as a string
     *
     * @since   0.1.1
     */
    public String getId() {
        return this.id;
    }

    /**
     * Is the given character right-to-left?
     *
     * @param   c   The character to test
     *
     * @since   0.1.1
     */
    public static boolean isRightToLeft(char c) {
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
    public static char getNextChar(char current_char) {

        current_char++;

        // Skip the space & non-breaking space because that's a fixed width
        if (current_char == 32 || current_char == 160 || current_char == 132) {
            return getNextChar(current_char);
        } else if (current_char >= 130 && current_char <= 140) {
            current_char = 140;
            return getNextChar(current_char);
        } else if (current_char >= 155 && current_char <= 160) {
            current_char = 160;
            return getNextChar(current_char);
        } else if (current_char == '\\' || current_char == '§' || current_char == '&') {
            return getNextChar(current_char);
        } else if (current_char == 56) {
            // Skip 8 & 9
            current_char = 58;
        } else if (current_char == 1539) {
            current_char = 1786;
        } else {

            String test;

            do {

                // Do a directionality test first
                // (Right-to-left characters break stuff)
                if (Font.isRightToLeft(current_char)) {
                    current_char++;
                    continue;
                }

                int type = Character.getType(current_char);

                // Unassigned characters can behave weirdly
                if (type == Character.UNASSIGNED) {
                    current_char++;
                    continue;
                }

                // Skip combining characters too
                test = "" + current_char;

                if (test.matches("\\p{M}")) {
                    current_char++;
                } else {
                    break;
                }
            } while (true);
        }

        return current_char;
    }

    static {
        // Register default char widths (width = horizontal pixels + 1)
        DEFAULT.registerWidth(' ', 4);
        DEFAULT.registerWidth('f', 5);
        DEFAULT.registerWidth('i', 2);
        DEFAULT.registerWidth('k', 5);
        DEFAULT.registerWidth('l', 3);
        DEFAULT.registerWidth('t', 4);
        DEFAULT.registerWidth('I', 4);
        DEFAULT.registerWidth('í', 3);
        DEFAULT.registerWidth('Í', 4);
        DEFAULT.registerWidth('´', 3);
        DEFAULT.registerWidth('.', 2);
        DEFAULT.registerWidth(',', 2);
        DEFAULT.registerWidth(';', 2);
        DEFAULT.registerWidth(':', 2);
        DEFAULT.registerWidth('[', 4);
        DEFAULT.registerWidth(']', 4);
        DEFAULT.registerWidth('{', 4);
        DEFAULT.registerWidth('}', 4);
        DEFAULT.registerWidth('*', 4);
        DEFAULT.registerWidth('!', 2);
        DEFAULT.registerWidth('¡', 2);
        DEFAULT.registerWidth('"', 4);
        DEFAULT.registerWidth('(', 4);
        DEFAULT.registerWidth(')', 4);
        DEFAULT.registerWidth('°', 5);
        DEFAULT.registerWidth('|', 2);
        DEFAULT.registerWidth('`', 3);
        DEFAULT.registerWidth('\'', 2);
        DEFAULT.registerWidth('<', 5);
        DEFAULT.registerWidth('>', 5);
        DEFAULT.registerWidth('@', 7);
        DEFAULT.registerWidth('~', 7);

        SPACE.registerWidth('-', -6765);

        // Small negative movements
        SPACE.registerWidth('①', -1);
        SPACE.registerWidth('②', -2);
        SPACE.registerWidth('③', -3);
        SPACE.registerWidth('④', -4);
        SPACE.registerWidth('⑤', -5);
        SPACE.registerWidth('⑥', -6);
        SPACE.registerWidth('⑦', -7);
        SPACE.registerWidth('⑧', -8);
        SPACE.registerWidth('⑨', -9);

        // Full negative movements (decimals)
        SPACE.registerWidth('❶', -10);
        SPACE.registerWidth('❷', -20);
        SPACE.registerWidth('❸', -30);
        SPACE.registerWidth('❹', -40);
        SPACE.registerWidth('❺', -50);

        // Doubling negative movements
        SPACE.registerWidth('⓵', -128);
        SPACE.registerWidth('⓶', -256);

        // Positive movements
        SPACE.registerWidth('1', 2584);
        SPACE.registerWidth('2', 987);
        SPACE.registerWidth('3', 377);
        SPACE.registerWidth('4', 144);
        SPACE.registerWidth('5', 55);
        SPACE.registerWidth('6', 21);
        SPACE.registerWidth('7', 8);
        SPACE.registerWidth('8', 3);
        SPACE.registerWidth('9', 1);

        register(DEFAULT);
        register(SPACE);
    }
}
