package rocks.blackblock.screenbuilder.settings;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import rocks.blackblock.bib.util.BibItem;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsRegistry {

    protected final String name;

    // All the registered settings
    public final ArrayList<Setting> settings = new ArrayList<>();

    // All the registered settings by name
    public final HashMap<String, Setting> settings_by_name = new HashMap<>();

    /**
     * Create a new settings registry
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public SettingsRegistry(String name) {
        this.name = name;
    }

    /**
     * Register a new setting
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     *
     * @param    setting
     */
    public Setting register(Setting setting) {
        this.settings.add(setting);
        this.settings_by_name.put(setting.name, setting);
        return setting;
    }

    /**
     * Get the setting attached to a stack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public Setting getSetting(ItemStack stack) {

        if (stack == null || stack.isEmpty() || !BibItem.hasCustomNbt(stack)) {
            return null;
        }

        NbtCompound nbt = BibItem.getOrCreateCustomNbt(stack);
        String name = nbt.getString("setting_name");

        return this.getSetting(name);
    }

    /**
     * Get the setting by name
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public Setting getSetting(String name) {
        if (name == null) {
            return null;
        }

        return this.settings_by_name.get(name);
    }

    /**
     * Get all the settings as itemstacks
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public ArrayList<ItemStack> getAllAsItemStacks() {

        ArrayList<ItemStack> options = new ArrayList<>();

        for (Setting setting : this.settings) {
            options.add(setting.getStack());
        }

        return options;
    }

}
