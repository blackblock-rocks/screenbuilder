package rocks.blackblock.screenbuilder.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LineHeightFont extends Font {

    private final LineHeightFontCollection collection;
    private final int line_index;
    private static final Map<Integer, BufferedImage> images = new HashMap();

    /**
     * Creates a font that inherits its character widths from a parent font
     * Used for fonts that share the same textures with another one
     *
     * @since   0.1.1
     */
    public LineHeightFont(LineHeightFontCollection collection, int line_index) {
        super(collection.getFontIdForLine(line_index), collection.getCharacterHeight(), collection.getParentFont());
        this.collection = collection;
        this.line_index = line_index;
    }

    /**
     * Get the line index of this font
     */
    public int getLineIndex() {
        return this.line_index;
    }

    /**
     * Get the json of this font!
     * @return
     */
    public String getJson() {

        JsonObject json;

        if (this.line_index < 0) {
            json = AbsoluteFontCollection.BASE_NEGATIVE.deepCopy();
        } else {
            json = AbsoluteFontCollection.BASE_POSITIVE.deepCopy();
        }

        JsonArray providers = json.getAsJsonArray("providers");

        int height_adjustment = this.line_index * 0;

        Integer height;
        Integer ascent;

        ArrayList<JsonElement> to_remove = new ArrayList<>();

        for (JsonElement element : providers) {
            JsonObject provider = element.getAsJsonObject();

            if (provider.has("file")) {
                String name = provider.get("file").getAsString();

                // Always remove the nonlatin ones for now
                if (name.equals("minecraft:font/nonlatin_european.png")) {
                    to_remove.add(element);
                    continue;
                }

                // And remove the negative accented ones too, they don't work
                if (this.line_index < -10) {
                    if (name.equals("minecraft:font/accented.png")) {
                        to_remove.add(element);
                        continue;
                    }
                }
            }

            if (provider.has("height")) {
                height = provider.get("height").getAsInt();
            } else {
                height = 0;
            }

            if (provider.has("ascent")) {
                ascent = provider.get("ascent").getAsInt();
            } else {
                ascent = 0;
            }

            if (height_adjustment != 0) {
                height += height_adjustment;
                provider.addProperty("height", height);
            }

            int new_ascent = this.collection.getAscentForLine(ascent, this.line_index);

            if (new_ascent > 256) {
                to_remove.add(element);
                continue;
            }

            /*if (ascent_adjustment != 0) {
                ascent += ascent_adjustment;
                provider.addProperty("ascent", ascent);
            }*/
            provider.addProperty("ascent", new_ascent);
        }

        for (JsonElement element : to_remove) {
            providers.remove(element);
        }

        return json.toString();
    }

    /**
     * Generate the font with the adjusted heights
     *
     * @param   piece   Because each font image can only be 4000 pixels high,
     *                  we need to split it up into two pieces
     *
     * @return
     */
    public static BufferedImage getFontImage(int piece) {

        if (images.containsKey(piece)) {
            return images.get(piece);
        }

        // Get the original ascii font image
        InputStream source_font_stream = LineHeightFont.class.getResourceAsStream("/assets/bbsb/textures/font/ascii.png");

        // Get the image data
        byte[] data;
        BufferedImage source_image = null;

        try {
            data = source_font_stream.readAllBytes();
            source_image = ImageIO.read(new ByteArrayInputStream(data));
        } catch (Exception e) {
            System.out.println("Error loading base font image");
            return null;
        }

        // Get the height of the font
        int original_image_height = source_image.getHeight();

        // Get the amount of rows in the original font
        int rows = original_image_height / 8;

        // The first two rows are empty, and can be skipped
        int skip = 2;

        // The target amount of rows
        int target_rows = 7;

        // The new height of a single line
        // (256 is the max height a character can be! Otherwise the font won't load)
        int new_height = 256;

        // Get the new height of the font image
        int target_height = new_height * target_rows;

        // The starting row
        int start_row = skip + (piece * target_rows);

        // Create the target image
        BufferedImage target_image = new BufferedImage(source_image.getWidth(), target_height, BufferedImage.TYPE_INT_ARGB);

        // Get the target graphics instance
        Graphics2D target = target_image.createGraphics();

        int sx = 0;
        int dx = target_image.getWidth();
        int target_row = -1;

        // Copy over each row
        for (int row = start_row; row < rows; row++) {
            target_row++;

            int source_sy = row * 8;
            int source_dy = source_sy + 8;

            int target_sy = target_row * new_height;
            int target_dy = target_sy + 8;

            target.drawImage(
                    source_image,
                    sx, target_sy, dx, target_dy,
                    sx, source_sy, dx, source_dy,
                    null
            );

        }

        images.put(piece, target_image);

        return target_image;
    }
}
