package rocks.blackblock.screenbuilder.inventories;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public interface BaseInventory extends Inventory, Iterable<ItemStack> {

    /**
     * Method that gets the contents
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    DefaultedList<ItemStack> getContents();

    /**
     * Method that sets the contents
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    void setContents(DefaultedList<ItemStack> contents);

    /**
     * This method should return the BlockItem you want to use in case
     * the inventory data should be written to a dropped item
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default BlockItem getDroppedItem() {
        return null;
    }

    /**
     * Get an ItemStack representation of this block
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default ItemStack getItemStack() {

        BlockItem item = this.getDroppedItem();

        if (item == null) {
            return null;
        }

        ItemStack stack = new ItemStack(item);

        if (!this.isEmpty()) {
            NbtCompound nbt = this.writeInventoryToNbt(new NbtCompound());

            if (!nbt.isEmpty()) {
                stack.setSubNbt("BlockEntityTag", nbt);
            }
        }

        if (this instanceof Nameable named) {
            if (named.hasCustomName()) {
                stack.setCustomName(named.getCustomName());
            }
        }

        return stack;
    }

    /**
     * Is this inventory empty?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default boolean isEmpty() {
        Iterator<ItemStack> var1 = this.getContents().iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = var1.next();
        } while(itemStack.isEmpty());

        return false;
    }

    /**
     * Count how many stacks are in this inventory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default int countStacks() {

        int result = 0;

        for (ItemStack stack : this.getContents()) {
            if (!stack.isEmpty()) {
                result++;
            }
        }

        return result;
    }

    /**
     * Get the ItemStack at the given slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default ItemStack getStack(int slot) {
        return this.getContents().get(slot);
    }

    /**
     * Remove a certain amount of ItemStack at the given slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.1
     */
    default ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(this.getContents(), slot, amount);
        this.onStackRemoved(slot, result);
        this.fireContentChangedEvents();
        return result;
    }

    /**
     * Remove the entire ItemStack at the given slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.1
     */
    default ItemStack removeStack(int slot) {
        ItemStack result = Inventories.removeStack(this.getContents(), slot);
        this.onStackRemoved(slot, result);
        this.fireContentChangedEvents();
        return result;
    }

    /**
     * Called after an itemstack has been removed from a slot (through a `removeStack` call)
     * but called before contentChangeEvents are fired
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    default void onStackRemoved(int slot, ItemStack removed_stack) {
        // NOOP
    }

    /**
     * Set the ItemStack at the given slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default void setStack(int slot, ItemStack stack) {
        this.getContents().set(slot, stack);
        this.fireContentChangedEvents();
    }

    /**
     * Set the contents from nbt data
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default void setContentsFromNbt(NbtCompound nbt) {
        DefaultedList<ItemStack> contents = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, contents);
        this.setContents(contents);
    }

    /**
     * Write the contents to nbt data
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default NbtCompound writeInventoryToNbt(NbtCompound nbt) {

        if (this.getContents() == null) {
            return nbt;
        }

        Inventories.writeNbt(nbt, this.getContents());
        return nbt;
    }

    /**
     * Clear the entire inventory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default void clear() {
        this.getContents().clear();
        this.fireContentChangedEvents();
    }

    /**
     * Mark the inventory as dirty
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default void markDirty() {

        if (this.getContents() == null) {
            return;
        }

        this.contentsChanged();
    }

    /**
     * Mark the inventory as dirty
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default void fireContentChangedEvents() {

        if (this.getContents() == null) {
            return;
        }

        List<InventoryChangedListener> listeners = this.getListeners();

        if (listeners != null) {
            for (InventoryChangedListener listener : listeners) {
                listener.onInventoryChanged(this);
            }
        }

        this.contentsChanged();
    }

    default boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    /**
     * Create the contents iterator
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    @NotNull
    @Override
    default Iterator<ItemStack> iterator() {
        return this.getContents().iterator();
    }

    /**
     * Get the listeners
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default List<InventoryChangedListener> getListeners() {
        return null;
    }

    /**
     * Set the listeners
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default void setListeners(List<InventoryChangedListener> listeners) {}

    /**
     * Add a listener
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default void addListener(InventoryChangedListener listener) {

        List<InventoryChangedListener> listeners = this.getListeners();

        if (listeners != null) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a listener
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    default void removeListener(InventoryChangedListener listener) {

        List<InventoryChangedListener> listeners = this.getListeners();

        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Register a player interacting with this inventory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.2
     */
    default void openedByPlayer(PlayerEntity player) {
        // NOOP
    }

    /**
     * Unregister a player interacting with this inventory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.2
     */
    default void closedByPlayer(PlayerEntity player) {
        // NOOP
    }

    void contentsChanged();
}