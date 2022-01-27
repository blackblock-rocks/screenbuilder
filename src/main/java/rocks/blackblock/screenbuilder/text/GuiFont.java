package rocks.blackblock.screenbuilder.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.theepicblock.polymc.api.resource.ResourcePackMaker;
import net.minecraft.util.Formatting;
import rocks.blackblock.screenbuilder.textures.TexturePiece;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import java.nio.file.Path;
import java.util.ArrayList;

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
        return (char) (33 + (this.index++));
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

        System.out.println("Adding pieces " + this.texture_pieces.size());

        for (TexturePiece piece : this.texture_pieces) {
            JsonObject provider = new JsonObject();

            provider.addProperty("type", "bitmap");
            provider.addProperty("file", piece.getJsonFilename());
            provider.addProperty("ascent", piece.getAscent());
            provider.addProperty("height", piece.getHeight());

            JsonArray chars = new JsonArray();
            provider.add("chars", chars);
            chars.add(piece.getCharacter());

            providers.add(provider);

            System.out.println("Added provider: " + provider.toString());
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

        for (TexturePiece piece : this.texture_pieces) {
            String path = "assets/bbsb/textures/" + piece.getPath();
            System.out.println("Adding texture to: " + path);
            GuiUtils.writeToPath(buildLocation.resolve(path), piece.getImage());
        }
    }
}
