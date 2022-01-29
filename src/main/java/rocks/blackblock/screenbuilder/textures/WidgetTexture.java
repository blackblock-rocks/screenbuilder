package rocks.blackblock.screenbuilder.textures;

import net.minecraft.util.Identifier;

/**
 * The Texture class used for GUI widgets
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.1
 * @version  0.1.1
 */
public class WidgetTexture extends BaseTexture {

    // The y-coordinate of where the texture should go in its respective container
    protected final Integer target_y;

    // The parent gui texture
    protected GuiTexture parent_gui;

    // The minimum amount of pieces this widget should be split into
    protected Integer min_pieces = null;

    /**
     * Create a new WidgetTexture
     *
     * @param   texture_path   The path to the texture
     * @param   parent_gui     The GUI this texture will be used in
     * @param   target_y       The y-coordinate of where the texture should go in its respective container
     * @param   min_pieces     The minimum amount of pieces this widget should be split into
     */
    public WidgetTexture(Identifier texture_path, GuiTexture parent_gui, int target_y, Integer min_pieces) {
        this(texture_path, target_y - parent_gui.getOriginalY() - parent_gui.getOriginalScreenTitleY(), min_pieces);
        this.parent_gui = parent_gui;
    }

    /**
     * Create a new WidgetTexture
     *
     * @param   texture_path   The path to the texture
     * @param   target_y       The y-coordinate of where the texture should go in its respective container
     * @param   min_pieces     The minimum amount of pieces this widget should be split into
     */
    public WidgetTexture(Identifier texture_path, int target_y, Integer min_pieces) {
        super(texture_path, false);
        this.target_y = target_y;
        this.min_pieces = min_pieces;
        this.calculate();
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
