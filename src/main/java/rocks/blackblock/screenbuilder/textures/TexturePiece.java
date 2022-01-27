package rocks.blackblock.screenbuilder.textures;

import net.minecraft.util.Identifier;

import java.awt.image.BufferedImage;

public class TexturePiece {

    private final BaseTexture parent;
    private final int index;
    private final char character;
    private BufferedImage image = null;

    public TexturePiece(BaseTexture parent, int index, char character) {
        this.parent = parent;
        this.index = index;
        this.character = character;
    }

    /**
     * Get the index of this piece
     *
     * @since   0.1.1
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the character of this piece
     *
     * @since   0.1.1
     */
    public char getCharacter() {
        return character;
    }

    /**
     * Set the image of this piece
     *
     * @since   0.1.1
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * Get the image of this piece
     *
     * @since   0.1.1
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Get the path to the file of this piece
     *
     * @since   0.1.1
     */
    public String getPath() {
        Identifier texture = this.parent.getTextureIdentifier();
        String texture_name = texture.getNamespace() + "_" + this.parent.getGuiNumber() + "_" + this.index;
        String result = "gui/" + texture_name + ".png";
        return result;
    }

    /**
     * Get the filename to use inside the font's json definition
     *
     * @since   0.1.1
     */
    public String getJsonFilename() {
        String result = "bbsb:" + this.getPath();
        return result;
    }

    /**
     * Calculate the font's ascent
     *
     * @since   0.1.1
     */
    public int getAscent() {
        return this.parent.getAscent();
    }

    /**
     * Get the height of this piece
     *
     * @since   0.1.1
     */
    public int getHeight() {
        return image.getHeight();
    }

    /**
     * Get the width of this piece
     *
     * @since   0.1.1
     */
    public int getWidth() {
        return image.getWidth();
    }

}
