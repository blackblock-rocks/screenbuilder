package rocks.blackblock.screenbuilder.utils;

import net.minecraft.SharedConstants;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import rocks.blackblock.screenbuilder.interfaces.CompareForScenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * Set the title/custom name of the given stack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public static void setTitle(ItemStack stack, Text text) {
        stack.set(DataComponentTypes.CUSTOM_NAME, text);
    }

    /**
     * Append to the actual Lore nbt data
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public static void appendLore(ItemStack stack, Text text) {
        var lore_component = stack.getOrDefault(DataComponentTypes.LORE, new LoreComponent(List.of()));

        List<Text> list = lore_component.lines();
        list.add(text);
    }

    /**
     * Replace the lore
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.1
     */
    public static void replaceLore(ItemStack stack, List<MutableText> lines) {
        List<Text> simplified_lines = new ArrayList<>(lines);
        stack.set(DataComponentTypes.LORE, new LoreComponent(simplified_lines));
    }

    /**
     * Replace the lore
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public static void replaceLore(ItemStack stack, Text text) {
        List<Text> simplified_lines = new ArrayList<>(1);
        simplified_lines.add(text);
        stack.set(DataComponentTypes.LORE, new LoreComponent(simplified_lines));
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

        return Objects.equals(left.getComponents(), right.getComponents());
    }

    /**
     * Can the 2 stacks be combined?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.4.3
     */
    public static boolean canCombine(ItemStack left, ItemStack right) {
        return ItemStack.areItemsAndComponentsEqual(left, right);
    }

    /**
     * Return the json string representation of the given text
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.4.3
     */
    public static String serializeTextToJson(Text text) {
        return Text.Serialization.toJsonString(text, getDynamicRegistry());
    }

    /**
     * Get the old-style NBT compound
     *
     * @since    0.5.2
     */
    public static NbtCompound getCustomNbt(ItemStack stack) {
        NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);

        if (data == null) {
            data = NbtComponent.of(new NbtCompound());
            stack.set(DataComponentTypes.CUSTOM_DATA, data);
        }

        return data.getNbt();
    }

    /**
     * Get the old-style NBT compound
     *
     * @since    0.5.2
     */
    public static void setCustomNbt(ItemStack stack, NbtCompound nbt) {
        NbtComponent data = NbtComponent.of(nbt);
        stack.set(DataComponentTypes.CUSTOM_DATA, data);
    }

    /**
     * Does the given stack have custom nbt?
     *
     * @since    0.5.2
     */
    public static boolean hasCustomNbt(ItemStack stack) {

        NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);

        if (data == null) {
            return false;
        }

        return !data.isEmpty();
    }

    /**
     * Remove invalid characters from a packet name.
     * Used to be a function in SharedConstants
     *
     * @since    0.5.2
     */
    public static String stripInvalidChars(String input) {
        for (char c : SharedConstants.INVALID_CHARS_LEVEL_NAME) {
            input = input.replace(c, '_');
        }

        return input;
    }

    /**
     * Get a dynamic registry.
     * Putting this here for now because I don't know
     * how important it is to get the correct one in certain places
     *
     * @since    0.5.2
     */
    public static RegistryWrapper.WrapperLookup getDynamicRegistry() {
        return DynamicRegistryManager.EMPTY;
    }

    /**
     * Serialize an ItemStack into an NbtElement
     *
     * @since    0.5.2
     */
    public static NbtElement serializeStack(ItemStack stack) {

        if (stack == null) {
            return null;
        }

        return stack.encode(getDynamicRegistry());
    }

    /**
     * Deserialize an NbtElement into an ItemStack
     *
     * @since    0.5.2
     */
    public static ItemStack deserializeToStack(NbtElement nbt) {
        return ItemStack.fromNbt(getDynamicRegistry(), nbt).orElse(null);
    }
}
