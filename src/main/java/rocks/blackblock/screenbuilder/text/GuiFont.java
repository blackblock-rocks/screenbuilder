package rocks.blackblock.screenbuilder.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.theepicblock.polymc.api.resource.ResourcePackMaker;
import net.minecraft.util.Formatting;
import rocks.blackblock.screenbuilder.textures.TexturePiece;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The GUI font class, used to register pieces of GUI textures in a font
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.1
 * @version  0.1.1
 */
public class GuiFont extends Font {

    private ArrayList<TexturePiece> texture_pieces = new ArrayList<>();
    private int index = 0;
    private char current_char = (char) 33;

    public GuiFont(String name) {
        super(name, 0);

        // GUI Screen Titles are always colored grey, and any coloring "tints" the bitmap used for the character
        // So we always have to undo the tinting, by setting the color to white
        this.font_style = this.font_style.withColor(Formatting.WHITE);
    }

    /**
     * Get the next free character to use
     * (All the initial control codes are skipped)
     * @since   0.1.1
     */
    public char getNextChar() {
        this.index++;
        this.current_char = Font.getNextChar(this.current_char);
        return this.current_char;
    }

    /**
     * Add a texture piece to the list
     * @since   0.1.1
     */
    public void registerTexturePiece(TexturePiece piece) {
        this.texture_pieces.add(piece);
    }

    /**
     * Get the JSON string for this font
     */
    public JsonObject getJson() {

        JsonObject root = new JsonObject();
        JsonArray providers = new JsonArray();
        root.add("providers", providers);

        HashMap<String, JsonArray> provider_chars = new HashMap<>();

        for (TexturePiece piece : this.texture_pieces) {
            String path = piece.getPath();
            JsonArray chars = provider_chars.get(path);

            if (chars == null) {

                JsonObject provider = new JsonObject();

                provider.addProperty("type", "bitmap");
                provider.addProperty("file", piece.getJsonFilename());
                provider.addProperty("ascent", piece.getAscent());
                provider.addProperty("height", piece.getHeight());

                chars = new JsonArray();
                provider.add("chars", chars);
                providers.add(provider);

                provider_chars.put(path, chars);
                chars.add("");
            }

            // Multiple array elements count as different Y levels,
            // so we need to add the characters to the first entry
            String char_string = chars.get(0).getAsString();
            char_string += piece.getCharacter();
            chars.remove(0);
            chars.add(char_string);
        }

        return root;
    }

    /**
     * Add this font resources to the given data pack
     */
    public void addToResourcePack(ResourcePackMaker pack) {

        Path buildLocation = pack.getBuildLocation();

        JsonObject root = this.getJson();
        String json = root.toString();

        String target_path_str = "assets/bbsb/font/gui.json";
        Path target_path = buildLocation.resolve(target_path_str);

        GuiUtils.writeToPath(target_path, json);

        // @TODO: add the images of the texture pieces to the pack

        HashMap<String, Boolean> registered_piece = new HashMap<>();

        for (TexturePiece piece : this.texture_pieces) {

            String image_path = piece.getPath();
            Boolean already_registered = registered_piece.get(image_path);

            if (already_registered == null) {
                registered_piece.put(image_path, true);
                String path = "assets/bbsb/textures/" + image_path;
                GuiUtils.writeToPath(buildLocation.resolve(path), piece.getImage());
            }
        }

        /*
        for (TexturePiece piece : this.texture_pieces) {
            String path = "assets/bbsb/textures/" + piece.getPath();
            GuiUtils.writeToPath(buildLocation.resolve(path), piece.getImage());
        }*/
    }
}
