package rocks.blackblock.screenbuilder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.inventories.BaseInventory;
import rocks.blackblock.screenbuilder.items.GuiItem;
import rocks.blackblock.screenbuilder.mixin.ScreenHandlerAccessor;
import rocks.blackblock.screenbuilder.slots.BaseSlot;
import rocks.blackblock.screenbuilder.slots.SlotBuilder;
import rocks.blackblock.screenbuilder.slots.StaticSlot;
import rocks.blackblock.screenbuilder.slots.WidgetSlot;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.text.TextGroup;
import rocks.blackblock.screenbuilder.utils.NbtUtils;

import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.Random;

/**
 * A base ScreenHandler class used to implement modded GUIs
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.0
 * @version  0.1.0
 */
public class TexturedScreenHandler extends ScreenHandler {

    private int width = 9;
    private int height = 6;

    private final Inventory inventory;
    private final ScreenBuilder builder;
    private final PlayerInventory player_inventory;
    private final PlayerEntity player;
    private final ServerPlayerEntity server_player;
    private InventoryChangedListener listener = null;

    public BaseInventory base_inventory = null;
    public SimpleInventory simple_inventory = null;
    public SlotActionType current_action_type = null;
    public Slot clicked_slot = null;

    // The factory that created this handler
    private NamedScreenHandlerFactory origin_factory = null;

    // The optional previous factory
    protected NamedScreenHandlerFactory previous_factory = null;

    // The current renamed string (for anvil guis)
    private String renamed_value = null;

    // The current title
    private Text current_title = null;

    /**
     * Constructed without an attached inventory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    sync_id             The identifier used to communicate between client & server
     * @param    builder             The ScreenBuilder with the screen definitions
     * @param    player_inventory    The inventory of the player that opened the dialog
     */
    protected TexturedScreenHandler(int sync_id, ScreenBuilder builder, PlayerEntity player, PlayerInventory player_inventory) {
        // Instantiate with a fake server-side inventory then
        this(sync_id, builder, player, player_inventory, new SimpleInventory(builder.getScreenTypeSlotCount()));
    }

    /**
     * Constructed with an attached inventory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    sync_id             The identifier used to communicate between client & server
     * @param    builder             The ScreenBuilder with the screen definitions
     * @param    player_inventory    The inventory of the player that opened the dialog
     * @param    inventory           The attached server-side inventory (Probably a BlockEntity)
     */
    public TexturedScreenHandler(int sync_id, ScreenBuilder builder, PlayerEntity player, PlayerInventory player_inventory, Inventory inventory) {

        // These screens are always defined using the 9x6 ScreenHandlerType
        // This is the biggest possible inventory screen Vanilla clients know about
        super(builder.screen_type, sync_id);

        this.player = player;
        this.player_inventory = player_inventory;
        this.inventory = inventory;
        this.builder = builder;

        this.setSlots();

        if (inventory instanceof SimpleInventory simple_inventory) {
            this.listener = this::onContentChanged;

            this.simple_inventory = simple_inventory;
            simple_inventory.addListener(this.listener);
        } else if (inventory instanceof BaseInventory base_inventory) {
            this.base_inventory = base_inventory;

            if (this.base_inventory.getListeners() != null) {
                this.listener = this::onContentChanged;
                this.base_inventory.addListener(this.listener);
            }

            base_inventory.openedByPlayer(player);
        }

        if (inventory instanceof NamedScreenHandlerFactory factory) {
            this.setOriginFactory(factory);
        }

        if (player instanceof ServerPlayerEntity spe) {
            this.server_player = spe;
        } else {
            this.server_player = null;
        }

        if (builder.screen_type == ScreenHandlerType.ANVIL) {
            this.enableSyncing();
        }
    }

    /**
     * Intercept onSlotClick so we can detect which slot action is currently happening
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {

        this.current_action_type = actionType;

        if (slotIndex > -1) {
            this.clicked_slot = this.getSlot(slotIndex);

            if (this.clicked_slot != null && this.clicked_slot instanceof WidgetSlot widget_slot) {
                widget_slot.handleSlotClick(button, actionType, player);

                this.clicked_slot = null;
                this.current_action_type = null;
                return;
            }
        }

        super.onSlotClick(slotIndex, button, actionType, player);
        this.clicked_slot = null;
        this.current_action_type = null;
    }

    /**
     * Set the current title of the screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public void setCurrentTitle(Text title) {
        this.current_title = title;
    }

    /**
     * Get the attached PlayerInventory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public PlayerInventory getPlayerInventory() {
        return this.player_inventory;
    }

    /**
     * Get the player that opened this screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public PlayerEntity getPlayer() {
        return this.player;
    }

    /**
     * Don't return the attached inventory: some sorters use it and we don't want that!
     * @TODO: Maybe return a wrapper around the inventory that is sortable?
     *
     * @deprecated
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public Inventory getInventory() {
        return null;
    }

    /**
     * Get the actual inventory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public Inventory getActualInventory() {
        return this.inventory;
    }

    /**
     * Get the size of this screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public int getScreenSize() {
        return this.width * this.height;
    }

    /**
     * Are we currently quick-crafting?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public boolean isQuickCrafting() {
        int stage = ((ScreenHandlerAccessor) this).getQuickCraftStage();
        return stage != 0;
    }

    /**
     * Are we currently swapping?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public boolean isSwapping() {
        return this.current_action_type == SlotActionType.SWAP;
    }

    /**
     * Are we currently picking up?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public boolean isPickingUp() {
        return this.current_action_type == SlotActionType.PICKUP;
    }

    /**
     * Get the clicked-on slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public Slot getClickedSlot() {
        return this.clicked_slot;
    }

    /**
     * Get the current SlotActionType
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public SlotActionType getSlotActionType() {
        return this.current_action_type;
    }

    /**
     * Set the stack of the cursor
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setCursorStack(ItemStack stack) {
        super.setCursorStack(stack);
    }

    /**
     * Set the slots
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public void setSlots() {

        int screen_index = 0;

        for (Slot slot : this.builder.getSlots()) {

            if (slot instanceof SlotBuilder build_slot) {
                SlotBuilder generated_slot = build_slot.createSlot(this);

                generated_slot.setScreenIndex(screen_index);

                slot = generated_slot;
            } else if (slot instanceof BaseSlot base_slot) {
                BaseSlot generated_slot = base_slot.createSlot(this);
                generated_slot.setScreenIndex(screen_index);

                slot = generated_slot;
            }

            this.addSlot(slot);
            screen_index++;
        }

        if (this.builder.getShowPlayerInventory()) {
            // Show the player's inventory
            for (int y = 0; y < 3; ++y) {
                for (int x = 0; x < 9; ++x) {
                    Slot slot = new Slot(this.player_inventory, x + y * 9 + 9, 0, 0);
                    this.addSlot(slot);
                }
            }
        } else {
            // Do not show the player's inventory
            for (int y = 0; y < 3; ++y) {
                for (int x = 0; x < 9; ++x) {
                    Slot slot = new StaticSlot(new ItemStack(Items.AIR));
                    this.addSlot(slot);
                }
            }
        }

        if (this.builder.getShowPlayerHotbar()) {
            // Show the player's hotbar
            // (This is linked to the actual hotbar players see on the bottom of their screen)
            for (int hotbar = 0; hotbar < 9; ++hotbar) {
                Slot slot = new Slot(this.player_inventory, hotbar, 0, 0);
                this.addSlot(slot);
            }
        } else {
            // Don't show the hotbar
            for (int hotbar = 0; hotbar < 9; ++hotbar) {
                Slot slot = new StaticSlot(new ItemStack(Items.AIR));
                this.addSlot(slot);
            }
        }
    }

    /**
     * The player is taking something out of an output slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    player        The player that took the stack out
     * @param    stack         The stack that was taken out
     * @param    output_slot   The actual output slot
     */
    public void onTakeOutput(PlayerEntity player, ItemStack stack, SlotBuilder output_slot) {

        int amount_moved = stack.getCount();

        output_slot.callOnOutput(stack, amount_moved);

        for (SlotBuilder input_slot : output_slot.getActiveSlotsToDecrement()) {
            input_slot.getStack().decrement(amount_moved);
        }
    }

    /**
     * The player transferred something out of an output slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    player         The player that initiated the transfer
     * @param    output_slot    The output slot we transferred something out of
     * @param    source_stack   The output slot's stack (may already be empty)
     * @param    amount_moved   The amount of items that were transferred
     * @param    target_slot    The slot the items were moved into
     */
    public void onTransferOutput(PlayerEntity player, SlotBuilder output_slot, ItemStack source_stack, int amount_moved, Slot target_slot) {

        output_slot.callOnOutput(source_stack, amount_moved);

        for (SlotBuilder input_slot : output_slot.getActiveSlotsToDecrement()) {
            input_slot.getStack().decrement(amount_moved);
        }
    }

    /**
     * Handle a shift click on the specific screen slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    player          The player that is doing the clicking
     * @param    screen_index    The index of the slot that was clicked on the screen
     */
    @Override
    public ItemStack transferSlot(PlayerEntity player, int screen_index) {

        ItemStack new_stack = ItemStack.EMPTY;

        // Get the actual slot that was clicked
        Slot screen_slot = this.slots.get(screen_index);

        // Clicking on a StaticSlot can always be ignored
        if (screen_slot instanceof StaticSlot) {
            return new_stack;
        }

        if (screen_slot.hasStack()) {

            int screen_size = this.width * this.height;

            ItemStack visual_stack = screen_slot.getStack();
            ItemStack original_stack = visual_stack;

            if (screen_slot instanceof SlotBuilder sb) {
                original_stack = sb.unwrapStack(visual_stack);
            }

            new_stack = original_stack.copy();

            if (screen_index < screen_size) {

                if (screen_slot instanceof SlotBuilder build_slot) {
                    if (build_slot.transferToPlayerInventorySlots(player)) {
                        this.triggerBaseInventoryChange();
                        return ItemStack.EMPTY;
                    }
                } else {

                    // Move the items from the GUI to the player's inventory
                    // using the confusing insertItem method
                    if (!this.insertItem(original_stack, screen_size, this.slots.size(), true)) {
                        this.triggerBaseInventoryChange();
                        return ItemStack.EMPTY;
                    }
                }

            } else {

                Item item = original_stack.getItem();
                int index = -1;
                boolean inserted_all = false;
                ItemStack wrapped_stack = original_stack;

                for (Slot slot : this.slots) {
                    index++;

                    // Only check the GUI slots, not the player inventory
                    if (index >= screen_size) {
                        break;
                    }

                    if (slot instanceof StaticSlot) {
                        continue;
                    }

                    original_stack.setCount(wrapped_stack.getCount());

                    if (slot instanceof SlotBuilder sb) {
                        wrapped_stack = sb.wrapStack(original_stack);
                    }

                    if (slot instanceof SlotBuilder build_slot) {
                        Integer inventory_index = build_slot.getInventoryIndex();

                        // If it's a sided inventory we can also check that
                        // If that check fails, we can skip this slot too
                        if (inventory_index != null && this.inventory instanceof SidedInventory && !((SidedInventory) this.inventory).canInsert(inventory_index, wrapped_stack, null)) {
                            continue;
                        }
                    }

                    // Can this slot take the type of stack?
                    // (canInsert methods mostly don't check the size of the stacks)
                    if (slot.canInsert(visual_stack)) {

                        int max_screen_stack_size = slot.getMaxItemCount();

                        // We only need to check this if the max item count in the slot is different than
                        // what the item allows
                        if (max_screen_stack_size > 0 && max_screen_stack_size < wrapped_stack.getMaxCount()) {

                            int clicked_stack_size = wrapped_stack.getCount();

                            ItemStack screen_stack = slot.getStack();

                            // If the stack in this slot is empty, we can probably put some stuff in there
                            // So create a new stack with this item, but set the count to 0
                            if (screen_stack.isEmpty()) {
                                screen_stack = wrapped_stack.copy();
                                screen_stack.setCount(0);
                                slot.setStack(screen_stack);
                            } else if (!ItemStack.canCombine(wrapped_stack, screen_stack)) {
                                // If the items are not combinable (different types maybe? NBT tags?)
                                // then continue to the next slot
                                continue;
                            }

                            int screen_stack_size = screen_stack.getCount();

                            if (screen_stack_size < max_screen_stack_size) {
                                // The stack on the screen hasn't reached the max size yet!

                                // Calculate the sum of the 2 stacks
                                int combined_stack_size = clicked_stack_size + screen_stack_size;

                                if (combined_stack_size <= max_screen_stack_size) {
                                    wrapped_stack.setCount(0);
                                    screen_stack.setCount(combined_stack_size);
                                    slot.markDirty();
                                    inserted_all = true;
                                    break;
                                } else {
                                    int amount_to_move = max_screen_stack_size - screen_stack_size;
                                    wrapped_stack.decrement(amount_to_move);
                                    screen_stack.increment(amount_to_move);
                                    slot.markDirty();
                                }
                            }

                            continue;
                        }

                        // From this point on, we just rely on the allowed Item stack sizes
                        if (this.insertItem(wrapped_stack, index, index +1, false)) {
                            inserted_all = true;
                            break;
                        }
                    }
                }

                original_stack.setCount(wrapped_stack.getCount());

                if (!inserted_all) {
                    this.triggerBaseInventoryChange();
                    return ItemStack.EMPTY;
                }

            }

            if (original_stack.isEmpty()) {
                screen_slot.setStack(ItemStack.EMPTY);
            } else {
                screen_slot.markDirty();
            }
        }

        this.triggerBaseInventoryChange();

        return new_stack;
    }

    /**
     * If a base inventory is attached to this screen, trigger a change on it
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void triggerBaseInventoryChange() {
        if (this.base_inventory != null) {
            this.base_inventory.contentsChanged();
        }
    }

    /**
     * Move the contents of the given slot to the PlayerInventory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    slot      The slot that contains the items that should be moved
     * @param    player    The player that triggered the transfer
     */
    public boolean transferSlotToPlayerInventorySlot(Slot slot, @Nullable PlayerEntity player) {
        return this.transferSlotToOtherSlots(slot, this.getScreenSize(), this.slots.size(), player);
    }

    /**
     * Move the contents of the given slot to any of the slots at the given indexes
     *
     * @TODO: "fromLast" functionality (transfer in a certain direction)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    slot          The slot that contains the items that should be moved
     * @param    start_index   The index of the first slot to try
     * @param    end_index     The exclusive index of the last slot
     * @param    player        The player that triggered the transfer
     */
    public boolean transferSlotToOtherSlots(Slot slot, int start_index, int end_index, @Nullable PlayerEntity player) {

        boolean moved_everything = false;
        boolean moved_anything = false;

        // The stack in the slot might be wrapped, that's why we call it the "visual stack"
        ItemStack visual_stack = slot.getStack();

        // Use the visual stack as the source stack
        ItemStack source_stack = visual_stack;

        // If the slot is a SlotBuilder instance, the stack might be wrapped!
        // Let's unwrap it
        if (slot instanceof SlotBuilder sb) {
            source_stack = sb.unwrapStack(source_stack);
        }

        Slot target_slot;
        ItemStack visual_target_stack;
        ItemStack target_stack;

        // We might have to wrap the source for putting it into some slots
        ItemStack wrapped_source_stack = source_stack;

        int index = start_index - 1;

        // First pass: see if any stacks can be merged
        if (source_stack.isStackable()) {
            while (!source_stack.isEmpty()) {
                index++;

                if (index >= end_index) {
                    break;
                }

                target_slot = this.slots.get(index);
                visual_target_stack = target_slot.getStack();
                target_stack = visual_target_stack;

                source_stack.setCount(wrapped_source_stack.getCount());

                if (target_slot instanceof SlotBuilder sb) {
                    target_stack = sb.unwrapStack(visual_target_stack);
                    wrapped_source_stack = sb.wrapStack(source_stack);
                } else {
                    wrapped_source_stack = source_stack;
                }

                if (target_stack.isEmpty() || !ItemStack.canCombine(wrapped_source_stack, target_stack)) {
                    continue;
                }

                int target_stack_count = target_stack.getCount();
                int combined_size = wrapped_source_stack.getCount() + target_stack_count;
                int max_count;

                if (target_slot instanceof SlotBuilder build_slot) {
                    max_count = build_slot.getMaxItemCount();
                } else {
                    max_count = target_stack.getMaxCount();
                }

                // If the combined size is smaller than the max stack size, it's allowed!
                if (combined_size <= max_count) {

                    int moved_amount = wrapped_source_stack.getCount();

                    wrapped_source_stack.setCount(0);
                    target_stack.setCount(combined_size);

                    if (slot instanceof SlotBuilder build_slot) {
                        build_slot.onTransferOut(player, wrapped_source_stack, moved_amount, target_slot);
                    }

                    target_slot.markDirty();
                    moved_everything = true;
                    moved_anything = true;
                    break;
                }

                // The combined size might not fit, but see if there's room for some items
                if (target_stack_count < max_count) {

                    int moved_amount = max_count - target_stack_count;

                    wrapped_source_stack.decrement(moved_amount);
                    target_stack.setCount(max_count);

                    if (slot instanceof SlotBuilder build_slot) {
                        build_slot.onTransferOut(player, wrapped_source_stack, moved_amount, target_slot);
                    }

                    target_slot.markDirty();
                    moved_anything = true;
                }
            }

            // Sync the counts
            source_stack.setCount(wrapped_source_stack.getCount());
        }

        if (moved_everything) {
            visual_stack.setCount(source_stack.getCount());
            this.triggerBaseInventoryChange();
            return true;
        }

        index = start_index - 1;

        // Now do moving to empty slots
        while (true) {
            index++;

            if (index >= end_index) {
                break;
            }

            target_slot = this.slots.get(index);
            target_stack = target_slot.getStack();

            source_stack.setCount(wrapped_source_stack.getCount());

            if (target_slot instanceof SlotBuilder sb) {
                wrapped_source_stack = sb.wrapStack(source_stack);
            } else {
                wrapped_source_stack = source_stack;
            }

            if (target_stack.isEmpty() && target_slot.canInsert(wrapped_source_stack)) {

                int max_count;

                if (target_slot instanceof SlotBuilder build_slot) {
                    max_count = build_slot.getMaxItemCount();
                } else {
                    max_count = wrapped_source_stack.getMaxCount();
                }

                if (wrapped_source_stack.getCount() > max_count) {

                    int moved_amount = wrapped_source_stack.getCount() - max_count;

                    target_slot.setStack(wrapped_source_stack.split(max_count));
                    target_slot.markDirty();
                    moved_anything = true;

                    if (slot instanceof SlotBuilder build_slot) {
                        build_slot.onTransferOut(player, wrapped_source_stack, moved_amount, target_slot);
                    }

                    continue;
                } else {

                    int moved_amount = wrapped_source_stack.getCount();

                    target_slot.setStack(wrapped_source_stack.split(moved_amount));
                    moved_everything = true;

                    if (slot instanceof SlotBuilder build_slot) {
                        build_slot.onTransferOut(player, wrapped_source_stack, moved_amount, target_slot);
                    }

                    break;
                }

            }
        }

        // Sync the counts
        source_stack.setCount(wrapped_source_stack.getCount());

        visual_stack.setCount(source_stack.getCount());
        this.triggerBaseInventoryChange();

        return moved_everything;
    }

    /**
     * Close the GUI
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public void close() {

        if (this.server_player != null) {
            BBSB.log("Should close this screen", this);

            // Make sure there is a screen handler open first
            if (this.server_player.currentScreenHandler != null) {

                // Now see if it's the same screen handler
                if (this.server_player.currentScreenHandler.syncId == this.syncId) {
                    this.server_player.closeHandledScreen();
                }
            }
        }

    }

    /**
     * The GUI has been closed
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    player      The player that was using this GUI
     */
    public void close(PlayerEntity player) {

        if (this.builder != null) {
            this.builder.handleClose(player);
        }

        super.close(player);
        this.dropInputs(player);

        if (this.base_inventory != null) {
            this.base_inventory.closedByPlayer(player);
        }

        if (this.listener != null) {
            if (this.base_inventory != null && this.base_inventory.getListeners() != null) {
                this.base_inventory.removeListener(this.listener);
            }

            if (this.simple_inventory != null) {
                this.simple_inventory.removeListener(this.listener);
            }
        }
    }

    /**
     * Drop the input items
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    player      The player that was using this GUI
     */
    public void dropInputs(PlayerEntity player) {

        for (SlotBuilder slot : this.getInputSlots()) {
            if (slot.dropOnClose()) {
                this.dropInventory(player, slot.inventory);
            }
        }
    }

    /**
     * Get all the input slots
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ArrayList<SlotBuilder> getInputSlots() {

        ArrayList<SlotBuilder> result = new ArrayList<>();

        for (Slot slot : this.slots) {
            if (slot instanceof SlotBuilder build_slot) {
                if (build_slot.isInput()) {
                    result.add(build_slot);
                }
            }
        }

        return result;
    }

    /**
     * The attached inventory has informed us of changes to its content.
     * Inventories don't have to do this,
     * you'll probably have to implement a call to this method yourself.
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    inventory      The inventory that changes
     */
    public void onContentChanged(Inventory inventory) {
        super.sendContentUpdates();
        this.builder.screenHasChanged(this);
    }

    /**
     * Something told us the content has changed
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void onContentChanged() {
        super.sendContentUpdates();
        this.builder.screenHasChanged(this);
    }

    /**
     * Handle a rename action (for Anvil screens)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void onRenameItem(String new_name) {

        this.renamed_value = new_name;

        this.getSlot(0).getStack().setCustomName(new LiteralText(""));

        Slot output = this.getSlot(2);
        ItemStack output_stack = new ItemStack(GuiItem.get("true"));
        output_stack.setCustomName(new LiteralText("Accept").setStyle(Style.EMPTY.withItalic(false)));
        output.setStack(output_stack);

        NbtUtils.appendLore(output_stack, new LiteralText("\"").append(new_name).append("\""));

        this.sendContentUpdates();
    }

    /**
     * Send content updates
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void sendContentUpdates() {

        // We have to keep updating the output slot for anvils
        if (this.builder.screen_type == ScreenHandlerType.ANVIL) {
            ScreenHandlerSyncHandler handler = ((ScreenHandlerAccessor) this).getSyncHandler();

            if (handler != null) {

                Slot input_1 = this.getSlot(0);
                //Slot input_2 = this.getSlot(1);
                Slot output = this.getSlot(2);

                handler.updateSlot(this, 0, input_1.getStack());
                //handler.updateSlot(this, 1, input_2.getStack());
                handler.updateSlot(this, 2, output.getStack());

                // Override levelcost property
                handler.updateProperty(this, 0, 0);
            }
        }

        super.sendContentUpdates();
    }

    /**
     * Force a specific slot to update
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void forceSlotUpdate(int index) {

        Slot slot = this.getSlot(index);

        if (slot == null) {
            return;
        }

        ScreenHandlerSyncHandler handler = ((ScreenHandlerAccessor) this).getSyncHandler();

        if (handler != null) {
            handler.updateSlot(this, index, slot.getStack());
        }

    }

    /**
     * Set the factory that created this handler
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setOriginFactory(NamedScreenHandlerFactory factory) {
        this.origin_factory = factory;
    }

    /**
     * Set the previous screen's factory, allowing us to go back
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setPreviousFactory(NamedScreenHandlerFactory factory) {
        this.previous_factory = factory;
    }

    /**
     * Show the previous screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void showPreviousScreen() {

        if (this.previous_factory != null) {
            this.showScreen(this.previous_factory);
        } else {
            this.close();
        }
    }

    /**
     * Get the screenbuilder instance
     *
     * @since   0.1.1
     */
    public ScreenBuilder getScreenBuilder() {
        return this.builder;
    }

    /**
     * Show another screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public TexturedScreenHandler showScreen(NamedScreenHandlerFactory factory) {

        PlayerEntity player = this.getPlayer();

        if (!(player instanceof ServerPlayerEntity server_player)) {
            return null;
        }

        OptionalInt result = server_player.openHandledScreen(factory);

        if (result.isEmpty()) {
            return null;
        }

        ScreenHandler handler = server_player.currentScreenHandler;

        if (handler == null) {
            return null;
        }

        if (handler instanceof TexturedScreenHandler ts_handler) {
            return ts_handler;
        }

        return null;
    }

    /**
     * Push another screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void pushScreen(NamedScreenHandlerFactory factory) {

        TexturedScreenHandler handler = this.showScreen(factory);

        if (handler == null) {
            return;
        }

        if (this.origin_factory != null) {
            handler.setPreviousFactory(this.origin_factory);
        }
    }

    /**
     * Replace this screen with another screen, keeping the history
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public void replaceScreen(NamedScreenHandlerFactory factory) {

        TexturedScreenHandler handler = this.showScreen(factory);

        if (handler == null) {
            return;
        }

        if (this.previous_factory != null) {
            handler.setPreviousFactory(this.previous_factory);
        }
    }

    /**
     * Refresh the screen by re-opening it
     * (This is an example implementation, this can actually be used to update the screen)
     *
     * @since   0.1.1
     */
    public void refresh() {

        PlayerEntity player = this.getPlayer();

        if (player instanceof ServerPlayerEntity sp) {

            TextBuilder builder = this.getTextBuilder();
            Text title = builder.build();

            // Send the "OpenSCreen" packet to the client, with the existing sync id.
            sp.networkHandler.sendPacket(new OpenScreenS2CPacket(this.syncId, this.getType(), title));

            // Always sync the state afterwards, that's needed to keep the contents of the cursor
            this.syncState();
        }
    }

    /**
     * Get the factory that created this screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public NamedScreenHandlerFactory getOriginFactory() {
        return this.origin_factory;
    }

    /**
     * Get the WidgetDataProvider
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public WidgetDataProvider getWidgetDataProvider() {

        if (this.origin_factory != null) {
            if (this.origin_factory instanceof WidgetDataProvider provider) {
                return provider;
            }
        }

        return null;
    }

    /**
     * Get the previously shown factory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public NamedScreenHandlerFactory getPreviousFactory() {
        return this.previous_factory;
    }

    /**
     * Get the renamed value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public String getRenamedValue() {
        return this.renamed_value;
    }

    /**
     * Get a TextBuilder instance for this screen.
     * It can then be used to supply the DisplayName.
     *
     * @since     0.1.1
     */
    public TextBuilder getTextBuilder() {

        // See if we already know the title
        Text title = this.current_title;

        // Create the text builder
        TextBuilder text_builder = new TextBuilder(this);

        // If we don't, get it from the factory
        if (title == null) {
            NamedScreenHandlerFactory factory = this.getOriginFactory();

            if (factory != null) {
                title = factory.getDisplayName();
            }

            if (title == null && this.builder != null) {
                title = this.builder.getDisplayName();
            }
        }

        this.builder.addToTextBuilder(text_builder);

        text_builder.setTitle(title);

        return text_builder;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}