package rocks.blackblock.screenbuilder.settings;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import rocks.blackblock.bib.util.BibItem;
import rocks.blackblock.screenbuilder.values.SettingValue;

public abstract class Setting {

    // The name of the setting
    protected final String name;

    // The title of the setting
    protected String title = null;

    // The item to use as the icon
    public Item icon = null;

    /**
     * Setting constructor
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     *
     * @param    name
     */
    public Setting(String name) {
        this.name = name;
    }

    /**
     * Get the name of this setting
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the title to use
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     *
     * @param    title
     */
    public Setting setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Get the title to use
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public String getTitle() {

        if (this.title != null) {
            return this.title;
        }

        return this.name;
    }

    /**
     * Set the icon to use
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     *
     * @param    item
     */
    public Setting setIcon(Item item) {
        this.icon = item;
        return this;
    }

    /**
     * Create an empty value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public abstract SettingValue createValue();

    /**
     * Create a value from Nbt data
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public SettingValue createValue(NbtCompound nbt) {
        SettingValue value = this.createValue();
        value.readFromNbt(nbt);
        return value;
    }

    /**
     * Get an ItemStack representation
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public ItemStack getStack() {

        ItemStack result = new ItemStack(this.icon);
        String title = this.title;

        if (title == null) {
            title = this.name;
        }

        BibItem.setCustomName(result, (Text.literal(title)).setStyle(Style.EMPTY.withItalic(false)));

        NbtCompound nbt = BibItem.getCustomNbt(result);
        nbt.putString("setting_name", this.name);

        return result;
    }
}
