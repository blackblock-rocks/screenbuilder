package rocks.blackblock.screenbuilder.inventories;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class ItemInventory implements BaseInventory {

    protected DefaultedList<ItemStack> contents;
    private PlayerEntity player;
    private ItemStack stack;
    private int size;

    /**
     * Create a new inventory with the given stack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public ItemInventory(PlayerEntity player, ItemStack stack, int size) {
        this.player = player;
        this.stack = stack;
        this.size = size;

        NbtCompound nbt = stack.getOrCreateNbt();
        NbtCompound item_inventory = nbt.getCompound("ItemInventory");

        if (item_inventory == null) {
            item_inventory = new NbtCompound();
            nbt.put("ItemInventory", item_inventory);
        }

        this.setContentsFromNbt(item_inventory);

        //this.contents = DefaultedList.ofSize(size, ItemStack.EMPTY);
        //Inventories.readNbt(item_inventory, this.contents);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public DefaultedList<ItemStack> getContents() {
        return this.contents;
    }

    /**
     * Set the contents
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    @Override
    public void setContents(DefaultedList<ItemStack> contents) {
        this.contents = contents;
    }

    /**
     * Trigger a write to the ItemStack on any changes
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public void contentsChanged() {
        this.writeToItemStack();
    }

    /**
     * Write the NBT data to the itemstack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public void writeToItemStack() {
        NbtCompound nbt = this.stack.getOrCreateNbt();
        NbtCompound item_inventory = nbt.getCompound("ItemInventory");

        if (item_inventory == null) {
            item_inventory = new NbtCompound();
        }

        Inventories.writeNbt(item_inventory, this.contents);

        nbt.put("ItemInventory", item_inventory);
    }
}