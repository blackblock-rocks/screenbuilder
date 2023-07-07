package rocks.blackblock.screenbuilder.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.interfaces.GuiListener;
import rocks.blackblock.screenbuilder.interfaces.SlotAccessListener;
import rocks.blackblock.screenbuilder.interfaces.SlotStackChecker;
import rocks.blackblock.screenbuilder.interfaces.SlotWrapperListener;
import rocks.blackblock.screenbuilder.inventories.EmptyInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SlotBuilder extends Slot implements Cloneable {

    // The optional forced inventory to use
    private Inventory forced_inventory = null;

    // The index in the inventory this slot is mapped to
    private Integer inventory_index = null;

    // The index in the screen this slot takes up
    private Integer screen_index = null;

    // Items that are allowed in this slot
    public ArrayList<Item> allowed_items = null;

    // Item classes that are allowed in this slot
    public ArrayList<Class<?>> allowed_item_classes = null;

    // Items that are forbidden in this slot
    public ArrayList<Item> forbidden_items = null;

    // Item classes that are forbidden in this slot
    public ArrayList<Class<?>> forbidden_item_classes = null;

    // If this slot has any Item or Item classes that are allowed
    private boolean has_allowed_items = false;

    // If this slot has any Item or Item classes that are forbidden
    private boolean has_forbidden_items = false;

    // If the slot has a specific maximum stack size
    private Integer max_stack_size = null;

    // Is this slot an input?
    private boolean is_input = false;

    // Is this slot an output?
    private boolean is_output = false;

    // Drop inventory on close?
    // (False by default for normal slots, true for input slots)
    private Boolean drop_on_close = null;

    // For generated slots: the current ScreenHandler
    public TexturedScreenHandler active_handler = null;

    // Only for is_output slots: decrement the stack in these slots
    private ArrayList<SlotBuilder> decrements_slots = null;

    // Only for is_output slots: call this consumer
    private ArrayList<GuiListener> on_output_consumers = null;

    // Consumer that will wrap an item
    private SlotWrapperListener wrap_stack = null;

    // Consumer that will unwrap an item
    private SlotWrapperListener unwrap_stack = null;

    // Listener that will check for take-access
    private SlotAccessListener take_access_listener = null;

    // Listener that will check for input-access
    private SlotStackChecker input_access_listener = null;

    /**
     * Create an empty SlotBuilder instance,
     * one that will not actually be used in a real screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public SlotBuilder() {
        super(EmptyInventory.INSTANCE, 0, 0, 0);
    }

    /**
     * Create an empty SlotBuilder instance
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public SlotBuilder(Inventory inventory, int inventory_index) {
        super(inventory, inventory_index, 0, 0);
    }

    /**
     * Set the actual index to use in the inventory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    inventory_index   The inventory index to use
     */
    public SlotBuilder mapInventory(int inventory_index) {
        this.inventory_index = inventory_index;
        return this;
    }

    /**
     * Set the actual index to use in the inventory,
     * and the actual inventory to use.
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.4.0
     *
     * @param    inventory_index   The inventory index to use
     * @param    forced_inventory
     */
    public SlotBuilder mapInventory(int inventory_index, Inventory forced_inventory) {
        this.forced_inventory = forced_inventory;
        return this.mapInventory(inventory_index);
    }

    /**
     * Add an item that should be forbidden
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    item   The item to add to the blacklist
     */
    public SlotBuilder deny(Item item) {

        if (this.forbidden_items == null) {
            this.forbidden_items = new ArrayList<>();
        }

        this.forbidden_items.add(item);
        this.has_forbidden_items = true;
        return this;
    }

    /**
     * Add an item class that should be forbidden
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    item_class   The class of the item to add to the blacklist
     */
    public SlotBuilder deny(Class<?> item_class) {

        if (this.forbidden_item_classes == null) {
            this.forbidden_item_classes = new ArrayList<>();
        }

        this.forbidden_item_classes.add(item_class);
        this.has_forbidden_items = true;
        return this;
    }

    /**
     * Add an item that can be allowed
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    item   The item to add to the whitelist
     */
    public SlotBuilder allow(Item item) {

        if (this.allowed_items == null) {
            this.allowed_items = new ArrayList<>();
        }

        this.allowed_items.add(item);
        this.has_allowed_items = true;
        return this;
    }

    /**
     * Add an item class that can be allowed
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    item_class   The class of item to allow
     */
    public SlotBuilder allow(Class<?> item_class) {

        if (this.allowed_item_classes == null) {
            this.allowed_item_classes = new ArrayList<>();
        }

        this.allowed_item_classes.add(item_class);
        this.has_allowed_items = true;
        return this;
    }

    /**
     * Drop on close
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    value   Should the contents drop on close?
     */
    public SlotBuilder dropOnClose(Boolean value) {
        this.drop_on_close = value;
        return this;
    }

    /**
     * Does this drop on close?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public boolean dropOnClose() {

        // If there is no explicit drop_on_close set,
        // only drop if it's an input
        if (this.drop_on_close == null) {
            return this.is_input;
        }

        return this.drop_on_close;
    }

    /**
     * Make this an input slot?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    is_input
     */
    public SlotBuilder isInput(boolean is_input) {
        this.is_input = is_input;

        if (is_input) {
            // If this is an input, it can not be an output
            this.is_output = false;
        }

        return this;
    }

    /**
     * Is this an input slot?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public boolean isInput() {
        return this.is_input;
    }

    /**
     * Make this an output slot?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    is_output
     */
    public SlotBuilder isOutput(boolean is_output) {
        this.is_output = is_output;

        if (is_output) {
            // If this is an output, it can not be an input
            this.is_input = false;
        }

        return this;
    }

    /**
     * Is this an output slot?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public boolean isOutput() {
        return this.is_output;
    }

    /**
     * Call the consumer when the slot is taken out.
     * Multiple listeners are allowed
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    on_output   The function that will be called on output
     */
    public SlotBuilder onOutput(GuiListener on_output) {

        if (this.on_output_consumers == null) {
            this.on_output_consumers = new ArrayList<>();
        }

        this.on_output_consumers.add(on_output);

        return this;
    }

    /**
     * Possibly modify an item on input.
     * Only 1 listener is allowed
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public SlotBuilder wrapStack(SlotWrapperListener wrapper) {
        this.wrap_stack = wrapper;
        return this;
    }

    /**
     * Possibly modify an item on output
     * Only 1 listener is allowed
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public SlotBuilder unwrapStack(SlotWrapperListener unwrapper) {
        this.unwrap_stack = unwrapper;
        return this;
    }

    /**
     * A method that will check if the player can take something out of this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public SlotBuilder checkTakeAccess(SlotAccessListener checker) {
        this.take_access_listener = checker;
        return this;
    }

    /**
     * A method that will check if the player can put something in this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public SlotBuilder checkInputAccess(SlotStackChecker checker) {
        this.input_access_listener = checker;
        return this;
    }

    /**
     * Does taking an item from this decrement another slot?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    slot   The other slot this will decrement
     */
    public SlotBuilder decrements(SlotBuilder slot) {

        if (this.decrements_slots == null) {
            this.decrements_slots = new ArrayList<>();
        }

        this.decrements_slots.add(slot);

        return this;
    }

    /**
     * Set the maximum stack size
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    max_size   The maximum stack size to place here
     */
    public SlotBuilder maxStackSize(int max_size) {
        this.max_stack_size = max_size;
        return this;
    }

    /**
     * Get the inventory index this maps to
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public Integer getInventoryIndex() {
        return this.inventory_index;
    }

    /**
     * Generate the slot to use in an actual screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    handler   The handler we're creating a slot for
     */
    public SlotBuilder createSlot(TexturedScreenHandler handler) {

        PlayerInventory player_inventory = handler.getPlayerInventory();
        Inventory inventory;
        Integer inventory_index = this.inventory_index;

        if (this.forced_inventory != null) {
            inventory = this.forced_inventory;
        } else if (inventory_index != null) {
            // When this slot is specifically mapped to an inventory slot,
            // the handler's main inventory is what we should use
            inventory = handler.getActualInventory();
        } else {
            // This slot has not been mapped to anything! What now?

            if (this.is_output) {
                // It's an output slot, so let's create a CraftingResultInventory for it (it's always size 1)
                inventory = new CraftingResultInventory();
                inventory_index = 0;
            } else {
                // Map it to a temporary inventory then
                inventory = new SimpleInventory(1);
                inventory_index = 0;
            }
        }

        SlotBuilder slot = new SlotBuilder(inventory, inventory_index);

        slot.active_handler = handler;

        if (this.has_allowed_items) {
            slot.allowed_items = this.allowed_items;
            slot.allowed_item_classes = this.allowed_item_classes;
            slot.has_allowed_items = true;
        }

        if (this.has_forbidden_items) {
            slot.forbidden_items = this.forbidden_items;
            slot.forbidden_item_classes = this.forbidden_item_classes;
            slot.has_forbidden_items = true;
        }

        slot.is_input = this.is_input;
        slot.is_output = this.is_output;
        slot.decrements_slots = this.decrements_slots;
        slot.max_stack_size = this.max_stack_size;
        slot.inventory_index = inventory_index;
        slot.on_output_consumers = this.on_output_consumers;
        slot.wrap_stack = this.wrap_stack;
        slot.unwrap_stack = this.unwrap_stack;
        slot.take_access_listener = this.take_access_listener;
        slot.drop_on_close = this.drop_on_close;
        slot.input_access_listener = this.input_access_listener;

        return slot;
    }

    /**
     * Can the given stack be inserted in this slot?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    stack   The stack to insert
     */
    @Override
    public boolean canInsert(ItemStack stack) {

        // You can not put any items in an output slot
        if (this.is_output) {
            return false;
        }

        Item item = stack.getItem();

        // Allow empty slots by default
        if (item == Items.AIR) {
            return true;
        }

        if (!this.checkStackInputAccess(stack)) {
            return false;
        }

        // Check the blacklists first, they get precedence
        if (this.has_forbidden_items) {
            if (this.isBlacklisted(item)) {
                return false;
            }
        }

        // Check the whitelist last
        if (this.has_allowed_items) {
            if (this.isWhitelisted(item)) {
                return true;
            }

            return false;
        }

        return true;
    }

    /**
     * Is the given item the whitelist?
     * Will be false if there are no whitelists.
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    item        The item to test
     */
    public boolean isWhitelisted(Item item) {
        return this.isOnList(item, this.allowed_items, this.allowed_item_classes);
    }

    /**
     * Is the given item the blacklist?
     * Will be false if there are no blacklists.
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    item        The item to test
     */
    public boolean isBlacklisted(Item item) {
        return this.isOnList(item, this.forbidden_items, this.forbidden_item_classes);
    }

    /**
     * Is the given item on the list?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    item        The item to test
     * @param    item_list   A list of items to test against
     * @param    class_list  A list of classes to test against
     */
    public boolean isOnList(Item item, @Nullable List<Item> item_list, @Nullable List<Class<?>> class_list) {

        if (item_list != null) {
            if (item_list.contains(item)) {
                return true;
            }
        }

        if (class_list != null) {
            for (Class<?> entry_class : class_list) {
                if (entry_class.isInstance(item)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Can the player take something out of this slot?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    player
     */
    public boolean canTakeItems(PlayerEntity player) {

        boolean result = true;

        if (this.take_access_listener != null) {
            result = this.take_access_listener.checkAccess(this, player);
        }

        return result;
    }

    /**
     * The player took something out of this slot and is currently holding it
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    player   The player that took something out of the slot
     * @param    stack    The stack that was taken
     */
    public void onTakeItem(PlayerEntity player, ItemStack stack) {

        if (this.is_output) {
            this.active_handler.onTakeOutput(player, stack, this);
        }

        super.onTakeItem(player, stack);
        this.active_handler.onContentChanged();
    }

    /**
     * The player transferred an amount of items out of this slot
     * (Only called when this slot is in a TexturedScreenHandler)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    player         The player that took something out of the slot
     * @param    source_stack   The source stack items were moved from, may already be empty
     * @param    amount_moved   How many items were moved
     * @param    target_slot    The slot the items were transferred to
     */
    public void onTransferOut(PlayerEntity player, ItemStack source_stack, int amount_moved, Slot target_slot) {

        if (this.is_output) {
            this.active_handler.onTransferOutput(player, this, source_stack, amount_moved, target_slot);
        }

        this.active_handler.onContentChanged();
    }

    /**
     * Get the maximum number of items that can be stored in this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public int getMaxItemCount() {
        return Objects.requireNonNullElseGet(this.max_stack_size, super::getMaxItemCount);
    }

    /**
     * Set the index in the screen of this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setScreenIndex(Integer screen_index) {
        this.screen_index = screen_index;
    }

    /**
     * Get the slots to decrement
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ArrayList<SlotBuilder> getActiveSlotsToDecrement() {

        if (this.active_handler == null) {
            return null;
        }

        ArrayList<SlotBuilder> result = new ArrayList<>();

        if (this.decrements_slots == null) {
            return result;
        }

        for (SlotBuilder slot : this.decrements_slots) {
            result.add((SlotBuilder) this.active_handler.slots.get(slot.screen_index));
        }

        return result;
    }

    /**
     * Call the functions on output
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void callOnOutput(ItemStack stack_moved, int amount_moved) {

        if (this.on_output_consumers == null) {
            return;
        }

        for (GuiListener listener : this.on_output_consumers) {
            listener.listenMethod(this.active_handler, this, stack_moved, amount_moved);
        }
    }

    /**
     * Try to move the contents of this slot to a slot in the player's inventory
     * If everything has been moved, return true
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    player   The player that triggered the transfer
     */
    public boolean transferToPlayerInventorySlots(@Nullable PlayerEntity player) {

        ItemStack stack = this.getStack();

        if (stack.isEmpty()) {
            return true;
        }

        // Yeah, we're actually going back to the TexturedScreenHandler
        return this.active_handler.transferSlotToPlayerInventorySlot(this, player);
    }

    /**
     * Insert an mount of items of the given stack in this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    stack   The original stack
     * @param    count   How many items to insert
     */
    public ItemStack insertStack(ItemStack stack, int count) {

        if (!this.checkStackInputAccess(stack)) {
            return stack;
        }

        boolean is_exchanging = this.isExchangingStacks();

        if (!is_exchanging) {
            // Only need to wrap the stack when NO exchange of stacks is occurring,
            // otherwise the stacks will be wrapped automatically
            stack = this.wrapStack(stack);
        }

        // Insert the stack, and get the stack of items remaining
        stack = super.insertStack(stack, count);

        // Set the stack again, it might have been set without getting wrapped.
        if (is_exchanging) {
            // Explicitly get the unwrapped stack, just in case
            ItemStack new_stack = this.unwrapStack(this.getStack());

            // Explicitly use the super setStack method, so it doesn't doubly-wrap the stack!
            super.setStack(this.wrapStack(new_stack));
        }

        // Return the remaining stack, but make sure it's unwrapped
        // (This stack will go into the cursor)
        return this.unwrapStack(stack);
    }

    /**
     * Is this stack allowed (according to the input access listener?)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    stack   The stack
     *
     * @return   True if the stack is allowed (or no listener was set)
     */
    public boolean checkStackInputAccess(ItemStack stack) {

        // If we have an input access listener, use that
        if (this.input_access_listener != null) {
            Boolean result = this.input_access_listener.checkAccess(this.active_handler, this, stack);

            if (result != null && !result) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the stack in this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ItemStack takeStack(int amount) {
        ItemStack result = super.takeStack(amount);
        return this.unwrapStack(result);
    }

    /**
     * Wrap the stack if it's needed
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    stack   The original stack
     */
    public ItemStack wrapStack(ItemStack stack) {

        if (this.wrap_stack == null) {
            return stack;
        }

        return this.wrap_stack.processStack(this.active_handler, this, stack);
    }

    /**
     * Are we swapping slot stacks?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public boolean isSwapping() {

        if (this.active_handler != null) {
            return this.active_handler.isSwapping();
        }

        return false;
    }

    /**
     * Are we quickcrafting?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public boolean isQuickCrafting() {

        if (this.active_handler != null) {
            return this.active_handler.isQuickCrafting();
        }

        return false;
    }

    /**
     * Are we picking up?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public boolean isPickingUp() {

        if (this.active_handler != null) {
            return this.active_handler.isPickingUp();
        }

        return false;
    }

    /**
     * Are we exchanging items with another stack?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public boolean isExchangingStacks() {

        if (this.active_handler == null) {
            return false;
        }

        boolean result = false;

        if (this.active_handler.getSlotActionType() == SlotActionType.QUICK_MOVE) {
            return false;
        }

        Slot clicked_slot = this.active_handler.getClickedSlot();

        if (clicked_slot != null) {

            ItemStack cursor_stack = this.active_handler.getCursorStack();

            if (cursor_stack != null && !cursor_stack.isEmpty()) {
                ItemStack clicked_stack;

                if (clicked_slot instanceof SlotBuilder sb) {
                    clicked_stack = sb.getStackAsIs();
                } else {
                    clicked_stack = clicked_slot.getStack();
                }

                result = !clicked_stack.isEmpty();
            }


        }

        return result;
    }

    /**
     * Insert a stack into this slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ItemStack insertStack(ItemStack stack) {
        return super.insertStack(stack);
    }

    /**
     * Get the slot as it is stored in the inventory
     * (Can be wrapped)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ItemStack getStackAsIs() {
        return super.getStack();
    }

    /**
     * Get the stack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ItemStack getStack() {

        ItemStack result = super.getStack();

        if (this.isSwapping() || this.isExchangingStacks()) {
            result = this.unwrapStack(result);
        }

        return result;
    }

    /**
     * Set the stack
     * (Canceling this will delete the stack)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    stack   The original stack
     */
    @Override
    public void setStack(ItemStack stack) {

        if (this.isQuickCrafting() || this.isSwapping() || this.isExchangingStacks()) {
            stack = this.wrapStack(stack);
        }

        super.setStack(stack);
    }

    /**
     * Unwrap the stack if it's needed
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    stack   The original stack
     */
    public ItemStack unwrapStack(ItemStack stack) {

        if (this.unwrap_stack == null) {
            return stack;
        }

        return this.unwrap_stack.processStack(this.active_handler, this, stack);
    }

    /**
     * Clone this SlotBuilder
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    @Override
    public SlotBuilder clone() {

        SlotBuilder clone = new SlotBuilder();

        if (this.allowed_items != null) {
            clone.allowed_items = (ArrayList<Item>) this.allowed_items.clone();
        }

        if (this.allowed_item_classes != null) {
            clone.allowed_item_classes = (ArrayList<Class<?>>) this.allowed_item_classes.clone();
        }

        if (this.forbidden_items != null) {
            clone.forbidden_items = (ArrayList<Item>) this.forbidden_items.clone();
        }

        if (this.forbidden_item_classes != null) {
            clone.forbidden_item_classes = (ArrayList<Class<?>>) this.forbidden_item_classes.clone();
        }

        clone.has_allowed_items = this.has_allowed_items;
        clone.has_forbidden_items = this.has_forbidden_items;
        clone.max_stack_size = this.max_stack_size;
        clone.is_input = this.is_input;
        clone.is_output = this.is_output;
        clone.drop_on_close = this.drop_on_close;

        clone.wrap_stack = this.wrap_stack;
        clone.unwrap_stack = this.unwrap_stack;
        clone.take_access_listener = this.take_access_listener;
        clone.input_access_listener = this.input_access_listener;

        if (this.decrements_slots != null) {
            clone.decrements_slots = (ArrayList<SlotBuilder>) this.decrements_slots.clone();
        }

        if (this.on_output_consumers != null) {
            clone.on_output_consumers = (ArrayList<GuiListener>) this.on_output_consumers.clone();
        }

        return clone;
    }
}