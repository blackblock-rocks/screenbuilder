package rocks.blackblock.screenbuilder.inventories;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.bib.BibMod;
import rocks.blackblock.bib.util.BibInventory;
import rocks.blackblock.bib.util.BibItem;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemInventory implements BibInventory.Base {

    public static final Map<PlayerEntity, List<ItemInventory>> PLAYER_INVENTORIES = new HashMap<>();

    protected DefaultedList<ItemStack> contents;
    private PlayerEntity player;
    private ItemStack stack;
    private int size;
    private boolean is_destroyed = false;

    /**
     * Handle dropped items
     * @since    0.5.0
     */
    public static void checkItemInventories(PlayerEntity player, ItemStack stack) {

        var player_list = PLAYER_INVENTORIES.get(player);

        if (player_list == null) {
            return;
        }

        List<ItemInventory> to_remove = new ArrayList<>();

        for (ItemInventory inventory : player_list) {
            if (inventory.destroyIfStackMoved()) {
                to_remove.add(inventory);
            }
        }

        for (ItemInventory inventory : to_remove) {
            player_list.remove(inventory);
        }

    }

    /**
     * Create a new inventory with the given stack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public ItemInventory(@Nullable PlayerEntity player, ItemStack stack, int size) {
        this.player = player;
        this.stack = stack;
        this.size = size;

        NbtCompound nbt = BibItem.getOrCreateCustomNbt(stack);
        NbtCompound item_inventory = nbt.getCompound("ItemInventory");

        if (item_inventory == null) {
            item_inventory = new NbtCompound();
            nbt.put("ItemInventory", item_inventory);
        }

        this.setContentsFromNbt(item_inventory, BibMod.getDynamicRegistry(player));

        var player_list = PLAYER_INVENTORIES.computeIfAbsent(player, k -> new ArrayList<>());
        player_list.add(this);
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
     * Make sure the item stack is still in its original slot
     * @since    0.5.0
     */
    public boolean destroyIfStackMoved() {

        if (this.is_destroyed) {
            return true;
        }

        var inventory = this.player.getInventory();
        int size = inventory.size();
        boolean found_stack = false;

        for (int i = 0; i < size; i++) {
            ItemStack stack = inventory.getStack(i);

            if (stack == this.stack) {
                found_stack = true;
                break;
            }
        }

        if (!found_stack) {
            this.destroy();
            return true;
        }

        return false;
    }

    /**
     * Destroy this inventory.
     * It will be totally cleared!
     *
     * @since    0.5.0
     */
    public void destroy() {
        this.is_destroyed = true;
        this.clear();

        var player = this.player;

        if (player instanceof ServerPlayerEntity server_player) {
            var screen_handler = server_player.currentScreenHandler;

            if (screen_handler instanceof TexturedScreenHandler textured_handler) {
                var handler_inventory = textured_handler.getActualInventory();
                boolean close_current_screen = false;

                if (handler_inventory == this) {
                    close_current_screen = true;
                } else if (handler_inventory instanceof BibInventory.Proxy proxy_inventory) {
                    var proxied_inventory = proxy_inventory.getProxiedInventory();

                    if (proxied_inventory == this) {
                        close_current_screen = true;
                    }
                }

                if (close_current_screen) {
                    textured_handler.close();
                }
            }
        }
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
    @Override
    public void contentsChanged() {
        if (this.destroyIfStackMoved()) {
            return;
        }
        this.writeToItemStack();
    }

    /**
     * Write the NBT data to the itemstack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public void writeToItemStack() {

        NbtCompound nbt = BibItem.getOrCreateCustomNbt(this.stack);
        NbtCompound item_inventory = nbt.getCompound("ItemInventory");

        if (item_inventory == null) {
            item_inventory = new NbtCompound();
        }

        Inventories.writeNbt(item_inventory, this.contents, this.player.getRegistryManager());

        nbt.put("ItemInventory", item_inventory);
    }
}