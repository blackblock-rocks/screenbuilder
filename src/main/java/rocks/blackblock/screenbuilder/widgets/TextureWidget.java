package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import rocks.blackblock.bib.util.BibLog;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.textures.GuiTexture;
import rocks.blackblock.screenbuilder.textures.WidgetTexture;

import java.nio.file.Path;

/**
 * GUI widgets that use font textures
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.1
 * @version  0.1.1
 */
public abstract class TextureWidget<T> extends Widget<T> {

    // The parent gui it will be used in
    protected GuiTexture parent_gui = null;

    // The actual widget texture instance
    protected WidgetTexture widget_texture = null;

    // The path to the texture
    protected final Identifier texture_identifier;

    // The wanted X position
    protected int x = 0;

    // The wanted Y position
    protected int y = 0;

    // The optional path to the texture
    protected Path texture_path = null;

    /**
     * Create the widget
     *
     * @param   widget_texture
     *
     * @since   0.1.3
     */
    public TextureWidget(WidgetTexture widget_texture) {
        this.widget_texture = widget_texture;
        this.texture_identifier = widget_texture.getTextureIdentifier();
    }

    /**
     * Create the widget
     *
     * @param   texture_identifier
     *
     * @since   0.1.1
     */
    public TextureWidget(Identifier texture_identifier) {
        this.texture_identifier = texture_identifier;
    }

    /**
     * Create the widget with a specific path to the texture
     *
     * @param   texture_identifier
     * @param   texture_path
     *
     * @since   0.1.2
     */
    public TextureWidget(Identifier texture_identifier, Path texture_path) {
        this.texture_identifier = texture_identifier;
        this.setTexturePath(texture_path);
    }

    /**
     * Set the optional path to the texture
     * (in case it's not in a mod)
     *
     * @since   0.1.1
     */
    public void setTexturePath(Path texture_path) {
        this.texture_path = texture_path;

        if (this.widget_texture != null) {
            this.widget_texture.setTexturePath(texture_path);
        }
    }

    /**
     * Create the widget texture
     *
     * @since   0.1.1
     */
    protected WidgetTexture createWidgetTexture() {

        if (this.texture_identifier == null) {
            return null;
        }

        if (this.widget_texture != null) {
            return this.widget_texture;
        }

        this.widget_texture = WidgetTexture.getWidgetTexture(this.texture_identifier, this.getWantedAmountOfTexturePieces());

        if (this.parent_gui != null) {
            this.widget_texture.setParentGuiTexture(this.parent_gui);
        }

        this.widget_texture.setTargetY(this.y);
        this.widget_texture.setMinPieces(this.getWantedAmountOfTexturePieces());

        if (this.texture_path != null) {
            this.widget_texture.setTexturePath(this.texture_path);
        }

        this.widget_texture.calculate();

        return this.widget_texture;
    }

    /**
     * Get the minimum amount of pieces the texture needs to have
     *
     * @since   0.1.1
     */
    public int getWantedAmountOfTexturePieces() {
        return 1;
    }

    /**
     * Add the widget to the given screenbuilder.
     *
     * @param   builder   The builder to add to
     * @param   id        The unique id of the widget in this screenbuilder
     * @param   x         The x position of the widget in the current screenbuilder's gui
     * @param   y         The y position of the widget in the current screenbuilder's gui
     *
     * @since   0.1.1
     */
    public void addToScreenBuilder(ScreenBuilder builder, String id, int x, int y) {

        GuiTexture gui_texture = builder.getFontTexture();

        if (gui_texture == null) {
            return;
        }

        this.id = id;
        this.parent_gui = gui_texture;
        this.x = x;
        this.y = y;

        this.createWidgetTexture();
        builder.addWidget(id, this);
    }

    /**
     * Add the widget to the text builder
     *
     * @since   0.1.1
     */
    public void addToTextBuilder(TextBuilder builder) {
        super.addToTextBuilder(builder);
    }

    /**
     * Append to a BibLog.Arg representation
     *
     * @since 0.5.0
     */
    @Override
    public void appendToBibLogArg(@NotNull BibLog.Arg arg) {

        if (this.widget_texture != null) {
            arg.add("texture", this.widget_texture);
        }

        if (this.texture_identifier != null) {
            arg.add("texture_identifier", this.texture_identifier);
        }
    }
}
