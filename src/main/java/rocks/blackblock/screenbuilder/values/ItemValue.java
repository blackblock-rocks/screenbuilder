package rocks.blackblock.screenbuilder.values;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.inputs.ItemInput;
import rocks.blackblock.screenbuilder.utils.NbtUtils;

public class ItemValue extends Value {

    protected Boolean check_damage = null;
    protected Boolean check_name = null;
    protected Boolean check_nbt = null;
    protected NbtCompound boolean_data = null;

    /**
     * Get the actual value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    @Override
    public ItemStack getValue() {

        if (this.value == null) {
            return null;
        }

        return (ItemStack) this.value;
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

        if (!(other instanceof ItemValue other_value)) {
            return false;
        }

        ItemStack this_stack = this.getValue();
        ItemStack other_stack = other_value.getValue();

        if (this_stack == null || other_stack == null) {
            return other_stack == this_stack;
        }

        Item this_item = this_stack.getItem();
        Item other_item = other_stack.getItem();

        // The item should always be the same
        if (this_item != other_item) {
            return false;
        }

        if (this.check_damage == Boolean.TRUE || other_value.check_damage == Boolean.TRUE) {
            if (this_stack.getDamage() != other_stack.getDamage()) {
                return false;
            }
        }

        if (this.check_name == Boolean.TRUE || other_value.check_name == Boolean.TRUE) {
            String this_name = this_stack.getName().getString();
            String other_name = other_stack.getName().getString();

            if (!this_name.equals(other_name)) {
                return false;
            }
        }

        if (!(this.check_nbt == Boolean.TRUE || other_value.check_nbt == Boolean.TRUE)) {
            return true;
        }

        NbtCompound this_nbt = this_stack.getNbt();
        NbtCompound other_nbt = other_stack.getNbt();

        if (this_nbt != null && this_nbt.isEmpty()) {
            this_nbt = null;
        }

        if (other_nbt != null && other_nbt.isEmpty()) {
            other_nbt = null;
        }

        if (this_nbt == null || other_nbt == null) {
            return this_nbt == other_nbt;
        }

        this_nbt = this_nbt.copy();
        other_nbt = other_nbt.copy();

        this_nbt.remove("Damage");
        other_nbt.remove("Damage");

        this_nbt.remove("display");
        other_nbt.remove("display");

        return this_nbt.equals(other_nbt);
    }

    @Override
    public Item getIcon() {
        return Items.ITEM_FRAME;
    }

    @Override
    public String getType() {
        return "item";
    }

    @Override
    public ItemStack getStack(ItemStack result) {

        MutableText lore = (Text.literal("Value: ")).setStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY));
        MutableText value;

        ItemStack stack = this.getValue();

        if (stack == null || stack.isEmpty()) {
            value = Text.literal("empty").setStyle(Style.EMPTY.withColor(Formatting.RED).withItalic(true));
        } else {
            value = Text.literal("").append(stack.getName()).setStyle(Style.EMPTY.withColor(Formatting.RED));
        }

        NbtUtils.appendLore(result, lore.append(value));

        return result;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Item");
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {

        if (nbt.contains("stack")) {
            ItemStack stack = ItemStack.fromNbt(nbt.getCompound("stack"));
            this.setValue(stack);
        }

        if (nbt.contains("boolean_data")) {
            this.setBooleanData(nbt.getCompound("boolean_data"));
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        ItemStack stack = this.getValue();

        if (stack != null) {
            NbtCompound stack_nbt = new NbtCompound();
            stack.writeNbt(stack_nbt);
            nbt.put("stack", stack_nbt);
        }

        NbtCompound boolean_data = this.getBooleanData();

        if (boolean_data != null && !boolean_data.isEmpty()) {
            nbt.put("boolean_data", boolean_data);
        }
    }

    @Override
    public Boolean isTruthy() {

        ItemStack stack = this.getValue();

        if (stack == null || stack.isEmpty()) {
            return false;
        }

        return true;
    }

    public NbtCompound getBooleanData() {

        if (this.boolean_data != null) {
            return this.boolean_data;
        }

        NbtCompound data = new NbtCompound();

        if (this.check_damage != null) {
            data.putBoolean("damage", this.check_damage);
        }

        if (this.check_name != null) {
            data.putBoolean("name", this.check_name);
        }

        if (this.check_nbt != null) {
            data.putBoolean("nbt", this.check_nbt);
        }

        this.boolean_data = data;

        return data;
    }


    public void setBooleanData(NbtCompound data) {

        this.boolean_data = data;

        this.check_damage = null;
        this.check_name = null;
        this.check_nbt = null;

        if (data == null) {
            return;
        }

        if (data.contains("damage")) {
            this.check_damage = data.getBoolean("damage");
        }

        if (data.contains("name")) {
            this.check_name = data.getBoolean("name");
        }

        if (data.contains("nbt")) {
            this.check_nbt = data.getBoolean("nbt");
        }
    }

    @Override
    public Double getNumber() {
        return null;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {

        ItemValue that = this;

        ItemInput input = new ItemInput();
        input.showAcceptButton(53);
        input.showBackButton(45);

        ItemStack current_stack = this.getValue();

        if (current_stack != null) {
            input.setValue(current_stack);
        }

        input.setBooleanValues(this.getBooleanData());

        ScreenBuilder builder = input.getScreenBuilder();

        input.setAcceptListener((screen, input1) -> {

            that.setValue(input.getValue());
            that.setBooleanData(input.getBooleanValues());

            screen.showPreviousScreen();
        });

        return builder.createScreenHandler(syncId, inv);
    }
}