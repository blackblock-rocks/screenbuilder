package rocks.blackblock.screenbuilder.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import rocks.blackblock.screenbuilder.inputs.BooleanInput;
import rocks.blackblock.screenbuilder.inputs.ItemInput;
import rocks.blackblock.screenbuilder.inputs.StringInput;
import rocks.blackblock.screenbuilder.items.GuiItem;
import rocks.blackblock.screenbuilder.slots.SelectSlot;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GuiUtils {
    public static final String MOD_ID = "blackblock";
    private static final HashMap<String, Item> items = new HashMap();

    public static List<Slot> removePlayerSlots(List<Slot> base) {
        return base.stream().filter(
                (slot) -> !(slot.inventory instanceof PlayerInventory)
        ).collect(Collectors.toList());
    }

    public static void resyncPlayerInventory(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            resyncPlayerInventory((ServerPlayerEntity)player);
        }
    }

    public static void resyncPlayerInventory(ServerPlayerEntity player) {
        player.currentScreenHandler.syncState();
    }

    /**
     * Register & create all (missing) gui items
     */
    public static void registerGuiItems() {
        GuiItem.get("true");
        GuiItem.get("false");
        GuiItem.get("arrow_left");
        GuiItem.get("checkbox_unchecked");
        GuiItem.get("checkbox_checked");

        SelectSlot.registerScreen();
        StringInput.registerScreen();
        BooleanInput.registerScreen();
        ItemInput.registerScreen();
    }

    /**
     * Get and/or create a gui item
     */
    public static Item getGuiItem(String name) {

        if (items.containsKey(name)) {
            return items.get(name);
        }

        Identifier identifier = new Identifier(MOD_ID, name);
        Item item = Registry.register(Registry.ITEM, identifier, new GuiItem());

        setGuiItem(name, item);

        return item;
    }

    public static void setGuiItem(String name, Item item) {
        items.put(name, item);
    }
}
