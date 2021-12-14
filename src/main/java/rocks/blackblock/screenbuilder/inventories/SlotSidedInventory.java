package rocks.blackblock.screenbuilder.inventories;

import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.slots.SlotBuilder;

public interface SlotSidedInventory extends SidedInventory {

    /**
     * Get the SlotBuilder definition that can be used for the given inventory slot.
     * If one is returned, the allow/deny/max stack counts of that slotbuilder will be used
     *
     * @param slot   The index of the inventory slot
     *
     * @return       The SlotBuilder instance to use
     */
    SlotBuilder getInventorySlotBuilderDefinition(int slot, Direction dir);

    /**
     * Determines whether the given stack can be inserted into this inventory at the specified slot position from the given direction.
     */
    default boolean canInsert(int inventory_index, ItemStack stack, @Nullable Direction dir) {

        SlotBuilder slot = this.getInventorySlotBuilderDefinition(inventory_index, dir);

        if (slot == null) {
            return false;
        }

        return slot.canInsert(stack);
    }

    /**
     * Determines whether the given stack can be removed from this inventory at the specified slot position from the given direction.
     */
    default boolean canExtract(int inventory_index, ItemStack stack, Direction dir) {

        SlotBuilder slot = this.getInventorySlotBuilderDefinition(inventory_index, dir);

        if (slot == null) {
            return false;
        }

        return true;
    }
}
