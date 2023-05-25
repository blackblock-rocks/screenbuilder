package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.TextColor;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.inputs.PageableInput;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.text.TextGroup;

public class PaginationWidget extends CombinedWidget<Integer> {

    protected ButtonWidgetSlot next_button;
    protected ButtonWidgetSlot previous_button;
    protected int min_value = 1;
    protected int max_value = 1;

    public PaginationWidget() {
        this.slot_width = 4;

        this.next_button = new ButtonWidgetSlot();
        this.next_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.EXTRA_SMALL);
        this.next_button.setTitle("Next");

        this.previous_button = new ButtonWidgetSlot();
        this.previous_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.EXTRA_SMALL);
        this.previous_button.setTitle("Previous");

        this.setSlot(0, this.previous_button);
        this.setSlot(3, this.next_button);

        this.next_button.addLeftClickListener((screen, slot) -> {
            this.adjustValue(screen,1);
        });

        this.previous_button.addLeftClickListener((screen, slot) -> {
            this.adjustValue(screen, -1);
        });
    }

    public void setMinValue(int min_value) {
        this.min_value = min_value;
    }

    public void setMaxValue(int max_value) {
        this.max_value = max_value;
    }

    private void adjustValue(TexturedScreenHandler handler, int amount) {

        ScreenBuilder builder = handler.getScreenBuilder();
        NamedScreenHandlerFactory factory = handler.getOriginFactory();

        if (factory instanceof PageableInput<?> pageable_input) {
            int current_value = pageable_input.getPage();
            int new_value = current_value + amount;
            pageable_input.setPage(new_value);

            if (this.on_change != null) {
                this.on_change.onEvent(handler, this);
            }

            handler.refresh();

            return;
        }

        WidgetDataProvider provider = handler.getWidgetDataProvider();

        if (provider != null) {
            Object value = provider.getWidgetValue(this.id);

            if (value instanceof Integer) {
                int int_value = (int) value;
                int_value += amount;
                amount = int_value;
            }

            if (amount < this.min_value) {
                amount = this.min_value;
            } else if (amount > this.max_value) {
                amount = this.max_value;
            }

            provider.setWidgetValue(this.id, amount);

            if (this.on_change != null) {
                this.on_change.onEvent(handler, this);
            }

            handler.refresh();
        }
    }

    @Override
    public void addWithValue(TextBuilder builder, Integer value) {

        if (value == null) {
            value = 0;
        }

        int x = this.previous_button.getSlotXInPixels();
        int y = this.previous_button.getSlotYInPixels();
        int y_line = this.previous_button.getFontLineNumber();

        Font font = Font.LH_INVENTORY_SLOT.getFontForLine(y_line);

        BBSB.PAGER.addToBuilder(builder, x, y + 1);

        TextGroup group = builder.createNewGroup();
        group.setColor(TextColor.fromRgb(0xc6c6c6));

        String str_value = value.toString();

        if (this.max_value > 1) {
            str_value += " / " + this.max_value;
        }

        int width = font.getWidth(str_value);
        int left = 34 - width;
        int text_x = x + 18;

        if (left > 0) {
            text_x += left / 2;
        }

        builder.setCursor(text_x);
        builder.print(str_value, font);
    }

}
