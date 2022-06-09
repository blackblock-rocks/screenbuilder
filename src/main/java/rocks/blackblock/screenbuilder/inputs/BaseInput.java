package rocks.blackblock.screenbuilder.inputs;

import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.interfaces.BaseInputChangeEventListener;
import rocks.blackblock.screenbuilder.items.GuiItem;
import rocks.blackblock.screenbuilder.screen.BasescreenFactory;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;

import java.util.ArrayList;
import java.util.List;

/**
 * The base input class
 *
 * @author  Jelle De Loecker   <jelle@elevenways.be>
 * @since   0.1.0
 * @version 0.1.3
 */
public abstract class BaseInput extends BasescreenFactory {

    // What should happen on a change?
    protected ChangeBehaviour change_behaviour = ChangeBehaviour.CLOSE_SCREEN;

    // Should an accept button be added?
    protected Integer show_accept_button = null;

    // The accept listener
    protected BaseInputChangeEventListener on_accept_click = null;

    // Should a back button be added?
    protected Integer show_back_button = null;

    // The back listener
    protected BaseInputChangeEventListener on_back_click = null;

    // The line where error messages should be shown
    protected Integer show_error_line = -2;

    // Error messages
    protected List<String> error_messages = null;

    /**
     * Set what should happen when the user changes the value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setChangeBehaviour(ChangeBehaviour behaviour) {
        this.change_behaviour = behaviour;
    }

    /**
     * Get what should happen when the user changes the value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ChangeBehaviour getChangeBehaviour() {
        return this.change_behaviour;
    }

    /**
     * Show the accept button
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void showAcceptButton(Integer slot_index) {
        this.show_accept_button = slot_index;
    }

    /**
     * Show the back button
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void showBackButton(Integer slot_index) {
        this.show_back_button = slot_index;
    }

    /**
     * Set the accept listener
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setAcceptListener(BaseInputChangeEventListener on_accept_click) {
        this.on_accept_click = on_accept_click;
    }

    /**
     * Set the back listener
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setBackListener(BaseInputChangeEventListener on_back_click) {
        this.on_back_click = on_back_click;
    }

    /**
     * Get a basic ScreenBuilder
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ScreenBuilder createBasicScreenBuilder(String name) {

        BaseInput that = this;

        ScreenBuilder sb = new ScreenBuilder(name);
        sb.setNamespace(BBSB.NAMESPACE);
        sb.useCustomTexture(true);
        sb.loadTextureItem();
        sb.setCloneSlots(false);

        if (this.show_accept_button != null) {
            ButtonWidgetSlot accept_button = new ButtonWidgetSlot();
            accept_button.setStack(GuiItem.get("true"));
            accept_button.setTitle("Accept");

            accept_button.addLeftClickListener((screen, slot) -> {
                if (that.on_accept_click != null) {
                    that.on_accept_click.onEvent(screen, that);
                }
            });

            sb.setSlot(this.show_accept_button, accept_button);
        }

        if (this.show_back_button != null) {
            ButtonWidgetSlot back_button = new ButtonWidgetSlot();
            back_button.setStack(GuiItem.get("false"));
            back_button.setTitle("Cancel");

            back_button.addLeftClickListener((screen, slot) -> {
                screen.showPreviousScreen();
            });

            sb.setSlot(this.show_back_button, back_button);
        }

        this.printErrors(sb);

        return sb;
    }

    /**
     * The user did something, choose what to do next
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void handleScreenBehaviour(TexturedScreenHandler screen) {

        if (this.change_behaviour == ChangeBehaviour.DO_NOTHING) {
            return;
        }

        if (this.change_behaviour == ChangeBehaviour.SHOW_PREVIOUS_SCREEN) {
            screen.showPreviousScreen();
            return;
        }

        if (this.change_behaviour == ChangeBehaviour.CLOSE_SCREEN) {
            screen.close();
            return;
        }
    }

    /**
     * Print the error messages
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    protected void printErrors(ScreenBuilder builder) {

        if (this.error_messages == null) {
            return;
        }

        for (String message : this.error_messages) {
            builder.addError(message);
        }
    }

    /**
     * Add an error message
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public void addError(String message) {

        if (this.error_messages == null) {
            this.error_messages = new ArrayList<>();
        }

        this.error_messages.add(message);
    }

    public enum ChangeBehaviour {
        DO_NOTHING,
        CLOSE_SCREEN,
        SHOW_PREVIOUS_SCREEN
    }
}
