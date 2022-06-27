package rocks.blackblock.screenbuilder.textures;

import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.text.TextBuilder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Another texture coloured on the fly
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.1
 * @version  0.2.1
 */
public class ColouredTexture extends BaseTexture {

    // The parent texture being coloured
    protected final BaseTexture parent;

    // Parent's original colour
    private TextColor original_parent_color = null;

    /**
     * Create the instance
     *
     * @since   0.2.1
     */
    public ColouredTexture(BaseTexture parent) {
        super(parent.texture_identifier, false);
        this.parent = parent;
    }

    @Override
    public List<TexturePiece> getPieces() {
        return this.parent.getPieces();
    }

    @Override
    public List<TexturePiece> getPieces(int title_y) {
        return this.parent.getPieces(title_y);
    }

    @Override
    public void registerYOffset(int title_y) {
        this.parent.registerYOffset(title_y);
    }

    @Override
    public void registerYOffset(ScreenBuilder gui, int y) {
        this.parent.registerYOffset(gui, y);
    }

    @Override
    public List<BufferedImage> getImagePieces() {
        return this.parent.getImagePieces();
    }

    @Override
    public void setPieces(List<TexturePiece> pieces) {
        this.parent.setPieces(pieces);
    }

    @Override
    public Path getTexturePath() {
        return this.parent.getTexturePath();
    }

    @Override
    public void setTexturePath(Path path) {
        this.parent.setTexturePath(path);
    }

    @Override
    public Identifier getTextureIdentifier() {
        return this.parent.getTextureIdentifier();
    }

    @Override
    public int getGuiNumber() {
        return this.parent.getGuiNumber();
    }

    @Override
    public int getAscent(int y_offset) {
        return this.parent.getAscent(y_offset);
    }

    @Override
    public int getMaxPieceWidth() {
        return this.parent.getMaxPieceWidth();
    }

    @Override
    public int getPreferredPieceWidth() {
        return this.parent.getPreferredPieceWidth();
    }

    @Override
    public Integer getPreferredAmountOfPieces() {
        return this.parent.getPreferredAmountOfPieces();
    }

    @Override
    public int getAmountOfPieces() {
        return this.parent.getAmountOfPieces();
    }

    @Override
    public int getPieceWidth() {
        return this.parent.getPieceWidth();
    }

    @Override
    public int getPieceWidth(int piece_index) {
        return this.parent.getPieceWidth(piece_index);
    }

    @Override
    public int getTargetImageWidth() {
        return this.parent.getTargetImageWidth();
    }

    @Override
    public int getPieceSourceXStart(int piece_index) {
        return this.parent.getPieceSourceXStart(piece_index);
    }

    @Override
    public int getPieceSourceXEnd(int piece_index) {
        return this.parent.getPieceSourceXEnd(piece_index);
    }

    @Override
    public void calculate() {
        this.parent.calculate();
    }

    @Override
    public List<TexturePiece> recalculate() {
        return this.parent.recalculate();
    }

    @Override
    public BufferedImage getSourceImage() throws IOException {
        return this.parent.getSourceImage();
    }

    @Override
    protected List<BufferedImage> generateImagePieces() {
        return this.parent.generateImagePieces();
    }

    @Override
    protected List<TexturePiece> generateTexturePieces(int y) {
        return this.parent.generateTexturePieces(y);
    }

    @Override
    public void addToBuilder(TextBuilder builder, int x) {
        this.applyColourToParent();
        this.parent.addToBuilder(builder, x);
        this.resetParentColour();
    }

    @Override
    public void addToBuilder(TextBuilder builder, int x, int y) {
        this.applyColourToParent();
        this.parent.addToBuilder(builder, x, y);
        this.resetParentColour();
    }

    @Override
    public void addAmountToBuilder(TextBuilder builder, int x, int amount_of_pieces_to_add) {
        this.applyColourToParent();
        this.parent.addAmountToBuilder(builder, x, amount_of_pieces_to_add);
        this.resetParentColour();
    }

    @Override
    public int getPieceCount() {
        return this.parent.getPieceCount();
    }

    @Override
    public void addToBuilder(TextBuilder builder, int gui_x, int gui_y, int amount_of_pieces_to_add) {
        this.applyColourToParent();
        this.parent.addToBuilder(builder, gui_x, gui_y, amount_of_pieces_to_add);
        this.resetParentColour();
    }

    /**
     * Apply the colour to the parent texture
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.2.1
     */
    private void applyColourToParent() {

        if (this.texture_colour == null) {
            return;
        }

        if (this.original_parent_color != null) {
            return;
        }

        this.original_parent_color = this.parent.texture_colour;
        this.parent.texture_colour = this.texture_colour;
    }

    /**
     * Reset the parent's original colour
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.2.1
     */
    private void resetParentColour() {

        if (this.texture_colour == null) {
            return;
        }

        this.parent.texture_colour = this.original_parent_color;
        this.original_parent_color = null;
    }

    /**
     * Get the root, uncouloured texture
     *
     * @since   0.2.1
     */
    public BaseTexture getOriginalTexture() {
        return this.parent.getOriginalTexture();
    }
}
