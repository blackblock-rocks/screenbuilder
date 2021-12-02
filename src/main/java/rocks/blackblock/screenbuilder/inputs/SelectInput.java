package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.interfaces.SelectEventListener;
import rocks.blackblock.screenbuilder.interfaces.SlotEventListener;
import rocks.blackblock.screenbuilder.slots.ListenerWidgetSlot;

import java.util.ArrayList;

public class SelectInput extends BaseInput {

    // The registered GUI
    public static ScreenBuilder GUI;

    // A select listener
    protected SelectEventListener on_select_listener = null;

    // The default name
    protected String default_name = "Select an option...";

    // All the available options
    public ArrayList<ItemStack> options = null;

    /**
     * Set the select listener
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setSelectListener(SelectEventListener listener) {
        this.on_select_listener = listener;
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

        SelectInput that = this;

        ScreenBuilder sb = new ScreenBuilder("select_widget");
        sb.setNamespace("blackblock");
        sb.useCustomTexture(true);
        sb.loadTextureItem();
        sb.setCloneSlots(false);

        if (this.options != null && !this.options.isEmpty()) {
            int index = 0;


            SlotEventListener slot_listener = (screen, slot) -> {

                NamedScreenHandlerFactory factory = screen.getOriginFactory();

                if (factory instanceof SelectInput input) {
                    if (input.on_select_listener != null) {
                        input.on_select_listener.onSelect(screen, slot.getStack());
                    }
                }

                that.handleScreenBehaviour(screen);
            };

            for (ItemStack stack : options) {

                ListenerWidgetSlot button = new ListenerWidgetSlot();
                button.setStack(stack);
                button.setCloneBeforeScreen(false);
                button.addLeftClickListener(slot_listener);

                if (index % 9 == 0) {
                    index++;
                }

                sb.setSlot(index++, button);

                // @TODO; add paging?
                if (index > 53) {
                    break;
                }
            }
        }

        // Back button
        sb.setBackButton(45);

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

        GUI = new ScreenBuilder("select_widget");
        GUI.setNamespace("blackblock");
        GUI.useCustomTexture(true);

        GUI.register();

        return GUI;
    }

}