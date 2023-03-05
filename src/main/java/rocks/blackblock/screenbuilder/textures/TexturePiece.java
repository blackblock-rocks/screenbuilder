package rocks.blackblock.screenbuilder.textures;

import net.minecraft.util.Identifier;

import java.awt.image.BufferedImage;

public class TexturePiece {

    private final BaseTexture parent;
    private final int index;
    private final char character;
    private BufferedImage image = null;
    private boolean uses_shared_image = false;
    private int width = 0;
    private int y_offset = 0;

    public TexturePiece(BaseTexture parent, int index, int y_offset, char character) {
        this.parent = parent;
        this.index = index;
        this.y_offset = y_offset;
        this.character = character;
    }

    /**
     * Does this piece use a shared image?
     *
     * @since   0.1.1
     */
    public void setUsesSharedImage(boolean uses_shared_image) {
        this.uses_shared_image = uses_shared_image;
    }

    /**
     * Does this piece use a shared image?
     *
     * @since   0.1.1
     */
    public boolean getUsesSharedImage() {
        return this.uses_shared_image;
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
     * Get the Y offset of this piece
     *
     * @since   0.1.3
     */
    public int getYOffset() {
        return this.y_offset;
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
        String texture_name = texture.getNamespace() + "_" + this.parent.getGuiNumber();

        if (!this.uses_shared_image) {
            texture_name += "_" + this.index;
        }

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
     * (Ascent is how much higher the font is drawn relative to the Y coordinate)
     *
     * @since   0.1.1
     */
    public int getAscent() {
        return this.parent.getAscent(this.y_offset);
    }

    /**
     * Get the height of this piece
     *
     * @since   0.3.0
     */
    public int getGuiHeight() {
        return this.parent.height;
    }

    /**
     * Get the width of this piece inside the gui
     *
     * @since   0.3.0
     */
    public int getGuiWidth() {

        int gui_width = this.getImageWidth();

        if (this.parent != null && this.parent.scale != 1d) {
            gui_width = (int) Math.floor(gui_width / this.parent.scale);
        }

        return gui_width;
    }

    /**
     * Get the height of this piece's image
     *
     * @since   0.1.1
     */
    public int getImageHeight() {
        return image.getHeight();
    }

    /**
     * Get the width of this piece
     *
     * @since   0.1.1
     */
    public int getImageWidth() {

        if (this.uses_shared_image) {
            return this.parent.getImagePieceWidth();
        }

        return image.getWidth();
    }

    /**
     * Return the string representation of this instance
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    @Override
    public String toString() {
        String result = this.getClass().getSimpleName() + "{" + this.getPath() + ", piece=" + this.getIndex() + ", image_width=" + this.getImageWidth() + ", image_height=" + this.getImageHeight() + ", ascent=" + this.getAscent() + ", y_offset=" + this.getYOffset() + "}";
        return result;
    }

}
