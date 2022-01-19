package rocks.blackblock.screenbuilder.textures;

import java.awt.image.BufferedImage;

public class GuiTexturePiece {

    private final GuiTexture parent;
    private final int index;
    private final char character;
    private BufferedImage image = null;

    public GuiTexturePiece(GuiTexture parent, int index, char character) {
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
        String result = "gui/" + this.index + ".png";
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

        int result = 0;
        int original_y = this.parent.getOriginalY();

        if (original_y != 0) {
            result += original_y;
        }

        result += this.parent.getOriginalContainerTitleStartY();

        // I think 1 always needs to be subtraced if it's negative?
        result -= 1;

        System.out.println("Ascent of gui piece is: " + result);

        return result;
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
