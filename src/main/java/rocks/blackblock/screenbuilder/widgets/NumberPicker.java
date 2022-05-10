package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.text.TextGroup;

public class NumberPicker extends CombinedWidget<Integer> {

    protected ButtonWidgetSlot plus_button;
    protected ButtonWidgetSlot minus_button;
    protected int min_value = 0;
    protected int max_value = 64;
    protected boolean print_amount = true;

    public NumberPicker() {
        this(3);
    }

    protected NumberPicker(int slot_width) {
        this.slot_width = slot_width;

        this.plus_button = new ButtonWidgetSlot();
        this.plus_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.EXTRA_SMALL);
        this.plus_button.setTitle("Add");

        this.minus_button = new ButtonWidgetSlot();
        this.minus_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.EXTRA_SMALL);
        this.minus_button.setTitle("Subtract");

        this.setSlot(0, this.minus_button);
        this.setSlot(slot_width - 1, this.plus_button);

        this.plus_button.addLeftClickListener((screen, slot) -> {
            this.adjustValue(screen,1);
        });

        this.minus_button.addLeftClickListener((screen, slot) -> {
            this.adjustValue(screen, -1);
        });
    }

    public void setMinValue(int min_value) {
        this.min_value = min_value;
    }

    public void setMaxValue(int max_value) {
        this.max_value = max_value;
    }

    protected void adjustValue(TexturedScreenHandler handler, int amount) {

        ScreenBuilder builder = handler.getScreenBuilder();
        NamedScreenHandlerFactory factory = handler.getOriginFactory();
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

            handler.refresh();
        }
    }

    @Override
    public void addWithValue(TextBuilder builder, Integer value) {

        if (value == null) {
            value = 0;
        }

        int x = this.minus_button.getSlotXInPixels();
        int y = this.minus_button.getSlotY();

        Font font = Font.LH_INVENTORY_SLOT.getFontForLine(y);

        builder.setCursor(x + 6);
        TextGroup group = builder.createNewGroup();
        group.setColor(TextColor.fromRgb(0x878787));
        builder.print("-", font);

        // Move to the middle slot
        x += 19;

        builder.setCursor(x + 19 + 5);
        builder.print("+", font);

        if (this.print_amount) {

            group = builder.createNewGroup();
            group.setColor(TextColor.fromRgb(0x101010));

            String str_value = value.toString();

            int width = font.getWidth(str_value);
            int left = 16 - width;

            if (left > 0) {
                x += left / 2;
            }

            builder.setCursor(x);
            builder.print(value.toString(), font);
        }
    }
}
