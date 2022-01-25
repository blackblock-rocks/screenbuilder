package rocks.blackblock.screenbuilder.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.Formatting;
import rocks.blackblock.screenbuilder.textures.GuiTexturePiece;

public class PixelFont extends Font {

    private final PixelFontCollection collection;
    private final int line_index;

    public PixelFont(PixelFontCollection collection, int line_index) {
        super(collection.getFontIdForLine(line_index), collection.getHeight());
        this.collection = collection;
        this.line_index = line_index;

        // GUI Screen Titles are always colored grey, and any coloring "tints" the bitmap used for the character
        // So we always have to undo the tinting, by setting the color to white
        this.font_style = this.font_style.withColor(Formatting.WHITE);
    }

    /**
     * Get the line index of this font
     */
    public int getLineIndex() {
        return this.line_index;
    }

    /**
     * Get the JSON string for this font
     */
    public JsonObject getJson() {

        JsonObject root = new JsonObject();
        JsonArray providers = new JsonArray();
        root.add("providers", providers);

        // Add the space first (1 pixel to the right)
        JsonObject provider = new JsonObject();
        provider.addProperty("type", "bitmap");
        provider.addProperty("file", "bbsb:font/space_nosplit.png");
        provider.addProperty("ascent", -32768);
        provider.addProperty("height", -1);

        JsonArray chars = new JsonArray();
        provider.add("chars", chars);
        chars.add("9");
        providers.add(provider);

        // Now do the pixels
        int ascent = -this.line_index * 2;

        provider = new JsonObject();

        provider.addProperty("type", "bitmap");
        provider.addProperty("file", collection.getPixelImageJsonFilename());
        provider.addProperty("ascent", ascent);
        provider.addProperty("height", 2);

        chars = new JsonArray();
        provider.add("chars", chars);

        String char_string = "";

        for (char c : collection.color_characters) {
            char_string += c;
        }

        chars.add(char_string);

        providers.add(provider);

        System.out.println("Added pixel font: " + this.line_index);

        return root;
    }

}
