package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import rocks.blackblock.bib.util.BibItem;
import rocks.blackblock.bib.util.BibLog;
import rocks.blackblock.bib.util.BibText;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.interfaces.SelectEventListener;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.slots.MirrorWidgetSlot;
import rocks.blackblock.screenbuilder.text.MiniText;
import rocks.blackblock.screenbuilder.text.TextBuilder;

import java.util.ArrayList;
import java.util.List;

public class MirrorWidget extends CombinedWidget<ItemStack> {

    private MirrorWidgetSlot slot = null;
    protected SelectEventListener on_change_item = null;
    protected boolean print_image = true;
    protected Text title = null;
    protected List<Text> lore = null;

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
     * Set the title of this button
     * @since    0.7.1
     */
    public MirrorWidget setTitle(Text title) {
        this.title = title;
        return this;
    }

    /**
     * Set the lore of this button
     * @since    0.7.1
     */
    public MirrorWidget setLore(Text lore) {
        this.lore = new ArrayList<>();
        this.lore.add(lore);
        return this;
    }

    /**
     * Set the lore of this button
     * @since    0.7.1
     */
    public MirrorWidget setLore(BibText.Lore lore) {
        this.lore = lore.getLines();
        return this;
    }

    /**
     * Set the lore of this button
     * @since    0.7.1
     */
    public MirrorWidget setLore(List<Text> lore) {
        this.lore = lore;
        return this;
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

        if (this.title != null) {
            BibItem.setCustomName(result, this.title);
        } else {
            BibItem.setCustomName(result, new MiniText("Item placeholder"));
        }

        if (this.lore != null) {
            BibItem.replaceLore(result, this.lore);
        } else {
            BibItem.appendLore(result, new MiniText("The item you place here will be cloned"));
            BibItem.appendLore(result, new MiniText("You will not lose it"));
        }

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
