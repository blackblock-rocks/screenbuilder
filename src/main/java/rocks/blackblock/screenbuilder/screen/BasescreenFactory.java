package rocks.blackblock.screenbuilder.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;

public abstract class BasescreenFactory implements NamedScreenHandlerFactory {

    // The registered GUI
    public static ScreenBuilder GUI;

    // The custom name to use for this screen
    protected Text display_name_text = null;

    // The default text
    protected String default_name = "Placeholder name";

    /**
     * Get the screen handler factory
     * (This is the same as this class)
     *
     * @since   0.3.1
     */
    public NamedScreenHandlerFactory getScreenHandlerFactory() {
        return this;
    }

    /**
     * Set the name to put on this screen
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     * @version 0.1.0
     */
    public void setDisplayName(String name) {
        this.setDisplayName(Text.literal(name).setStyle(Style.EMPTY.withItalic(false)));
    }

    /**
     * Set the name to put on this screen
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     * @version 0.1.0
     */
    public void setDisplayName(Text name) {
        this.display_name_text = name;
    }

    /**
     * Get the non-default name to put on this screen
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.1
     */
    @Nullable
    public Text getNonDefaultDisplayName() {
        return this.display_name_text;
    }

    /**
     * Get the name to put on this screen
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     * @version 0.1.0
     */
    @Override
    @NotNull
    public Text getDisplayName() {

        if (this.display_name_text != null) {
            return this.display_name_text;
        }

        return Text.literal(this.default_name);
    }

    /**
     * Get the screenbuilder
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     * @version 0.1.0
     */
    public abstract ScreenBuilder getScreenBuilder();

    /**
     * Create the actual handler
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     * @version 0.1.0
     */
    @Nullable
    @Override
    public TexturedScreenHandler createMenu(int sync_id, PlayerInventory player_inventory, PlayerEntity player) {

        TexturedScreenHandler handler;
        ScreenBuilder sb = this.getScreenBuilder();

        if (sb == null) {
            BBSB.LOGGER.error("No ScreenBuilder found for {}", this.getClass().getName());
            return null;
        }

        if (this instanceof Inventory inventory) {
            handler = sb.createScreenHandler(sync_id, player_inventory, inventory);
        } else {
            handler = sb.createScreenHandler(sync_id, player_inventory);
        }

        handler.setOriginFactory(this);
        return handler;
    }
}