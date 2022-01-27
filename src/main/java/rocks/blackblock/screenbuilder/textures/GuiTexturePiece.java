package rocks.blackblock.screenbuilder.textures;

import net.minecraft.data.client.model.Texture;
import net.minecraft.util.Identifier;

import java.awt.image.BufferedImage;

public class GuiTexturePiece {

    private final BaseTexture parent;
    private final int index;
    private final char character;
    private BufferedImage image = null;

    public GuiTexturePiece(BaseTexture parent, int index, char character) {
        this.parent = parent;
        this.index = index;
        this.character = character;
    }

    /**
     * Get the index of this piece
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the character of this piece
     */
    public char getCharacter() {
        return character;
    }

    /**
     * Set the image of this piece
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * Get the image of this piece
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Get the path to the file of this piece
     */
    public String getPath() {
        Identifier texture = this.parent.getTextureIdentifier();
        String texture_name = texture.getNamespace() + "_" + this.parent.getGuiNumber() + "_" + this.index;
        String result = "gui/" + texture_name + ".png";
        return result;
    }

    /**
     * Get the filename to use inside the font's json definition
     */
    public String getJsonFilename() {
        String result = "bbsb:" + this.getPath();
        return result;
    }

    /**
     * Calculate the font's ascent
     */
    public int getAscent() {
        return this.parent.getAscent();
    }

    /**
     * Get the height of this piece
     */
    public int getHeight() {
        return image.getHeight();
    }

    /**
     * Get the width of this piece
     */
    public int getWidth() {
        return image.getWidth();
    }

}
