package rocks.blackblock.screenbuilder.values;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;

public abstract class Value implements NamedScreenHandlerFactory {

    protected Object value;

    /**
     * Get an Item representation
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public abstract Item getIcon();

    /**
     * Get the type name
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public abstract String getType();

    /**
     * Get the title of this value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public String getTitle() {
        String name = this.getClass().getSimpleName();
        int index = name.indexOf("Value");

        return name.substring(0, index);
    }

    /**
     * Get an ItemStack representation
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public ItemStack getStack() {

        // Create a new stack with the icon
        ItemStack stack = new ItemStack(this.getIcon());

        // Create the nbt data
        NbtCompound nbt = stack.getOrCreateNbt();

        // Set the (default) title
        stack.setCustomName(Text.literal(this.getTitle()).setStyle(Style.EMPTY.withItalic(false)));

        // Put the class type
        nbt.putString("bclass", "value");

        // Put the type name
        nbt.putString("type", this.getType());

        // Put the nbt back
        stack.setNbt(nbt);

        // Write the current value to NBT
        this.writeToNbt(nbt);

        return this.getStack(stack);
    }

    /**
     * Does this value equal the given one?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     *
     * @param    other   The other value to compare against
     */
    public Boolean equals(Value other) {
        return this.getValue().equals(other.getValue());
    }

    /**
     * Get an ItemStack representation
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public abstract ItemStack getStack(ItemStack result);

    /**
     * Set the value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public void setValue(Object value) {
        this.value = value;
    };

    /**
     * Get the actual value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Get the display name to use on the edit screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    @Override
    public abstract Text getDisplayName();

    /**
     * Read this value from NBT
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public abstract void readFromNbt(NbtCompound nbt);

    /**
     * Write this value to NBT
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public abstract void writeToNbt(NbtCompound nbt);

    /**
     * Cast this to a boolean
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public abstract Boolean isTruthy();

    /**
     * Get the numeric representation of this value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public abstract Double getNumber();

    /**
     * Get all the base values as itemstacks
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public static ArrayList<ItemStack> getAllAsItemStacks() {

        ArrayList<ItemStack> options = new ArrayList<>();

        NbtCompound nbt;

        ItemStack number = (new NumberValue()).getStack();
        options.add(number);

        ItemStack boolean_stack = (new BooleanValue()).getStack();
        options.add(boolean_stack);

        options.add(new StringValue().getStack());
        options.add(new ItemValue().getStack());

        return options;
    }

    /**
     * Get a value instance from a stack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    public static Value getFromStack(ItemStack stack) {

        if (stack == null || stack.isEmpty()) {
            return null;
        }

        NbtCompound nbt = stack.getNbt();

        if (nbt == null) {
            return null;
        }

        String class_name = nbt.getString("bclass");

        if (!class_name.equals("value")) {
            return null;
        }

        String type = nbt.getString("type");

        Value result = null;

        if (type.equals("number")) {
            result = new NumberValue();
        } else if (type.equals("boolean")) {
            result = new BooleanValue();
        } else if (type.equals("item")) {
            result = new ItemValue();
        } else if (type.equals("string")) {
            result = new StringValue();
        }

        if (result != null) {
            result.readFromNbt(nbt);
        }

        return result;
    }
}