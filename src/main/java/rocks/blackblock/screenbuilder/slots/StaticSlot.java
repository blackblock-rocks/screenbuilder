package rocks.blackblock.screenbuilder.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import rocks.blackblock.bib.util.BibInventory;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

public class StaticSlot extends BaseSlot {

    // Use the stack in the mapped inventory?
    protected Boolean use_inventory_stack = null;

    // The dummy inventory
    protected BibInventory.Dummy dummy_inventory = null;

    /**
     * Create a new StaticSlot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public StaticSlot(ItemStack stack) {
        super(new BibInventory.Dummy(1), 0);

        // Immediately get the inventory, before it can change
        this.dummy_inventory = (BibInventory.Dummy) this.getInventory();

        if (stack != null) {
            this.setDummyStack(stack);
        }

        // Do not use the inventory by default
        if (this.use_inventory_stack == null) {
            this.setUseRealInventory(false);
        }
    }

    /**
     * Create a new StaticSlot that uses a real inventory
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public StaticSlot(Inventory inventory, int index) {
        super(inventory, index);
        this.setUseRealInventory(true);
    }

    /**
     * Create a new StaticSlot, but without any stack pre-set
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public StaticSlot() {
        this(null);
    }

    public void onQuickTransfer(ItemStack originalItem, ItemStack itemStack) {
        throw new AssertionError("The contents of a static, unchangeable slot were changed. Containing: " + this.getStack().toString());
    }

    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        throw new AssertionError("Tried to take item out of an static, unchangeable slot. Containing: " + stack.toString());
    }

    /**
     * Set the dummy stack
     *
     * @deprecated
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public void setDummyStack(ItemStack stack) {
        // The dummy stack is always at slot 0 in the dummy inventory
        this.dummy_inventory.setStack(0, stack);
    }

    /**
     * It should never be possible to take something out of a StaticSlot
     *
     * @deprecated
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public ItemStack takeStack(int amount) {
        return ItemStack.EMPTY;
    }

    /**
     * It should never be possible to take something out of a StaticSlot
     *
     * @deprecated
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public boolean canTakeItems(PlayerEntity playerEntity) {
        GuiUtils.resyncPlayerInventory(playerEntity);
        return false;
    }

    /**
     * Players should not be able to insert anything in a StaticSlot
     *
     * @deprecated
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    public int getMaxItemCount() {
        return this.getStack().getCount();
    }

    public int getMaxItemCount(ItemStack stack) {
        return this.getMaxItemCount();
    }

    /**
     * Set the inventory index to use
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    @Override
    public StaticSlot mapInventory(int inventory_index) {
        super.mapInventory(inventory_index);

        // Mapping to a real inventory index automatically makes it use the inventory
        this.setUseRealInventory(true);

        return this;
    }

    /**
     * Copy over properties to the given slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    slot   The target slot
     */
    public void copyPropertiesToSlot(StaticSlot slot) {
        super.copyPropertiesToSlot(slot);
        slot.use_inventory_stack = this.use_inventory_stack;

        if (this.shouldUseInventory()) {
            return;
        }

        ItemStack stack = this.getStack();

        if (stack != null) {
            slot.setStack(stack.copy());
        }
    }

    /**
     * Clone this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public StaticSlot clone(Inventory inventory, Integer index) {

        ItemStack stack = null;

        if (!this.shouldUseInventory()) {
            stack = this.getStack();

            if (stack != null) {
                stack = stack.copy();
            }
        }

        StaticSlot slot;

        if (inventory == null) {
            slot = new StaticSlot(stack);
        } else {
            slot = new StaticSlot(inventory, index);
        }

        this.copyPropertiesToSlot(slot);

        return slot;
    }
}