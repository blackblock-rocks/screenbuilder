package rocks.blackblock.screenbuilder.textures;

import net.minecraft.util.Identifier;

import java.nio.file.Path;

/**
 * The Texture class used for GUI widgets
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.1
 * @version  0.1.1
 */
public class WidgetTexture extends BaseTexture {

    // The y-coordinate of where the texture should go in its respective container
    protected Integer target_y;

    // The original target_y, before it was adjusted to the parent texture
    protected Integer original_target_y;

    // The parent gui texture
    protected GuiTexture parent_gui;

    // The minimum amount of pieces this widget should be split into
    protected Integer min_pieces = null;

    /**
     * Create a new WidgetTexture
     *
     * @param   texture_identifier   The path to the texture
     * @param   parent_gui           The GUI this texture will be used in
     * @param   target_y             The y-coordinate of where the texture should go in its respective container
     * @param   min_pieces           The minimum amount of pieces this widget should be split into
     */
    public WidgetTexture(Identifier texture_identifier, GuiTexture parent_gui, int target_y, Integer min_pieces) {
        //this(texture_identifier, target_y - parent_gui.getOriginalY() - parent_gui.getOriginalScreenTitleY(), min_pieces);
        this(texture_identifier);
        this.setParentGuiTexture(parent_gui);
        this.setMinPieces(min_pieces);
        this.setTargetY(target_y);
        this.calculate();
    }

    /**
     * Create a new WidgetTexture
     *
     * @param   texture_identifier   The path to the texture
     * @param   target_y             The y-coordinate of where the texture should go in its respective container
     * @param   min_pieces           The minimum amount of pieces this widget should be split into
     */
    public WidgetTexture(Identifier texture_identifier, int target_y, Integer min_pieces) {
        super(texture_identifier);
        this.setTargetY(target_y);
        this.setMinPieces(min_pieces);

        // Make sure the pieces are generated
        this.calculate();
    }

    /**
     * Create a new WidgetTexture with a specific path to the source image
     *
     * @param   texture_identifier   The path to the texture
     * @param   target_y             The y-coordinate of where the texture should go in its respective container
     * @param   min_pieces           The minimum amount of pieces this widget should be split into
     */
    public WidgetTexture(Identifier texture_identifier, Path texture_path, int target_y, Integer min_pieces) {
        super(texture_identifier);
        this.setTargetY(target_y);
        this.setMinPieces(min_pieces);

        this.setTexturePath(texture_path);

        // Make sure the pieces are generated
        this.calculate();
    }

    /**
     * Create an uninitialized texture
     *
     * @param   texture_identifier   The path to the texture
     *
     * @since   0.1.2
     */
    public WidgetTexture(Identifier texture_identifier) {
        super(texture_identifier);
    }

    /**
     * Set the target_y
     *
     * @since   0.1.2
     */
    public void setTargetY(int target_y) {
        this.original_target_y = target_y;

        if (this.parent_gui != null) {
            target_y -= this.parent_gui.getOriginalY();
            target_y -= this.parent_gui.getOriginalScreenTitleY();
        }

        this.target_y = target_y;
    }

    /**
     * Set the minimum amount of pieces
     *
     * @since   0.1.2
     */
    public void setMinPieces(int min_pieces) {
        this.min_pieces = min_pieces;
    }

    /**
     * Set the parent GUI texture
     *
     * @since   0.1.2
     */
    public void setParentGuiTexture(GuiTexture parent_gui) {
        this.parent_gui = parent_gui;

        if (this.original_target_y != null) {
            this.setTargetY(this.original_target_y);
        }
    }

    /**
     * Calculate the font's ascent
     *
     * @since   0.1.1
     */
    @Override
    public int getAscent() {
        int result = -this.target_y;

        return result;
    }

    /**
     * Get the amount of pieces this widget should be split into
     *
     * @since   0.1.1
     */
    @Override
    public Integer getPreferredAmountOfPieces() {
        return this.min_pieces;
    }
}
