package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.TextColor;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.interfaces.SlotEventListener;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.widgets.PaginationWidget;

import java.util.*;

/**
 * Select a value from a list of options
 *
 * @author  Jelle De Loecker   <jelle@elevenways.be>
 * @since   0.3.0
 */
public class SelectValueInput<T> extends EmptyInput implements WidgetDataProvider {

    // The page we're on
    protected int page = 1;

    // All the available options
    protected List<T> options = new ArrayList<>();

    // Map the slots to the values
    protected Map<Integer, ValueEntry<T>> slot_map = new HashMap<>();

    // The current selected entry
    protected ValueEntry<T> selected_entry = null;

    // Is a confirm button needed?
    protected boolean require_confirm_button = false;

    // The option decorator to use
    protected OptionDecorator<T> option_decorator = null;

    // The select listener
    protected AcceptedValueListener<T> on_accepted_value_listener = null;

    // Extra buttons
    protected List<CustomButtonAdderEntry> extra_button_adders = new ArrayList<>();

    /**
     * Is a confirm button needed?
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public void setRequireConfirmButton(boolean confirm_button) {
        this.require_confirm_button = confirm_button;
    }

    /**
     * Add an option
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public void addOption(T option) {
        this.options.add(option);
    }

    /**
     * Add multiple options
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.1
     */
    public void addOptions(Collection<T> options) {
        this.options.addAll(options);
    }

    /**
     * Set the option decorator
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public void setOptionDecorator(OptionDecorator<T> decorator) {
        this.option_decorator = decorator;
    }

    /**
     * Set the accepted value listener
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public void setOnAcceptedValueListener(AcceptedValueListener<T> listener) {
        this.on_accepted_value_listener = listener;
    }

    /**
     * Add a new button to the screen
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public void addButton(CustomButtonAdder adder) {
        this.extra_button_adders.add(new CustomButtonAdderEntry(adder));
    }

    /**
     * Add a new button to the screen, but prefer the given index
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public void addButton(int preferred_slot, CustomButtonAdder adder) {
        this.extra_button_adders.add(new CustomButtonAdderEntry(adder, preferred_slot));
    }

    /**
     * Decorate the given option
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    protected ButtonWidgetSlot decorateOption(ScreenBuilder sb, T value, int slot_index) {

        ButtonWidgetSlot button = sb.addButton(slot_index);

        ValueEntry<T> entry = new ValueEntry<>(value, slot_index);
        this.slot_map.put(slot_index, entry);

        if (option_decorator != null) {
            option_decorator.decorateOption(sb, value, button);
        } else {
            button.setStack(new ItemStack(Items.BARRIER));

            if (value == null) {
                button.setTitle("null");
            } else {
                button.setTitle(value.toString());
            }
        }

        return button;
    }

    /**
     * Set the selected entry
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public void setSelectedEntry(ValueEntry<T> entry) {
        this.selected_entry = entry;
    }

    /**
     * Do the confirmation
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public void doConfirm(TexturedScreenHandler screen) {

        if (this.on_accepted_value_listener != null) {
            this.on_accepted_value_listener.onValueAccepted(screen, this.selected_entry.getValue());
        }

    }

    /**
     * Get the ScreenBuilder instance
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    @Override
    public ScreenBuilder getScreenBuilder() {

        ScreenBuilder sb = new ScreenBuilder("select_value_input");
        sb.setNamespace(BBSB.NAMESPACE);
        sb.setFontTexture(BBSB.TOP_FOUR);
        sb.loadTextureItem();
        sb.setCloneSlots(false);
        sb.setDisplayName(this.getDisplayName());

        int slots_per_page = 36;
        int page = this.page;
        int item_count = 0;

        if (this.options != null) {
            item_count = this.options.size();
        }

        int max_page_value = 1;

        if (item_count > 0) {
            max_page_value = Math.max(1, (int) Math.ceil(item_count / (double) slots_per_page));
        }

        int start = (page - 1) * slots_per_page;
        int end = Math.min(start + slots_per_page, item_count);

        if (page > max_page_value) {
            page = max_page_value;
            this.page = page;
        }

        this.slot_map.clear();

        Set<Integer> used_slots = new HashSet<>();

        // The actual slot listener
        SlotEventListener slot_listener = (screen, slot) -> {

            NamedScreenHandlerFactory factory = screen.getOriginFactory();

            if (factory != this) {
                return;
            }

            ValueEntry<T> value_entry = this.slot_map.get(slot.getScreenIndex());

            this.setSelectedEntry(value_entry);

            if (value_entry != null) {
                if (!this.require_confirm_button) {
                    this.doConfirm(screen);
                }

                return;
            }

            screen.replaceScreen(this);
        };

        boolean has_pagination = false;

        if (item_count > 0) {
            List<T> items = this.options.subList(start, end);

            for (int i = 0; i < items.size(); i++) {
                T value = items.get(i);

                ButtonWidgetSlot button = this.decorateOption(sb, value, i);
                button.addLeftClickListener(slot_listener);
            }

            if (item_count > 36) {

                used_slots.add(45);
                used_slots.add(46);
                used_slots.add(47);
                used_slots.add(48);

                PaginationWidget pagination = new PaginationWidget();
                pagination.setId("pagination");
                pagination.setSlotIndex(45);
                pagination.setMaxValue(max_page_value);

                int current_page = page;
                pagination.setOnChangeListener((texturedScreenHandler, widget) -> {

                    if (current_page == this.page) {
                        return;
                    }

                    texturedScreenHandler.replaceScreen(this);
                });

                sb.addWidget(pagination);
                has_pagination = true;
            }
        }

        int current_index = 44;

        if (this.require_confirm_button) {
            used_slots.add(current_index);
            ButtonWidgetSlot confirm_button = sb.addButton(current_index);
            confirm_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
            confirm_button.addOverlay(BBSB.CHECK_ICON.getColoured(TextColor.fromRgb(0x15b700)));

            confirm_button.setTitle("Confirm");

            confirm_button.addLeftClickListener((screen, slot) -> {
                this.doConfirm(screen);
            });
            current_index--;
        }

        if (!this.extra_button_adders.isEmpty()) {

            for (CustomButtonAdderEntry entry : this.extra_button_adders) {
                CustomButtonAdder adder = entry.adder;
                Integer preferred_slot = entry.preferred_slot;

                Integer slot_index = null;

                if (preferred_slot != null && preferred_slot >= current_index && !used_slots.contains(preferred_slot)) {
                    slot_index = preferred_slot;
                    used_slots.add(preferred_slot);
                } else {
                    preferred_slot = null;
                    slot_index = current_index;
                }

                ButtonWidgetSlot button = adder.addButton(sb, slot_index);

                if (button == null) {
                    continue;
                }

                if (preferred_slot == null || preferred_slot == current_index) {
                    current_index--;
                }

                if (has_pagination) {
                    if (current_index < 49 && current_index > 44) {
                        current_index = 44;
                    }
                }

                // Stop if we're out of slots
                if (current_index < 36) {
                    break;
                }
            }
        }

        return sb;
    }

    /**
     * Get the value of the given widget
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    @Override
    public Object getWidgetValue(String widget_id) {

        if (widget_id.equals("pagination")) {
            return this.page;
        }

        return null;
    }

    /**
     * Set a widget value
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    @Override
    public void setWidgetValue(String widget_id, Object value) {

        if (widget_id.equals("pagination")) {
            this.page = (int) value;
        }
    }

    /**
     * The custom button adder
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public interface CustomButtonAdder {
        ButtonWidgetSlot addButton(ScreenBuilder sb, int button_index);
    }

    /**
     * The accepted value listener
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public interface AcceptedValueListener<T> {
        void onValueAccepted(TexturedScreenHandler screen, T value);
    }

    /**
     * The option decorator interface
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.0
     */
    public interface OptionDecorator<T> {
        void decorateOption(ScreenBuilder sb, T option, ButtonWidgetSlot button);
    }

    /**
     * The custom button class
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.1
     */
    public static class CustomButtonAdderEntry {

        protected final CustomButtonAdder adder;
        protected Integer preferred_slot = null;

        public CustomButtonAdderEntry(CustomButtonAdder adder) {
            this.adder = adder;
        }

        public CustomButtonAdderEntry(CustomButtonAdder adder, int preferred_slot) {
            this.adder = adder;
            this.preferred_slot = preferred_slot;
        }
    }

    /**
     * The entry class
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.0
     */
    public static class ValueEntry<T> {

        // The value
        private T value;

        // The index
        private int index;

        public ValueEntry(T value, int index) {
            this.value = value;
            this.index = index;
        }

        public T getValue() {
            return value;
        }
    }

}
