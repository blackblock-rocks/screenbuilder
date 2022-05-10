package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.slots.StaticSlot;
import rocks.blackblock.screenbuilder.text.TextBuilder;

public class StackSizePicker extends NumberPicker {

    private StaticSlot item_slot;
    private ItemStack source_stack = null;

    public StackSizePicker() {
        this(3);
    }

    protected StackSizePicker(int slot_width) {
        super(slot_width);

        this.item_slot = new StaticSlot();
        this.setSlot(1, this.item_slot);
    }

    public void setSourceStack(ItemStack stack) {
        this.source_stack = stack;
    }

    public void setSourceStack(Item item) {
        this.setSourceStack(new ItemStack(item));
    }

    @Override
    protected void adjustValue(TexturedScreenHandler handler, int amount) {

        if (this.source_stack != null) {
            ItemStack stack = this.source_stack.copy();
            stack.setCount(amount);
            this.item_slot.setStack(stack);
        }

        super.adjustValue(handler, amount);
    }

    @Override
    public void addWithValue(TextBuilder builder, Integer value) {

        if (value == null) {
            value = 0;
        }

        if (this.source_stack == null || this.source_stack.isEmpty()) {
            this.print_amount = true;
        } else {
            this.print_amount = false;
        }

        super.addWithValue(builder, value);

        if (this.source_stack != null) {
            ItemStack stack = this.source_stack.copy();
            stack.setCount(value);
            this.item_slot.setStack(stack);
        }

    }

}
