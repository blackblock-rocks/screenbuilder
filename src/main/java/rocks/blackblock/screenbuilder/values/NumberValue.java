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
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.inputs.BaseInput;
import rocks.blackblock.screenbuilder.inputs.StringInput;

public class NumberValue extends Value<Double> {

    /**
     * Get an Item representation
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    @Override
    public Item getIcon() {
        return BBSB.GUI_NUMBER;
    }

    /**
     * Return the type name
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    @Override
    public String getType() {
        return "number";
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
        MutableText value = Text.literal(this.getValue().toString()).setStyle(Style.EMPTY.withColor(Formatting.GREEN));

        BibItem.appendLore(result, lore.append(value));

        return result;
    }

    /**
     * Get the actual value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    @Override
    public Double getValue() {

        if (this.value == null) {
            return 0D;
        }

        return this.value;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Number");
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
        Double value = nbt.getDouble("value");
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
        nbt.putDouble("value", this.getValue());
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
        return this.getValue() != 0D;
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
        return this.getValue();
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

        NumberValue that = this;

        StringInput input = new StringInput();

        ScreenBuilder builder = input.getScreenBuilder();

        input.setRenamedListener((screen, new_value) -> {
            Double number = Double.parseDouble(new_value);
            that.setValue(number);
        });

        input.setChangeBehaviour(BaseInput.ChangeBehaviour.SHOW_PREVIOUS_SCREEN);

        TexturedScreenHandler handler = builder.createScreenHandler(syncId, inv);
        handler.setOriginFactory(input);

        return handler;
    }

}