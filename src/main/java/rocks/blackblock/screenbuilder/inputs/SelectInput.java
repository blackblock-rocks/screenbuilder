package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.interfaces.SelectEventListener;
import rocks.blackblock.screenbuilder.interfaces.SelectEventWithValueListener;
import rocks.blackblock.screenbuilder.interfaces.SlotEventListener;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.slots.ListenerWidgetSlot;
import rocks.blackblock.screenbuilder.widgets.PaginationWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectInput<T> extends BaseInput implements WidgetDataProvider {

    // A select listener
    protected SelectEventListener on_select_listener = null;
    protected SelectEventWithValueListener<T> on_select_value_listener = null;

    // The default name
    protected String default_name = "Select an option...";

    // All the available options
    public List<ItemStack> options = null;

    // All the values
    private Map<ItemStack, T> value_map = new HashMap<>();

    // The page we're on
    protected int page = 1;

    /**
     * Set the select listener
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public void setSelectListener(SelectEventListener listener) {
        this.on_select_listener = listener;
    }

    /**
     * Set the select listener with a value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public void setSelectListener(SelectEventWithValueListener<T> listener) {
        this.on_select_value_listener = listener;
    }

    /**
     * Add an option
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public ItemStack addOption(T option, ItemStack representation) {

        if (this.options == null) {
            this.options = new ArrayList<>();
        }

        this.options.add(representation);
        this.value_map.put(representation, option);

        return representation;
    }

    /**
     * Get the option's value
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public T getValue(ItemStack representation) {
        return this.value_map.get(representation);
    }

    /**
     * Get a screenbuilder
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     */
    @Override
    public ScreenBuilder getScreenBuilder() {

        SelectInput that = this;

        ScreenBuilder sb = new ScreenBuilder("select_widget");
        sb.setNamespace(BBSB.NAMESPACE);
        sb.setFontTexture(BBSB.TOP_FOUR);
        sb.loadTextureItem();
        sb.setCloneSlots(false);

        int slots_per_page = 36;
        int page = this.page;
        int item_count = 0;

        if (this.options != null) {
            item_count = this.options.size();
        }

        int max_page_value = (int) Math.ceil(item_count / (double) slots_per_page);
        int start = (page - 1) * slots_per_page;
        int end = Math.min(start + slots_per_page, item_count);

        if (page > max_page_value) {
            page = max_page_value;
            this.page = page;
        }

        SlotEventListener slot_listener = (screen, slot) -> {

            NamedScreenHandlerFactory factory = screen.getOriginFactory();

            if (factory instanceof SelectInput input) {
                ItemStack stack = slot.getStack();

                if (input.on_select_listener != null) {
                    input.on_select_listener.onSelect(screen, stack);
                }

                if (input.on_select_value_listener != null) {
                    T value = (T) input.getValue(stack);
                    input.on_select_value_listener.onSelect(screen, stack, value);
                }
            }

            that.handleScreenBehaviour(screen);
        };

        if (item_count > 0) {
            List<ItemStack> items = this.options.subList(start, end);

            for (int i = 0; i < items.size(); i++) {
                ItemStack stack = items.get(i);
                Item item = stack.getItem();

                ButtonWidgetSlot button = sb.addButton(i);
                button.setStack(stack);

                button.addLeftClickListener(slot_listener);
            }

            if (item_count > 36) {
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
            }
        }

        // Back button
        sb.setBackButton(49);

        return sb;
    }


    @Override
    public Object getWidgetValue(String widget_id) {

        if (widget_id.equals("pagination")) {
            return this.page;
        }

        return null;
    }

    @Override
    public void setWidgetValue(String widget_id, Object value) {

        if (widget_id.equals("pagination")) {
            this.page = (int) value;
        }
    }

}