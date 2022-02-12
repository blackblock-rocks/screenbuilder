package rocks.blackblock.screenbuilder.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

public abstract class WidgetSlot extends StaticSlot {

    /**
     * WidgetSlot constructor
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public WidgetSlot(ItemStack stack) {
        super(stack);
    }

    /**
     * WidgetSlot constructor
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public WidgetSlot() {
        this(new ItemStack(Items.STRUCTURE_VOID));
    }

    /**
     * WidgetSlot constructor
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public WidgetSlot(Inventory inventory, Integer index) {
        super(inventory, index);
    }

    /**
     * Called when the user left-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public abstract void onLeftClick();

    /**
     * Called when the user right-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public abstract void onRightClick();

    /**
     * Called when the user middle-clicks the slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public abstract void onMiddleClick();

    /**
     * Handle click
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public WidgetSlot handleSlotClick(int button, SlotActionType action_type, PlayerEntity player) {

        // button 0 = left, 1 = right, 2 = middle

        if (button == 0) { // Left click
            this.onLeftClick();
        } else if (button == 1) { // Right click
            this.onRightClick();
        } else if (button == 2) { // Middle click
            this.onMiddleClick();
        }

        GuiUtils.resyncPlayerInventory(player);

        return this;
    }

    /**
     * Trigger leftClick when taking a stack
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public ItemStack takeStack(int amount) {
        System.out.println("Huh? TakeStack shouldn't be called");
        return ItemStack.EMPTY;
    }
}