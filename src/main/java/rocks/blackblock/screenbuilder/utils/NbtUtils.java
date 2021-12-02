package rocks.blackblock.screenbuilder.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;

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
}
