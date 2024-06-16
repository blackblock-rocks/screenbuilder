package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.item.ItemStack;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.interfaces.SelectEventListener;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.slots.MirrorWidgetSlot;
import rocks.blackblock.screenbuilder.text.MiniText;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.utils.NbtUtils;

public class MirrorWidget extends CombinedWidget<ItemStack> {

    private MirrorWidgetSlot slot = null;
    protected SelectEventListener on_change_item = null;
    protected boolean print_image = true;

    public MirrorWidget() {
        super();
        this.slot = new MirrorWidgetSlot();
        this.setSlot(0, this.slot);

        this.slot.setChangeListener((screen, stack) -> {

            WidgetDataProvider provider = screen.getWidgetDataProvider();

            if (provider != null) {
                provider.setWidgetValue(this.getId(), stack);
            }

            if (this.on_change_item != null) {
                this.on_change_item.onSelect(screen, stack);
            }
        });
    }

    /**
     * Set a change listener
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public void setChangeListener(SelectEventListener on_change_item) {
        this.on_change_item = on_change_item;
    }

    /**
     * Set the itemstack to show
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public void setStack(ItemStack stack) {
        this.slot.setStack(stack);
    }

    /**
     * Print the image?
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public void setPrintImage(boolean print_image) {
        this.print_image = print_image;
    }

    public ItemStack createPlaceholderStack() {
        ItemStack result = new ItemStack(BBSB.GUI_TRANSPARENT);

        NbtUtils.setTitle(result, new MiniText("Item placeholder"));

        NbtUtils.appendLore(result, new MiniText("Put the type of item you want"));
        NbtUtils.appendLore(result, new MiniText("to sell in this slot."));
        NbtUtils.appendLore(result, new MiniText(""));
        NbtUtils.appendLore(result, new MiniText("It will be cloned, you will not lose it."));

        return result;
    }

    @Override
    public void addWithValue(TextBuilder builder, ItemStack value) {

        if (value != null) {
            this.slot.setStack(value.copy());
        } else {
            this.slot.setStack(this.createPlaceholderStack());
        }

        if (this.print_image) {
            int x = this.slot.getSlotXInPixels();
            int y = this.slot.getSlotYInPixels();
            BBSB.MIRROR_SLOT.addToBuilder(builder, x, y);
        }
    }
}
