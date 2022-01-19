package rocks.blackblock.screenbuilder.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LineHeightFont extends Font {

    private final LineHeightFontCollection collection;
    private final int line_index;

    /**
     * Creates a font that inherits its character widths from a parent font
     * Used for fonts that share the same textures with another one
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
}
