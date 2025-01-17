package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import rocks.blackblock.bib.util.BibItem;
import rocks.blackblock.bib.util.BibLog;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.interfaces.SelectEventListener;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.slots.MirrorWidgetSlot;
import rocks.blackblock.screenbuilder.text.MiniText;
import rocks.blackblock.screenbuilder.text.TextBuilder;

public class MirrorWidget extends CombinedWidget<ItemStack> {

    private MirrorWidgetSlot slot = null;
    protected SelectEventListener on_change_item = null;
    protected boolean print_image = true;

    public MirrorWidget(String id) {
        super();
        this.setId(id);
        this.slot = new MirrorWidgetSlot();
        this.setSlot(0, this.slot);

        this.slot.setChangeListener((screen, stack) -> {

            WidgetDataProvider provider = screen.getWidgetDataProvider();

            if (provider != null) {
                provider.setWidgetValue(this, stack);
            }

            if (this.on_change_item != null) {
                this.on_change_item.onSelect(screen, stack);
            }
        });
    }

    public MirrorWidget() {
        this(null);
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

        BibItem.setCustomName(result, new MiniText("Item placeholder"));

        BibItem.appendLore(result, new MiniText("Put the type of item you want"));
        BibItem.appendLore(result, new MiniText("to sell in this slot."));
        BibItem.appendLore(result, new MiniText(""));
        BibItem.appendLore(result, new MiniText("It will be cloned, you will not lose it."));

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

    /**
     * Append to a BibLog.Arg representation
     *
     * @since 0.5.0
     */
    @Override
    public void appendToBibLogArg(@NotNull BibLog.Arg arg) {
        arg.add("slot", this.slot);
    }
}
