package rocks.blackblock.screenbuilder.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import rocks.blackblock.screenbuilder.interfaces.CompareForScenario;

public class NbtUtils {
    /**
     * Append to the actual Lore nbt data
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public static void appendLore(ItemStack stack, Text text) {
        NbtCompound display = stack.getOrCreateSubNbt("display");
        NbtList list = display.getList("Lore", 8);

        if (list == null) {
            list = new NbtList();
        }

        list.add(NbtString.of(Text.Serializer.toJson(text)));

        display.put("Lore", list);
    }

    /**
     * See if these 2 stacks are equal for the given scenario
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public static boolean areEqualFor(ItemStack leftStack, ItemStack rightStack, String scenario) {

        Item left = leftStack.getItem();
        Item right = rightStack.getItem();

        // Check if the items are the same
        if (left != right) {
            return false;
        }

        Boolean result = null;

        if (left instanceof CompareForScenario scenarioItem && scenarioItem.supportsScenario(scenario)) {
            result = scenarioItem.compareForScenario(leftStack, rightStack, scenario);

            if (result != null) {
                return result;
            }
        }

        return ItemStack.areEqual(leftStack, rightStack);
    }
}
