package rocks.blackblock.screenbuilder.interfaces;

import net.minecraft.item.ItemStack;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.slots.BaseSlot;

import java.util.ArrayList;

@FunctionalInterface
public interface OptionsGetterInterface {
    ArrayList<ItemStack> getOptions(TexturedScreenHandler handler, BaseSlot slot);
}
