package rocks.blackblock.screenbuilder.interfaces;

import net.minecraft.item.ItemStack;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;

public interface SelectEventWithValueListener<T> {
    void onSelect(TexturedScreenHandler screen, ItemStack stack, T value);
}
