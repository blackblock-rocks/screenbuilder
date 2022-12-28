package rocks.blackblock.screenbuilder.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import rocks.blackblock.screenbuilder.interfaces.CompareForScenario;

public class NbtUtils {

    /**
     * Get a list without checking the type
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public static NbtList getList(NbtCompound compound, String key) {

        NbtElement element = compound.get(key);

        if (element == null) {
            return null;
        }

        return (NbtList) element;
    }

    /**
     * Set the title of the given stack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public static void setTitle(ItemStack stack, Text text) {
        stack.setCustomName(text);
    }

    /**
     * Append to the actual Lore nbt data
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public static void appendLore(ItemStack stack, Text text) {
        NbtCompound display = stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY);
        NbtList list = display.getList(ItemStack.LORE_KEY, 8);

        if (list == null) {
            list = new NbtList();
        }

        list.add(NbtString.of(Text.Serializer.toJson(text)));

        display.put(ItemStack.LORE_KEY, list);
    }

    /**
     * Replace the lore
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public static void replaceLore(ItemStack stack, Text text) {
        NbtCompound display = stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY);
        NbtList list = new NbtList();

        list.add(NbtString.of(Text.Serializer.toJson(text)));

        display.put(ItemStack.LORE_KEY, list);
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

    /**
     * Are the 2 item stacks equal, ignoring damage?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.0
     */
    public static boolean areEqualIgnoreDamage(ItemStack left, ItemStack right) {

        if (left.getItem() != right.getItem()) {
            return false;
        }

        if (left.isDamageable()) {
            left = left.copy();
            right = right.copy();

            left.setDamage(0);
            right.setDamage(0);
        }

        NbtCompound left_nbt = left.getNbt();
        NbtCompound right_nbt = right.getNbt();

        if (left_nbt == right_nbt) {
            return true;
        }

        if (left_nbt == null || right_nbt == null) {
            return false;
        }

        return left.getNbt().equals(right.getNbt());
    }
}
