package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.item.ItemStack;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.interfaces.BooleanEventListener;
import rocks.blackblock.screenbuilder.items.GuiItem;
import rocks.blackblock.screenbuilder.screen.BasescreenFactory;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.slots.CheckboxWidgetSlot;

import java.util.ArrayList;

public class BooleanInput extends BasescreenFactory {

    // The registered GUI
    public static ScreenBuilder GUI;

    // A change event listener
    protected BooleanEventListener on_change_listener = null;

    // The default name
    protected String default_name = "Toggle the checkbox...";

    // All the available options
    public ArrayList<ItemStack> options = null;

    // The current value
    protected Boolean value = null;

    // The working value
    protected Boolean working_value = null;

    /**
     * Set the change listener
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setChangeListener(BooleanEventListener listener) {
        this.on_change_listener = listener;
    }

    /**
     * Set the value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setValue(Boolean value) {
        this.value = value;
        this.working_value = value;
    }

    /**
     * Get the value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public Boolean getValue() {
        return this.value;
    }

    /**
     * Get a screenbuilder
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     * @version 0.1.0
     */
    @Override
    public ScreenBuilder getScreenBuilder() {

        this.working_value = this.value;

        ScreenBuilder sb = new ScreenBuilder("empty");
        sb.setNamespace("bbsb");
        sb.useCustomTexture(true);
        sb.loadTextureItem();
        sb.setCloneSlots(false);

        BooleanInput that = this;

        CheckboxWidgetSlot checkbox = new CheckboxWidgetSlot();
        checkbox.setValue(this.working_value);
        sb.setSlot(22, checkbox);

        checkbox.setChangeListener((screen, new_value) -> {
            that.working_value = new_value;
        });

        ButtonWidgetSlot accept = sb.addButton(42);
        accept.setStack(GuiItem.get("true"));
        accept.setTitle("Accept");

        // Accept the current working value
        accept.addLeftClickListener((screen, slot) -> {
            that.value = that.working_value;

            if (that.on_change_listener != null) {
                that.on_change_listener.onChange(screen, that.value);
            }

            screen.showPreviousScreen();
        });

        ButtonWidgetSlot cancel = sb.addButton(38);
        cancel.setStack(GuiItem.get("false"));
        cancel.setTitle("Cancel");

        cancel.addLeftClickListener((screen, slot) -> {
            that.working_value = that.value;
            screen.showPreviousScreen();
        });

        return sb;
    }

    /**
     * Register the GUI
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public static ScreenBuilder registerScreen() {

        if (GUI != null) {
            return GUI;
        }

        GUI = new ScreenBuilder("empty");
        GUI.setNamespace("bbsb");
        GUI.useCustomTexture(true);

        GUI.register();

        return GUI;
    }
}
