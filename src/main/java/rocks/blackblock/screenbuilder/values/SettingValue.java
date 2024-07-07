package rocks.blackblock.screenbuilder.values;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.bib.util.BibItem;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.settings.Setting;

import java.util.ArrayList;

public class SettingValue extends Value {

    // The setting this is for
    public final Setting setting;

    // The type of value
    public final Value value;

    /**
     * Value constructor
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     *
     * @param    setting
     */
    public SettingValue(Setting setting, Value value) {
        this.setting = setting;
        this.value = value;
    }

    /**
     * Get the item representation from the Setting
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public Item getIcon() {
        return null;
    }

    /**
     * Return the type of this value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    @Override
    public String getType() {
        return this.value.getType();
    }

    /**
     * Get an ItemStack representation
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public ItemStack getStack() {
        return this.getStack(this.setting.getStack());
    }

    /**
     * Get an ItemStack representation
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public ItemStack getStack(ItemStack result) {
        return this.value.getStack(result);
    }

    /**
     * Set the value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public void setValue(Object value) {
        this.value.setValue(value);

        if (this.on_change != null) {
            this.on_change.run();
        }
    };

    /**
     * Get the actual value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public Object getValue() {
        return this.value.getValue();
    }

    /**
     * Get the display name to use on the edit screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    @Override
    public Text getDisplayName() {
        return Text.literal(this.setting.getTitle());
    }

    /**
     * Read this value from NBT
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public void readFromNbt(NbtCompound nbt) {
        this.value.readFromNbt(nbt);
    }

    /**
     * Write this value to NBT
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public void writeToNbt(NbtCompound nbt) {
        this.value.writeToNbt(nbt);
    }

    /**
     * Is this value truthy?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    @Override
    public Boolean isTruthy() {
        return this.value.isTruthy();
    }

    /**
     * Get the number value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    @Override
    public Double getNumber() {
        return this.value.getNumber();
    }

    /**
     * Get all the values as itemstacks
     *
     * @deprecated
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public static ArrayList<ItemStack> getAllAsItemStacks() {

        ArrayList<ItemStack> options = new ArrayList<>();

        NbtCompound nbt;

        ItemStack number = new ItemStack(BBSB.GUI_NUMBER);
        BibItem.setCustomName(number, Text.literal("Number").setStyle(Style.EMPTY.withItalic(false)));
        nbt = BibItem.getOrCreateCustomNbt(number);
        nbt.putString("type", "number");
        BibItem.setCustomNbt(number, nbt);

        options.add(number);

        return options;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {

        this.value.setOnChange(() -> {
            if (this.on_change != null) {
                this.on_change.run();
            }
        });

        return this.value.createMenu(syncId, inv, player);
    }
}