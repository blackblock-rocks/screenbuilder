package rocks.blackblock.screenbuilder.text;

import net.minecraft.text.BaseText;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class SpacerFont extends Font {

    // A map of the widths
    private final Map<Integer, Character> width_map = new HashMap<>();

    // A map of all the negative space
    private final Map<Integer, Character> negative_space_map = new TreeMap<>();

    // A map of all the positive space
    private final Map<Integer, Character> positive_space_map = new TreeMap<>();

    // A sorted map of all the positive space
    private Map<Integer, Character> sorted_positive_space_map = null;

    // A sorted map of all the negaitve space
    private Map<Integer, Character> sorted_negative_space_map = null;

    public SpacerFont(@NotNull String id, int height) {
        super(id, height);
    }

    /**
     * Registers the width of a character for this font, if different from the default of 6
     *
     * @param character the character to register the width for
     * @param width     the width of the character
     */
    public void registerWidth(char character, int width) {
        super.registerWidth(character, width);
        width_map.put(width, character);

        if (width < 0) {
            negative_space_map.put(width, character);
        } else {
            positive_space_map.put(width, character);
            sorted_positive_space_map = null;
        }
    }

    /**
     * Get the sorted positive space map (highest width goes first)
     * @version   0.1.1
     */
    public Map<Integer, Character> getSortedPositiveSpaceMap() {

        if (this.sorted_positive_space_map == null) {
            this.sorted_positive_space_map = this.positive_space_map.entrySet().stream()
                    .sorted(Comparator.comparing(e -> -e.getKey()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> {throw new AssertionError();},
                            LinkedHashMap::new)
                    );
        }

        return this.sorted_positive_space_map;
    }

    /**
     * Get the sorted negative space map (shorted absolute width goes first)
     * @version   0.1.1
     */
    public Map<Integer, Character> getSortedNegativeSpaceMap() {

        if (this.sorted_negative_space_map == null) {
            this.sorted_negative_space_map = this.negative_space_map.entrySet().stream()
                    .sorted(Comparator.comparing(e -> -e.getKey()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> {throw new AssertionError();},
                            LinkedHashMap::new)
                    );
        }

        return this.sorted_negative_space_map;
    }

    /**
     * Get the char to use for the given width
     *
     * @param width     the wanted width of the character
     */
    public char lookupWidth(int width) {
        return width_map.get(width);
    }

    /**
     * Turn the wanted movement into text
     *
     * @param wanted_position     the wanted movement in pixels
     */
    public String getMovementString(int wanted_position) {
        return this.getMovementString(wanted_position, 0);
    }

    /**
     * Add the given movement to the builder
     *
     * @param builder             the builder to add the movement to
     * @param wanted_position     the wanted movement in pixels
     * @param current_position    the starting position
     */
    public void addMovementToBuilder(TextBuilder builder, int wanted_position, int current_position) {
        String movement = this.getMovementString(wanted_position, current_position);
        this.addTo(builder, movement);
    }

    /**
     * Turn the wanted movement into text
     *
     * @param wanted_position     the wanted movement in pixels
     * @param current_position    the starting position
     */
    public String getMovementString(int wanted_position, int current_position) {

        // The temp calculation
        int temp_calc;

        // The current char
        char current_char;

        StringBuilder builder = new StringBuilder();

        int wanted_change = wanted_position - current_position;

        // If we have a negative moment, add negative spaces first
        if (wanted_position < current_position) {

            // See if we can do it with just one char
            for (int width : negative_space_map.keySet()) {
                if (wanted_change == width) {
                    current_position = current_position + width;
                    current_char = negative_space_map.get(width);
                    builder.append(current_char);
                    break;
                }
            }

            // Add the smallest negative change first if it immediately takes us back far enough
            if (current_position > wanted_position) {
                for (int width : this.getSortedNegativeSpaceMap().keySet()) {
                    temp_calc = current_position + width;

                    if (temp_calc <= wanted_position) {
                        current_position = current_position + width;
                        current_char = negative_space_map.get(width);
                        builder.append(current_char);
                        break;
                    }
                }
            }

            // Keep on adding the highest amount of negative space as long as needed
            if (current_position > wanted_position) {

                // The bullshit you have to do to just reverse something
                List<Integer> list = new ArrayList(this.getSortedNegativeSpaceMap().keySet());
                Collections.sort(list, Collections.reverseOrder());
                Set<Integer> keys = new LinkedHashSet(list);

                for (int width : keys) {
                    while (current_position > wanted_position) {
                        temp_calc = current_position + width;

                        if (temp_calc <= wanted_position) {
                            current_position = current_position + width;
                            current_char = negative_space_map.get(width);
                            builder.append(current_char);
                        } else {
                            // Can't add it, so we have to break out
                            break;
                        }
                    }
                }
            }
        }

        if (wanted_position > current_position) {
            for (int width : this.getSortedPositiveSpaceMap().keySet()) {

                while (current_position < wanted_position) {
                    temp_calc = current_position + width;

                    // If adding this width gets us closer to the wanted movement, add it
                    if (temp_calc <= wanted_position) {
                        current_position = temp_calc;
                        current_char = positive_space_map.get(width);
                        builder.append(current_char);
                    } else {
                        // We can't add it, so we have to break out
                        break;
                    }
                }

                if (current_position >= wanted_position) {
                    break;
                }
            }
        }

        return builder.toString();
    }

}
