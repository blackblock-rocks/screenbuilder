package rocks.blackblock.screenbuilder.inventories;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Act as an inventory proxy for another inventory
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.4.0
 */
@SuppressWarnings("unused")
public interface InventoryProxy extends Inventory {

    /**
     * Get the proxied inventory
     *
     * @since 0.4.0
     */
    Inventory getProxiedInventory();

    /**
     * Get the size of the inventory
     *
     * @since 0.4.0
     */
    @Override
    default int size() {
        return this.getProxiedInventory().size();
    }

    /**
     * Is this inventory empty?
     *
     * @since 0.4.0
     */
    @Override
    default boolean isEmpty() {
        return this.getProxiedInventory().isEmpty();
    }

    /**
     * Get the stack at the given index
     *
     * @since 0.4.0
     */
    @Override
    default ItemStack getStack(int slot) {
        return this.getProxiedInventory().getStack(slot);
    }

    /**
     * Remove the stack at the given index
     *
     * @since 0.4.0
     */
    @Override
    default ItemStack removeStack(int slot) {
        return this.getProxiedInventory().removeStack(slot);
    }

    /**
     * Remove the amount of the stack at the given index
     *
     * @since 0.4.0
     */
    @Override
    default ItemStack removeStack(int slot, int amount) {
        return this.getProxiedInventory().removeStack(slot, amount);
    }

    /**
     * Set the stack at the given index
     *
     * @since 0.4.0
     */
    @Override
    default void setStack(int slot, ItemStack stack) {
        this.getProxiedInventory().setStack(slot, stack);
    }

    /**
     * Get the maximum count per stack
     *
     * @since 0.4.0
     */
    @Override
    default int getMaxCountPerStack() {
        return this.getProxiedInventory().getMaxCountPerStack();
    }

    /**
     * Mark the inventory as dirty
     *
     * @since 0.4.0
     */
    @Override
    default void markDirty() {
        this.getProxiedInventory().markDirty();
    }

    /**
     * See if this inventory can be used by the given player
     *
     * @since 0.4.0
     */
    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return this.getProxiedInventory().canPlayerUse(player);
    }

    /**
     * Do something when the player opens this inventory
     *
     * @since 0.4.0
     */
    @Override
    default void onOpen(PlayerEntity player) {
        this.getProxiedInventory().onOpen(player);
    }

    /**
     * Do something when the player closes this inventory
     *
     * @since 0.4.0
     */
    @Override
    default void onClose(PlayerEntity player) {
        this.getProxiedInventory().onClose(player);
    }

    /**
     * See if the given stack is a valid stack for the given slot
     *
     * @since 0.4.0
     */
    @Override
    default boolean isValid(int slot, ItemStack stack) {
        return this.getProxiedInventory().isValid(slot, stack);
    }

    /**
     * Can the stack in the slot be transferred?
     *
     * @since 0.4.0
     */
    @Override
    default boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return this.getProxiedInventory().canTransferTo(hopperInventory, slot, stack);
    }

    /**
     * Count the amount of items in the inventory
     *
     * @since 0.4.0
     */
    @Override
    default int count(Item item) {
        return this.getProxiedInventory().count(item);
    }

    /**
     * See if this inventory contains any of the given item
     *
     * @since 0.4.0
     */
    @Override
    default boolean containsAny(Set<Item> items) {
        return this.getProxiedInventory().containsAny(items);
    }

    /**
     * See if this inventory contains any of the given item
     *
     * @since 0.4.0
     */
    @Override
    default boolean containsAny(Predicate<ItemStack> predicate) {
        return this.getProxiedInventory().containsAny(predicate);
    }

    /**
     * Clear the inventory
     *
     * @since 0.4.0
     */
    @Override
    default void clear() {
        this.getProxiedInventory().clear();
    }
}
