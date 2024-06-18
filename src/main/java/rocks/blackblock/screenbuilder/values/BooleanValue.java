package rocks.blackblock.screenbuilder.values;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.bib.util.BibItem;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.inputs.BooleanInput;

public class BooleanValue extends Value<Boolean> {

    @Override
    public Item getIcon() {
        return BBSB.GUI_BOOLEAN;
    }

    /**
     * Return the type name
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    @Override
    public String getType() {
        return "boolean";
    }

    /**
     * Get an ItemStack representation
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    @Override
    public ItemStack getStack(ItemStack result) {

        MutableText lore = (Text.literal("Value: ")).setStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY));
        MutableText value;

        if (this.getValue()) {
            value = Text.literal("True").setStyle(Style.EMPTY.withColor(Formatting.GREEN));
        } else {
            value = Text.literal("False").setStyle(Style.EMPTY.withColor(Formatting.RED));
        }

        BibItem.appendLore(result, lore.append(value));

        return result;
    }

    /**
     * Get the actual value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    @Override
    public Boolean getValue() {

        if (this.value == null) {
            return false;
        }

        return this.value;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Boolean");
    }

    /**
     * Read this value from NBT
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    @Override
    public void readFromNbt(NbtCompound nbt) {
        Boolean value = nbt.getBoolean("value");
        this.setValue(value);
    }

    /**
     * Write this value to NBT
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putBoolean("value", this.getValue());
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
        return this.getValue();
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
        if (this.getValue()) {
            return 1.0D;
        } else {
            return 0.0D;
        }
    }

    /**
     * Create the edit screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {

        BooleanValue that = this;

        BooleanInput input = new BooleanInput();
        input.setValue(this.getValue());
        ScreenBuilder builder = input.getScreenBuilder();

        input.setChangeListener((screen, new_value) -> {
            that.setValue(new_value);
        });

        return builder.createScreenHandler(syncId, inv);
    }
}
