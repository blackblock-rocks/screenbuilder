package rocks.blackblock.screenbuilder.values;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ObjectValue extends Value<Object> {
    @Override
    public Item getIcon() {
        return Items.BARREL;
    }

    @Override
    public String getType() {
        return "object";
    }

    @Override
    public ItemStack getStack(ItemStack result) {
        return result;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Object");
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        // Objects are not meant to be stored
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        // Objects are not meant to be stored
    }

    @Override
    public Boolean isTruthy() {
        return this.getValue() != null;
    }

    @Override
    public Double getNumber() {
        return null;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return null;
    }
}
