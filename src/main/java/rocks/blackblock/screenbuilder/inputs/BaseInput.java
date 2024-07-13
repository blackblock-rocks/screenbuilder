package rocks.blackblock.screenbuilder.inputs;

import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.interfaces.BaseInputChangeEventListener;
import rocks.blackblock.screenbuilder.items.GuiItem;
import rocks.blackblock.screenbuilder.screen.BasescreenFactory;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.textures.BaseTexture;
import rocks.blackblock.screenbuilder.textures.IconTexture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The base input class
 *
 * @author  Jelle De Loecker   <jelle@elevenways.be>
 * @since   0.1.0
 * @version 0.1.3
 */
@SuppressWarnings("unused")
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
     * @since    0.1.0
     */
    public void setChangeBehaviour(ChangeBehaviour behaviour) {
        this.change_behaviour = behaviour;
    }

    /**
     * Get what should happen when the user changes the value
     *
     * @since    0.1.0
     */
    public ChangeBehaviour getChangeBehaviour() {
        return this.change_behaviour;
    }

    /**
     * Show the accept button
     *
     * @since    0.1.0
     */
    public void showAcceptButton(Integer slot_index) {
        this.show_accept_button = slot_index;
    }

    /**
     * Show the back button
     *
     * @since    0.1.0
     */
    public void showBackButton(Integer slot_index) {
        this.show_back_button = slot_index;
    }

    /**
     * Set the accept listener
     *
     * @since    0.1.0
     */
    public void setAcceptListener(BaseInputChangeEventListener on_accept_click) {
        this.on_accept_click = on_accept_click;
    }

    /**
     * Set the back listener
     *
     * @since    0.1.0
     */
    public void setBackListener(BaseInputChangeEventListener on_back_click) {
        this.on_back_click = on_back_click;
    }

    /**
     * Get a basic ScreenBuilder
     *
     * @since    0.1.0
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
     */
    @Nullable
    public TexturedScreenHandler handleScreenBehaviour(TexturedScreenHandler screen) {

        if (this.change_behaviour == ChangeBehaviour.DO_NOTHING) {
            return screen;
        }

        if (this.change_behaviour == ChangeBehaviour.SHOW_PREVIOUS_SCREEN) {
            return screen.showPreviousScreen();
        }

        if (this.change_behaviour == ChangeBehaviour.CLOSE_SCREEN) {
            screen.close();
            return null;
        }

        return null;
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

    /**
     * Set a value.
     * If an error is thrown, show the error.
     *
     * @since    0.5.0
     */
    protected <T> boolean applyValueSet(TexturedScreenHandler handler, Consumer<T> setter, T value) {
        try {
            setter.accept(value);
            return true;
        } catch (Throwable e) {
            this.addError(e.getMessage());
            handler.replaceScreen(this);
            return false;
        }
    }

    /**
     * Add a button that sets a string
     *
     * @since    0.5.0
     */
    public ButtonWidgetSlot addStringButton(ScreenBuilder sb, int slot_index, String title_prefix, IconTexture icon, GetStringValue getter, SetStringValue setter) {

        if (icon == null) {
            icon = BBSB.PENCIL_ICON;
        }

        String current_value = getter.getCurrentValue();

        ButtonWidgetSlot string_button = sb.addButton(slot_index);
        string_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
        string_button.setTitle(title_prefix + ": " + current_value);
        string_button.addOverlay(icon);

        string_button.addLeftClickListener((screen, slot) -> {
            StringInput rename_input = new StringInput();

            rename_input.setRenamedListener((screen_1, value) -> {
                if (this.applyValueSet(screen_1, setter::setCurrentValue, value)) {
                    screen_1.replaceScreen(this);
                }
            });

            screen.pushScreen(rename_input);
        });

        return string_button;
    }

    /**
     * Add a button that sets numeric values
     *
     * @since    0.5.0
     */
    public ButtonWidgetSlot addNumericButton(ScreenBuilder sb, int slot_index, String title_prefix, GetNumericValue getter, SetNumericValue setter) {

        int current_value = getter.getCurrentValue();

        // If the title prefix does not end with a colon, add one
        if (!title_prefix.endsWith(": ")) {
            title_prefix += ": ";
        }

        ButtonWidgetSlot numeric_button = sb.addButton(slot_index);
        numeric_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
        numeric_button.setTitle(title_prefix + current_value);
        numeric_button.setButtonText("" + current_value);

        numeric_button.addLeftClickListener((screen, slot) -> {
            int new_value = getter.getCurrentValue() + this.getChangeAmount(screen);

            if (this.applyValueSet(screen, setter::setCurrentValue, new_value)) {
                screen.replaceScreen(this);
            }
        });

        numeric_button.addRightClickListener((screen, slot) -> {
            int new_value = getter.getCurrentValue() - this.getChangeAmount(screen);

            if (this.applyValueSet(screen, setter::setCurrentValue, new_value)) {
                screen.replaceScreen(this);
            }
        });

        return numeric_button;
    }

    /**
     * Add a button that toggles between values
     *
     * @since    0.5.0
     */
    public ButtonWidgetSlot addToggleButton(ScreenBuilder sb, int slot_index, String title_prefix, ToggleOptions options) {

        ToggleOption current_option = options.getCurrentOption();

        ButtonWidgetSlot toggle_button = sb.addButton(slot_index);
        toggle_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
        toggle_button.setTitle(title_prefix + current_option.title);
        current_option.addToButton(toggle_button);

        toggle_button.addLeftClickListener((screen, slot) -> {

            ToggleOption current = options.getCurrentOption();
            int new_value = current.value + 1;

            if (options.getOption(new_value) == null) {
                new_value = 0;
            }

            ToggleOption new_option = options.getOption(new_value);

            if (this.applyValueSet(screen, options.setter::setCurrentValue, new_option.value)) {
                screen.replaceScreen(this);
            }
        });

        toggle_button.addRightClickListener((screen, slot) -> {
            ToggleOption current = options.getCurrentOption();
            int new_value = current.value - 1;

            if (options.getOption(new_value) == null) {
                new_value = options.options.size() - 1;
            }

            ToggleOption new_option = options.getOption(new_value);

            if (this.applyValueSet(screen, options.setter::setCurrentValue, new_option.value)) {
                screen.replaceScreen(this);
            }
        });

        return toggle_button;
    }

    /**
     * Get the preferred numerical change amount
     *
     * @since    0.5.0
     */
    protected int getChangeAmount(TexturedScreenHandler screen) {

        int amount = 1;

        if (screen.isPressingShift()) {
            amount = 10;
        }

        return amount;
    }

    /**
     * Represents a toggleable options
     *
     * @since    0.5.0
     */
    public static class ToggleOptions {
        Map<Integer, ToggleOption> options = new HashMap<>();
        GetNumericValue getter;
        SetNumericValue setter;

        public ToggleOption add(int value, String title, String lore) {
            ToggleOption option = new ToggleOption(value, title, lore);
            options.put(value, option);
            return option;
        }

        public void setGetter(GetNumericValue getter) {
            this.getter = getter;
        }

        public void setSetter(SetNumericValue setter) {
            this.setter = setter;
        }

        public ToggleOption getCurrentOption() {
            return options.get(getter.getCurrentValue());
        }

        public ToggleOption getOption(int value) {
            return options.get(value);
        }
    }

    /**
     * Represents a toggleable option
     *
     * @since    0.5.0
     */
    public static class ToggleOption {
        int value;
        String title;
        String lore;
        List<BaseTexture> textures = new ArrayList<>();

        public ToggleOption(int value, String title, String lore) {
            this.value = value;
            this.title = title;
            this.lore = lore;
        }

        public void addOverlay(BaseTexture texture) {
            textures.add(texture);
        }

        public void addToButton(ButtonWidgetSlot button) {
            for (BaseTexture texture : textures) {
                button.addOverlay(texture);
            }
        }
    }

    public interface GetNumericValue {
        int getCurrentValue();
    }

    public interface SetNumericValue {
        void setCurrentValue(int value);
    }

    public interface GetStringValue {
        String getCurrentValue();
    }

    public interface SetStringValue {
        void setCurrentValue(String value);
    }

    public enum ChangeBehaviour {
        DO_NOTHING,
        CLOSE_SCREEN,
        SHOW_PREVIOUS_SCREEN
    }
}
