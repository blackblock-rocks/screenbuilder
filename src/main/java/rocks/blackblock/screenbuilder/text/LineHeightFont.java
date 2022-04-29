package rocks.blackblock.screenbuilder.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class LineHeightFont extends Font {

    private final LineHeightFontCollection collection;
    private final int line_index;
    private static BufferedImage image = null;

    /**
     * Creates a font that inherits its character widths from a parent font
     * Used for fonts that share the same textures with another one
     *
     * @since   0.1.1
     */
    public LineHeightFont(LineHeightFontCollection collection, int line_index) {
        super(collection.getFontIdForLine(line_index), collection.getOriginalHeight(), collection.getParentFont());
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
            json = LineHeightFontCollection.BASE_NEGATIVE.deepCopy();
        } else {
            json = LineHeightFontCollection.BASE_POSITIVE.deepCopy();
        }

        JsonArray providers = json.getAsJsonArray("providers");

        int height_adjustment = this.line_index * this.collection.getHeightAdjustment();
        int ascent_adjustment = this.line_index * this.collection.getAscentAdjustment() * -1;

        Integer height;
        Integer ascent;

        for (JsonElement element : providers) {
            JsonObject provider = element.getAsJsonObject();

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

            if (ascent_adjustment != 0) {
                ascent += ascent_adjustment;
                provider.addProperty("ascent", ascent);
            }
        }

        return json.toString();
    }

    /**
     * Generate the font
     * @return
     */
    public static BufferedImage getFontImage() {

        if (image != null) {
            return image;
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

        // Get the amount of rows in the font
        int rows = original_image_height / 8;

        // Get the new height of the font
        int target_height = 400 * rows;

        // Create the target image
        BufferedImage target_image = new BufferedImage(source_image.getWidth(), target_height, BufferedImage.TYPE_INT_ARGB);

        // Get the target graphics instance
        Graphics2D target = target_image.createGraphics();

        int sx = 0;
        int dx = target_image.getWidth();

        // Copy over each row
        for (int row = 0; row < rows; row++) {
            int source_sy = row * 8;
            int source_dy = source_sy + 8;
            int target_sy = (400-8) + row * 400;
            int target_dy = target_sy + 8;

            target.drawImage(
                    source_image,
                    sx, target_sy, dx, target_dy,
                    sx, source_sy, dx, source_dy,
                    null
            );

        }

        image = target_image;

        return target_image;
    }
}
