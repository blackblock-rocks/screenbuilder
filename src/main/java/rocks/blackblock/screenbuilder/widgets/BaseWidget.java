package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.interfaces.MapWidgetAddedListener;
import rocks.blackblock.screenbuilder.interfaces.WidgetAddedListener;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.textures.GuiTexture;
import rocks.blackblock.screenbuilder.textures.WidgetTexture;

/**
 * GUI widgets that use font textures
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.1
 * @version  0.1.1
 */
public abstract class BaseWidget {

    // The parent gui it will be used in
    protected GuiTexture parent_gui = null;

    // The actual widget texture instance
    protected WidgetTexture widget_texture = null;

    // The path to the texture
    protected final Identifier texture_path;

    // The wanted X position
    protected int x = 0;

    // The wanted Y position
    protected int y = 0;

    // The name of this widget
    protected String id = null;

    // The added listener
    protected WidgetAddedListener added_listener = null;

    /**
     * Create the widget
     *
     * @param   texture_path
     *
     * @since   0.1.1
     */
    public BaseWidget(Identifier texture_path) {
        this.texture_path = texture_path;
    }

    /**
     * Create the widget texture
     *
     * @since   0.1.1
     */
    protected void createWidgetTexture() {

        if (this.texture_path == null) {
            return;
        }

        this.widget_texture = new WidgetTexture(this.texture_path, this.parent_gui, this.y, this.getWantedAmountOfTexturePieces());
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
     * Listen for value updates
     *
     * @since   0.1.1
     */
    public abstract void addWithValue(TextBuilder builder, Object value);

    /**
     * Add the widget to the text builder
     *
     * @since   0.1.1
     */
    public void addToTextBuilder(TextBuilder builder) {

        ScreenBuilder screenbuilder = builder.getScreenBuilder();
        TexturedScreenHandler handler = builder.getScreenHandler();

        if (handler != null) {
            NamedScreenHandlerFactory factory = handler.getOriginFactory();

            if (factory instanceof WidgetDataProvider provider) {
                this.addWithValue(builder, provider.getWidgetValue(this.id));

                if (this.added_listener != null) {
                    this.added_listener.onAdded(builder, null);
                }

                return;
            }
        }

        if (this.widget_texture != null) {
            this.widget_texture.addToBuilder(builder, this.x);
        }

        if (this.added_listener != null) {
            this.added_listener.onAdded(builder, null);
        }
    }

    /**
     * Set the added listener
     *
     * @since   0.1.1
     */
    public void setAddedListener(WidgetAddedListener listener) {
        this.added_listener = listener;
    }
}
